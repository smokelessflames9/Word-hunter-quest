package com.ttws.wordhunterquest.ads

import android.app.Activity
import android.content.Context
import android.util.Log
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback

class InterstitialAdManager(private val context: Context) {

    private val tag = "InterstitialAdManager"
    private var interstitialAd: InterstitialAd? = null
    private var isLoading = false

    init {
        preloadAd()
    }

    fun preloadAd() {
        if (isLoading || interstitialAd != null) return
        isLoading = true

        try {
            val adRequest = AdRequest.Builder().build()
            InterstitialAd.load(
                context,
                AdConfig.INTERSTITIAL_AD_UNIT_ID,
                adRequest,
                object : InterstitialAdLoadCallback() {
                    override fun onAdLoaded(ad: InterstitialAd) {
                        interstitialAd = ad
                        isLoading = false
                        Log.d(tag, "Interstitial Ad loaded successfully")
                    }

                    override fun onAdFailedToLoad(error: LoadAdError) {
                        interstitialAd = null
                        isLoading = false
                        Log.w(tag, "Interstitial Ad failed to load: ${error.message}")
                    }
                }
            )
        } catch (e: Exception) {
            isLoading = false
            interstitialAd = null
            Log.e(tag, "Error loading interstitial: ${e.message}")
        }
    }

    fun showInterstitialIfEligible(
        activity: Activity,
        completedLevelsCount: Int,
        onDone: () -> Unit
    ) {
        // Show after every 3 completed levels
        val shouldShow = completedLevelsCount > 0 &&
                (completedLevelsCount % AdConfig.INTERSTITIAL_LEVEL_INTERVAL == 0)

        if (!shouldShow || interstitialAd == null) {
            onDone()
            if (interstitialAd == null) {
                preloadAd()
            }
            return
        }

        val ad = interstitialAd
        interstitialAd = null // Consume current ad

        ad?.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                Log.d(tag, "Interstitial dismissed by player")
                onDone()
                preloadAd()
            }

            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                Log.w(tag, "Interstitial failed to show: ${adError.message}")
                onDone()
                preloadAd()
            }

            override fun onAdShowedFullScreenContent() {
                Log.d(tag, "Interstitial displayed successfully")
            }
        }

        try {
            ad?.show(activity)
        } catch (e: Exception) {
            Log.e(tag, "Error showing interstitial: ${e.message}")
            onDone()
            preloadAd()
        }
    }
}
