package me.kyd3snik.test.diff.changes

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File
import java.io.InputStream

class GitDiffParserTest {

    private val parser = GitDiffParser(File("/test/"))

    @Test
    fun parseEmpty() {
        val changes = parser.parse(InputStream.nullInputStream())

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
        """.trimIndent()

        val expectedChanges = listOf(
            File("/test/dir/added.txt"),
            File("/test/dir/modified.txt"),
        )

        val actualChanges = parser.parse(diff.encodeToByteArray().inputStream())

        assertEquals(expectedChanges, actualChanges)
    }
}