package me.kyd3snik.test.diff

import com.android.build.gradle.api.BaseVariant
import com.android.builder.core.ComponentType.Companion.UNIT_TEST_PREFIX
import com.android.builder.core.ComponentType.Companion.UNIT_TEST_SUFFIX
import me.kyd3snik.test.diff.changes.ChangesStore
import me.kyd3snik.test.diff.test.resolver.FilterTestResolver
import me.kyd3snik.test.diff.test.resolver.TestResolver
import me.kyd3snik.test.diff.test.resolver.UsageTestResolver
import me.kyd3snik.test.diff.test.resolver.usage.AsmUsageCollector
import me.kyd3snik.test.diff.utils.TaskCompat
import me.kyd3snik.test.diff.utils.capitalized
import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.FileTree
import org.gradle.api.file.RegularFile
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.logging.Logger
import org.gradle.api.logging.Logging
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.api.specs.Spec
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.TaskProvider
import org.gradle.api.tasks.testing.Test
import org.gradle.api.tasks.testing.TestFilter
import org.gradle.internal.logging.slf4j.DefaultContextAwareTaskLogger
import javax.inject.Inject

abstract class TestDiffTask : DefaultTask() {

    @get:InputFile
//    @get:SkipWhenEmpty
    abstract val changesFile: RegularFileProperty

    @get:Internal
    abstract val testClassesDirs: Property<FileTree>

    @get: Internal
    abstract val filter: Property<TestFilter>

    @get:Inject
    abstract val objectFactory: ObjectFactory

    @TaskAction
    fun testDiff() {
        val changesStore = ChangesStore(changesFile.get().asFile)
        val changes = objectFactory.fileCollection().from(changesStore.read())
        logChanges(changes)
        val testResolver = createTestResolver()
        val filter = filter.get()
        testResolver.resolve(changes, filter)
        logFilter(filter)
    }

    private fun createTestResolver(): TestResolver = FilterTestResolver(
        supportedExtensions = setOf("java", "kt"),
        delegate = UsageTestResolver(AsmUsageCollector(), testClassesDirs.get())
    )

    @Internal
    override fun getLogger(): Logger = Companion.logger

    private fun logFilter(filter: TestFilter) {
        logger.info(
            filter.includePatterns.joinToString(
                prefix = "Includes:\n{\n\t",
                separator = "\n\t",
                postfix = "\n}"
            )
        )
    }

    private fun logChanges(changes: ConfigurableFileCollection) {
        logger.info(changes.joinToString(prefix = "Changes:\n{\n\t", separator = "\n\t", postfix = "\n}"))
    }

    companion object {

        private val logger = DefaultContextAwareTaskLogger(Logging.getLogger(TestDiffTask::class.java))

        fun register(
            project: Project,
            variant: BaseVariant,
            changesFile: Provider<RegularFile>,
        ): TaskProvider<TestDiffTask> {
            val variantName = variant.name.capitalized()
            return project.tasks.register("testDiff${variantName}UnitTest", TestDiffTask::class.java) { task ->
                val delegateName = "$UNIT_TEST_PREFIX$variantName$UNIT_TEST_SUFFIX"
                val delegate = project.tasks.named(delegateName, Test::class.java).get()
                task.changesFile.set(changesFile)
                task.testClassesDirs.set(delegate.testClassesDirs.asFileTree)
                task.filter.set(delegate.filter)
                task.dependsOn(delegate.classpath)
                delegate.dependsOn(task) // cancel running delegate if this task failed
                delegate.onlyIf(OnlyIfHasFiltersSpec())
                task.finalizedBy(delegate)

                task.group = delegate.group
                task.description = "Runs tests for changed files"
                TaskCompat.notCompatibleWithConfigurationCache(
                    task,
                    "Unsupported to write tasks that configure other tasks at execution time"
                )
            }
        }
    }
}

class OnlyIfHasFiltersSpec : Spec<Task> {

    override fun isSatisfiedBy(element: Task?): Boolean =
        element !is Test || element.filter.includePatterns.isNotEmpty()
}
