package me.kyd3snik.test.diff

import org.gradle.api.Plugin
import org.gradle.api.Project

@Suppress("UNUSED") // used in plugin definition
class TestDiffPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        project.afterEvaluate {
            it.logger.error("Hello from plugin!")
        }
    }
}