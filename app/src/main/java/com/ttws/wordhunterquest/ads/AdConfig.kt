package com.ttws.wordhunterquest.ads

/**
 * ============================================================================
 * WORD HUNTER QUEST - CENTRALIZED ADMOB CONFIGURATION
 * ============================================================================
 *
 * DEVELOPMENT / TEST ADS:
 * Google's official sample/test Ad Unit IDs are configured below for safe
 * testing during development without violating AdMob policies.
 *
 * TO SWITCH TO PRODUCTION:
 * Replace the test IDs below with your actual AdMob App ID and Ad Unit IDs
 * from your Google AdMob dashboard: https://admob.google.com/
 * Also ensure the App ID in AndroidManifest.xml matches ADMOB_APP_ID.
 */
object AdConfig {

    // ========================================================================
    // Production AdMob IDs
    // ========================================================================
    const val ADMOB_APP_ID = "ca-app-pub-2042705130906630~3461597778"

    // Banner Ad Unit ID
    const val BANNER_AD_UNIT_ID = "ca-app-pub-2042705130906630~3461597778"

    // Interstitial Ad Unit ID
    const val INTERSTITIAL_AD_UNIT_ID = "ca-app-pub-2042705130906630/1569931609"

    // Rewarded Ad Unit ID
    const val REWARDED_AD_UNIT_ID = "ca-app-pub-2042705130906630/8386621392"

    // Number of completed levels between interstitial displays (e.g., levels 3, 6, 9, 12, 15, 18)
    const val INTERSTITIAL_LEVEL_INTERVAL = 3
}
