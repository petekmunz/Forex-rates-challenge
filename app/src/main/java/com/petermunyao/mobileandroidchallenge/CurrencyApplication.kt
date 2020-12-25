package com.petermunyao.mobileandroidchallenge

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class CurrencyApplication: Application() {
    override fun onCreate() {
        super.onCreate()

        //Initialize Timber
        /*if (BuildConfig.Debug) {
            Timber.plant(Timber.DebugTree())
        }*/
    }
}