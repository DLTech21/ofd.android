package io.github.dltech21.pdf.listener;

import android.graphics.Bitmap;


public interface PdfTaskListener {
    void onBefor();

    void onComplete(Bitmap bitmap);
}
