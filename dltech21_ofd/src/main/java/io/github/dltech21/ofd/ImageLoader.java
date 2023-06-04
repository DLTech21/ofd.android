package io.github.dltech21.ofd;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

/**
 * Created by Donal on 2017/5/16.
 */

public class ImageLoader {
    private static int TYPE = 1;
    public final static int TYPE_FILE = TYPE++;
    public final static int TYPE_ASSET = TYPE++;
    public final static int TYPE_RAW = TYPE++;
    public final static int TYPE_DRAWABLE = TYPE++;
    public final static int TYPE_HTTP = TYPE++;
    public final static int TYPE_HTTPS = TYPE++;


    public static void loadPdfImgByImageLoader(String path, int placeDrawable, ImageView mImageView, ImageLoadingListener listener) {
        String header = "file://";
        if (header == null) {
            return;
        }
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showImageOnLoading(placeDrawable)
                .showImageForEmptyUri(placeDrawable)
                .showImageOnFail(placeDrawable)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .build();
        com.nostra13.universalimageloader.core.ImageLoader.getInstance().displayImage(header + path, mImageView, options, listener);
    }

    public static void loadPhotoByImageLoader(Context context, int type, String path, int placeDrawable, ImageView mImageView, ImageLoadingListener listener) {
        String header = getByType(context, type);
        if (header == null) {
            return;
        }
        if (context == null) {
            return;
        }
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showImageOnLoading(placeDrawable)
                .showImageForEmptyUri(placeDrawable)
                .showImageOnFail(placeDrawable)
                .cacheInMemory(false)
                .cacheOnDisk(false)
                .imageScaleType(ImageScaleType.EXACTLY)
                .bitmapConfig(Bitmap.Config.ARGB_8888)
                .build();
        com.nostra13.universalimageloader.core.ImageLoader.getInstance().displayImage(header + path, mImageView, options, listener);
    }

    private static String getByType(Context context, int type) {
        String sType = null;
        if (type == TYPE_FILE) {
            sType = "file://";
        } else if (type == TYPE_ASSET) {
            sType = "file:///android_asset/";
        } else if (type == TYPE_RAW) {
            sType = "Android.resource://" + context.getPackageName() + "/raw/";
        } else if (type == TYPE_DRAWABLE) {
            sType = "android.resource://" + context.getPackageName() + "/drawable/";
        } else if (type == TYPE_HTTP || type == TYPE_HTTPS) {
            sType = "";
        }
        return sType;
    }
}
