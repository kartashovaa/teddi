package io.github.kartashovaa.teddi

import io.github.kartashovaa.teddi.util.*
import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Ignore
import org.junit.Test
import java.io.File

class TestDiffTest {

    private val projectDir = File("build/sandbox")
    private val project: TeddiProjectWriter = DefaultProjectWriter(projectDir)

    @Before
    fun setUp() {
        assert(projectDir.mkdirs())
    }

    @After
    fun tearDown() {
        assert(projectDir.deleteRecursively())
    }

    @Test
    fun runApplication() {
        project.createMinimalRootProject()
        val app = project.createAndroidApplicationModule(name = "app")
        app.kotlin("com.example.app.OtherViewModel", OTHER_VIEW_MODEL_CONTENT)
        val viewModelSource = app.kotlin("com.example.app.MainViewModel", DEFAULT_VIEW_MODEL_CONTENT)
        app.kotlin("com.example.app.MainViewModelTest", DEFAULT_VIEWMODEL_TEST_CONTENT, "test")
        app.kotlin("com.example.app.OtherViewModelTest", DEFAULT_OTHER_VIEWMODEL_TEST_CONTENT, "test")

        project.commit("Initial commit")
        viewModelSource.appendText("\n\n")

        val result = GradleRunner.create()
            .withProjectDir(projectDir)
            .forwardOutput()
            .withArguments(":app:testDiffDebugUnitTest")
            .build()

        assertEquals(TaskOutcome.SUCCESS, result.task(":app:testDiffDebugUnitTest")?.outcome)
        assertEquals(TaskOutcome.SUCCESS, result.task(":app:testDebugUnitTest")?.outcome)

        TeddiSandboxTestResultCollector(projectDir, "app").test()
            .assertCount(1)
            .assertSuccess("com.example.app.MainViewModelTest")
    }

    @Test
    fun runApplicationWithRootIgnores() {
        project.createMinimalRootProject(extraConfiguration = IGNORE_DEBUG_VARIANT_CONFIGURATION)
        project.createAndroidApplicationModule(name = "app")
        val result = GradleRunner.create()
            .withProjectDir(projectDir)
            .forwardOutput()
            .withArguments(":app:testDiffDebugUnitTest")
            .buildAndFail()
        assertTrue("Task 'testDiffDebugUnitTest' not found in project ':app'." in result.output)
    }

    @Test
    fun runApplicationWithLocalIgnores() {
        project.createMinimalRootProject()
        project.createAndroidApplicationModule(name = "app", extraConfiguration = IGNORE_DEBUG_VARIANT_CONFIGURATION)
        val result = GradleRunner.create()
            .withProjectDir(projectDir)
            .forwardOutput()
            .withArguments(":app:testDiffDebugUnitTest")
            .buildAndFail()
        assertTrue("Task 'testDiffDebugUnitTest' not found in project ':app'." in result.output)
    }

    @Test
    fun runLibraryWithIgnores() {
        project.createMinimalRootProject(extraConfiguration = IGNORE_DEBUG_VARIANT_CONFIGURATION)
        project.createAndroidLibraryModule(name = "feature")
        val result = GradleRunner.create()
            .withProjectDir(projectDir)
            .forwardOutput()
            .withArguments(":feature:testDiffDebugUnitTest")
            .buildAndFail()
        assertTrue("Task 'testDiffDebugUnitTest' not found in project ':feature'." in result.output)
    }

    @Test
    @Ignore("Fix MainViewModel in FeatureViewModel.kt")
    fun runLibrary() {
        project.createMinimalRootProject()
        val feature = project.createAndroidLibraryModule(name = "feature")
        feature.kotlin("com.example.feature.OtherViewModel", OTHER_VIEW_MODEL_CONTENT)
        val viewModelSource = feature.kotlin("com.example.feature.FeatureViewModel", DEFAULT_VIEW_MODEL_CONTENT)
        feature.kotlin("com.example.feature.FeatureViewModelTest", DEFAULT_VIEWMODEL_TEST_CONTENT, "test")
        feature.kotlin("com.example.feature.OtherViewModelTest", DEFAULT_OTHER_VIEWMODEL_TEST_CONTENT, "test")

        project.commit("Initial commit")
        viewModelSource.appendText("\n\n")

        val result = GradleRunner.create()
            .withProjectDir(projectDir)
            .forwardOutput()
            .withArguments(":feature:testDiffDebugUnitTest")
            .build()

        assertEquals(TaskOutcome.SUCCESS, result.task(":feature:testDiffDebugUnitTest")?.outcome)
        assertEquals(TaskOutcome.SUCCESS, result.task(":feature:testDebugUnitTest")?.outcome)

        TeddiSandboxTestResultCollector(projectDir, "feature").test()
            .assertCount(1)
            .assertSuccess("com.example.feature.FeatureViewModelTest")
    }

