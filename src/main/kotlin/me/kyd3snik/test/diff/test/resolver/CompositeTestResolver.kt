package me.kyd3snik.test.diff.test.resolver

import me.kyd3snik.test.diff.changes.FileChange
import org.gradle.api.tasks.testing.TestFilter

class CompositeTestResolver(
    private val delegates: List<TestResolver>
) : TestResolver {

    override fun resolve(change: FileChange, filter: TestFilter) {
        delegates.forEach { delegate -> delegate.resolve(change, filter) }
    }

    class Builder {
        private val delegates = ArrayList<TestResolver>()

        fun addDelegate(delegate: TestResolver) {
            delegates.add(delegate)
        }

        fun build() = CompositeTestResolver(delegates)
    }

    companion object {

        fun build(action: Builder.() -> Unit): CompositeTestResolver = Builder().apply(action).build()
    }
}