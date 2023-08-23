package io.github.kartashovaa.teddi.util

interface VCSProjectWriter {

    fun commit(message: String): String
}
