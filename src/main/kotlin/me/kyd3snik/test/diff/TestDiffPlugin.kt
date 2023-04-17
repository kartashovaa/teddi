package me.kyd3snik.test.diff

import com.android.build.gradle.AppExtension
import com.android.build.gradle.LibraryExtension
import me.kyd3snik.test.diff.changes.CollectChangesTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.RegularFile
import org.gradle.api.provider.Provider

@Suppress("UNUSED") // used in plugin definition
class TestDiffPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        val changesFile: Provider<RegularFile> = project.layout.buildDirectory.file("test/changes.bin")
        val collectChangesTask = CollectChangesTask.register(project, changesFile)

        // TODO: old extension, probably will become deprecated soon
        project.extensions.findByType(AppExtension::class.java)?.let { ext ->
            ext.applicationVariants.all { variant ->
                TestDiffTask.register(project, variant, changesFile, ext.unitTestVariants)
                    // TODO: make dependency implicit through changesFile
                    .configure { it.dependsOn(collectChangesTask) }
            }
        }

        project.extensions.findByType(LibraryExtension::class.java)?.let { ext ->
            ext.libraryVariants.all { variant ->
                TestDiffTask.register(project, variant, changesFile, ext.unitTestVariants)
                    // TODO: make dependency implicit through changesFile
                    .configure { it.dependsOn(collectChangesTask) }
            }
        }
    }
}