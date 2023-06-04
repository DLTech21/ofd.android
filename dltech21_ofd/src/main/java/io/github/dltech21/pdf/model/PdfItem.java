package io.github.dltech21.pdf.model;

import android.graphics.Bitmap;

import io.github.dltech21.pdf.listener.OnPdfResultListener;


public class PdfItem {
    private int page;
    private String path;
    private Bitmap bitmap;
    private String fileHash;
    private OnPdfResultListener listener;

    public PdfItem(int page, String fileHash, OnPdfResultListener listener) {
        this.page = page;
        this.listener = listener;
        this.fileHash = fileHash;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public OnPdfResultListener getListener() {
        return listener;
    }

    public void setListener(OnPdfResultListener listener) {
        this.listener = listener;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public String getFileHash() {
        return fileHash;
    }

    public void setFileHash(String fileHash) {
        this.fileHash = fileHash;
    }
}
