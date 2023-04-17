package me.kyd3snik.test.diff

import me.kyd3snik.test.diff.changes.CollectChangesTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.RegularFile
import org.gradle.api.provider.Provider

@Suppress("UNUSED") // used in plugin definition
class TestDiffPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        project.afterEvaluate {
            it.logger.error("Hello from plugin!")
        }

        val changesFile: Provider<RegularFile> = project.layout.buildDirectory.file("test/changes.txt")
        CollectChangesTask.register(project, changesFile)
    }
}