package com.bajrangi.todayinhistory.config

/**
 * Local feature flags.
 *
 * Swap these for Firebase Remote Config later — the UI reads
 * from this object, so the switch is a one-file change.
 *
 * Usage:
 *   if (FeatureFlags.feedMode == FeedMode.REELS) { ... }
 */
object FeatureFlags {

    /**
     * Controls the home screen layout.
     *
     * CARDS → Scrollable card feed (Google Discover style)
     * REELS → Full-screen vertical pager (TikTok style)
     */
    var feedMode: FeedMode = FeedMode.CARDS
}

enum class FeedMode {
    CARDS,
    REELS,
}
