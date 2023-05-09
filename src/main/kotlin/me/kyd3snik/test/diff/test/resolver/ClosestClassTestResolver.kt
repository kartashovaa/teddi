package me.kyd3snik.test.diff.test.resolver

import me.kyd3snik.test.diff.utils.ClassFileVisitor
import me.kyd3snik.test.diff.utils.EditDistanceCalculator
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
        val changedFiles = changes.filter { file -> file.extension in SUPPORTED_EXTENSIONS }
        tests.visit(TestCollector(fileSystemLayout, changedFiles, filter))
    }

    companion object {

        private val SUPPORTED_EXTENSIONS = setOf("java", "kt")
    }
}

class TestCollector(
    private val fileSystemLayout: FileSystemLayout,
    private val sourceFiles: FileCollection,
    private val filter: TestFilter
) : ClassFileVisitor() {

    private val calculator = EditDistanceCalculator()

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
                calculator.calculate(testClassName, file.nameWithoutExtension)
            }
            if (closestFile == sourceFile) {
                // TODO: consider using filter.includeTest
                filter.includeTestsMatching(getQualifiedClassName(details))
            }
        }
    }
}

class FileSystemLayout {

    private val siblingsCache = HashMap<File, List<File>>()

    fun getParent(file: File): File = file.parentFile

    fun getSiblings(file: File): List<File> {
        require(file.isAbsolute) { "Unsupported relative paths: $file" }
        val parent = file.parentFile ?: return emptyList()
        var siblings = siblingsCache[parent]
        if (siblings == null) {
            siblings = parent.listFiles()?.toList().orEmpty()
            siblingsCache[parent] = siblings
        }
        return siblings
    }
}