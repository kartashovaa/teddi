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

    //TODO: check out forwarding ANDROID_HOME from -P optiion to test environment
    //TODO: remove local.properties from TeddiSandbox.zip
    @Before
    fun setUp() {
//        assert(System.getenv("ANDROID_HOME") != null)
        assert(projectDir.mkdirs())
        unzipResource("TeddiSandbox.zip", projectDir)
    }

    @After
    fun tearDown() {
        assert(projectDir.deleteRecursively())
    }

    @Test
    fun runApplication() {
        val result = GradleRunner.create()
            .withProjectDir(projectDir)
            .forwardOutput()
            .withArguments(":app:testDiffDebugUnitTest", "-PagpVersion=7.3.0")
            .build()

        assertEquals(TaskOutcome.SUCCESS, result.task(":app:testDiffDebugUnitTest")?.outcome)
        assertEquals(TaskOutcome.SUCCESS, result.task(":app:testDebugUnitTest")?.outcome)

        val testResults = TeddiSandboxTestResultCollector(projectDir, "app").collectResults()
        assertEquals(1, testResults.size)
        assertEquals("com.example.app.MainViewModelTest", testResults.first().className)
    }

    @Test
    fun runLibrary() {
        val result = GradleRunner.create()
            .withProjectDir(projectDir)
            .forwardOutput()
            .withArguments(":feature:testDiffDebugUnitTest", "-PagpVersion=7.3.0")
            .build()

        assertEquals(TaskOutcome.SUCCESS, result.task(":feature:testDiffDebugUnitTest")?.outcome)
        assertEquals(TaskOutcome.SUCCESS, result.task(":feature:testDebugUnitTest")?.outcome)

        val testResults = TeddiSandboxTestResultCollector(projectDir, "feature").collectResults()
        assertEquals(1, testResults.size)
        assertEquals("com.example.feature.FeatureViewModelTest", testResults.first().className)
    }

    @Test
    fun runRoot() {
        val result = GradleRunner.create()
            .withProjectDir(projectDir)
            .forwardOutput()
            .withArguments(":testDiffUnitTest", "-PagpVersion=7.3.0")
            .build()

        assertEquals(TaskOutcome.SUCCESS, result.task(":app:testDiffDebugUnitTest")?.outcome)
        assertEquals(TaskOutcome.SUCCESS, result.task(":app:testDebugUnitTest")?.outcome)
        assertEquals(TaskOutcome.SUCCESS, result.task(":feature:testDiffDebugUnitTest")?.outcome)
        assertEquals(TaskOutcome.SUCCESS, result.task(":feature:testDebugUnitTest")?.outcome)

        val (appTestResult) = TeddiSandboxTestResultCollector(projectDir, "app").collectResults()
            .also { assertEquals(1, it.size) }
        val (featureTestResult) = TeddiSandboxTestResultCollector(projectDir, "feature").collectResults()
            .also { assertEquals(1, it.size) }

        assertEquals("com.example.app.MainViewModelTest", appTestResult.className)
        assertEquals("com.example.feature.FeatureViewModelTest", featureTestResult.className)
    }
}
