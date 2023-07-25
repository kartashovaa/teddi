package io.github.kartashovaa.teddi.changes

import org.slf4j.LoggerFactory
import java.io.File
import java.io.InputStream

class GitDiffParser(private val workingDir: File) {

    fun parse(stream: InputStream): List<File> = stream
        .bufferedReader()
        .lineSequence()
        .mapNotNull(::parse)
        .toList()

    private fun parse(record: String): File? {
        val regex = Regex("(\\S+)\\s+(\\S+)")
        val result = regex.matchEntire(record)?.groupValues?.takeIf { it.size > 2 }

        return result?.let { (_, changeType, filePath) -> createFileChange(changeType, filePath) }
    }

    // TODO: support all types of changes
    //  https://git-scm.com/docs/git-diff#Documentation/git-diff.txt---diff-filterACDMRTUXB82308203
    private fun createFileChange(changeType: String, filePath: String): File? = when (changeType) {
        "A", "M" -> File(workingDir, filePath)
        else -> null.also { logger.debug("Unknown change type $changeType") }
    }

    companion object {
        private val logger = LoggerFactory.getLogger(GitDiffParser::class.java)
    }
}
