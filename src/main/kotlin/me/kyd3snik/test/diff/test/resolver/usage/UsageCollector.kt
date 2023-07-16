package me.kyd3snik.test.diff.test.resolver.usage

import java.io.File

interface UsageCollector {

    fun collect(classFile: File): Set<String>
}


