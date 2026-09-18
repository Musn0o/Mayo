package com.musno.mayo

import android.app.Application
import com.musno.mayo.data.AppDatabase
import com.musno.mayo.util.PdfGenerator

class MayoApplication : Application() {
    val database: AppDatabase by lazy { AppDatabase.getDatabase(this) }

    override fun onCreate() {
        super.onCreate()
        PdfGenerator.init(this)
    }
}