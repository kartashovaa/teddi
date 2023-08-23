package io.github.kartashovaa.teddi.options

import io.github.kartashovaa.teddi.changes.CollectChangesTask
import org.gradle.api.Project

class DiffConstraintsOptionsHandler(private val project: Project) {

    private val collectChangesTask get() = CollectChangesTask.get(project).get()

    fun setFromBlob(fromBlob: String) = collectChangesTask.fromBlob.set(fromBlob)

    fun setToBlob(toBlob: String) = collectChangesTask.toBlob.set(toBlob)

    companion object {
        const val FROM_BLOB_OPTION = "fromBlob"
        const val FROM_BLOB_OPTION_DESCRIPTION = "Start blob of changes to test"
        const val TO_BLOB_OPTION = "toBlob"
        const val TO_BLOB_OPTION_DESCRIPTION = "End blob of changes to test"
    }
}