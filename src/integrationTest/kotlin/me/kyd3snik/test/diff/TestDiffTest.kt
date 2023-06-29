package me.kyd3snik.test.diff

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
    }

    @Test
    fun runTwice() {
        val runner = GradleRunner.create()
            .withProjectDir(projectDir)
            .forwardOutput()
            .withArguments(":app:testDiffDebugUnitTest")
        val result1 = runner.build()
        val result2 = runner.build()

        assertEquals(TaskOutcome.SUCCESS, result1.task(":app:testDiffDebugUnitTest")?.outcome)
        assertEquals(TaskOutcome.SUCCESS, result1.task(":app:testDebugUnitTest")?.outcome)

        assertEquals(TaskOutcome.SUCCESS, result2.task(":app:testDiffDebugUnitTest")?.outcome)
        assertEquals(TaskOutcome.UP_TO_DATE, result2.task(":app:testDebugUnitTest")?.outcome)
    }
}