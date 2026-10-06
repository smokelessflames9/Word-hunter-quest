package com.ttws.wordhunterquest.ads

import android.app.Activity
import android.content.Context
import android.util.Log
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback

class RewardedAdManager(private val context: Context) {

    private val tag = "RewardedAdManager"
    private var rewardedAd: RewardedAd? = null
    private var isLoading = false

    init {
        preloadAd()
    }

    fun preloadAd() {
        if (isLoading || rewardedAd != null) return
        isLoading = true

        try {
            val adRequest = AdRequest.Builder().build()
            RewardedAd.load(
                context,
                AdConfig.REWARDED_AD_UNIT_ID,
                adRequest,
                object : RewardedAdLoadCallback() {
                    override fun onAdLoaded(ad: RewardedAd) {
                        rewardedAd = ad
                        isLoading = false
                        Log.d(tag, "Rewarded Ad loaded successfully")
                    }

                    override fun onAdFailedToLoad(error: LoadAdError) {
                        rewardedAd = null
                        isLoading = false
                        Log.w(tag, "Rewarded Ad failed to load: ${error.message}")
                    }
                }
            )
        } catch (e: Exception) {
            isLoading = false
            rewardedAd = null
            Log.e(tag, "Error loading rewarded ad: ${e.message}")
        }
    }

    fun isAdAvailable(): Boolean = rewardedAd != null

    fun showRewardedAd(
        activity: Activity,
        onRewardEarned: () -> Unit,
        onUnavailable: () -> Unit
    ) {
        val ad = rewardedAd
        if (ad == null) {
            preloadAd()
            onUnavailable()
            return
        }

        rewardedAd = null // Prevent duplicate use
        var hasUserEarnedReward = false

        ad.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                Log.d(tag, "Rewarded ad dismissed. Earned = $hasUserEarnedReward")
                if (hasUserEarnedReward) {
                    onRewardEarned()
                }
                preloadAd()
            }

            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                Log.w(tag, "Rewarded ad failed to show: ${adError.message}")
                preloadAd()
                onUnavailable()
            }

            override fun onAdShowedFullScreenContent() {
                Log.d(tag, "Rewarded ad showed successfully")
            }
        }

        try {
            ad.show(activity) { rewardItem ->
                Log.d(tag, "User earned reward: ${rewardItem.amount} ${rewardItem.type}")
                hasUserEarnedReward = true
            }
        } catch (e: Exception) {
            Log.e(tag, "Error showing rewarded ad: ${e.message}")
            preloadAd()
            onUnavailable()
        }
    }
}
