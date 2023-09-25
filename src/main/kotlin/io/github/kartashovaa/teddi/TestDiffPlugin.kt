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
            TestDiffExtension.register(project)
            project.subprojects(::apply)
            TestDiffRootTask.register(project)
        } else {
            val changesFile = CollectChangesTask.register(project).flatMap { it.output }
            project.pluginManager.withPlugin("com.android.application") {
                val ext = TestDiffExtension.get(project.rootProject)
                // TODO: old extension, probably will become deprecated soon
                project.extensions.findByType(AppExtension::class.java)
                    ?.applicationVariants
                    ?.matching(ext::isAcceptable)
                    ?.all { variant ->
                        TestDiffTask.register(project, variant, changesFile)
                    }

            }
            project.pluginManager.withPlugin("com.android.library") {
                val ext = TestDiffExtension.get(project.rootProject)
                project.extensions.findByType(LibraryExtension::class.java)
                    ?.libraryVariants
                    ?.matching(ext::isAcceptable)
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