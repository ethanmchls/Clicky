package com.ethanmichaelis.clicky

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.GoTrue
import timber.log.Timber

@HiltAndroidApp
class ClickyApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
    }
}