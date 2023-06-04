package io.github.dltech21.ofd.model;

import io.github.dltech21.ofd.listener.OnOfdResultListener;

public class OfdItem {
    private int page;
    private String path;
    private String fileHash;
    private OnOfdResultListener listener;

    public OfdItem(int page, String fileHash, OnOfdResultListener listener) {
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

    public OnOfdResultListener getListener() {
        return listener;
    }

    public void setListener(OnOfdResultListener listener) {
        this.listener = listener;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getFileHash() {
        return fileHash;
    }

    public void setFileHash(String fileHash) {
        this.fileHash = fileHash;
    }
}
