package com.ttws.wordhunterquest.ads

import android.content.Context
import android.os.Build
import android.util.Log
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration

object AdMobManager {
    private const val TAG = "AdMobManager"
    private var isInitialized = false

    fun isEmulator(): Boolean {
        return Build.FINGERPRINT.startsWith("generic") ||
            Build.FINGERPRINT.startsWith("unknown") ||
            Build.MODEL.contains("google_sdk") ||
            Build.MODEL.contains("Emulator") ||
            Build.MODEL.contains("Android SDK built for") ||
            Build.MODEL.contains("sdk_gphone") ||
            Build.HARDWARE.contains("goldfish") ||
            Build.HARDWARE.contains("ranchu") ||
            Build.PRODUCT.contains("sdk") ||
            Build.PRODUCT.contains("emulator")
    }

    fun initialize(context: Context) {
        if (isInitialized || isEmulator()) return
        try {
            val configuration = RequestConfiguration.Builder()
                .setTestDeviceIds(listOf(AdRequest.DEVICE_ID_EMULATOR))
                .build()
            MobileAds.setRequestConfiguration(configuration)

            MobileAds.initialize(context) { initializationStatus ->
                isInitialized = true
                Log.d(TAG, "AdMob SDK Initialized: ${initializationStatus.adapterStatusMap.keys}")
            }
        } catch (e: Exception) {
            Log.w(TAG, "AdMob initialization skipped: ${e.message}")
        }
    }
}
