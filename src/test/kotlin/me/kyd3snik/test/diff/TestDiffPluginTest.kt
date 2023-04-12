package me.kyd3snik.test.diff

import org.gradle.testfixtures.ProjectBuilder
import org.junit.jupiter.api.Test
import kotlin.test.assertTrue

class TestDiffPluginTest {

    @Test
    fun applySucceed() {
        val project = ProjectBuilder.builder().build()
        project.plugins.apply(TestDiffPlugin::class.java)

        assertTrue(project.plugins.hasPlugin(TestDiffPlugin::class.java))
    }
}