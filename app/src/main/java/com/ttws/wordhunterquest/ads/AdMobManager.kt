package com.ttws.wordhunterquest.ads

import android.content.Context
import android.util.Log
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration

object AdMobManager {
    private const val TAG = "AdMobManager"
    private var isInitialized = false

    fun initialize(context: Context) {
        if (isInitialized) return
        try {
            // Configure test device request settings for emulator and development
            val configuration = RequestConfiguration.Builder()
                .setTestDeviceIds(listOf(AdRequest.DEVICE_ID_EMULATOR))
                .build()
            MobileAds.setRequestConfiguration(configuration)

            // Initialize Mobile Ads SDK
            MobileAds.initialize(context) { initializationStatus ->
                isInitialized = true
                Log.d(TAG, "AdMob SDK Initialized: ${initializationStatus.adapterStatusMap.keys}")
            }
        } catch (e: Exception) {
            // Graceful handling: the game continues without interruption if ads fail to initialize
            Log.e(TAG, "AdMob initialization failed gracefully: ${e.message}")
        }
    }
}
