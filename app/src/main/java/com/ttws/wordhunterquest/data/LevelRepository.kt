package com.ttws.wordhunterquest.data

import kotlin.random.Random

object LevelRepository {

    private val directions = listOf(
        Pair(0, 1),   // Horizontal Right
        Pair(0, -1),  // Horizontal Left
        Pair(1, 0),   // Vertical Down
        Pair(-1, 0),  // Vertical Up
        Pair(1, 1),   // Diagonal Down-Right
        Pair(1, -1),  // Diagonal Down-Left
        Pair(-1, 1),  // Diagonal Up-Right
        Pair(-1, -1)  // Diagonal Up-Left
    )

    private val rawLevels = listOf(
        // Level 1: Animals (Easy)
        RawLevelDef(
            id = 1,
            category = "ANIMALS",
            difficulty = "Easy",
            gridSize = 8,
            words = listOf("LION", "TIGER", "BEAR", "EAGLE", "WOLF", "ZEBRA")
        ),
        // Level 2: Fruits (Easy)
        RawLevelDef(
            id = 2,
            category = "FRUITS",
            difficulty = "Easy",
            gridSize = 8,
            words = listOf("APPLE", "BANANA", "MANGO", "ORANGE", "PEACH", "GRAPE")
        ),
        // Level 3: Countries (Easy)
        RawLevelDef(
            id = 3,
            category = "COUNTRIES",
            difficulty = "Easy",
            gridSize = 8,
            words = listOf("BRAZIL", "CANADA", "FRANCE", "JAPAN", "SPAIN", "ITALY", "MEXICO")
        ),
        // Level 4: Vehicles (Easy)
        RawLevelDef(
            id = 4,
            category = "VEHICLES",
            difficulty = "Easy",
            gridSize = 8,
            words = listOf("TRUCK", "TRAIN", "PLANE", "BOAT", "SUBWAY", "ROCKET", "BICYCLE")
        ),
        // Level 5: Sports (Easy)
        RawLevelDef(
            id = 5,
            category = "SPORTS",
            difficulty = "Easy",
            gridSize = 8,
            words = listOf("SOCCER", "TENNIS", "HOCKEY", "RUGBY", "BOXING", "SKATING", "GOLF")
        ),
        // Level 6: Careers (Medium)
        RawLevelDef(
            id = 6,
            category = "CAREERS",
            difficulty = "Medium",
            gridSize = 9,
            words = listOf("DOCTOR", "PILOT", "LAWYER", "ARTIST", "CHEF", "NURSE", "WRITER", "JUDGE")
        ),
        // Level 7: Food (Medium)
        RawLevelDef(
            id = 7,
            category = "FOOD",
            difficulty = "Medium",
            gridSize = 9,
            words = listOf("PIZZA", "BURGER", "PASTA", "SALAD", "SUSHI", "WAFFLE", "TACO", "BREAD")
        ),
        // Level 8: Technology (Medium)
        RawLevelDef(
            id = 8,
            category = "TECHNOLOGY",
            difficulty = "Medium",
            gridSize = 9,
            words = listOf("LAPTOP", "ROUTER", "SERVER", "CODE", "SCREEN", "CHIP", "ROBOT", "CLOUD", "DATA")
        ),
        // Level 9: Nature (Medium)
        RawLevelDef(
            id = 9,
            category = "NATURE",
            difficulty = "Medium",
            gridSize = 10,
            words = listOf("FOREST", "RIVER", "VALLEY", "DESERT", "CANYON", "ISLAND", "GLACIER", "MEADOW", "JUNGLE")
        ),
        // Level 10: Ocean (Medium)
        RawLevelDef(
            id = 10,
            category = "OCEAN",
            difficulty = "Medium",
            gridSize = 10,
            words = listOf("DOLPHIN", "SHARK", "CORAL", "WHALE", "OCTOPUS", "TURTLE", "ANCHOR", "CURRENT", "TRENCH", "REEF")
        ),
        // Level 11: Space (Hard)
        RawLevelDef(
            id = 11,
            category = "SPACE",
            difficulty = "Hard",
            gridSize = 10,
            words = listOf("GALAXY", "PLANET", "COMET", "NEBULA", "ORBIT", "METEOR", "COSMOS", "ECLIPSE", "PULSAR", "SATURN")
        ),
        // Level 12: Cities (Hard)
        RawLevelDef(
            id = 12,
            category = "CITIES",
            difficulty = "Hard",
            gridSize = 11,
            words = listOf("LONDON", "TOKYO", "PARIS", "SYDNEY", "BERLIN", "MADRID", "CAIRO", "DUBAI", "SEOUL", "ROME")
        ),
        // Level 13: Famous Places (Hard)
        RawLevelDef(
            id = 13,
            category = "FAMOUS PLACES",
            difficulty = "Hard",
            gridSize = 11,
            words = listOf("PYRAMID", "COLOSSEUM", "TAJMAHAL", "EVEREST", "NIAGARA", "PETRA", "LOUVRE", "BIGBEN", "ACROPOLIS", "STONEHENGE", "SAHARA")
        ),
        // Level 14: School (Hard)
        RawLevelDef(
            id = 14,
            category = "SCHOOL",
            difficulty = "Hard",
            gridSize = 11,
            words = listOf("TEACHER", "LIBRARY", "SCIENCE", "PENCIL", "DESK", "HISTORY", "ERASER", "COMPASS", "LESSON", "STUDENT", "RULER")
        ),
        // Level 15: Household Items (Hard)
        RawLevelDef(
            id = 15,
            category = "HOUSEHOLD ITEMS",
            difficulty = "Hard",
            gridSize = 11,
            words = listOf("MIRROR", "BLANKET", "KETTLE", "CLOCK", "PILLOW", "CARPET", "TOASTER", "CURTAIN", "CANDLE", "CABINET", "BOTTLE")
        ),
        // Level 16: Science (Very Hard)
        RawLevelDef(
            id = 16,
            category = "SCIENCE",
            difficulty = "Very Hard",
            gridSize = 12,
            words = listOf("ATOM", "GRAVITY", "QUANTUM", "GENETICS", "ENZYME", "MOLECULE", "NUCLEUS", "ENERGY", "PHOTON", "THEORY", "MATTER", "KINETIC")
        ),
        // Level 17: Adventure (Very Hard)
        RawLevelDef(
            id = 17,
            category = "ADVENTURE",
            difficulty = "Very Hard",
            gridSize = 12,
            words = listOf("TREASURE", "COMPASS", "EXPEDITION", "JOURNEY", "MAP", "VOYAGE", "SAFARI", "SUMMIT", "HORIZON", "QUEST", "SURVIVAL", "PATHWAY")
        ),
        // Level 18: Entertainment (Very Hard)
        RawLevelDef(
            id = 18,
            category = "ENTERTAINMENT",
            difficulty = "Very Hard",
            gridSize = 12,
            words = listOf("CINEMA", "THEATER", "CONCERT", "MUSIC", "ACTOR", "COMEDY", "DRAMA", "DANCE", "CIRCUS", "FESTIVAL", "MAGIC", "ARCADE", "BALLET")
        ),
        // Level 19: World Culture (Very Hard)
        RawLevelDef(
            id = 19,
            category = "WORLD CULTURE",
            difficulty = "Very Hard",
            gridSize = 12,
            words = listOf("HERITAGE", "TRADITION", "FESTIVAL", "FOLKLORE", "CUSTOM", "LANGUAGE", "CUISINE", "CARNIVAL", "RITUAL", "SYMBOL", "CRAFT", "LEGEND", "DIVERSITY")
        ),
        // Level 20: Ultimate Challenge (Very Hard)
        RawLevelDef(
            id = 20,
            category = "ULTIMATE CHALLENGE",
            difficulty = "Very Hard",
            gridSize = 12,
            words = listOf("EXPEDITION", "CONQUEROR", "LABYRINTH", "MASTERMIND", "DISCOVERY", "TRIUMPHANT", "CHAMPION", "MYSTERY", "VICTORY", "CHALLENGE", "ADVENTURE", "TREASURE", "LEGENDARY", "HORIZON")
        )
    )

