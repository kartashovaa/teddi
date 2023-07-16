package me.kyd3snik.test.diff.test.resolver

import me.kyd3snik.test.diff.test.resolver.usage.UsageCollector
import me.kyd3snik.test.diff.utils.ClassFileVisitor
import org.gradle.api.file.FileCollection
import org.gradle.api.file.FileTree
import org.gradle.api.file.FileVisitDetails
import org.gradle.api.tasks.testing.TestFilter

class UsageTestResolver(
    private val usageCollector: UsageCollector,
    private val tests: FileTree
) : TestResolver {
    override fun resolve(changes: FileCollection, filter: TestFilter) {
        tests.visit(TestVisitor(usageCollector, changes, filter))
    }
}

private class TestVisitor(
    private val usageCollector: UsageCollector,
    private val sourceFiles: FileCollection,
    private val filter: TestFilter
) : ClassFileVisitor() {

    override fun visitClassFile(details: FileVisitDetails) {
        val usages = usageCollector.collect(details.file)
        val isAnyUsageChanged = sourceFiles.asSequence()
            .map { file -> file.path.removeExtension() }
            .any { source -> usages.any { usage -> source.endsWith(usage) } }

        if (isAnyUsageChanged) {
            filter.includeTest(getQualifiedClassName(details), null)
        }
    }

    private fun String.removeExtension(): String = replaceAfterLast('.', "").removeSuffix(".")
}
