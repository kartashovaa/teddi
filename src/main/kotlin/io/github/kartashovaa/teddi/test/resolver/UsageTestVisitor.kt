package io.github.kartashovaa.teddi.test.resolver

import io.github.kartashovaa.teddi.test.resolver.usage.UsageCollector
import io.github.kartashovaa.teddi.utils.ClassFileVisitor
import org.gradle.api.file.FileCollection
import org.gradle.api.file.FileVisitDetails
import org.gradle.api.tasks.testing.TestFilter
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class UsageTestVisitor(
    private val usageCollector: UsageCollector,
    private val sourceFiles: FileCollection,
    private val filter: TestFilter
) : ClassFileVisitor() {

    override fun visitClassFile(details: FileVisitDetails) {
        val usages = usageCollector.collect(details.file)
            .map { usage -> usage.substringBefore("$").removeSuffix("Kt") }

        if (logger.isDebugEnabled) {
            logger.debug("Usages:" + usages.joinToString())
        }

        val isAnyUsageChanged = sourceFiles.asSequence()
            .map { file -> file.path.removeExtension() }
            .any { source -> usages.any { usage -> source.endsWith(usage) } }

        if (isAnyUsageChanged) {
            filter.includeTest(getQualifiedClassName(details), null)
        }
    }

    private fun String.removeExtension(): String = replaceAfterLast('.', "").removeSuffix(".")

    private companion object {
        val logger: Logger = LoggerFactory.getLogger(UsageTestVisitor::class.java)
    }
}