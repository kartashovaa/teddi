package io.github.kartashovaa.teddi.test.resolver

import java.io.File

class FileSystemLayout {

    private val childrenCache = HashMap<File, List<File>>()

    fun getParent(file: File): File = file.parentFile

    fun getSiblings(file: File): List<File> {
        require(file.isAbsolute) { "Unsupported relative paths: $file" }
        val parent = file.parentFile ?: return emptyList()
        var children = childrenCache[parent]
        if (children == null) {
            children = parent.listFiles()?.toList().orEmpty()
            childrenCache[parent] = children
        }
        return children
    }
}