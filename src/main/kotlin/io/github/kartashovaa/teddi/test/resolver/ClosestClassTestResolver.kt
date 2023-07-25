package io.github.kartashovaa.teddi.test.resolver

import io.github.kartashovaa.teddi.utils.ClassFileVisitor
import io.github.kartashovaa.teddi.utils.EditDistanceCalculator
import org.gradle.api.file.FileCollection
import org.gradle.api.file.FileTree
import org.gradle.api.file.FileVisitDetails
import org.gradle.api.tasks.testing.TestFilter
import java.io.File

class ClosestClassTestResolver(
    private val fileSystemLayout: FileSystemLayout,
    private val tests: FileTree
) : TestResolver {

    override fun resolve(changes: FileCollection, filter: TestFilter) {
        tests.visit(TestCollector(fileSystemLayout, changes, filter))
    }
}

private class TestCollector(
    private val fileSystemLayout: FileSystemLayout,
    private val sourceFiles: FileCollection,
    private val filter: TestFilter
) : ClassFileVisitor() {

    private val distanceCalculator = EditDistanceCalculator()

    override fun visitClassFile(details: FileVisitDetails) {
        sourceFiles.forEach { source -> resolve(source, details) }
    }

    private fun resolve(sourceFile: File, details: FileVisitDetails) {
        val sourceSiblings = fileSystemLayout.getSiblings(sourceFile)
        val classDir = fileSystemLayout.getParent(sourceFile)
        val testDir = details.relativePath.parent.pathString
        if (classDir.endsWith(testDir)) { // same package
            val testClassName = getSimpleClassName(details).substringBefore("$") // ignore inner classes in test files

            val closestFile = sourceSiblings.minByOrNull { file ->
                distanceCalculator.calculate(testClassName, file.nameWithoutExtension)
            }
            if (closestFile == sourceFile) {
                filter.includeTest(getQualifiedClassName(details), null)
            }
        }
    }
}

