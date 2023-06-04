package io.github.dltech21;

import android.content.Context;

import com.blankj.utilcode.util.FileUtils;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

import java.io.File;

import io.github.dltech21.ofd.ui.OfdActivity;
import io.github.dltech21.pdf.ui.PdfActivity;

public class ReaderManager {
    public static String barColor = "#FCFCFC";
    public static String titleColor = "#ff000000";

    public static void setNavBarColor(String barColor) {
        ReaderManager.barColor = barColor;
    }

    public static void setNavBarTitleColor(String barColor) {
        ReaderManager.titleColor = barColor;
    }


    public static void init(Context mContext) {
        initImageLoader(mContext);
    }

    public static void init(Context mContext, String barColor, String titleColor) {
        initImageLoader(mContext);
        ReaderManager.barColor = barColor;
        ReaderManager.titleColor = titleColor;
    }

    private static void initImageLoader(Context context) {
        ImageLoaderConfiguration.Builder config = new ImageLoaderConfiguration.Builder(context);
        config.threadPriority(Thread.NORM_PRIORITY - 2);
        config.denyCacheImageMultipleSizesInMemory();
        config.diskCacheFileNameGenerator(new Md5FileNameGenerator());
        config.diskCacheSize(50 * 1024 * 1024);
        config.tasksProcessingOrder(QueueProcessingType.LIFO);
        config.writeDebugLogs();
        ImageLoader.getInstance().init(config.build());
    }

//    public static void openOfd(Context mContext, String path) {
//        OfdActivity.open(mContext, path, "");
//    }
//
//    public static void openPdf(Context mContext, String path) {
//        PdfActivity.open(mContext, path, "");
//    }

    public static void openFile(Context mContext, String path) {
        File file = new File(path);
        if (file.exists()) {
            String ext = FileUtils.getFileExtension(file).toLowerCase();
            if ("pdf".equals(ext)) {
                PdfActivity.open(mContext, path, "");
            } else if ("ofd".equals(ext)) {
                OfdActivity.open(mContext, path, "");
            }
        }
    }
}
