package io.github.dltech21.ofd.listener;

import android.graphics.Bitmap;


public interface OfdTaskListener {
    void onBefor();

    void onComplete(String path);
}
