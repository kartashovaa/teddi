package me.kyd3snik.test.diff

import junit.framework.TestCase.assertEquals
import me.kyd3snik.test.diff.testkit.unzipResource
import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import org.junit.Before
import org.junit.Test
import java.io.File

//@Ignore("It is more like documentation, there is no reason to test it regularly")
class Assumptions {

    private val projectDir = File("build/sandbox")

    private val gradleRunner: GradleRunner
        get() = GradleRunner.create()
            .withProjectDir(projectDir)
            .withPluginClasspath()
            .withGradleVersion("7.5")
            .forwardOutput()

    @Before
    fun initProject() {
        check(!projectDir.exists() || projectDir.deleteRecursively())
        unzipResource("build.zip", projectDir)
    }

    @Test
    fun runProject() {
        val taskName = ":app:collectChanges"

        val runner = gradleRunner.withArguments(
            taskName,
            "-PfromBlob=caa2d0689e266c54e6df02a46ed32906c35fdda9",
            "--stacktrace"
        )
        val result1 = runner.build()
        val result2 = runner.build()

        assertEquals(TaskOutcome.SUCCESS, result1.task(taskName)?.outcome)
        assertEquals(TaskOutcome.UP_TO_DATE, result2.task(taskName)?.outcome)
    }

    @Test
    fun runProjectNoBuildCache() {
        val taskName = ":app:collectChanges"

        val runner = gradleRunner.withArguments(taskName, "-PfromBlob=HEAD~1", "--stacktrace")

        val result1 = runner.build()
        val result2 = runner.build()

        assertEquals(TaskOutcome.SUCCESS, result1.task(taskName)?.outcome)
        assertEquals(TaskOutcome.SUCCESS, result2.task(taskName)?.outcome)
    }
}