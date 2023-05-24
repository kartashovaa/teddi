package me.kyd3snik.test.diff

import me.kyd3snik.test.diff.testkit.TestProject
import org.gradle.testkit.runner.TaskOutcome
import org.junit.Assert.assertEquals
import org.junit.Test
import java.io.File

class Sandbox {

    @Test
    fun run() {
        val projectDir = File("build/unitTest/test-project").absoluteFile
        val project = TestProject.Builder(projectDir)
            .withBuildFile(
                """
                plugins {
                    id "java"
                    id "me.kyd3snik.teddi" version "0.0.1"
                }
            """.trimIndent(),
            )
            .withSettingsFile(
                """
                pluginManagement {
                    repositories {
                        mavenLocal()
                        mavenCentral()
                    }
                }
                dependencyResolutionManagement {
                    repositories {
                        mavenCentral()
                    }
                }
                rootProject.name = "SandBoxProject"
            """.trimIndent()
            )
            .withSrcFile(
                "app/Hello.java",
                """
                    package app;
                    
                    class Hello {
                        public static void main(String... args) {
                            System.out.println("Hello, world!");
                        }
                    }
                """.trimIndent()
            )
            .inflate()

        println()
        println("------------------------------------------------------------")

        val result = project.build("assemble")
        val result1 = project.build("assemble")

        assertEquals(TaskOutcome.SUCCESS, result.task(":collectChanges")?.outcome)
        assertEquals(TaskOutcome.UP_TO_DATE, result1.task(":collectChanges")?.outcome)
    }
}