package me.kyd3snik.test.diff

import com.android.build.gradle.api.BaseVariant
import com.android.builder.core.ComponentType.Companion.UNIT_TEST_PREFIX
import com.android.builder.core.ComponentType.Companion.UNIT_TEST_SUFFIX
import me.kyd3snik.test.diff.changes.FileChange
import me.kyd3snik.test.diff.test.resolver.*
import me.kyd3snik.test.diff.utils.capitalized
import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.file.RegularFile
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.TaskProvider
import org.gradle.api.tasks.testing.Test
import java.io.File
import java.io.FileInputStream
import java.io.ObjectInputStream

abstract class TestDiffTask : DefaultTask() {

    private lateinit var changesFile: Provider<RegularFile>
    private lateinit var testsSourceSets: Provider<Set<File>>

    private lateinit var delegate: Test

    @TaskAction
    fun testDiff() {
        val changes = readFileChanges()
        logChanges(changes)
        val testResolver = buildTestResolver()
        testResolver.resolveAll(changes, delegate.filter)
        logIncludes()
    }

    private fun buildTestResolver() = CompositeTestResolver.build {
        addDelegate(ClosestClassTestResolver(delegate.testClassesDirs.asFileTree))
        for (testSourceRoot in testsSourceSets.get()) {
            addDelegate(SelfTestResolver(testSourceRoot))
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun readFileChanges() = ObjectInputStream(FileInputStream(changesFile.get().asFile))
        .readObject() as List<FileChange>

    private fun logIncludes() {
        logger.debug(
            delegate.filter.includePatterns.joinToString(
                prefix = "Includes:\n{\n\t", separator = "\n\t", postfix = "\n}"
            )
        )
    }

    private fun logChanges(changes: List<FileChange>) {
        logger.debug(
            changes.joinToString(
                prefix = "Changes:\n{\n\t", separator = "\n\t", postfix = "\n}"
            ) { it.file.toString() })
    }

    companion object {

        fun register(
            project: Project,
            variant: BaseVariant,
            changesFile: Provider<RegularFile>,
            testsVariants: Iterable<BaseVariant>,
        ): TaskProvider<TestDiffTask> {
            val variantName = variant.name.capitalized()
            return project.tasks.register("testDiff${variantName}UnitTest", TestDiffTask::class.java) { task ->
                val delegateName = "$UNIT_TEST_PREFIX$variantName$UNIT_TEST_SUFFIX"
                val delegate = project.tasks.named(delegateName, Test::class.java).get()
                task.delegate = delegate
                task.changesFile = changesFile
                task.testsSourceSets = project.collectSourceSets(testsVariants)
                task.dependsOn(
                    project.provider {
                        // TODO: potentially slow implementation(travers whole task graph),
                        //  benchmark and find alternatives if any
                        //  looks like delegate depends only compileKolinTestUnitTest
                        //  main goal to get dependencies of delegate except our task
                        delegate.taskDependencies.getDependencies(delegate).apply { remove(task) }
                    }
                )
                delegate.dependsOn(task) // cancel running delegate if this task failed
                task.finalizedBy(delegate)
            }
        }

        private fun Project.collectSourceSets(testVariants: Iterable<BaseVariant>): Provider<Set<File>> {
            return provider {
                testVariants.flatMap { variant ->
                    variant.sourceSets.flatMap { source ->
                        source.javaDirectories + source.kotlinDirectories
                    }
                }.toSet()
            }
        }
    }
}