    @Test
    fun runLibraryNormal() {
        project.createMinimalRootProject()
        val feature = project.createAndroidLibraryModule(name = "feature")
        feature.kotlin("com.example.feature.OtherViewModel", OTHER_VIEW_MODEL_CONTENT)
        val viewModelSource = feature.kotlin("com.example.feature.MainViewModel", DEFAULT_VIEW_MODEL_CONTENT)
        feature.kotlin("com.example.feature.MainViewModelTest", DEFAULT_VIEWMODEL_TEST_CONTENT, "test")
        feature.kotlin("com.example.feature.OtherViewModelTest", DEFAULT_OTHER_VIEWMODEL_TEST_CONTENT, "test")

        project.commit("Initial commit")
        viewModelSource.appendText("\n\n")

        val result = GradleRunner.create()
            .withProjectDir(projectDir)
            .forwardOutput()
            .withArguments(":feature:testDiffDebugUnitTest")
            .build()

        assertEquals(TaskOutcome.SUCCESS, result.task(":feature:testDiffDebugUnitTest")?.outcome)
        assertEquals(TaskOutcome.SUCCESS, result.task(":feature:testDebugUnitTest")?.outcome)

        TeddiSandboxTestResultCollector(projectDir, "feature").test()
            .assertCount(1)
            .assertSuccess("com.example.feature.MainViewModelTest")
    }

    @Test
    fun runRoot() {
        project.createMinimalRootProject()
        val app = project.createAndroidApplicationModule(name = "app", dependencies = listOf("feature"))
        app.kotlin("com.example.app.OtherMainViewModel", OTHER_VIEW_MODEL_CONTENT)
        val appViewModelSource = app.kotlin("com.example.app.MainViewModel", DEFAULT_VIEW_MODEL_CONTENT)
        app.kotlin("com.example.app.MainViewModelTest", DEFAULT_VIEWMODEL_TEST_CONTENT, "test")
        app.kotlin("com.example.app.OtherViewModelTest", DEFAULT_OTHER_VIEWMODEL_TEST_CONTENT, "test")

        val feature = project.createAndroidLibraryModule(name = "feature")
        feature.kotlin("com.example.feature.OtherViewModel", OTHER_VIEW_MODEL_CONTENT)
        val featureViewModelSource = feature.kotlin("com.example.feature.MainViewModel", DEFAULT_VIEW_MODEL_CONTENT)
        feature.kotlin("com.example.feature.MainViewModelTest", DEFAULT_VIEWMODEL_TEST_CONTENT, "test")
        feature.kotlin("com.example.feature.OtherViewModelTest", DEFAULT_OTHER_VIEWMODEL_TEST_CONTENT, "test")

        val initialCommit = project.commit("Initial commit")
        appViewModelSource.appendText("\n\n")
        featureViewModelSource.appendText("\n\n")
        project.commit("Second commit")

        val result = GradleRunner.create()
            .withProjectDir(projectDir)
            .forwardOutput()
            .withArguments(":testDiffUnitTest", "--fromBlob=$initialCommit")
            .build()

        assertEquals(TaskOutcome.SUCCESS, result.task(":app:testDiffDebugUnitTest")?.outcome)
        assertEquals(TaskOutcome.SUCCESS, result.task(":app:testDebugUnitTest")?.outcome)
        assertEquals(TaskOutcome.SUCCESS, result.task(":feature:testDiffDebugUnitTest")?.outcome)
        assertEquals(TaskOutcome.SUCCESS, result.task(":feature:testDebugUnitTest")?.outcome)

        TeddiSandboxTestResultCollector(projectDir, "app").test()
            .assertCount(1)
            .assertSuccess("com.example.app.MainViewModelTest")

        TeddiSandboxTestResultCollector(projectDir, "feature").test()
            .assertCount(1)
            .assertSuccess("com.example.feature.MainViewModelTest")
    }

