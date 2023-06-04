package io.github.dltech21.pdf.listener;

import android.graphics.Bitmap;


public interface OnPdfResultListener {
    void onRenderFinish(String path, Bitmap bitmap);
}
