package io.github.kartashovaa.teddi.util

import org.gradle.api.internal.tasks.testing.junit.result.TestClassResult
import org.gradle.api.internal.tasks.testing.junit.result.TestResultSerializer
import java.io.File

class TeddiSandboxTestResultCollector(
    private val projectDir: File,
    private val moduleName: String,
    private val variant: String = "testDebugUnitTest"
) {

    fun collectResults(): List<TestClassResult> = ArrayList<TestClassResult>().also { results ->
        TestResultSerializer(File(projectDir, "$moduleName/build/test-results/$variant/binary"))
            .read(results::add)
    }
}

fun TeddiSandboxTestResultCollector.test(): TestResultVerifier = TestResultVerifier(collectResults())

