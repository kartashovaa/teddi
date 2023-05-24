package me.kyd3snik.test.diff.test.resolver

import java.io.File

class FileSystemLayout {

    private val siblingsCache = HashMap<File, List<File>>()

    fun getParent(file: File): File = file.parentFile

    fun getSiblings(file: File): List<File> {
        require(file.isAbsolute) { "Unsupported relative paths: $file" }
        val parent = file.parentFile ?: return emptyList()
        var siblings = siblingsCache[parent]
        if (siblings == null) {
            siblings = parent.listFiles()?.toList().orEmpty()
            siblingsCache[parent] = siblings
        }
        return siblings
    }
}