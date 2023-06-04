package io.github.dltech21.pdf.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.RectF;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.collection.ArrayMap;
import androidx.core.content.ContextCompat;

import io.github.dltech21.ofd.R;
import io.github.dltech21.pdf.PdfManager;
import io.github.dltech21.pdf.model.HandSignInfo;
import io.github.dltech21.pdf.model.SignatureInformation;
import io.github.dltech21.pdf.model.VailLogoItem;
import io.github.dltech21.pdf.model.VailResult;
import io.github.dltech21.pdf.model.WidgetArea;
import io.github.dltech21.pdf.ui.ElectronicSignResultActivity;

import com.artifex.mupdf.viewer.MuPDFCore;
import com.github.chrisbanes.photoview.OnMatrixChangedListener;
import com.github.chrisbanes.photoview.OnPhotoTapListener;
import com.github.chrisbanes.photoview.OnViewListener;
import com.github.chrisbanes.photoview.PhotoView;
import com.github.chrisbanes.photoview.PhotoViewAttacher;

import java.util.Map;


public class PdfView extends RelativeLayout {
    private static int vaildWidth, vaildHeight;
    private final static float disDicount = 0.9f;
    private ArrayMap<String, ImageView> vaildSigns = new ArrayMap<>();
    private PhotoView contentView;
    private RectF lastRect = new RectF();
    private ChangeListener changeListener;

    private View touchView;
    private PointF mSize;
    private MuPDFCore mCore;
    private int pageNum;
    private WidgetArea mWidgetAreas;

    private boolean renderFinish = false;
    private float mX, mY;

    private ImageView qualityView;
    private int qualityTag = 0;
    private boolean isTouch = false;
    private boolean isDelayMessage = false;

