package net.denisen.ml

import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class HillClimbTest {
    @Test(timeout = 3000)
    fun linearSearch() {
        val result = HillClimb.search(
                initialState = 0,
                distanceFunc = { Math.abs(it-7).toDouble() },
                goalFunc = { it == 7 },
                operator = { setOf(it+1, it-1) })
        assertEquals(
                listOf(0,1,2,3,4,5,6,7),
                result
        )
    }

    @Test(timeout = 3000)
    fun initialStateIsGoal() {
        val result = HillClimb.search(
                initialState = 0,
                distanceFunc = { (it-7).toDouble() },
                goalFunc = { it == 0 },
                operator = { throw IllegalStateException("Shouldn't need to call operator") })
        assertEquals(
                listOf(0),
                result
        )
    }

    @Test(timeout = 3000)
    fun targetNotFound() {
        val result = HillClimb.search(
                initialState = 0,
                distanceFunc = { (it-12).toDouble() },
                goalFunc = { it == 12 },
                operator = {
                    if (Math.abs(it) < 6) {
                        setOf(it+1, it-1)
                    } else {
                        setOf()
                    }
                })
        assertNull(result)
    }
}