package me.kyd3snik.test.diff.util

import org.gradle.api.internal.tasks.testing.junit.result.TestClassResult
import org.gradle.api.internal.tasks.testing.junit.result.TestResultSerializer
import java.io.File

class TeddiSandboxTestResultCollector(private val projectDir: File) {

    fun collectResults(): List<TestClassResult> = ArrayList<TestClassResult>().also { results ->
        TestResultSerializer(File(projectDir, "app/build/test-results/testDebugUnitTest/binary"))
            .read(results::add)
    }
}