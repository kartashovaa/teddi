package me.kyd3snik.test.diff

import me.kyd3snik.test.diff.util.TeddiSandboxTestResultCollector
import me.kyd3snik.test.diff.util.unzipResource
import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.io.File

class TestDiffTest {

    private val projectDir = File("build/sandbox")

    @Before
    fun setUp() {
        assert(projectDir.mkdirs())
        unzipResource("TeddiSandbox.zip", projectDir)
    }

    @After
    fun tearDown() {
        assert(projectDir.deleteRecursively())
    }

    @Test
    fun run() {
        val result = GradleRunner.create()
            .withProjectDir(projectDir)
            .forwardOutput()
            .withArguments(":app:testDiffDebugUnitTest", "-PagpVersion=7.3.0")
            .build()

        assertEquals(TaskOutcome.SUCCESS, result.task(":app:testDiffDebugUnitTest")?.outcome)
        assertEquals(TaskOutcome.SUCCESS, result.task(":app:testDebugUnitTest")?.outcome)

        val testResults = TeddiSandboxTestResultCollector(projectDir).collectResults()
        assertEquals(1, testResults.size)
        assertEquals("com.example.app.MainViewModelTest", testResults.first().className)
    }
}
