package io.github.kartashovaa.teddi.utils

import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

@RunWith(Parameterized::class)
class EditDistanceCalculatorTest(
    private val expectedDistance: Int,
    private val a: String,
    private val b: String
) {

    private val calculator = EditDistanceCalculator()

    @Test
    fun calculate() {
        assertEquals(expectedDistance, calculator.calculate(a, b))
    }

    companion object {
        @JvmStatic
        @Parameterized.Parameters
        fun data(): Collection<Array<Any>> = listOf(
            arrayOf(0, "", ""),
            arrayOf(0, "Word", "Word"),
            arrayOf(2, "One", "Another"),
            arrayOf(2, "OneWord", "DifferentWord"),
            arrayOf(5, "stopWatchingForBagItems", "startWatchingForItemsInBag"),
        )
    }
}