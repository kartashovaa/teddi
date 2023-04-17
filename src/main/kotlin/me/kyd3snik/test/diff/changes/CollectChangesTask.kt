package me.kyd3snik.test.diff.changes

import org.gradle.api.Project
import org.gradle.api.file.RegularFile
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.Exec
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskProvider
import org.gradle.work.DisableCachingByDefault
import java.io.*

@DisableCachingByDefault(because = "May work not as expected for HEAD based blobs")
abstract class CollectChangesTask : Exec() {

    private lateinit var fromBlob: Provider<String>
    private lateinit var toBlob: Provider<String>

    private lateinit var rootProjectDir: File

    @get:OutputFile
    lateinit var output: Provider<RegularFile>
        private set

    override fun exec() {
        val changesStream = ByteArrayOutputStream(128)
        setupGitDiff(changesStream)
        super.exec()

        executionResult.get()
            .assertNormalExitValue()
            .rethrowFailure()

        val changes = parseGitDiff(changesStream)
        dumpChanges(changes)
    }

    private fun setupGitDiff(changesStream: ByteArrayOutputStream) {
        standardOutput = changesStream
        commandLine("git", "diff", "--name-status", fromBlob.get(), toBlob.get())
    }

    private fun parseGitDiff(changesStream: ByteArrayOutputStream): List<FileChange> =
        ByteArrayInputStream(changesStream.toByteArray())
            .bufferedReader()
            .lineSequence()
            .let { GitDiffParser(rootProjectDir).parse(it) }

    private fun dumpChanges(changes: List<FileChange>) {
        ObjectOutputStream(FileOutputStream(output.get().asFile))
            .use { it.writeObject(changes) }
    }

    companion object {
        fun register(project: Project, output: Provider<RegularFile>): TaskProvider<CollectChangesTask> =
            project.tasks.register("collectChanges", CollectChangesTask::class.java) { task ->
                task.fromBlob = project.provider { requireNotNull(project.fromBlob) { "fromBlob isn't set" } }
                task.toBlob = project.provider { project.toBlob ?: "HEAD" }
                task.output = output
                task.workingDir = project.rootDir
                task.rootProjectDir = project.rootDir
            }

        private val Project.fromBlob: String? get() = properties["fromBlob"] as? String
        private val Project.toBlob: String? get() = properties["toBlob"] as? String
    }
}

