package io.github.kartashovaa.teddi.util

import org.intellij.lang.annotations.Language

interface AndroidProjectWriter : JvmProjectWriter {
    fun resource(type: String, name: String, content: String) = source("res/$type/$name.xml", content, "main")
    fun layout(name: String, content: String) = resource("layout", name, content)
    fun manifest(@Language("xml") content: String) = source("AndroidManifest.xml", content, "main")
}