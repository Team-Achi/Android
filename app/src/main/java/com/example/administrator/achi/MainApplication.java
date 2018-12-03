package com.example.administrator.achi;

import android.app.Application;

import org.andresoviedo.util.android.AndroidURLStreamHandlerFactory;

import java.net.URL;

public class MainApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        URL.setURLStreamHandlerFactory(new AndroidURLStreamHandlerFactory());
    }
}
