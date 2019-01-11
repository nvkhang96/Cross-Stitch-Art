package com.a9itgroup.crossstitchart

public class CrossStitch{
    private val board: Array<IntArray>? = null

    private val crossStitchListener: CrossStitchListener? = null

    interface CrossStitchListener {
//        fun gameWonBy(boardPlayer: BoardPlayer, winPoints: Array<SquareCoordinates>)

//        fun gameEndsWithATie()

        fun movedAt(x: Int, y: Int, move: Int)
    }

    fun moveAt(x: Int, y: Int): Boolean {
        crossStitchListener?.movedAt(x, y, 1)
//        board?.get(x)?.set(y, 1)

        return true
    }
}