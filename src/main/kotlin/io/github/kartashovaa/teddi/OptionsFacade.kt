package io.github.kartashovaa.teddi

import io.github.kartashovaa.teddi.options.DiffConstraintsOptionsHandler.Companion.FROM_BLOB_OPTION
import io.github.kartashovaa.teddi.options.DiffConstraintsOptionsHandler.Companion.FROM_BLOB_OPTION_DESCRIPTION
import io.github.kartashovaa.teddi.options.DiffConstraintsOptionsHandler.Companion.TO_BLOB_OPTION
import io.github.kartashovaa.teddi.options.DiffConstraintsOptionsHandler.Companion.TO_BLOB_OPTION_DESCRIPTION
import org.gradle.api.tasks.options.Option

interface OptionsFacade {

    @Option(option = FROM_BLOB_OPTION, description = FROM_BLOB_OPTION_DESCRIPTION)
    fun setFromBlob(fromBlob: String)

    @Option(option = TO_BLOB_OPTION, description = TO_BLOB_OPTION_DESCRIPTION)
    fun setToBlob(toBlob: String)

    @Option(option = "verbose", description = "Prints acquired changes and included tests")
    fun setVerbose(isVerbose: Boolean)
}