    private PdfManager pdfManager;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (changeListener != null) {
                changeListener.onTouchUp(PdfView.this, qualityTag);
            }
        }
    };

    public PdfView(Context context) {
        super(context);
        init();
    }

    public PdfView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public PdfView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public PdfView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs);
    }

    public PdfView(Context context, MuPDFCore mCore, int pageNum, PdfManager pdfManager) {
        super(context);
        this.mCore = mCore;
        this.pageNum = pageNum;
        this.pdfManager = pdfManager;
        init();
    }

    private void init(AttributeSet attrs) {
//        TypedArray a = getContext().obtainStyledAttributes(attrs,
//                R.styleable.PdfView);

        init();
    }

    private void init() {
        contentView = new PhotoView(getContext());
        contentView.setOrientation(PhotoViewAttacher.HORIZONTAL);
        contentView.setMaximumScale(4);
        contentView.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.main_recycler_background));
        addView(contentView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);

        contentView.setOnPhotoTapListener(new OnPhotoTapListener() {
            @Override
            public void onPhotoTap(ImageView view, float x, float y) {
                x = x * contentView.getDisplayRect().width() + contentView.getDisplayRect().left;
                y = y * contentView.getDisplayRect().height() + contentView.getDisplayRect().top;
                if (!pdfManager.isCheckSign()) {
                    if (mSize != null) {
                        float scale = contentView.getDisplayRect().width() / mSize.x;
                        final float docRelX = (x - contentView.getDisplayRect().left) / scale;
                        final float docRelY = (y - contentView.getDisplayRect().top) / scale;
                        if (mWidgetAreas != null) {
                            boolean hit = false;
                            int currentWidget = 0;
                            RectF rectF = new RectF();
                            for (int i = 0; i < mWidgetAreas.getBoundRect().length && !hit; i++) {
                                if (mWidgetAreas.getBoundRect()[i].contains(docRelX, docRelY)) {
                                    currentWidget = i;
                                    hit = true;
                                    rectF.set(scale * mWidgetAreas.getBoundRect()[i].left + contentView.getDisplayRect().left,
                                            scale * mWidgetAreas.getBoundRect()[i].top + contentView.getDisplayRect().top,
                                            scale * mWidgetAreas.getBoundRect()[i].right + contentView.getDisplayRect().left,
                                            scale * mWidgetAreas.getBoundRect()[i].bottom + contentView.getDisplayRect().top);
                                    break;
                                }
                            }
                            if (hit) {
                                float[] rect = new float[4];
                                rect[0] = mWidgetAreas.getRealRect()[currentWidget].left;
                                rect[1] = mWidgetAreas.getRealRect()[currentWidget].top;
                                rect[2] = mWidgetAreas.getRealRect()[currentWidget].right;
                                rect[3] = mWidgetAreas.getRealRect()[currentWidget].bottom;
                                SignatureInformation info = mCore.getSignatureInformationByCoordinate(pageNum, rect);
                                if (info != null) {
                                    ElectronicSignResultActivity.open(getContext(), info);
                                }
                            }
                        }
                    }
                } else {
                    Toast.makeText(getContext(), "正在验签中，请稍后再试", Toast.LENGTH_SHORT).show();
                }
            }
        });

        contentView.setOnViewDownListener(new OnViewListener() {
            @Override
            public void onViewDown(View view, float x, float y) {
                if (!pdfManager.isCheckSign() && mSize != null && mWidgetAreas != null) {
                    RectF rectF = new RectF();
                    boolean hit = false;
                    float scale = contentView.getDisplayRect().width() / mSize.x;
                    final float docRelX = (x - contentView.getDisplayRect().left) / scale;
                    final float docRelY = (y - contentView.getDisplayRect().top) / scale;
                    for (int i = 0; i < mWidgetAreas.getBoundRect().length; i++)
                        if (mWidgetAreas.getBoundRect()[i].contains(docRelX, docRelY)) {
                            hit = true;
                            mX = x;
                            mY = y;
                            rectF.set(scale * mWidgetAreas.getBoundRect()[i].left + contentView.getDisplayRect().left,
                                    scale * mWidgetAreas.getBoundRect()[i].top + contentView.getDisplayRect().top,
                                    scale * mWidgetAreas.getBoundRect()[i].right + contentView.getDisplayRect().left,
                                    scale * mWidgetAreas.getBoundRect()[i].bottom + contentView.getDisplayRect().top);
                            break;
                        }
                    if (hit) {
                        touchView.setTranslationX(rectF.left);
                        touchView.setTranslationY(rectF.top);
                        touchView.setScaleX(rectF.width());
                        touchView.setScaleY(rectF.height());
                        touchView.setVisibility(VISIBLE);
                    }
                }
                isTouch = true;
            }

            @Override
            public void onViewUp(float x, float y) {
                touchView.setVisibility(GONE);
                isTouch = true;
                if (isDelayMessage) {
                    handler.removeMessages(0);
                    handler.sendEmptyMessageDelayed(0, 400);
                    isDelayMessage = false;
                }
            }

            @Override
            public void onViewMove(MotionEvent ev) {
                float dx = Math.abs(ev.getX() - mX);
                float dy = Math.abs(ev.getY() - mY);
                if (dx >= 16 && dy >= 16) {
                    touchView.setVisibility(GONE);
                }
            }

            @Override
            public void onPointerDonw(MotionEvent ev) {
                touchView.setVisibility(GONE);
            }
        });

        contentView.setOnMatrixChangeListener(new OnMatrixChangedListener() {
            @Override
            public void onMatrixChanged(RectF rect) {
                if (delayInfo != null) {
                    addSign(delayInfo);
                    delayInfo = null;
                }
                if (lastRect.width() != 0) {
                    boolean isScale = false;
                    if (rect.width() != lastRect.width()) {
                        doScale(rect);
                        isScale = true;
                    }
                    doTranslate(isScale, rect);
                    if (lastRect.width() > 0 && (Math.abs(lastRect.width() - rect.width()) > 1 || Math.abs(lastRect.height() - rect.height()) > 1
                            || Math.abs(lastRect.left - rect.left) > 1 || Math.abs(lastRect.top - rect.top) > 1)) {
                        qualityView.setVisibility(GONE);
                        qualityTag++;
                        if (isTouch) {
                            isDelayMessage = true;
                        } else {
                            handler.removeMessages(0);
                            handler.sendEmptyMessageDelayed(0, 400);
                        }
                    }
                }
                lastRect.set(rect.left, rect.top, rect.right, rect.bottom);
            }
        });

        qualityView = new ImageView(getContext());
        addView(qualityView);
        qualityView.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
            }
        });

        touchView = new View(getContext());
        LayoutParams tParams = new LayoutParams(1, 1);
        touchView.setLayoutParams(tParams);
        touchView.setBackgroundColor(Color.parseColor("#4DFFE3B2"));
        touchView.setPivotX(0);
        touchView.setPivotY(0);
        touchView.setVisibility(VISIBLE);
        addView(touchView);
        getSigntureAreas();
    }

    private void doScale(RectF rect) {
        float scale = rect.width() / lastRect.width();
        for (ImageView view : vaildSigns.values()) {
            view.setScaleX(scale * view.getScaleX());
            view.setScaleY(scale * view.getScaleY());
        }
    }

    private void doTranslate(boolean isScale, RectF rect) {
        float xDis = rect.left - lastRect.left;
        float yDis = rect.top - lastRect.top;
        float scale = rect.width() / lastRect.width();
        for (ImageView view : vaildSigns.values()) {
            if (isScale) {
                view.setTranslationX(rect.left - (lastRect.left - view.getX()) * scale);
                view.setTranslationY(rect.top - (lastRect.top - view.getY()) * scale);
            } else {
                if (xDis != 0) {
                    view.setTranslationX(view.getX() + xDis);
                }
                if (yDis != 0) {
                    view.setTranslationY(view.getY() + yDis);
                }
            }
        }
    }

    private HandSignInfo delayInfo = null;

    public void addSign(HandSignInfo info) {
        if (info == null) {
            return;
        } else {
            if (contentView.getDisplayRect().width() == 0 && contentView.getDisplayRect().height() == 0) {
                delayInfo = info;
                return;
            }
            RectF cRectf = contentView.getDisplayRect();
            for (Map.Entry<String, VailLogoItem> entry : info.getLogos().entrySet()) {
                if (vaildWidth == 0 && vaildHeight == 0) {
                    Bitmap bitmap = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.shuiyinyangshi_wuxiao);
                    vaildWidth = bitmap.getWidth();
                    vaildHeight = bitmap.getHeight();
                }

                ImageView view = new ImageView(getContext());
                view.setScaleType(ImageView.ScaleType.CENTER);
                view.setImageResource(R.drawable.shuiyinyangshi_wuxiao);
                LayoutParams params = new LayoutParams(vaildWidth, vaildHeight);
                view.setLayoutParams(params);
                view.setPivotX(0);
                view.setPivotY(0);

                final float scale = cRectf.width() / entry.getValue().getParent().width();
                float scaleY = cRectf.height() / entry.getValue().getParent().height();

                float sScaleX = entry.getValue().getRectF().width() / (float) vaildWidth * scale;
                float sScaleY = entry.getValue().getRectF().height() / (float) vaildHeight * scaleY;
                view.setScaleX(sScaleX * disDicount);
                view.setScaleY(sScaleY * disDicount);
                view.setTranslationX(entry.getValue().getRectF().left * scale + vaildWidth * sScaleX * 0.1f / 2 + cRectf.left);
                view.setTranslationY(entry.getValue().getRectF().top * scaleY + vaildHeight * sScaleY * 0.1f / 2 + cRectf.top);
                view.setOnTouchListener(new OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        return false;
                    }
                });
                addView(view);
                vaildSigns.put(entry.getKey(), view);
            }
        }
    }

    public void addVaildLogo(VailResult item) {
        if (vaildWidth == 0 && vaildHeight == 0) {
            Bitmap bitmap = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.shuiyinyangshi_wuxiao);
            vaildWidth = bitmap.getWidth();
            vaildHeight = bitmap.getHeight();
        }

        ImageView view = new ImageView(getContext());
        view.setScaleType(ImageView.ScaleType.CENTER);
        view.setImageResource(R.drawable.shuiyinyangshi_wuxiao);
        LayoutParams params = new LayoutParams(vaildWidth, vaildHeight);
        view.setLayoutParams(params);
        view.setPivotX(0);
        view.setPivotY(0);
        RectF rectF = contentView.getDisplayRect();
        final float scale = rectF.width() / item.parent.width();
        float scaleY = rectF.height() / item.parent.height();

        float sScaleX = item.rectF.width() / (float) vaildWidth;
        float sScaleY = item.rectF.height() / (float) vaildHeight;
        view.setScaleX(sScaleX * scale * disDicount);
        view.setScaleY(sScaleY * scaleY * disDicount);

        view.setTranslationX(item.rectF.left * scale + vaildWidth * sScaleX * scale * 0.1f / 2 + rectF.left);
        view.setTranslationY(item.rectF.top * scaleY + vaildHeight * sScaleY * scaleY * 0.1f / 2 + rectF.top);

        addView(view);
        vaildSigns.put(item.key, view);
    }

    private void getSigntureAreas() {
        AsyncTask<Void, Void, WidgetArea> mLoadWidgetAreas = new AsyncTask<Void, Void, WidgetArea>() {
            @Override
            protected WidgetArea doInBackground(Void... arg0) {
                return mCore.getWidgetAreas(pageNum);
            }

            @Override
            protected void onPostExecute(WidgetArea result) {
                mWidgetAreas = result;
            }
        };

        mLoadWidgetAreas.execute();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        handler.removeCallbacksAndMessages(null);
    }

    public PhotoView getContentView() {
        return contentView;
    }

    public PointF getSize() {
        return mSize;
    }

    public void setSize(PointF mSize) {
        this.mSize = mSize;
    }

    public boolean isRenderFinish() {
        return renderFinish;
    }

    public void setRenderFinish(boolean renderFinish) {
        this.renderFinish = renderFinish;
    }

    public ChangeListener getChangeListener() {
        return changeListener;
    }

    public void setChangeListener(ChangeListener changeListener) {
        this.changeListener = changeListener;
    }

    public int getPageNum() {
        return pageNum;
    }

    public void setQualityBitmap(Bitmap bitmap, float x, float y) {
        qualityView.setImageBitmap(bitmap);
        LayoutParams params = (LayoutParams) qualityView.getLayoutParams();
        params.width = bitmap.getWidth();
        params.height = bitmap.getHeight();
        qualityView.setLayoutParams(params);
        qualityView.setX(x);
        qualityView.setY(y);
        qualityView.setVisibility(VISIBLE);
    }

    public int getQualityTag() {
        return qualityTag;
    }

    public interface ChangeListener {
        void onTouchUp(View view, int tag);
    }
}
