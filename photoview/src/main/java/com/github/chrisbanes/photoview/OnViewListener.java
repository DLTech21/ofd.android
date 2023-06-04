package com.github.chrisbanes.photoview;

import android.view.MotionEvent;
import android.view.View;


public interface OnViewListener {
    void onViewDown(View view, float x, float y);

    void onViewUp(float x, float y);

    void onViewMove(MotionEvent ev);

    void onPointerDonw(MotionEvent ev);
}
