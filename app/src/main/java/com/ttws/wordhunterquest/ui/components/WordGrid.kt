package com.ttws.wordhunterquest.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
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
import com.ttws.wordhunterquest.ui.theme.QuestGoldLight
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

/**
 * Container-aware square puzzle grid that scales cleanly to fit any aspect ratio
 * (phones, Fire tablets, Android tablets, foldables, Fire TV in portrait or landscape)
 * with zero clipping or stretching, supporting both touch-and-drag and Fire TV D-pad/keyboard input.
 */
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

    // Fire TV Remote / D-pad / Keyboard cursor & anchor selection state
    var isGridFocused by remember { mutableStateOf(false) }
    var cursorCell by remember { mutableStateOf(CellPosition(0, 0)) }
    var dpadAnchorCell by remember { mutableStateOf<CellPosition?>(null) }

    // Reset selection state cleanly when level/grid changes
    LaunchedEffect(grid, gridSize) {
        startCell = null
        currentSelection = emptyList()
        dpadAnchorCell = null
        cursorCell = CellPosition(0, 0)
    }

    // Map each cell to its discovered word highlight color
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
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        // Fit the largest 1:1 square inside the available container dimensions
        val availableWidth = if (maxWidth.value.isFinite()) maxWidth else 360.dp
        val availableHeight = if (maxHeight.value.isFinite()) maxHeight else availableWidth
        val boardSide = minOf(availableWidth, availableHeight).coerceAtLeast(160.dp)
        val outerPadding = (boardSide * 0.018f).coerceIn(4.dp, 10.dp)
        val innerSide = (boardSide - outerPadding * 2).coerceAtLeast(120.dp)
        val cellSizeDp = innerSide / gridSize
        val cellPadding = (cellSizeDp * 0.045f).coerceIn(1.dp, 3.dp)
        val cellCornerRadius = (cellSizeDp * 0.16f).coerceIn(4.dp, 10.dp)
        val letterFontSizeSp = (cellSizeDp.value * 0.48f).coerceIn(11f, 34f).sp

        val density = LocalDensity.current
        val cellSizePx = remember(innerSide, gridSize, density) {
            with(density) { innerSide.toPx() / gridSize }
        }

        val boardShape = RoundedCornerShape(16.dp)

        Box(
            modifier = Modifier
                .size(boardSide)
                .clip(boardShape)
                .background(MidnightBlue)
                .border(
                    width = if (isGridFocused) 2.5.dp else 2.dp,
                    color = if (isGridFocused) QuestGoldLight else GridCellBorder,
                    shape = boardShape
                )
                .padding(outerPadding)
                .onFocusChanged { focusState ->
                    isGridFocused = focusState.isFocused
                    if (!focusState.isFocused) {
                        dpadAnchorCell = null
                        if (startCell == null) {
                            currentSelection = emptyList()
                        }
                    }
                }
                .focusable()
                .onPreviewKeyEvent { keyEvent ->
                    if (keyEvent.type != KeyEventType.KeyDown) return@onPreviewKeyEvent false

                    fun moveCursor(dRow: Int, dCol: Int): Boolean {
                        val nextRow = cursorCell.row + dRow
                        val nextCol = cursorCell.col + dCol
                        // Allow leaving grid to adjacent controls when not mid-word selection
                        if (dpadAnchorCell == null && (nextRow !in 0 until gridSize || nextCol !in 0 until gridSize)) {
                            return false
                        }
                        val clamped = CellPosition(
                            row = nextRow.coerceIn(0, gridSize - 1),
                            col = nextCol.coerceIn(0, gridSize - 1)
                        )
                        cursorCell = clamped
                        dpadAnchorCell?.let { anchor ->
                            currentSelection = calculateLineCells(anchor, clamped, gridSize)
                        }
                        return true
                    }

                    when (keyEvent.key) {
                        Key.DirectionUp -> moveCursor(-1, 0)
                        Key.DirectionDown -> moveCursor(1, 0)
                        Key.DirectionLeft -> moveCursor(0, -1)
                        Key.DirectionRight -> moveCursor(0, 1)
                        Key.DirectionCenter, Key.Enter, Key.NumPadEnter, Key.Spacebar -> {
                            val anchor = dpadAnchorCell
                            if (anchor == null) {
                                dpadAnchorCell = cursorCell
                                currentSelection = listOf(cursorCell)
                            } else {
                                val line = calculateLineCells(anchor, cursorCell, gridSize)
                                val selectedLetters = line
                                    .mapNotNull { grid.getOrNull(it.row)?.getOrNull(it.col) }
                                    .joinToString("")
                                if (selectedLetters.isNotEmpty()) {
                                    onWordSelected(selectedLetters, line)
                                }
                                dpadAnchorCell = null
                                currentSelection = emptyList()
                            }
                            true
                        }
                        Key.Back, Key.Escape -> {
                            if (dpadAnchorCell != null) {
                                dpadAnchorCell = null
                                currentSelection = emptyList()
                                true
                            } else {
                                false
                            }
                        }
                        else -> false
                    }
                }
                .testTag("word_puzzle_grid")
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(grid, gridSize, cellSizePx) {
                        detectDragGestures(
                            onDragStart = { offset ->
                                dpadAnchorCell = null
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
                                        .mapNotNull { grid.getOrNull(it.row)?.getOrNull(it.col) }
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
                Column(modifier = Modifier.fillMaxSize()) {
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
                                val isCursor = isGridFocused && cursorCell == pos

                                val backgroundColor by animateColorAsState(
                                    targetValue = when {
                                        isIncorrectFlash && isSelected -> QuestRuby.copy(alpha = 0.85f)
                                        isSelected -> QuestCyan.copy(alpha = 0.78f)
                                        isDiscovered -> discoveredColor.copy(alpha = 0.65f)
                                        isHinted -> QuestGold.copy(alpha = 0.5f)
                                        isCursor -> QuestGold.copy(alpha = 0.25f)
                                        else -> GridCellDefault
                                    },
                                    animationSpec = tween(durationMillis = 140),
                                    label = "cell_bg"
                                )

                                val borderColor = when {
                                    isCursor -> QuestGoldLight
                                    isSelected -> QuestCyan
                                    isDiscovered -> discoveredColor
                                    isHinted -> QuestGold
                                    else -> GridCellBorder.copy(alpha = 0.5f)
                                }

                                val cellShape = RoundedCornerShape(cellCornerRadius)

                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxSize()
                                        .padding(cellPadding)
                                        .clip(cellShape)
                                        .background(backgroundColor)
                                        .border(
                                            width = when {
                                                isCursor -> 2.5.dp
                                                isSelected || isDiscovered || isHinted -> 1.5.dp
                                                else -> 0.5.dp
                                            },
                                            color = borderColor,
                                            shape = cellShape
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = letter.toString(),
                                        fontSize = letterFontSizeSp,
                                        fontWeight = if (isSelected || isDiscovered || isCursor) FontWeight.Black else FontWeight.Bold,
                                        color = when {
                                            isSelected -> Color.White
                                            isDiscovered -> Color.White
                                            isCursor -> QuestGoldLight
                                            isHinted -> QuestGold
                                            else -> TextPrimary
                                        },
                                        maxLines = 1,
                                        softWrap = false
                                    )
                                }
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
 * with intuitive angle snapping so mobile drag and D-pad selection both feel natural.
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
