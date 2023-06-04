package io.github.dltech21.ofd;

import android.util.Log;

import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.PathUtils;
import com.blankj.utilcode.util.ResourceUtils;
import com.blankj.utilcode.util.Utils;

import java.io.IOException;
import java.util.Map;

public class FontManager {
    private volatile static FontManager mInstance;

    private Map<String, String> fontMap;

    public static FontManager getInstance() {
        if (mInstance == null) {
            synchronized (FontManager.class) {
                if (mInstance == null) {
                    mInstance = new FontManager();
                }
            }
        }
        return mInstance;
    }

    private boolean copyFileFromAssets(final String assetsFilePath, final String destFilePath) {
        boolean res = true;
        try {
            String[] assets = Utils.getApp().getAssets().list(assetsFilePath);
            if (assets != null && assets.length > 0) {
                for (String asset : assets) {
                    res &= copyFileFromAssets(assetsFilePath + "/" + asset, destFilePath + "/" + asset);
                }
            } else {
                if (!FileUtils.isFileExists(destFilePath)) {
                    res = ResourceUtils.copyFileFromAssets(assetsFilePath, destFilePath);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            res = false;
        }
        return res;
    }

    public boolean copyFontFromAssets(String assetsDir) {
        String fontPath = PathUtils.getCachePathExternalFirst() + System.getProperty("file.separator") + "fonts";
        return copyFileFromAssets(assetsDir, fontPath);
    }

    public String getFontDir() {
        return PathUtils.getCachePathExternalFirst() + System.getProperty("file.separator") + "fonts";
    }

    public Map<String, String> getFontMap() {
        return fontMap;
    }

    public void setFontMap(Map<String, String> fontMap) {
        this.fontMap = fontMap;
    }
}
