package me.kyd3snik.test.diff.changes

import java.io.File

class GitDiffParser(private val rootProjectDir: File) {

    fun parse(lines: Sequence<String>): List<FileChange> = lines.mapNotNull(::parse).toList()

    private fun parse(record: String): FileChange? {
        val regex = Regex("(\\S+)\\s+(\\S+)")
        val result = regex.matchEntire(record)?.groupValues?.takeIf { it.size > 2 }

        return result?.let { createFileChange(result[1], result[2]) }
    }

    // TODO: support all types of changes
    //  https://git-scm.com/docs/git-diff#Documentation/git-diff.txt---diff-filterACDMRTUXB82308203
    private fun createFileChange(changeType: String, filePath: String): FileChange? {
        val file = File(rootProjectDir, filePath)
        return when (changeType) {
            "A" -> FileChange.Created(file)
            "M" -> FileChange.Modified(file)
            "D" -> FileChange.Deleted(file)
            else -> null // TODO: @logging log "Unknown change type $changeType"
        }
    }
}
