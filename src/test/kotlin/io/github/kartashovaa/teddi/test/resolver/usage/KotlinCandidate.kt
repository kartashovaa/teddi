package io.github.kartashovaa.teddi.test.resolver.usage

fun topLevelFunction() = Unit

class KotlinCandidate {

    val a = String.format("Hello, world")

    fun test() {
        val b = List(12) { 1 }
        val c = Int.MAX_VALUE
        topLevelFunction()
    }
}