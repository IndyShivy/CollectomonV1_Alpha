package com.example.collectomon;

import android.app.Application;

import androidx.appcompat.app.AppCompatDelegate;

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // Force the app to always use the dark theme
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
    }
}