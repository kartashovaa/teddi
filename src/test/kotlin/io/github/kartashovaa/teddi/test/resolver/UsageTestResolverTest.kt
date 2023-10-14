package io.github.kartashovaa.teddi.test.resolver

import io.github.kartashovaa.teddi.test.resolver.usage.UsageCollector
import org.gradle.api.file.RelativePath
import org.gradle.api.internal.file.AbstractFileCollection
import org.gradle.api.internal.file.DefaultFileVisitDetails
import org.gradle.api.internal.tasks.testing.filter.DefaultTestFilter
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import java.io.File
import java.util.concurrent.atomic.AtomicBoolean

@RunWith(Parameterized::class)
class UsageTestResolverTest(
    usage: String,
    changeFile: String,
    private val expectedToTest: Boolean
) {

    private val usageCollector = TestUsageCollector(setOf(usage))
    private val changedFiles = TestFileCollection(listOf(changeFile))
    private val filter = DefaultTestFilter()
    private val sut = UsageTestVisitor(usageCollector, changedFiles, filter)

    @Test
    fun run() {
        val details = DefaultFileVisitDetails(
            File("com/example/app/Test.class"),
            RelativePath(true, "com/example/app/Test.class"),
            AtomicBoolean(),
            null,
            null
        )

        sut.visitClassFile(details)

        assertEquals(expectedToTest, setOf("com.example.app.Test.*") == filter.includePatterns)
    }

    companion object {

        @JvmStatic
        @Parameterized.Parameters
        fun data(): Collection<Array<Any>> = listOf(
            arrayOf("com/example/ViewModel", "src/main/kotlin/com/example/ViewModel.kt", true),
            arrayOf("com/example/UtilsKt", "com/example/Utils.kt", true),
            arrayOf("com/example/Parent\$Child", "com/example/Parent.kt", true),
            arrayOf("com/example/Some", "com/example/Other.kt", false),
            arrayOf("com/example/Some", "com/example/deeper/Some.kt", false),
        )
    }
}

private class TestUsageCollector(var changes: Set<String> = emptySet()) : UsageCollector {
    override fun collect(classFile: File): Set<String> = changes
}

private class TestFileCollection(var files: List<String> = emptyList()) : AbstractFileCollection() {

    override fun getDisplayName(): String = "TestFileCollection"
    override fun iterator(): MutableIterator<File> = files.map { File(it) }.toMutableList().listIterator()
}