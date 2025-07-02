package org.chaynik.dch

import android.app.Application

class App: Application() {

    override fun onCreate() {
        super.onCreate()
        initRestClient()
    }

    private fun initRestClient() {

    }
}