package io.github.kartashovaa.teddi

import com.android.build.gradle.api.BaseVariant
import com.android.builder.core.ComponentType.Companion.UNIT_TEST_PREFIX
import com.android.builder.core.ComponentType.Companion.UNIT_TEST_SUFFIX
import io.github.kartashovaa.teddi.changes.ChangesStore
import io.github.kartashovaa.teddi.test.resolver.FilterTestResolver
import io.github.kartashovaa.teddi.test.resolver.TestResolver
import io.github.kartashovaa.teddi.test.resolver.UsageTestResolver
import io.github.kartashovaa.teddi.test.resolver.usage.AsmUsageCollector
import io.github.kartashovaa.teddi.utils.TaskCompat
import io.github.kartashovaa.teddi.utils.capitalized
import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.FileTree
import org.gradle.api.file.RegularFile
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.logging.LogLevel
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
    abstract val changesFile: RegularFileProperty

    @get:Internal
    abstract val testClassesDirs: Property<FileTree>

    @get:Internal
    abstract val filter: Property<TestFilter>

    @get:Inject
    abstract val objectFactory: ObjectFactory

    private var logLevel = LogLevel.INFO

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

    private fun setVerbose(isVerbose: Boolean) {
        logLevel = if (isVerbose) LogLevel.LIFECYCLE else LogLevel.INFO
    }

    @Internal
    override fun getLogger(): Logger = Companion.logger

    private fun logFilter(filter: TestFilter) {
        logger.log(
            logLevel,
            filter.includePatterns.joinToString(
                prefix = "[$TAG] Included tests:\n{\n\t",
                separator = "\n\t",
                postfix = "\n}"
            )
        )
    }

    private fun logChanges(changes: ConfigurableFileCollection) {
        logger.log(
            logLevel,
            changes.joinToString(prefix = "[$TAG] Acquired changes:\n{\n\t", separator = "\n\t", postfix = "\n}")
        )
    }

    companion object {

        private const val TAG = "Teddi"
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
                task.init(changesFile, delegate)
            }
        }

        fun register(
            project: Project,
            changesFile: Provider<RegularFile>,
        ): TaskProvider<TestDiffTask> = project.tasks.register("testDiff", TestDiffTask::class.java) { task ->
            val delegate = project.tasks.named("test", Test::class.java).get()
            task.init(changesFile, delegate)
        }

        private fun TestDiffTask.init(changesFileProvider: Provider<RegularFile>, delegate: Test) {
            changesFile.set(changesFileProvider)
            testClassesDirs.set(delegate.testClassesDirs.asFileTree)
            filter.set(delegate.filter)
            setVerbose(project.properties["teddi.verbose"]?.toString().toBoolean())
            dependsOn(delegate.classpath)
            finalizedBy(delegate)

            doFirst {
                delegate.dependsOn(this) // cancel running delegate if this task failed
                delegate.onlyIf(OnlyIfHasFiltersSpec())
            }
            group = delegate.group
            description = "Runs tests for changed files"
            TaskCompat.notCompatibleWithConfigurationCache(
                this,
                "Unsupported to write tasks that configure other tasks at execution time"
            )
        }
    }
}

class OnlyIfHasFiltersSpec : Spec<Task> {

    override fun isSatisfiedBy(element: Task?): Boolean =
        element !is Test || element.filter.includePatterns.isNotEmpty()
}
