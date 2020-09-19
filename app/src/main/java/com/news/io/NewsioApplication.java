package com.news.io;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;


public class NewsioApplication extends Application {

    private static SharedPreferences sPreferences;
    private static NewsioApplication instance;

    public static SharedPreferences getPreferences() {
        return sPreferences;
    }

    public static NewsioApplication getAppContext() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        if (BuildConfig.DEBUG)
            instance = this;
        sPreferences = PreferenceManager.getDefaultSharedPreferences(this);

    }

}
