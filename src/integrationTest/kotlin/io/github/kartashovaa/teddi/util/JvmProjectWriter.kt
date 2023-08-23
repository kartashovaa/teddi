package io.github.kartashovaa.teddi.util

import org.intellij.lang.annotations.Language
import java.io.File

interface JvmProjectWriter : ProjectWriter {
    fun source(path: String, content: String, sourceSet: String) = write("src/$sourceSet/$path", content)

    fun kotlin(
        className: String,
        @Language("kotlin") content: String,
        sourceSet: String = "main",
    ): File {
        val path = className.replace('.', '/') + ".kt"
        val classPackage = className.substringBeforeLast('.')
        val contentPrefix = "package $classPackage\n\n"
        return source("kotlin/$path", contentPrefix + content, sourceSet)
    }

}