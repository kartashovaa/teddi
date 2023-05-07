package me.kyd3snik.test.diff.test.resolver

import me.kyd3snik.test.diff.changes.FileChange
import me.kyd3snik.test.diff.utils.ClassFileVisitor
import me.kyd3snik.test.diff.utils.EditDistanceCalculator
import org.gradle.api.file.FileTree
import org.gradle.api.file.FileVisitDetails
import org.gradle.api.tasks.testing.TestFilter
import java.io.File

class ClosestClassTestResolver(
    private val tests: FileTree
) : TestResolver {

    override fun resolve(change: FileChange, filter: TestFilter) {
        // TODO: Warn slow implementation
        resolveAll(listOf(change), filter)
    }

    override fun resolveAll(changes: List<FileChange>, filter: TestFilter) {
        val existingChanges = changes.filter {
            (it is FileChange.Modified || it is FileChange.Created) && it.file.extension in SUPPORTED_EXTENSIONS
        }
        tests.visit(TestCollector(FileSystemLayout(), existingChanges, filter))
    }

    companion object {

        private val SUPPORTED_EXTENSIONS = setOf("java", "kt")
    }
}

private class TestCollector(
    private val fileSystemLayout: FileSystemLayout,
    private val sourceFiles: List<FileChange>,
    private val filter: TestFilter
) : ClassFileVisitor() {

    private val calculator = EditDistanceCalculator()

    override fun visitClassFile(details: FileVisitDetails) {
        sourceFiles.forEach { handle(it.file, details) }
    }

    private fun handle(sourceFile: File, details: FileVisitDetails) {
        val sourceSiblings = fileSystemLayout.getSiblings(sourceFile)
        val classDir = sourceFile.parent
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

//    private val siblingsCache = HashMap<File, List<File>>()
//    private fun siblingscache(file: File): List<File> {
//        val parent = file.parentFile ?: return emptyList()
//        var siblings = siblingsCache[parent]
//        if (siblings == null) {
//            siblings = parent.listFiles()?.toList().orEmpty()
//            siblingsCache[parent] = siblings
//        }
//        return siblings
//    }

    fun getSiblings(file: File): List<File> {
        return file.parentFile.listFiles()?.toList().orEmpty()
    }
}