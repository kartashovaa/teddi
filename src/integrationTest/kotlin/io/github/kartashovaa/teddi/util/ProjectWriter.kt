package io.github.kartashovaa.teddi.util

import java.io.File

interface ProjectWriter {

    val projectDir: File

    fun write(path: String, content: String): File
}

