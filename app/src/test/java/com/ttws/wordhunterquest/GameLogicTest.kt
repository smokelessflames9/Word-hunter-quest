package com.ttws.wordhunterquest

import com.ttws.wordhunterquest.data.LevelRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class GameLogicTest {

    @Test
    fun testAll20LevelsExistAndAreSolvable() {
        val levels = LevelRepository.getAllLevels()
        assertEquals(20, levels.size)

        for (level in levels) {
            assertTrue("Level ${level.id} should have words", level.words.isNotEmpty())
            assertTrue("Grid size should be valid", level.gridSize in 8..14)
            assertEquals("Grid should have correct number of rows", level.gridSize, level.grid.size)

            // Verify each target word actually exists in the grid according to placements
            for (word in level.words) {
                val placement = level.placements[word]
                assertNotNull("Word '$word' must have placement in Level ${level.id}", placement)
                val placedWord = placement!!.cells.map { level.grid[it.row][it.col] }.joinToString("")
                assertEquals("Placed word letters must match target word in Level ${level.id}", word, placedWord)
            }
        }
    }
}
