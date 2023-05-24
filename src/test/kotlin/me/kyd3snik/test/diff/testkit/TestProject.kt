package me.kyd3snik.test.diff.testkit

import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.GradleRunner
import java.io.File

data class TestProject(
    val projectDir: File,
    val files: Map<String, String>
) {

    fun file(name: String) = files[name]

    fun build(vararg args: String): BuildResult {
        TestProjectInflater(projectDir).inflate(this)

        return GradleRunner.create()
            .withProjectDir(projectDir)
            .withGradleVersion("7.5")
            .forwardOutput()
            .withArguments(*args)
            .build()
    }

    class Builder(private val projectDir: File) {
        private val files = HashMap<String, String>()

        fun withBuildFile(content: String) = withFile("build.gradle", content)

        fun withSettingsFile(content: String) = withFile("settings.gradle", content)

        fun withSrcFile(fileName: String, content: String) = withFile("src/main/java/$fileName", content)

        fun withFile(fileName: String, content: String) = apply {
            files[fileName] = content
        }

        fun inflate(): TestProject {
            val project = TestProject(projectDir, files)
            TestProjectInflater(projectDir).inflate(project)
            return project
        }
    }
}