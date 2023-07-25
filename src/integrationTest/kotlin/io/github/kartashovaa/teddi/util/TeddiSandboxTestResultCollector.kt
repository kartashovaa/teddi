package io.github.kartashovaa.teddi.util

import org.gradle.api.internal.tasks.testing.junit.result.TestClassResult
import org.gradle.api.internal.tasks.testing.junit.result.TestResultSerializer
import java.io.File

class TeddiSandboxTestResultCollector(private val projectDir: File, private val moduleName: String) {

    fun collectResults(): List<TestClassResult> = ArrayList<TestClassResult>().also { results ->
        TestResultSerializer(File(projectDir, "$moduleName/build/test-results/testDebugUnitTest/binary"))
            .read(results::add)
    }
}