package io.github.kartashovaa.teddi.util

import org.gradle.api.internal.tasks.testing.junit.result.TestClassResult
import org.gradle.api.tasks.testing.TestResult
import org.junit.Assert
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull

class TestResultVerifier(private val results: List<TestClassResult>) {

    fun assertCount(count: Int) = apply { assertEquals(count, results.size) }
    fun assertSuccess(className: String) = apply {
        val result = results.find { result -> result.className == className }
        assertNotNull("result for $className not found", result)
        result?.results?.forEach { method ->
            assertEquals(
                "Unexpected result for $className#${method.name}",
                TestResult.ResultType.SUCCESS,
                method.resultType
            )
        }
    }
}