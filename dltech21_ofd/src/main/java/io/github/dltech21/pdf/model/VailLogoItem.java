package io.github.dltech21.pdf.model;

import android.graphics.Rect;
import android.graphics.RectF;


public class VailLogoItem {
    private RectF parent;
    private RectF rectF;

    public VailLogoItem(RectF parent, RectF rectF) {
        this.parent = parent;
        this.rectF = rectF;
    }

    public RectF getParent() {
        return parent;
    }

    public void setParent(RectF parent) {
        this.parent = parent;
    }

    public RectF getRectF() {
        return rectF;
    }

    public void setRectF(Rect rectFF) {
        this.rectF = rectF;
    }
}