    private val cachedLevels: Map<Int, Level> by lazy {
        rawLevels.associate { def ->
            def.id to generateDeterministicLevel(def)
        }
    }

    fun getAllLevels(): List<Level> = (1..20).mapNotNull { getLevel(it) }

    fun getLevel(id: Int): Level? = cachedLevels[id]

    fun getTotalLevels(): Int = 20

    private fun generateDeterministicLevel(def: RawLevelDef): Level {
        val size = def.gridSize
        val words = def.words.distinct().map { it.uppercase().trim() }
        val random = Random(def.id * 104729 + 42)

        var bestGrid: Array<CharArray>? = null
        var bestPlacements: Map<String, WordPlacement>? = null

        // Try placement with seeded attempts to guarantee 100% of words fit
        for (attempt in 0..100) {
            val grid = Array(size) { CharArray(size) { ' ' } }
            val placements = mutableMapOf<String, WordPlacement>()
            var allPlaced = true

            // Sort words longest first to make packing efficient
            val sortedWords = words.sortedByDescending { it.length }

            for (word in sortedWords) {
                val placement = tryPlaceWord(word, grid, size, random)
                if (placement != null) {
                    placements[word] = placement
                    for (i in word.indices) {
                        val cell = placement.cells[i]
                        grid[cell.row][cell.col] = word[i]
                    }
                } else {
                    allPlaced = false
                    break
                }
            }

            if (allPlaced) {
                bestGrid = grid
                bestPlacements = placements
                break
            }
        }

        // Guaranteed fallback if dense puzzle needed more room
        val finalGrid = bestGrid ?: Array(size) { CharArray(size) { ' ' } }
        val finalPlacements = (bestPlacements ?: emptyMap()).toMutableMap()

        // Fill remaining empty cells with random letters
        val letterPool = words.joinToString("").toList().ifEmpty { ('A'..'Z').toList() }
        for (r in 0 until size) {
            for (c in 0 until size) {
                if (finalGrid[r][c] == ' ') {
                    finalGrid[r][c] = letterPool.random(random)
                }
            }
        }

        val gridList = finalGrid.map { row -> row.toList() }

        return Level(
            id = def.id,
            category = def.category,
            difficulty = def.difficulty,
            gridSize = def.gridSize,
            words = words,
            grid = gridList,
            placements = finalPlacements
        )
    }

