package com.ttws.wordhunterquest.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ttws.wordhunterquest.data.CellPosition
import com.ttws.wordhunterquest.ui.theme.GridCellBorder
import com.ttws.wordhunterquest.ui.theme.GridCellDefault
import com.ttws.wordhunterquest.ui.theme.MidnightBlue
import com.ttws.wordhunterquest.ui.theme.QuestCyan
import com.ttws.wordhunterquest.ui.theme.QuestGold
import com.ttws.wordhunterquest.ui.theme.QuestRuby
import com.ttws.wordhunterquest.ui.theme.TextPrimary
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.sign

data class DiscoveredWordDisplay(
    val word: String,
    val cells: List<CellPosition>,
    val color: Color
)

@Composable
fun WordGrid(
    grid: List<List<Char>>,
    gridSize: Int,
    discoveredWords: List<DiscoveredWordDisplay>,
    hintCells: List<CellPosition>,
    isIncorrectFlash: Boolean,
    onWordSelected: (String, List<CellPosition>) -> Unit,
    modifier: Modifier = Modifier
) {
    var startCell by remember { mutableStateOf<CellPosition?>(null) }
    var currentSelection by remember { mutableStateOf<List<CellPosition>>(emptyList()) }

    // Map each cell to a list of colors from discovered words
    val discoveredCellColors = remember(discoveredWords) {
        val map = mutableMapOf<CellPosition, Color>()
        discoveredWords.forEach { dw ->
            dw.cells.forEach { cell ->
                map[cell] = dw.color
            }
        }
        map
    }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clip(RoundedCornerShape(16.dp))
            .background(MidnightBlue)
            .border(2.dp, GridCellBorder, RoundedCornerShape(16.dp))
            .padding(6.dp)
            .testTag("word_puzzle_grid")
    ) {
        val cellSizePx = remember(constraints.maxWidth, gridSize) {
            constraints.maxWidth.toFloat() / gridSize
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(grid, gridSize) {
                    detectDragGestures(
                        onDragStart = { offset ->
                            val col = (offset.x / cellSizePx).toInt().coerceIn(0, gridSize - 1)
                            val row = (offset.y / cellSizePx).toInt().coerceIn(0, gridSize - 1)
                            val start = CellPosition(row, col)
                            startCell = start
                            currentSelection = listOf(start)
                        },
                        onDrag = { change, _ ->
                            change.consume()
                            val start = startCell ?: return@detectDragGestures
                            val col = (change.position.x / cellSizePx).toInt().coerceIn(0, gridSize - 1)
                            val row = (change.position.y / cellSizePx).toInt().coerceIn(0, gridSize - 1)

                            currentSelection = calculateLineCells(start, CellPosition(row, col), gridSize)
                        },
                        onDragEnd = {
                            if (currentSelection.isNotEmpty()) {
                                val selectedLetters = currentSelection
                                    .map { grid[it.row][it.col] }
                                    .joinToString("")
                                onWordSelected(selectedLetters, currentSelection)
                            }
                            startCell = null
                            currentSelection = emptyList()
                        },
                        onDragCancel = {
                            startCell = null
                            currentSelection = emptyList()
                        }
                    )
                }
        ) {
            // Render perfectly responsive 2D grid using Column and Row
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                for (r in 0 until gridSize) {
                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                    ) {
                        for (c in 0 until gridSize) {
                            val pos = CellPosition(r, c)
                            val letter = grid.getOrNull(r)?.getOrNull(c) ?: ' '
                            val isDiscovered = discoveredCellColors.containsKey(pos)
                            val discoveredColor = discoveredCellColors[pos] ?: Color.Transparent
                            val isSelected = currentSelection.contains(pos)
                            val isHinted = hintCells.contains(pos)

                            val backgroundColor by animateColorAsState(
                                targetValue = when {
                                    isIncorrectFlash && isSelected -> QuestRuby.copy(alpha = 0.85f)
                                    isSelected -> QuestCyan.copy(alpha = 0.75f)
                                    isDiscovered -> discoveredColor.copy(alpha = 0.65f)
                                    isHinted -> QuestGold.copy(alpha = 0.5f)
                                    else -> GridCellDefault
                                },
                                animationSpec = tween(durationMillis = 150),
                                label = "cell_bg"
                            )

                            val borderColor = when {
                                isSelected -> QuestCyan
                                isDiscovered -> discoveredColor
                                isHinted -> QuestGold
                                else -> GridCellBorder.copy(alpha = 0.5f)
                            }

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .aspectRatio(1f)
                                    .padding(1.5.dp)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(backgroundColor)
                                    .border(
                                        width = if (isSelected || isDiscovered || isHinted) 1.5.dp else 0.5.dp,
                                        color = borderColor,
                                        shape = RoundedCornerShape(6.dp)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = letter.toString(),
                                    fontSize = when {
                                        gridSize <= 8 -> 22.sp
                                        gridSize <= 10 -> 18.sp
                                        else -> 14.sp
                                    },
                                    fontWeight = if (isSelected || isDiscovered) FontWeight.Black else FontWeight.Bold,
                                    color = when {
                                        isSelected -> Color.White
                                        isDiscovered -> Color.White
                                        isHinted -> QuestGold
                                        else -> TextPrimary
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Calculates a straight line of cells in 8 directions (horizontal, vertical, diagonal)
 * with intuitive angle snapping so mobile drag feels natural.
 */
private fun calculateLineCells(
    start: CellPosition,
    target: CellPosition,
    maxSize: Int
): List<CellPosition> {
    val dRow = target.row - start.row
    val dCol = target.col - start.col

    if (dRow == 0 && dCol == 0) {
        return listOf(start)
    }

    val absR = abs(dRow)
    val absC = abs(dCol)

    // Snap to 8 directions:
    val (stepR, stepC, count) = when {
        absR == 0 -> Triple(0, dCol.sign, absC)
        absC == 0 -> Triple(dRow.sign, 0, absR)
        absR > 2 * absC -> Triple(dRow.sign, 0, absR)
        absC > 2 * absR -> Triple(0, dCol.sign, absC)
        else -> {
            val length = max(absR, absC)
            Triple(dRow.sign, dCol.sign, length)
        }
    }

    val cells = mutableListOf<CellPosition>()
    for (i in 0..count) {
        val r = (start.row + stepR * i).coerceIn(0, maxSize - 1)
        val c = (start.col + stepC * i).coerceIn(0, maxSize - 1)
        cells.add(CellPosition(r, c))
    }
    return cells
}
