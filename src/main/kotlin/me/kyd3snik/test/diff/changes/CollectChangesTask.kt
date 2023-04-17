package me.kyd3snik.test.diff.changes

import org.gradle.api.Project
import org.gradle.api.file.RegularFile
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.Exec
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskProvider
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.FileOutputStream
import java.io.ObjectOutputStream

abstract class CollectChangesTask : Exec() {

    private lateinit var fromBlob: String
    private lateinit var toBlob: String

    @get:OutputFile
    lateinit var output: RegularFile
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
        commandLine("git", "diff", "--name-status", fromBlob, toBlob)
    }

    private fun parseGitDiff(changesStream: ByteArrayOutputStream): List<FileChange> =
        ByteArrayInputStream(changesStream.toByteArray())
            .bufferedReader()
            .lineSequence()
            .let { GitDiffParser().parse(it) }

    private fun dumpChanges(changes: List<FileChange>) {
        ObjectOutputStream(FileOutputStream(output.asFile))
            .use { it.writeObject(changes) }
    }

    companion object {
        fun register(project: Project, output: Provider<RegularFile>): TaskProvider<CollectChangesTask> =
            project.tasks.register("collectChanges", CollectChangesTask::class.java) { task ->
                task.fromBlob = requireNotNull(project.fromBlob) { "fromBlob didn't set" }
                task.toBlob = project.toBlob ?: "HEAD"
                task.output = output.get()
                task.workingDir = project.rootDir
            }

        private val Project.fromBlob: String? get() = properties["fromBlob"] as? String
        private val Project.toBlob: String? get() = properties["toBlob"] as? String
    }
}

