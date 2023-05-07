package me.kyd3snik.test.diff.test.resolver

import me.kyd3snik.test.diff.changes.FileChange
import org.gradle.api.tasks.testing.TestFilter

interface TestResolver {

    fun resolve(change: FileChange, filter: TestFilter)

    //TODO: get FileCollection, not list
    fun resolveAll(changes: List<FileChange>, filter: TestFilter) =
        changes.forEach { resolve(it, filter) }
}

