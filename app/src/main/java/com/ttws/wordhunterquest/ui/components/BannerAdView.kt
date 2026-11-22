package com.ttws.wordhunterquest.ui.components

import android.util.Log
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError
import com.ttws.wordhunterquest.ads.AdConfig
import com.ttws.wordhunterquest.ads.AdMobManager

@Composable
fun BannerAdView(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val metrics = LocalResponsiveMetrics.current
    var isAdLoaded by remember { mutableStateOf(false) }
    var loadedHeightDp by remember { mutableIntStateOf(50) }
    val skipAds = remember(context) { AdMobManager.shouldSkipAds(context) }
    val hasValidBannerUnit = remember { AdConfig.BANNER_AD_UNIT_ID.contains("/") }

    // Never crowd short landscape viewports or trap D-pad focus on TV
    if (skipAds || !hasValidBannerUnit || metrics.isTv) {
        return
    }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .testTag("banner_ad_container"),
        contentAlignment = Alignment.Center
    ) {
        val adaptiveWidthDp = maxWidth.value.toInt().coerceIn(320, 728)

        AndroidView(
            modifier = Modifier
                .fillMaxWidth()
                .then(if (isAdLoaded) Modifier.height(loadedHeightDp.dp) else Modifier.height(0.dp)),
            factory = { ctx ->
                try {
                    val adSize = AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(ctx, adaptiveWidthDp)
                    loadedHeightDp = adSize.height.coerceIn(50, 90)
                    AdView(ctx).apply {
                        setAdSize(adSize)
                        adUnitId = AdConfig.BANNER_AD_UNIT_ID
                        adListener = object : AdListener() {
                            override fun onAdLoaded() {
                                isAdLoaded = true
                                Log.d("BannerAdView", "Adaptive banner ad loaded (${adSize.width}x${adSize.height})")
                            }

                            override fun onAdFailedToLoad(error: LoadAdError) {
                                isAdLoaded = false
                                Log.w("BannerAdView", "Banner ad failed to load: ${error.message}")
                            }
                        }
                        loadAd(AdRequest.Builder().build())
                    }
                } catch (t: Throwable) {
                    Log.w("BannerAdView", "Banner ad view unavailable on this device: ${t.message}")
                    android.view.View(ctx)
                }
            }
        )
    }
}
