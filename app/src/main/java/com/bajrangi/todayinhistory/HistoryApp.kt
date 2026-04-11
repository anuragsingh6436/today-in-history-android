package com.bajrangi.todayinhistory

import android.app.Application
import coil.ImageLoader
import coil.ImageLoaderFactory
import dagger.hilt.android.HiltAndroidApp
import okhttp3.OkHttpClient

@HiltAndroidApp
class HistoryApp : Application(), ImageLoaderFactory {

    /**
     * Provides a custom Coil ImageLoader with a User-Agent header.
     *
     * Wikipedia returns 403 Forbidden for requests without a
     * proper User-Agent. This configures Coil globally so every
     * image load includes one.
     */
    override fun newImageLoader(): ImageLoader {
        val client = OkHttpClient.Builder()
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .header("User-Agent", "TodayInHistoryApp/1.0 (Android; educational project)")
                    .build()
                chain.proceed(request)
            }
            .build()

        return ImageLoader.Builder(this)
            .okHttpClient(client)
            .crossfade(300)
            .build()
    }
}
