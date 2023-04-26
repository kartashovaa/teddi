package me.kyd3snik.test.diff.changes

import java.io.File
import java.io.Serializable

sealed class FileChange : Serializable {
    /** absolute path */
    abstract val file: File

    data class Created(override val file: File) : FileChange()

    data class Modified(override val file: File) : FileChange()

    //TODO: do we really need deleted files?
    data class Deleted(override val file: File) : FileChange()
}
