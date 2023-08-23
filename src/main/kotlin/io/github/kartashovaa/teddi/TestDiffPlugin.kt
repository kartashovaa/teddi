package io.github.kartashovaa.teddi

import com.android.build.gradle.AppExtension
import com.android.build.gradle.LibraryExtension
import io.github.kartashovaa.teddi.changes.CollectChangesTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension

@Suppress("UNUSED") // used in plugin definition
class TestDiffPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        if (project === project.rootProject) {
            project.subprojects(::apply)
            project.tasks.register("testDiffUnitTest") { rootTask ->
                project.subprojects { child ->
                    child.tasks.withType(TestDiffTask::class.java).all { task -> rootTask.dependsOn(task) }
                }
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
            project.pluginManager.withPlugin("java") {
                project.extensions.findByType(JavaPluginExtension::class.java)?.let { ext ->
                    TestDiffTask.register(project, changesFile)
                }
            }
        }
    }
}