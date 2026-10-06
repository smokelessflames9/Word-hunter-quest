package com.ttws.wordhunterquest.data

data class CellPosition(val row: Int, val col: Int)

data class WordPlacement(
    val word: String,
    val startRow: Int,
    val startCol: Int,
    val endRow: Int,
    val endCol: Int,
    val cells: List<CellPosition>
)

data class Level(
    val id: Int,
    val category: String,
    val difficulty: String,
    val gridSize: Int,
    val words: List<String>,
    val grid: List<List<Char>>,
    val placements: Map<String, WordPlacement>
)
