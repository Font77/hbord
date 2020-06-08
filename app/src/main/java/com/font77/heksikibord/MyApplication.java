package com.font77.heksikibord;

import android.app.Application;
public final class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        fontsovArride.setDefaultFont(this, "DEFAULT", R.font.indish);
    }
}
