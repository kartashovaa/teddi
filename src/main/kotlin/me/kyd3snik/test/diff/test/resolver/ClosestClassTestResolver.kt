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
        if (change is FileChange.Modified || change is FileChange.Created) {
            if (change.file.extension in SUPPORTED_EXTENSIONS) {
                tests.visit(TestCollector(change.file, filter))
            }
        }
    }

    companion object {

        private val SUPPORTED_EXTENSIONS = setOf("java", "kt")
    }
}

//TODO: maybe add cache[fullQualifiedTestClassName] = fullQualifiedSourceClassName
//TODO: measure difference if walk tests once
private class TestCollector(private val sourceFile: File, private val filter: TestFilter) : ClassFileVisitor() {

    private val sourceSiblings = sourceFile.parentFile.listFiles()?.toList().orEmpty()
    private val classDir = sourceFile.parent

    private val calculator = EditDistanceCalculator()

    override fun visitClassFile(details: FileVisitDetails) {
        val testDir = details.relativePath.parent.pathString
        if (classDir.endsWith(testDir)) { // same package
            val testClassName = getSimpleClassName(details).substringBefore("$") // ignore inner classes in test files

            val closestFile = findClosestFileAmongSiblings(testClassName)
            if (closestFile == sourceFile) {
                // TODO: consider using filter.includeTest
                filter.includeTestsMatching(getQualifiedClassName(details))
            }
        }
    }

    private fun findClosestFileAmongSiblings(testClassName: String) = sourceSiblings.minByOrNull { file ->
        calculator.calculate(testClassName, file.nameWithoutExtension)
    }
}