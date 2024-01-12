package io.github.dltech21.pdf.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;

import androidx.viewpager.widget.PagerAdapter;

import io.github.dltech21.ofd.ImageLoader;
import io.github.dltech21.pdf.PdfManager;
import io.github.dltech21.pdf.listener.OnPdfResultListener;
import io.github.dltech21.pdf.listener.PdfTaskListener;
import io.github.dltech21.pdf.model.HandSignInfo;
import io.github.dltech21.pdf.model.VailLogoItem;
import io.github.dltech21.pdf.model.VailResult;
import io.github.dltech21.pdf.view.PdfView;

import com.artifex.mupdf.viewer.MuPDFCore;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.ArrayList;


public class PdfPagerAdapter extends PagerAdapter {
    private PdfView currentView;
    private SparseArray<HandSignInfo> signInfo = new SparseArray<>();
    private final SparseArray<PointF> mPageSizes = new SparseArray<>();

    private Context context;
    private final MuPDFCore mCore;
    private PdfManager pdfManager;

    public PdfPagerAdapter(Context context, String path, PdfManager pdfManager) {
        this.context = context;
        this.pdfManager = pdfManager;
        mCore = pdfManager.getCore();
    }

    @Override
    public int getCount() {
        return pdfManager.getPageCount();
    }

    @Override
    public View instantiateItem(final ViewGroup container, final int position) {
        final PdfView view = new PdfView(container.getContext(), mCore, position, pdfManager);
        view.setChangeListener(new PdfView.ChangeListener() {
            @Override
            public void onTouchUp(View view, final int tag) {
                final PdfView pdfView = (PdfView) view;
                Point requestSize = new Point((int) pdfView.getContentView().getDisplayRect().width(), (int) pdfView.getContentView().getDisplayRect().height());
                final Rect drawSize = new Rect((int) pdfView.getContentView().getDisplayRect().left,
                        (int) pdfView.getContentView().getDisplayRect().top,
                        (int) pdfView.getContentView().getDisplayRect().right,
                        (int) pdfView.getContentView().getDisplayRect().bottom);
                Rect d = new Rect(0, 0, pdfView.getWidth(), pdfView.getHeight());
                if (!d.intersect(drawSize)) {
                    return;
                }
                d.offset(-drawSize.left, -drawSize.top);
                pdfManager.startPdfDecode(pdfView.getPageNum(), requestSize, d, new PdfTaskListener() {
                    @Override
                    public void onBefor() {

                    }

                    @Override
                    public void onComplete(Bitmap bitmap) {
                        if (tag == pdfView.getQualityTag()) {
                            pdfView.setQualityBitmap(bitmap, Math.max(drawSize.left, 0), Math.max(drawSize.top, 0));
                        }
                    }
                });
            }
        });
        container.addView(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        pdfManager.putTask(context, position, new OnPdfResultListener() {
            @Override
            public void onRenderFinish(String path, Bitmap bitmap) {
                if (view.getParent() != null) {
                    if (bitmap != null) {
                        view.getContentView().setImageBitmap(bitmap);
                        view.setRenderFinish(true);
                        HandSignInfo info = signInfo.get(position);
                        if (info != null) {
                            view.addSign(info);
                        }
                    } else {
                        ImageLoader.loadPhotoByImageLoader(context, ImageLoader.TYPE_FILE, path, 0, view.getContentView(), new SimpleImageLoadingListener() {
                            @Override
                            public void onLoadingComplete(String imageUri, View v, Bitmap loadedImage) {
                                super.onLoadingComplete(imageUri, v, loadedImage);
                                view.setRenderFinish(true);
                                HandSignInfo info = signInfo.get(position);
                                if (info != null) {
                                    view.addSign(info);
                                }
                            }
                        });
                    }
                }
            }
        });
        PointF pageSize = mPageSizes.get(position);
        if (pageSize == null) {
            AsyncTask<Void, Void, PointF> sizingTask = new AsyncTask<Void, Void, PointF>() {
                @Override
                protected PointF doInBackground(Void... arg0) {
                    if (mCore != null) {
                        mCore.getPageSize(position);
                    }
                    return  new PointF(0, 0);
                }

                @Override
                protected void onPostExecute(PointF result) {
                    super.onPostExecute(result);
                    // We now know the page size
//                    Log.e("result", result.x + ",,,,,," + result.y);
                    mPageSizes.put(position, result);
                    view.setSize(result);
                }
            };
            sizingTask.execute((Void) null);
        } else {
            view.setSize(pageSize);
        }
        view.setTag(position);
        return view;
    }


    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
//        cacheViews.add((PdfView) object);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        super.setPrimaryItem(container, position, object);
        currentView = (PdfView) object;
    }

    public PdfView getPrimaryItem() {
        return currentView;
    }

    public void addVailLogo(ArrayList<VailResult> results) {
        for (VailResult item : results) {
            HandSignInfo info = signInfo.get(item.page);
            if (info == null) {
                info = new HandSignInfo();
                signInfo.put(item.page, info);
            }
            info.getLogos().put(item.key, new VailLogoItem(item.parent, item.rectF));
        }
    }

    public void addVailLogo(VailResult item) {
        HandSignInfo info = signInfo.get(item.page);
        if (info == null) {
            info = new HandSignInfo();
            signInfo.put(item.page, info);
        }
        info.getLogos().put(item.key, new VailLogoItem(item.parent, item.rectF));
    }
}
