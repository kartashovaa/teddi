package me.kyd3snik.test.diff.changes

import org.gradle.api.Project
import org.gradle.api.file.RegularFile
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.*
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File

abstract class CollectChangesTask : Exec() {

    @get:Input
    abstract val fromBlob: Property<String>

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
        if (toBlob != null) {
            commandLine("git", "diff", "--name-status", fromBlob.get(), toBlob)
        } else {
            commandLine("git", "diff", "--name-status", fromBlob.get())
        }

        super.exec()

        val parser = GitDiffParser(workingDir, projectDir)
        val storage = ChangesStorage(output.asFile.get())
        val bufferInputStream = ByteArrayInputStream(buffer.toByteArray())
        storage.write(parser.parse(bufferInputStream))
    }

    companion object {

        fun register(project: Project, output: Provider<RegularFile>): TaskProvider<CollectChangesTask> =
            project.tasks.register("collectChanges", CollectChangesTask::class.java) { task ->
                task.fromBlob.setFinal(project.fromBlob)
                task.toBlob.setFinal(project.toBlob)
                task.output.setFinal(output)
                task.outputs.upToDateWhen(OutDateForRelativeBlobs())
                task.workingDir = project.rootDir
                task.projectDir = project.projectDir
            }

        private val Project.fromBlob: Provider<String>
            get() = project.provider {
                val fromBlob = properties["fromBlob"] as? String
                requireNotNull(fromBlob) { "fromBlob isn't set. Usage: ./gradlew app:testDiff -PfromBlob=<commit-hash>" }
            }
        private val Project.toBlob: Provider<String?>
            get() = project.provider {
                val toBlob = properties["toBlob"] as? String
                toBlob.takeIf { it != "HEAD" }
            }

        private fun <T : Any?> Property<T>.setFinal(value: Provider<T>) {
            set(value)
            finalizeValue()
        }
    }
}

