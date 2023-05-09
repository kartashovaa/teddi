package me.kyd3snik.test.diff

import com.android.build.gradle.api.BaseVariant
import com.android.builder.core.ComponentType.Companion.UNIT_TEST_PREFIX
import com.android.builder.core.ComponentType.Companion.UNIT_TEST_SUFFIX
import me.kyd3snik.test.diff.changes.ChangesStorage
import me.kyd3snik.test.diff.test.resolver.ClosestClassTestResolver
import me.kyd3snik.test.diff.test.resolver.FileSystemLayout
import me.kyd3snik.test.diff.utils.capitalized
import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.file.FileTree
import org.gradle.api.file.RegularFile
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.TaskProvider
import org.gradle.api.tasks.testing.Test
import org.gradle.api.tasks.testing.TestFilter
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
        val changesStorage = ChangesStorage(changesFile.get().asFile)
        val changes = objectFactory.fileCollection().from(changesStorage.read())
        logger.debug(changes.joinToString(prefix = "Changes:\n{\n\t", separator = "\n\t", postfix = "\n}"))
        val testResolver = ClosestClassTestResolver(FileSystemLayout(), testClassesDirs.get())
        val filter = filter.get()
        testResolver.resolve(changes, filter)

        logger.debug(
            filter.includePatterns.joinToString(prefix = "Includes:\n{\n\t", separator = "\n\t", postfix = "\n}")
        )
    }

    companion object {

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
                task.testClassesDirs.set(project.provider { delegate.testClassesDirs.asFileTree })
                task.filter.set(project.provider { delegate.filter })
                task.dependsOn(
                    project.provider {
                        // TODO: potentially slow implementation(travers whole task graph),
                        //  benchmark and find alternatives if any
                        //  looks like delegate depends only compileKotlinTestUnitTest
                        //  main goal to get dependencies of delegate except our task
                        delegate.taskDependencies.getDependencies(delegate).apply { remove(task) }
                    }
                )
                delegate.dependsOn(task) // cancel running delegate if this task failed
                task.finalizedBy(delegate)

                task.group = delegate.group
                task.description = "Runs tests for changed files"
                //TODO: consider creating separate testing task
                task.notCompatibleWithConfigurationCache("Unsupported to write tasks that configure other tasks at execution time")
            }
        }
    }
}
