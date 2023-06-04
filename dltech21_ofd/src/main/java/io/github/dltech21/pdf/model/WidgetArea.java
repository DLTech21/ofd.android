package io.github.dltech21.pdf.model;

import android.graphics.RectF;

public class WidgetArea {
    RectF[] boundRect;
    RectF[] realRect;
    String[] signId;


    public WidgetArea(RectF[] boundRect, RectF[] realRect) {
        this.boundRect = boundRect;
        this.realRect = realRect;
    }

    public RectF[] getBoundRect() {
        return boundRect;
    }

    public void setBoundRect(RectF[] boundRect) {
        this.boundRect = boundRect;
    }

    public RectF[] getRealRect() {
        return realRect;
    }

    public void setRealRect(RectF[] realRect) {
        this.realRect = realRect;
    }

    public WidgetArea(RectF[] boundRect, String[] signId) {
        this.boundRect = boundRect;
        this.signId = signId;
    }

    public String[] getSignId() {
        return signId;
    }

    public void setSignId(String[] signId) {
        this.signId = signId;
    }
}
