package io.github.kartashovaa.teddi.test.resolver

import org.gradle.api.file.FileCollection
import org.gradle.api.tasks.testing.TestFilter

interface TestResolver {

    fun resolve(changes: FileCollection, filter: TestFilter)
}

