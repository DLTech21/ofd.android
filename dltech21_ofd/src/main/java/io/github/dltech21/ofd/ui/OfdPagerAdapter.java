package io.github.dltech21.ofd.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PointF;
import android.os.AsyncTask;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;

import androidx.viewpager.widget.PagerAdapter;

import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import io.github.dltech21.ofd.ImageLoader;
import io.github.dltech21.ofd.OfdManager;
import io.github.dltech21.ofd.listener.OfdTaskListener;
import io.github.dltech21.ofd.listener.OnOfdResultListener;
import io.github.dltech21.ofd.view.OfdView;


public class OfdPagerAdapter extends PagerAdapter {
    private OfdView currentView;
//    private SparseArray<HandSignInfo> signInfo = new SparseArray<>();
    private final SparseArray<PointF> mPageSizes = new SparseArray<>();

    private Context context;
    private OfdManager ofdManager;

    public OfdPagerAdapter(Context context, OfdManager ofdManager) {
        this.context = context;
        this.ofdManager = ofdManager;
    }

    @Override
    public int getCount() {
        return ofdManager.getPageCount();
    }

    @Override
    public View instantiateItem(final ViewGroup container, final int position) {
        final OfdView view = new OfdView(container.getContext(), position, ofdManager);
        view.setChangeListener(new OfdView.ChangeListener() {
            @Override
            public void onTouchUp(View view, final int tag) {
                final OfdView pdfView = (OfdView) view;
//                Point requestSize = new Point((int) pdfView.getContentView().getDisplayRect().width(), (int) pdfView.getContentView().getDisplayRect().height());
//                final Rect drawSize = new Rect((int) pdfView.getContentView().getDisplayRect().left,
//                        (int) pdfView.getContentView().getDisplayRect().top,
//                        (int) pdfView.getContentView().getDisplayRect().right,
//                        (int) pdfView.getContentView().getDisplayRect().bottom);
//                Rect d = new Rect(0, 0, pdfView.getWidth(), pdfView.getHeight());
//                if (!d.intersect(drawSize)) {
//                    return;
//                }
//                d.offset(-drawSize.left, -drawSize.top);
                ofdManager.startOfdDecode(pdfView.getPageNum(), new OfdTaskListener() {
                    @Override
                    public void onBefor() {

                    }

                    @Override
                    public void onComplete(String path) {
//                        if (tag == pdfView.getQualityTag()) {
//                            pdfView.setQualityBitmap(bitmap, Math.max(drawSize.left, 0), Math.max(drawSize.top, 0));
//                        }
                    }
                });
            }
        });
        container.addView(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        ofdManager.putTask(context, position, new OnOfdResultListener() {
            @Override
            public void onRenderFinish(String path) {
                if (view.getParent() != null) {
//                    if (bitmap != null) {
//                        view.getContentView().setImageBitmap(bitmap);
//                        view.setRenderFinish(true);
//                        HandSignInfo info = signInfo.get(position);
//                        if (info != null) {
//                            view.addSign(info);
//                        }
//                    } else {
                        ImageLoader.loadPhotoByImageLoader(context, ImageLoader.TYPE_FILE, path, 0, view.getContentView(), new SimpleImageLoadingListener() {
                            @Override
                            public void onLoadingComplete(String imageUri, View v, Bitmap loadedImage) {
                                super.onLoadingComplete(imageUri, v, loadedImage);
                                view.setRenderFinish(true);
//                                HandSignInfo info = signInfo.get(position);
//                                if (info != null) {
//                                    view.addSign(info);
//                                }
                            }
                        });
//                    }
                }
            }
        });
        PointF pageSize = mPageSizes.get(position);
        if (pageSize == null) {
            AsyncTask<Void, Void, PointF> sizingTask = new AsyncTask<Void, Void, PointF>() {
                @Override
                protected PointF doInBackground(Void... arg0) {
                    return ofdManager.getPageSize(position);
                }

                @Override
                protected void onPostExecute(PointF result) {
                    super.onPostExecute(result);
                    // We now know the page size
                    Log.e("result", result.x + ",,,,,," + result.y);
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
        currentView = (OfdView) object;
    }

    public OfdView getPrimaryItem() {
        return currentView;
    }

//    public void addVailLogo(ArrayList<VailResult> results) {
//        for (VailResult item : results) {
//            HandSignInfo info = signInfo.get(item.page);
//            if (info == null) {
//                info = new HandSignInfo();
//                signInfo.put(item.page, info);
//            }
//            info.getLogos().put(item.key, new VailLogoItem(item.parent, item.rectF));
//        }
//    }
//
//    public void addVailLogo(VailResult item) {
//        HandSignInfo info = signInfo.get(item.page);
//        if (info == null) {
//            info = new HandSignInfo();
//            signInfo.put(item.page, info);
//        }
//        info.getLogos().put(item.key, new VailLogoItem(item.parent, item.rectF));
//    }
}
