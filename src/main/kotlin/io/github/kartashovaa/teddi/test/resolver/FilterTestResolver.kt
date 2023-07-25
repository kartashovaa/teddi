package io.github.kartashovaa.teddi.test.resolver

import org.gradle.api.file.FileCollection
import org.gradle.api.tasks.testing.TestFilter

class FilterTestResolver(
    private val supportedExtensions: Set<String>,
    private val delegate: TestResolver
) : TestResolver {

    override fun resolve(changes: FileCollection, filter: TestFilter) = delegate.resolve(
        changes.filter { file -> file.extension in supportedExtensions },
        filter
    )
}
