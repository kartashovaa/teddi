package me.kyd3snik.test.diff

import org.gradle.testfixtures.ProjectBuilder
import org.gradle.testkit.runner.GradleRunner
import org.junit.Test
import java.io.File
import java.io.FileOutputStream
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
}