package me.kyd3snik.test.diff.changes

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.io.File

class GitDiffParserTest {

    private val parser = GitDiffParser()

    @Test
    fun parseEmpty() {
        val changes = parser.parse(emptySequence())

        assertTrue(changes.isEmpty())
    }

    @Test
    fun parse() {
        val diff = """
            A       dir/added.txt
            M       dir/modified.txt
            D       dir/deleted.txt
            R       dir/renamed.txt
            C       dir/copied.txt
            T       dir/changed.txt
        """.trimIndent().lineSequence()

        val expectedChanges = listOf(
            FileChange.Created(File("dir/added.txt")),
            FileChange.Modified(File("dir/modified.txt")),
            FileChange.Deleted(File("dir/deleted.txt")),
        )

        val actualChanges = parser.parse(diff)

        assertEquals(expectedChanges, actualChanges)
    }
}