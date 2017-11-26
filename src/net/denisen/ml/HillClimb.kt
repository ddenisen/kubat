package net.denisen.ml

import java.util.*
import kotlin.Comparator

object HillClimb {

    private data class State<S>(
        val model: S,
        val distance: Double,
        val depth: Long,
        val previous: State<S>? = null
    )

    fun <S> search(initialState: S,
                   distanceFunc: (S)->(Double),
                   goalFunc: (S)->Boolean,
                   operator: (S)->Set<S>): List<S>? {

        val cmp = Comparator<State<S>> { lhs, rhs ->
            Math.signum(Math.abs(lhs.distance) - Math.abs(rhs.distance)).toInt().let {
                if (it != 0) return@Comparator it
            }

            return@Comparator (lhs.depth - rhs.depth).let {
                when {
                    it > 0 -> 1
                    it < 0 -> -1
                    else -> 0
                }
            }
        }

        val pending = PriorityQueue<State<S>>(8, cmp)
        val visited = mutableSetOf<S>()

        pending.add(State(
                model = initialState,
                depth = 0,
                distance = distanceFunc(initialState)
        ))

        while (pending.isNotEmpty()) {
            val state = pending.poll()

            if (goalFunc(state.model)) {
                return traceback(state)
            }

            val candidates = operator(state.model)
                    .filterNot(visited::contains)
                    .also { visited.addAll(it) }
                    .map { State(it, distanceFunc(it), state.depth + 1, state) }
            pending.addAll(candidates)
        }

        // No solution found
        return null
    }

    private fun <S> traceback(target: State<S>): List<S> {
        var state: State<S>? = target
        val list = LinkedList<S>()
        while (state != null) {
            list.addFirst(state.model)
            state = state.previous
        }
        return list
    }
}