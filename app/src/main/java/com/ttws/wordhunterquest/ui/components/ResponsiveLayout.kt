package com.ttws.wordhunterquest.ui.components

import android.app.UiModeManager
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Build
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.ttws.wordhunterquest.ui.theme.QuestGoldLight

enum class WindowWidthClass {
    COMPACT,   // < 600dp (phones in portrait)
    MEDIUM,    // 600dp..839dp (small tablets, foldables, phones in landscape)
    EXPANDED   // >= 840dp (tablets in landscape, Fire HD 10 / Max 11, Fire TV)
}

enum class WindowHeightClass {
    COMPACT,   // < 480dp (phones in landscape, compact split-screen)
    MEDIUM,    // 480dp..899dp (phones in portrait, standard tablets, 720p/1080p TV)
    EXPANDED   // >= 900dp (large portrait tablets)
}

@Immutable
data class ResponsiveMetrics(
    val maxWidth: Dp = 412.dp,
    val maxHeight: Dp = 915.dp,
    val widthClass: WindowWidthClass = WindowWidthClass.COMPACT,
    val heightClass: WindowHeightClass = WindowHeightClass.MEDIUM,
    val isLandscape: Boolean = false,
    val useTwoPaneLayout: Boolean = false,
    val isCompactHeight: Boolean = false,
    val isTv: Boolean = false,
    val isAmazonDevice: Boolean = false,
    val safeHorizontalPadding: Dp = 0.dp,
    val safeVerticalPadding: Dp = 0.dp
) {
    val isTabletOrLarger: Boolean
        get() = widthClass != WindowWidthClass.COMPACT || isTv
}

val LocalResponsiveMetrics = compositionLocalOf { ResponsiveMetrics() }

/**
 * Detects Amazon Fire TV and Android TV devices via UI mode and system features.
 */
fun isTelevisionDevice(context: Context): Boolean {
    return try {
        val uiModeManager = context.getSystemService(Context.UI_MODE_SERVICE) as? UiModeManager
        val isTvMode = uiModeManager?.currentModeType == Configuration.UI_MODE_TYPE_TELEVISION
        val pm = context.packageManager
        val hasFireTvFeature = pm.hasSystemFeature("amazon.hardware.fire_tv")
        val hasLeanbackOnly = pm.hasSystemFeature(PackageManager.FEATURE_LEANBACK) &&
            !pm.hasSystemFeature(PackageManager.FEATURE_TOUCHSCREEN)
        isTvMode || hasFireTvFeature || hasLeanbackOnly
    } catch (_: Throwable) {
        false
    }
}

/**
 * Detects Amazon Fire tablets, Fire TV, and Fire OS devices.
 */
fun isAmazonFireDevice(): Boolean {
    return Build.MANUFACTURER.equals("Amazon", ignoreCase = true) ||
        Build.BRAND.equals("Amazon", ignoreCase = true)
}

/**
 * Measures the actual available window container using BoxWithConstraints (supports rotation,
 * multi-window split-screen, foldables, Fire tablets, Android tablets, and Fire TV overscan)
 * and provides [ResponsiveMetrics] down the composition tree.
 */
@Composable
fun ProvideResponsiveMetrics(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val isTv = remember(context) { isTelevisionDevice(context) }
    val isAmazon = remember { isAmazonFireDevice() }

    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val widthClass = when {
            maxWidth < 600.dp -> WindowWidthClass.COMPACT
            maxWidth < 840.dp -> WindowWidthClass.MEDIUM
            else -> WindowWidthClass.EXPANDED
        }
        val heightClass = when {
            maxHeight < 480.dp -> WindowHeightClass.COMPACT
            maxHeight < 900.dp -> WindowHeightClass.MEDIUM
            else -> WindowHeightClass.EXPANDED
        }
        val isLandscape = maxWidth > maxHeight
        val isCompactHeight = maxHeight < 520.dp
        val useTwoPaneLayout = isTv ||
            widthClass == WindowWidthClass.EXPANDED ||
            (isLandscape && maxWidth >= 560.dp)

        // Apply TV overscan-safe margins (~4.5%) automatically on Fire TV / Android TV
        val overscanH = if (isTv) (maxWidth * 0.045f).coerceIn(24.dp, 52.dp) else 0.dp
        val overscanV = if (isTv) (maxHeight * 0.04f).coerceIn(16.dp, 32.dp) else 0.dp

        val metrics = ResponsiveMetrics(
            maxWidth = maxWidth,
            maxHeight = maxHeight,
            widthClass = widthClass,
            heightClass = heightClass,
            isLandscape = isLandscape,
            useTwoPaneLayout = useTwoPaneLayout,
            isCompactHeight = isCompactHeight,
            isTv = isTv,
            isAmazonDevice = isAmazon,
            safeHorizontalPadding = overscanH,
            safeVerticalPadding = overscanV
        )

        CompositionLocalProvider(LocalResponsiveMetrics provides metrics) {
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = overscanH, vertical = overscanV)
            ) {
                content()
            }
        }
    }
}

/**
 * Adds a high-contrast focus outline when navigated via Fire TV Remote D-Pad,
 * hardware keyboard, or gamepad controller, with zero visual impact on touch input.
 */
fun Modifier.tvFocusBorder(
    shape: Shape = RoundedCornerShape(14.dp),
    focusColor: Color = QuestGoldLight,
    borderWidth: Dp = 2.5.dp
): Modifier = composed {
    var isFocused by remember { mutableStateOf(false) }
    this
        .onFocusChanged { isFocused = it.isFocused }
        .then(
            if (isFocused) {
                Modifier.border(borderWidth, focusColor, shape)
            } else {
                Modifier
            }
        )
}
