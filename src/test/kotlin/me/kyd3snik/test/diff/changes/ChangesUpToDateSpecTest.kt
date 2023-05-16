package me.kyd3snik.test.diff.changes

import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

@RunWith(Parameterized::class)
class ChangesUpToDateSpecTest(
    private val fromBlob: String?,
    private val toBlob: String?,
    private val expected: Boolean,
) {

    private val spec = ChangesUpToDateSpec()

    @Test
    fun isSatisfiedBy() {
        assertEquals("isSatisfiedBy($fromBlob, $toBlob)", expected, spec.isSatisfiedBy(fromBlob, toBlob))
    }

    companion object {

        private const val TEST_COMMIT_HASH = "some-hash-value"

        @JvmStatic
        @Parameterized.Parameters
        fun data(): Collection<Array<Any?>> = listOf(
            arrayOf(null, null, false),
            arrayOf(TEST_COMMIT_HASH, null, false),
            arrayOf(TEST_COMMIT_HASH, "HEAD", false),
            arrayOf(null, TEST_COMMIT_HASH, false),
            arrayOf("HEAD", TEST_COMMIT_HASH, false),
            arrayOf(TEST_COMMIT_HASH, TEST_COMMIT_HASH, true),
        )
    }
}