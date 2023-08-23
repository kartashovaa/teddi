package io.github.kartashovaa.teddi.util

import org.intellij.lang.annotations.Language
import java.io.File

interface GroovyProjectWriter : ProjectWriter {
    fun groovy(path: String, @Language("groovy") content: String): File = write(path, content)
}