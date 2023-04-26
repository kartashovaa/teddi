package me.kyd3snik.test.diff

import junit.framework.Assert.assertNotNull
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Assert.assertTrue
import org.junit.Test

class TestDiffPluginTest {

    @Test
    fun applySucceed() {
        val project = ProjectBuilder.builder().build()
        project.plugins.apply(TestDiffPlugin::class.java)

        assertTrue(project.plugins.hasPlugin(TestDiffPlugin::class.java))
        assertNotNull(project.tasks.findByName("collectChanges"))
    }
}