package io.github.kartashovaa.teddi.test.resolver

import io.github.kartashovaa.teddi.test.resolver.usage.UsageCollector
import org.gradle.api.file.FileCollection
import org.gradle.api.file.FileTree
import org.gradle.api.tasks.testing.TestFilter

class UsageTestResolver(
    private val usageCollector: UsageCollector,
    private val tests: FileTree
) : TestResolver {
    override fun resolve(changes: FileCollection, filter: TestFilter) {
        tests.visit(UsageTestVisitor(usageCollector, changes, filter))
    }
}

