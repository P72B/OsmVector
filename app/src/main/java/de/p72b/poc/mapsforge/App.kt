package de.p72b.poc.mapsforge

import android.annotation.SuppressLint
import android.content.Context

@SuppressLint("StaticFieldLeak")
object App {

    @Volatile
    lateinit var appContext: Context

    fun setContext(context: Context) {
        appContext = context
    }
}