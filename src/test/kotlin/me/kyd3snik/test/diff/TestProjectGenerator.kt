package me.kyd3snik.test.diff

import me.kyd3snik.test.diff.testkit.unzipResource
import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import org.junit.Assert.assertEquals
import org.junit.Test
import java.io.File
import java.io.FileWriter

class TestProjectGenerator {

    @Test
    fun test() {
        val projectDir = File("build/unitTest/test-project")
        GradleRunner.create()
            .withProjectDir(projectDir)

        FileWriter(File(projectDir, "test.txt")).use {
            it.write("Hello, world!\n")
        }
    }

    @Test
    fun unzip() {
        val destination = File("build/unzip")
        unzipResource("project.zip", destination)
    }

    @Test
    fun build() {
        val projectDir = File("build/teddi-test-project")
//        unzipResource("teddi-test-android-project.zip", projectDir)

        val result = GradleRunner.create()
            .withPluginClasspath()
            .withProjectDir(projectDir)
            .withGradleVersion("7.4")
            .forwardOutput()
            .withArguments("app:assemble", "-PfromBlob=HEAD~1", "--stacktrace")
            .build()

        assertEquals(TaskOutcome.SUCCESS, result.task(":app:testDebugUnitTest")?.outcome)
    }
}