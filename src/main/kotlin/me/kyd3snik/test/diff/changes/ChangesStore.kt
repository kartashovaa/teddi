package me.kyd3snik.test.diff.changes

import java.io.*

class ChangesStore(private val file: File) {

    @Suppress("UNCHECKED_CAST")
    fun read(): List<File> = ObjectInputStream(FileInputStream(file)).readObject() as List<File>

    fun write(files: List<File>) {
        ObjectOutputStream(FileOutputStream(file)).use { stream ->
            stream.writeObject(files)
        }
    }
}