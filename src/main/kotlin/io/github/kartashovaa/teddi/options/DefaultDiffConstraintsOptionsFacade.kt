package io.github.kartashovaa.teddi.options

import io.github.kartashovaa.teddi.changes.CollectChangesTask
import org.gradle.api.Project

class DefaultDiffConstraintsOptionsFacade(
    private val project: Project
) : DiffConstraintsOptionsFacade {

    private val collectChangesTask get() = CollectChangesTask.get(project).get()

    override fun setFromBlob(fromBlob: String) {
        collectChangesTask.fromBlob.set(fromBlob)
    }

    override fun setToBlob(toBlob: String) {
        collectChangesTask.toBlob.set(toBlob)
    }
}