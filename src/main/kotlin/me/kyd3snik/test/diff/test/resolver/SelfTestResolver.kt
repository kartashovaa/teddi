package me.kyd3snik.test.diff.test.resolver

import me.kyd3snik.test.diff.changes.FileChange
import org.gradle.api.tasks.testing.TestFilter
import java.io.File

class SelfTestResolver(private val testSourceSet: File) : TestResolver {

    override fun resolve(change: FileChange, filter: TestFilter) {
        if (change is FileChange.Modified || change is FileChange.Created) {
            if (change.file.extension == "kt" || change.file.extension == "java") {
                if (change.file.startsWith(testSourceSet)) {
                    val className = resolveClassName(change.file.relativeTo(testSourceSet))
                    filter.includeTestsMatching(className)
                }
            }
        }
    }

    private fun resolveClassName(file: File): String =
        file.path
            .substringBeforeLast(".") // remove extension
            .split("/")
            .joinToString(separator = ".")
}