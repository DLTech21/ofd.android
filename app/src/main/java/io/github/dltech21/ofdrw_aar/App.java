package io.github.dltech21.ofdrw_aar;

import android.app.Application;

import com.blankj.utilcode.util.Utils;

import io.github.dltech21.ReaderManager;

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Utils.init(this);
        ReaderManager.init(this);
    }
}
