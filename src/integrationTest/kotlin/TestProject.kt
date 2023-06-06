import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.GradleRunner
import java.io.File

data class TestProject(
    val gradleVersion: String,
    val projectDir: File,
    val files: Map<String, String>
) {

    fun file(name: String) = files[name]

    fun build(vararg args: String): BuildResult = GradleRunner.create()
        .withProjectDir(projectDir)
        .withGradleVersion(gradleVersion)
        .forwardOutput()
        .withArguments(*args)
        .build()


    class Builder(
        private val projectDir: File,
        private val gradleVersion: String = "7.4", //TODO: GradleVersion.current()
    ) {
        private val files = HashMap<String, String>()

        fun withBuildFile(content: String) = withFile("build.gradle", content)

        fun withSettingsFile(content: String) = withFile("settings.gradle", content)

        fun withSrcFile(fileName: String, content: String) = withFile("src/main/java/$fileName", content)

        //TODO: add paths and inflate later
        fun fromResource(resourceName: String) = apply { unzipResource(resourceName, projectDir) }

        fun withFile(fileName: String, content: String) = apply {
            files[fileName] = content
        }

        fun inflate(): TestProject {
            val project = TestProject(gradleVersion, projectDir, files)
            TestProjectInflater(projectDir).inflate(project)
            return project
        }
    }
}