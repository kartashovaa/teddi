package me.kyd3snik.test.diff.testkit

import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.GradleRunner
import java.io.File

fun runTask(projectDir: File, taskName: String, vararg extraArguments: String): BuildResult {
    return GradleRunner.create()
        .withProjectDir(projectDir)
        .withPluginClasspath()
        .forwardOutput()
        .withArguments(taskName, taskName, *extraArguments)
        .build()
}