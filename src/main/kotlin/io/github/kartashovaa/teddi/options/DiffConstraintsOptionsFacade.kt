package io.github.kartashovaa.teddi.options

import org.gradle.api.tasks.options.Option

interface DiffConstraintsOptionsFacade {

    @Option(option = "fromBlob", description = "Start blob of changes to test")
    fun setFromBlob(fromBlob: String)

    @Option(option = "toBlob", description = "End blob of changes to test")
    fun setToBlob(toBlob: String)
}