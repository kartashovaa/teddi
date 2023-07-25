package io.github.kartashovaa.teddi.test.resolver.usage

import java.io.File

interface UsageCollector {

    fun collect(classFile: File): Set<String>
}


