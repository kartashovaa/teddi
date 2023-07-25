package io.github.kartashovaa.teddi.utils

/**
 * Based on [Levenshtein distance](https://en.wikipedia.org/wiki/Levenshtein_distance)
 * supports only insertions and deletions, substitutions are not suitable here
 * Example with unsuitable distance: SomeViewModelTest | SomeViewModel | SomeViewModelFactory
 */
class EditDistanceCalculator(
    private val splitter: WordSplitter = WordSplitter()
) {
    fun calculate(a: String, b: String): Int {
        val aWords = splitter.split(a)
        val bWords = splitter.split(b)
        if (aWords.isEmpty()) return bWords.size
        if (bWords.isEmpty()) return aWords.size

        val table = Array(aWords.size + 1) { IntArray(bWords.size + 1) { 0 } }
        for (i in aWords.indices) {
            table[i+1][0] = i+1
        }
        for (i in bWords.indices) {
            table[0][i+1] = i+1
        }

        for (i in aWords.indices) {
            for (j in bWords.indices) {
                val bypass = if (aWords[i] == bWords[j]) table[i][j] else Int.MAX_VALUE
                table[i + 1][j + 1] = minOf(table[i][j+1] + 1, table[i+1][j] + 1, bypass)
            }
        }

        return table[aWords.size][bWords.size]
    }
}