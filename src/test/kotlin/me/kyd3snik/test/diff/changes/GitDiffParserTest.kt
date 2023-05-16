package me.kyd3snik.test.diff.changes

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class GitDiffParserTest {

    private val workingDir = File("/")
    private val projectDir = File("/")
    private val parser = GitDiffParser(workingDir, projectDir)

    @Test
    fun parseEmpty() {
//        val changes = parser.parse(emptySequence())

//        assertTrue(changes.isEmpty())
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
            File("dir/added.txt"),
            File("dir/modified.txt"),
            File("dir/deleted.txt"),
        )

//        val actualChanges = parser.parse(diff)

//        assertEquals(expectedChanges, actualChanges)
    }
}