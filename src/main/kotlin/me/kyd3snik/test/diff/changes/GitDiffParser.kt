package me.kyd3snik.test.diff.changes

import java.io.File
import java.io.InputStream

class GitDiffParser(private val workingDir: File, private val projectDir: File) {

    fun parse(stream: InputStream): List<File> = stream
        .bufferedReader()
        .lineSequence()
        .mapNotNull(::parse)
        .toList()

    private fun parse(record: String): File? {
        val regex = Regex("(\\S+)\\s+(\\S+)")
        val result = regex.matchEntire(record)?.groupValues?.takeIf { it.size > 2 }

        return result?.let { createFileChange(result[1], result[2]) }
    }

    // TODO: support all types of changes
    //  https://git-scm.com/docs/git-diff#Documentation/git-diff.txt---diff-filterACDMRTUXB82308203
    private fun createFileChange(changeType: String, filePath: String): File? {
        return when (changeType) {
            "A", "M" -> File(workingDir, filePath).relativeTo(projectDir)
            else -> null // TODO: @logging log "Unknown change type $changeType"
        }
    }
}
