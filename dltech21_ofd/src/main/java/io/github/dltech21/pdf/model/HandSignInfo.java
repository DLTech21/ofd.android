package io.github.dltech21.pdf.model;


import androidx.collection.ArrayMap;


public class HandSignInfo {
    private ArrayMap<String, VailLogoItem> logos = new ArrayMap<>();

    public HandSignInfo() {
    }

    public ArrayMap<String, VailLogoItem> getLogos() {
        return logos;
    }

    public void setLogos(ArrayMap<String, VailLogoItem> logos) {
        this.logos = logos;
    }

}
