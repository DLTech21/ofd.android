package io.github.dltech21.ofd;

import android.content.Context;
import android.graphics.Point;
import android.util.DisplayMetrics;

public class ViewUtils {
    public static final int TEXT_32 = 32;
    public static final int TEXT_40 = 40;
    public static final int TEXT_24 = 24;
    public static final int TEXT_36 = 36;
    public static final int TEXT_30 = 30;
    public static final int TEXT_34 = 34;
    public static final int TEXT_28 = 28;
    public static final int TEXT_38 = 38;

    public static final int INPUT_DRAWABLE = 88;
    public static final int DIVIDER_HEIGHT = 20;

    private static final int SAMPLE_WIDTH = 750;
    private static final int SAMPLE_HTIGHT = 1334;

    public static int getPercentHeight(int src, Context context) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        int screenHeight = dm.heightPixels;
        int result = src * screenHeight / SAMPLE_HTIGHT;
        return result;
    }

    public static int getPercentWidth(int src, Context context) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        int screenWidth = dm.widthPixels;
        int result = src * screenWidth / SAMPLE_WIDTH;
        return result;
    }

    /**
     * 获取屏幕宽度和高度，单位为px
     *
     * @param context
     * @return
     */
    public static Point getScreenMetrics(Context context) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        int w_screen = dm.widthPixels;
        int h_screen = dm.heightPixels;
        return new Point(w_screen, h_screen);

    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 将px值转换为sp值，保证文字大小不变
     *
     * @param pxValue
     * @return
     */
    public static int px2sp(Context context, float pxValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }

    /**
     * 将sp值转换为px值，保证文字大小不变
     *
     * @param spValue
     * @return
     */
    public static int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }
}
