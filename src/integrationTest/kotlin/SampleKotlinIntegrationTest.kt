import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import org.junit.Assert.assertEquals
import org.junit.Test
import spock.lang.Specification
import java.io.File

class SampleKotlinIntegrationTest : Specification() {


    @Test
    fun run() {
        val projectDir = File("build/teddi-test-android-project")

        val result = GradleRunner.create()
            .withProjectDir(projectDir)
            .withGradleVersion("7.5")
            .forwardOutput()
            .withArguments("app:testDiffDebugUnitTest", "-PfromBlob=HEAD~1")
            .build()

        assertEquals(TaskOutcome.SUCCESS, result.task(":app:testDiffDebugUnitTest")?.outcome)
    }

    @Test
    fun inflateAndRun() {
        val project = TestProject.Builder(File("build/sandbox/final-project-test"))
            .fromResource("teddi-test-android-project.zip")
            .inflate()

//        val result = project.build("app:testDiffDebugUnitTest", "-PfromBlob=HEAD~1")
//        assertEquals(TaskOutcome.SUCCESS, result.task(":app:testDiffDebugUnitTest")?.outcome)
    }
}