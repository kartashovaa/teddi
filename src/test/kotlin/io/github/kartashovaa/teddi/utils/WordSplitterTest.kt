package io.github.kartashovaa.teddi.utils

import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

@RunWith(Parameterized::class)
class WordSplitterTest(private val input: String, private val expectedOutput: String) {

    private val splitter = WordSplitter()

    @Test(timeout = 100)
    fun split() {
        val actualOutput = splitter.split(input).joinToString(separator = " ")
        assertEquals(expectedOutput, actualOutput)
    }

    companion object {

        @JvmStatic
        @Parameterized.Parameters
        fun data(): Collection<Array<Any>> = listOf(
            arrayOf("CamelCase", "camel case"),
            arrayOf("lowerCamelCase", "lower camel case"),
            arrayOf("snake_case", "snake case"),
            arrayOf("SomeLong_combinedCases", "some long combined cases"),
            arrayOf("HTTPAbbreviates", "http abbreviates"),
            arrayOf("PrefixHTTPAbbreviates", "prefix http abbreviates"),
            arrayOf("PrefixHTTP", "prefix http"),
            arrayOf("SomeDigits123", "some digits123"),
            arrayOf("SomeDigits123Suffix", "some digits123 suffix"),
        )
    }
}
