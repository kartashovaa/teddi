package io.github.kartashovaa.teddi.util

import org.intellij.lang.annotations.Language

interface GradleProjectWriter : GroovyProjectWriter {

    fun buildScript(@Language("groovy") content: String) = groovy("build.gradle", content)
    fun settingsScript(@Language("groovy") content: String) = groovy("settings.gradle", content)
    fun properties(content: String) = write("gradle.properties", content)
    fun subproject(name: String): GradleProjectWriter
}