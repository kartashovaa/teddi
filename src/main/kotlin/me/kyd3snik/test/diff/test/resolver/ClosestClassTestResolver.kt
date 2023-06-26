package me.kyd3snik.test.diff.test.resolver

import org.gradle.api.file.FileCollection
import org.gradle.api.file.FileTree
import org.gradle.api.tasks.testing.TestFilter

class ClosestClassTestResolver(
    private val fileSystemLayout: FileSystemLayout,
    private val tests: FileTree
) : TestResolver {

    override fun resolve(changes: FileCollection, filter: TestFilter) {
        val changedFiles = changes.filter { file -> file.extension in SUPPORTED_EXTENSIONS }
        if (!changedFiles.isEmpty) {
            tests.visit(TestCollector(fileSystemLayout, changedFiles, filter))
        }
    }

    companion object {

        private val SUPPORTED_EXTENSIONS = setOf("java", "kt")
    }
}

