package me.kyd3snik.test.diff.utils

class WordSplitter {

    private var input: String = ""
    private var startOfWord = 0
    private var idx = 0
    private val result: ArrayList<String> = ArrayList()

    fun split(input: String): List<String> {
        setup(input)
        parseWords()
        return result.toList()
    }

    private fun parseWords() {
        while (!eoi()) {
            when {
                peek() == '_' -> {
                    next()
                    startWord()
                }

                peek().isUpperCase() -> parseWord()
                peek().isLowerCase() -> parseLowerCaseWord()
                else -> error("Unexpected char ${peek()} at $idx: $input")
            }
        }
    }

    private fun startWord() {
        startOfWord = idx
    }

    private fun parseWord() {
        assert(peek().isUpperCase())
        startWord()
        while (!eoi() && peek().isUpperCase()) next()
        when {
            eoi() -> pushWord()
            // abbreviate
            wordLength() > 1 -> {
                prev()
                prev()
                pushWord()
            }

            else -> parseLowerCaseWord()
        }
    }

    private fun parseLowerCaseWord() {
        while (!eoi() && (peek().isLowerCase() || peek().isDigit())) next()
        pushWord()
    }

    private fun peek(): Char = input[idx]
    private fun next(): Char = input[idx++]
    private fun prev(): Char = input[--idx]

    private fun eoi(): Boolean = idx >= input.length
    private fun wordLength(): Int = (idx - startOfWord - 1).coerceAtLeast(0)

    private fun pushWord() {
        val word = input.substring(startOfWord, idx)
        if (word.isNotBlank()) result.add(word.lowercase())
    }

    private fun setup(input: String) {
        this.input = input
        startOfWord = 0
        idx = 0
        result.clear()
    }
}