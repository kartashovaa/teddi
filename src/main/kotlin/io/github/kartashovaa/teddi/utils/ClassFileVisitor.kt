package io.github.kartashovaa.teddi.utils

import org.gradle.api.file.FileVisitDetails
import org.gradle.api.file.FileVisitor
import org.gradle.api.file.ReproducibleFileVisitor
import java.util.regex.Pattern

private val anonymousClassRegex: Pattern = Pattern.compile(".*\\$\\d+")

abstract class ClassFileVisitor : FileVisitor, ReproducibleFileVisitor {


    abstract fun visitClassFile(details: FileVisitDetails)

    final override fun visitFile(fileDetails: FileVisitDetails) {
        if (isClass(fileDetails) && !isAnonymousClass(fileDetails)) {
            visitClassFile(fileDetails)
        }
    }

    final override fun visitDir(p0: FileVisitDetails) = Unit

    private fun isAnonymousClass(fileVisitDetails: FileVisitDetails): Boolean {
        return anonymousClassRegex.matcher(getQualifiedClassName(fileVisitDetails)).matches()
    }

    private fun isClass(fileVisitDetails: FileVisitDetails): Boolean {
        val fileName = fileVisitDetails.file.name
        return fileName.endsWith(".class") && "module-info.class" != fileName
    }

    protected fun getQualifiedClassName(fileDetails: FileVisitDetails): String {
        return fileDetails.relativePath.pathString
            .replace(".class", "")
            .replace('/', '.')
    }

    protected fun getSimpleClassName(fileDetails: FileVisitDetails): String {
        return fileDetails.relativePath.lastName
            .replace(".class", "")
    }

    override fun isReproducibleFileOrder(): Boolean = true
}