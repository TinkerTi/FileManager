package com.tinker.filemanager;

import android.app.Application;

/**
 * Created by tiankui on 16/9/3.
 */
public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        FileManagerContext.init(this);
    }
}
