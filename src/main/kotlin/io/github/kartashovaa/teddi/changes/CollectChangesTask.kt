package io.github.kartashovaa.teddi.changes

import org.gradle.api.Project
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.*
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File

// TODO: add kind of filters here
//  1) collect only changes related to current project
//  2) collect only source file changes if tests are not using resources
//  3) collect changes from sourceSets for current build variant
@Suppress("UnstableApiUsage")
@UntrackedTask(because = "Inputs are unstable so far")
abstract class CollectChangesTask : Exec() {

    @get:Input
    @get:Optional
    abstract val fromBlob: Property<String?>

    @get:Input
    @get:Optional
    abstract val toBlob: Property<String?>

    @get:OutputFile
    abstract val output: RegularFileProperty

    private lateinit var projectDir: File

    override fun exec() {
        val buffer = ByteArrayOutputStream(128)
        standardOutput = buffer
        isIgnoreExitValue = false
        val toBlob = toBlob.orNull?.takeIf(String::isNotBlank)
        val fromBlob = fromBlob.orNull?.takeIf(String::isNotBlank)

        commandLine(listOfNotNull("git", "diff", "--name-status", fromBlob, toBlob))

        super.exec()

        val parser = GitDiffParser(workingDir)
        val storage = ChangesStore(output.asFile.get())
        val bufferInputStream = ByteArrayInputStream(buffer.toByteArray())
        storage.write(parser.parse(bufferInputStream))
    }

    companion object {

        private const val TASK_NAME = "collectChanges"

        fun get(project: Project): TaskProvider<CollectChangesTask> =
            project.tasks.named(TASK_NAME, CollectChangesTask::class.java)

        fun register(project: Project): TaskProvider<CollectChangesTask> =
            project.tasks.register(TASK_NAME, CollectChangesTask::class.java) { task ->
                val output = project.layout.buildDirectory.file("test/changes.bin")
                task.output.set(output)
                task.output.finalizeValue()
                task.outputs.upToDateWhen(ChangesUpToDateSpec().asTaskSpec())
                task.workingDir = project.rootDir
                task.projectDir = project.projectDir
            }
    }
}

