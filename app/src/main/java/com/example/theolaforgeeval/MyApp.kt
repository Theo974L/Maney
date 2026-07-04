package com.example.theolaforgeeval

import android.app.Application
import com.example.theolaforgeeval.api.CategoryModule
import com.example.theolaforgeeval.api.DatabaseModule
import com.example.theolaforgeeval.api.TransactionModule
import com.example.theolaforgeeval.api.ViewModuleModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin


// J'ai créé ce fichier pour isoler Koin afin que le theme clair/sombre ne crash pas

class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@MyApp)
            modules(
                CategoryModule,
                TransactionModule,
                ViewModuleModule,
                DatabaseModule
            )
        }
    }
}
