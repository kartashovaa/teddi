package io.github.kartashovaa.teddi.test.resolver.usage

import org.junit.Test
import java.io.File

class AsmUsageCollectorTest {

    @Test
    fun collectKtUsages() {
        val collector = AsmUsageCollector()

        val file = File("build/classes/kotlin/test", KotlinCandidate::class.java.name.replace(".", "/") + ".class")
        println(file)
        val usages = collector.collect(file)
        println(usages)
    }
}