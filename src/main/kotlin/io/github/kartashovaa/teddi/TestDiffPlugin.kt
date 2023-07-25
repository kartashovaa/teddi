package io.github.kartashovaa.teddi

import com.android.build.gradle.AppExtension
import com.android.build.gradle.LibraryExtension
import io.github.kartashovaa.teddi.changes.CollectChangesTask
import org.gradle.api.Plugin
import org.gradle.api.Project

@Suppress("UNUSED") // used in plugin definition
class TestDiffPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        if (project === project.rootProject) {
            val rootTask = project.task("testDiffUnitTest")
            project.subprojects {
                apply(it)
                it.tasks.withType(TestDiffTask::class.java).all { rootTask.dependsOn(it) }
            }
        } else {
            val changesFile = CollectChangesTask.register(project).flatMap { it.output }
            project.pluginManager.withPlugin("com.android.application") {
                // TODO: old extension, probably will become deprecated soon
                project.extensions.findByType(AppExtension::class.java)
                    ?.applicationVariants
                    ?.all { variant ->
                        TestDiffTask.register(project, variant, changesFile)
                    }

            }
            project.pluginManager.withPlugin("com.android.library") {
                project.extensions.findByType(LibraryExtension::class.java)
                    ?.libraryVariants
                    ?.all { variant ->
                        TestDiffTask.register(project, variant, changesFile)
                    }
            }
        }
    }
}