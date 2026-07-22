package com.example.theolaforgeeval.api

import com.example.theolaforgeeval.core.preferences.AppPreferences
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val PreferencesModule = module {
    single {
        AppPreferences(androidContext())
    }
}