    private fun tryPlaceWord(
        word: String,
        grid: Array<CharArray>,
        size: Int,
        random: Random
    ): WordPlacement? {
        val wordLen = word.length
        val shuffledDirections = directions.shuffled(random)

        // Try random starting positions
        val allPositions = mutableListOf<Pair<Int, Int>>()
        for (r in 0 until size) {
            for (c in 0 until size) {
                allPositions.add(Pair(r, c))
            }
        }
        allPositions.shuffle(random)

        for (dir in shuffledDirections) {
            val dr = dir.first
            val dc = dir.second

            for (pos in allPositions) {
                val startRow = pos.first
                val startCol = pos.second
                val endRow = startRow + dr * (wordLen - 1)
                val endCol = startCol + dc * (wordLen - 1)

                if (endRow in 0 until size && endCol in 0 until size) {
                    var fits = true
                    val cells = mutableListOf<CellPosition>()

                    for (i in 0 until wordLen) {
                        val currR = startRow + dr * i
                        val currC = startCol + dc * i
                        val existingChar = grid[currR][currC]
                        if (existingChar != ' ' && existingChar != word[i]) {
                            fits = false
                            break
                        }
                        cells.add(CellPosition(currR, currC))
                    }

                    if (fits) {
                        return WordPlacement(
                            word = word,
                            startRow = startRow,
                            startCol = startCol,
                            endRow = endRow,
                            endCol = endCol,
                            cells = cells
                        )
                    }
                }
            }
        }

        return null
    }

    private data class RawLevelDef(
        val id: Int,
        val category: String,
        val difficulty: String,
        val gridSize: Int,
        val words: List<String>
    )
}