    @Test
    fun runKotlinModule() {
        project.createMinimalRootProject()
        val app = project.createKotlinModule(name = "lib")
        val appViewModelSource = app.kotlin("com.example.lib.MainViewModel", "class MainViewModel")
        app.kotlin(
            "com.example.lib.MainViewModelTest", """
            import org.junit.Test

            class MainViewModelTest {

                @Test
                fun test() {
                    val viewModel = MainViewModel()
                }
            }
        """.trimIndent(), "test"
        )

        project.commit("Initial commit")
        appViewModelSource.appendText("\n\n")

        val result = GradleRunner.create()
            .withProjectDir(projectDir)
            .forwardOutput()
            .withArguments(":lib:testDiff")
            .build()

        assertEquals(TaskOutcome.SUCCESS, result.task(":lib:testDiff")?.outcome)
        assertEquals(TaskOutcome.SUCCESS, result.task(":lib:test")?.outcome)

        TeddiSandboxTestResultCollector(projectDir, "lib", "test").test()
            .assertCount(1)
            .assertSuccess("com.example.lib.MainViewModelTest")
    }

    @Test
    fun testLastCommit() {
        project.createMinimalRootProject()
        val app = project.createAndroidApplicationModule(name = "app")
        val viewModelSource = app.kotlin("com.example.app.MainViewModel", DEFAULT_VIEW_MODEL_CONTENT)
        app.kotlin("com.example.app.MainViewModelTest", DEFAULT_VIEWMODEL_TEST_CONTENT, "test")

        project.commit("Initial commit")
        viewModelSource.appendText("\n\n")
        project.commit("Second commit")


        val result = GradleRunner.create()
            .withProjectDir(projectDir)
            .forwardOutput()
            .withArguments(":app:testDiffDebugUnitTest", "--fromBlob=HEAD~1")
            .build()

        assertEquals(TaskOutcome.SUCCESS, result.task(":app:testDiffDebugUnitTest")?.outcome)
        assertEquals(TaskOutcome.SUCCESS, result.task(":app:testDebugUnitTest")?.outcome)
    }

    @Test
    fun testVerbose() {
        project.createMinimalRootProject()
        val app = project.createAndroidApplicationModule(name = "app")
        val viewModelSource = app.kotlin("com.example.app.MainViewModel", DEFAULT_VIEW_MODEL_CONTENT)
        app.kotlin("com.example.app.MainViewModelTest", DEFAULT_VIEWMODEL_TEST_CONTENT, "test")

        project.commit("Initial commit")
        viewModelSource.appendText("\n\n")

        val result = GradleRunner.create()
            .withProjectDir(projectDir)
            .forwardOutput()
            .withArguments(":app:testDiffDebugUnitTest", "--verbose")
            .build()

        assertEquals(TaskOutcome.SUCCESS, result.task(":app:testDiffDebugUnitTest")?.outcome)
        assertEquals(TaskOutcome.SUCCESS, result.task(":app:testDebugUnitTest")?.outcome)

        assertTrue(result.output.contains("[Teddi] Included tests"))
        assertTrue(result.output.contains("[Teddi] Acquired changes"))
    }

    @Test
    fun testDoesNotAffectedByPluginDirectly() {
        project.createMinimalRootProject()
        val app = project.createAndroidApplicationModule(name = "app")
        app.kotlin("com.example.app.MainViewModel", DEFAULT_VIEW_MODEL_CONTENT)
        app.kotlin("com.example.app.MainViewModelTest", DEFAULT_VIEWMODEL_TEST_CONTENT, "test")

        project.commit("Initial commit")

        val result = GradleRunner.create()
            .withProjectDir(projectDir)
            .forwardOutput()
            .withArguments(":app:testDebugUnitTest")
            .build()

        assertNull(result.task(":app:testDiffDebugUnitTest"))
        assertEquals(TaskOutcome.SUCCESS, result.task(":app:testDebugUnitTest")?.outcome)
    }
}
