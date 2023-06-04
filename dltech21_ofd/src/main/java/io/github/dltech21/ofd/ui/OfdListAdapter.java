package io.github.dltech21.ofd.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.recyclerview.widget.RecyclerView;

import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.ArrayList;
import java.util.List;

import io.github.dltech21.ofd.ImageLoader;
import io.github.dltech21.ofd.OfdManager;
import io.github.dltech21.ofd.R;
import io.github.dltech21.ofd.ViewUtils;
import io.github.dltech21.ofd.listener.OnOfdResultListener;


/**
 * Created by Donal on 2017/6/19.
 */

public class OfdListAdapter extends RecyclerView.Adapter<OfdListAdapter.ViewHolder> {

    private Context mContext;
    private int currentPosition;
    private List<Integer> pages;
    private OfdManager ofdManager;
    private int count;

    public OfdListAdapter(Context mContext, int position, OfdManager ofdManager, int count) {
        this.mContext = mContext;
        this.ofdManager = ofdManager;
        this.currentPosition = position;
        this.count = count;
        pages = new ArrayList<>();
    }

    public List<Integer> getPages() {
        return pages;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewHolder holder = null;
        holder = new ViewHolder(LayoutInflater.from(
                mContext).inflate(R.layout.item_ofd, parent,
                false));
        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        ofdManager.putTask(mContext, position, new OnOfdResultListener() {
            @Override
            public void onRenderFinish(String path) {
                ImageLoader.loadPhotoByImageLoader(mContext, ImageLoader.TYPE_FILE, path, 0, holder.imageView, new SimpleImageLoadingListener() {
                    @Override
                    public void onLoadingComplete(String imageUri, View v, Bitmap loadedImage) {
                        super.onLoadingComplete(imageUri, v, loadedImage);
                    }
                });
            }
        });
        if (currentPosition == position) {
            holder.pdfLl.setSelected(true);
            holder.pdfLl.setVisibility(View.VISIBLE);
        } else {
            holder.pdfLl.setSelected(false);
            holder.pdfLl.setVisibility(View.INVISIBLE);
        }
        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((OfdListActivity) mContext).finishByposition(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return ofdManager.getPageCount();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView imageView;
        private View pdfLl;

        public ViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.iv_thumb);
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) imageView.getLayoutParams();
            DisplayMetrics mDisplayMetrics = mContext.getApplicationContext().getResources()
                    .getDisplayMetrics();
            int width = (mDisplayMetrics.widthPixels - ViewUtils.dip2px(mContext, 20) * 2 - ViewUtils.dip2px(mContext, 48) * 2) / 3;
            layoutParams.width = width;
            layoutParams.height = (int) (width * 94.0 / 65);
            imageView.setLayoutParams(layoutParams);
            pdfLl = itemView.findViewById(R.id.pdf_ll);
            layoutParams = (RelativeLayout.LayoutParams) pdfLl.getLayoutParams();
            layoutParams.height = (int) (width * 94.0 / 65);
            pdfLl.setLayoutParams(layoutParams);
        }

    }
}
