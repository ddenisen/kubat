package net.denisen.kubat.ch1

import net.denisen.ml.HillClimb
import java.util.*

/**
 * Solves the sliding-tiles puzzles using the hill climb algorithm
 */
object SlidingTiles {
    data class Grid(
        val cells: Array<Int> // 0 indicates empty cell
    ) {
        val side = Math.round(Math.sqrt(cells.size.toDouble())).toInt()
        val tileIndex = IntArray(cells.size) { cells.indexOf(it) }

        init {
            require(cells.isNotEmpty())
            require(side * side == cells.size) { "Grid must be square!" }
            require(!tileIndex.contains(-1)) { "Malformed grid" }
        }

        fun prettyFormat(): String {
            return StringBuilder().apply {
                append("┌" + "─────┬".repeat(side))
                replace(length-1, length, "┐")
                for (i in 0.until(side)) {
                    append("\n│" + "     │".repeat(side))
                    append("\n│")
                    cells.slice((i*side).until((i+1)*side)).forEach {
                        if (it != 0) {
                            append("%3d  │".format(it))
                        } else {
                            append("     │")
                        }
                    }
                    append("\n│" + "     │".repeat(side))
                    if (i < side-1) {
                        append("\n├" + "─────┼".repeat(side))
                        replace(length - 1, length, "┤")
                    } else {
                        append("\n└" + "─────┴".repeat(side))
                        replace(length - 1, length, "┘")
                    }
                }
                appendln()

            }.toString()
        }

        private fun swap(tileA: Int, tileB: Int): Grid {
            val cells = this.cells.copyOf()
            cells[tileIndex[tileA]] = tileB
            cells[tileIndex[tileB]] = tileA
            return Grid(cells)

        }

        fun nextStates(): Set<Grid> {
            val emptyCell = tileIndex[0]
            val row = emptyCell / side
            val column = emptyCell % side

            val result = mutableSetOf<Grid>()

            if (row > 0) {
                result.add(swap(0, cells[emptyCell - side]))
            }
            if (row < side-1) {
                result.add(swap(0, cells[emptyCell + side]))
            }
            if (column > 0) {
                result.add(swap(0, cells[emptyCell-1]))
            }
            if (column < side-1) {
                result.add(swap(0, cells[emptyCell+1]))
            }
            return result
        }

        override fun equals(other: Any?): Boolean =
            this === other ||
                other is Grid &&
                    this.cells contentEquals other.cells

        override fun hashCode() = Arrays.hashCode(cells)
    }

    fun manhattanDistance(gridA: Grid, gridB: Grid): Long {
        require(gridA.side == gridB.side)
        var total = 0L
        val side = gridA.side
        for (i in 1.until(gridA.cells.size)) {
            val deltaX = Math.abs(gridA.tileIndex[i] % side - gridB.tileIndex[i] % side)
            val deltaY = Math.abs(gridA.tileIndex[i] / side - gridB.tileIndex[i] / side)
            total += deltaX + deltaY
        }
        return total
    }

    @JvmStatic
    fun main(args: Array<String>) {
        val initialState = Grid(arrayOf(
                0, 2, 1,
                6, 7, 4,
                3, 8, 5
        ))
        val goalState = Grid(arrayOf(
                1, 2, 3,
                8, 0, 4,
                7, 6, 5
        ))

        val solution = HillClimb.search(initialState = initialState,
                distanceFunc = { manhattanDistance(it, goalState).toDouble() },
                goalFunc = { it == goalState },
                operator = Grid::nextStates)

        if (solution != null) {
            println("Found a solution with ${solution.size} steps. Press Enter to see it!")
            readLine()
            solution.forEach { step ->
                print("\u001b[2J\u001b[H")
                print(step.prettyFormat())
                Thread.sleep(800)
            }
        } else {
            println("Didn't find a solution!")
        }
    }
}