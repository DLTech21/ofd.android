package io.github.dltech21.pdf.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.recyclerview.widget.RecyclerView;

import io.github.dltech21.ofd.ImageLoader;
import io.github.dltech21.ofd.R;
import io.github.dltech21.ofd.ViewUtils;
import io.github.dltech21.pdf.PdfManager;
import io.github.dltech21.pdf.listener.OnPdfResultListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Donal on 2017/6/19.
 */

public class PdfListAdapter extends RecyclerView.Adapter<PdfListAdapter.ViewHolder> {

    private Context mContext;
    private int currentPosition;
    private List<Integer> pages;
    private PdfManager pdfManager;
    private int count;

    public PdfListAdapter(Context mContext, int position, PdfManager pdfManager, int count) {
        this.mContext = mContext;
        this.pdfManager = pdfManager;
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
                mContext).inflate(R.layout.item_pdf, parent,
                false));
        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
//        holder.imageView.setImageResource(R.drawable.pdf_img);
        pdfManager.putTask(mContext, position, new OnPdfResultListener() {
            @Override
            public void onRenderFinish(String path, Bitmap bitmap) {
                if (bitmap != null) {
                    holder.imageView.setImageBitmap(bitmap);
                } else {
                    ImageLoader.loadPhotoByImageLoader(mContext, ImageLoader.TYPE_FILE, path, 0, holder.imageView, new SimpleImageLoadingListener() {
                        @Override
                        public void onLoadingComplete(String imageUri, View v, Bitmap loadedImage) {
                            super.onLoadingComplete(imageUri, v, loadedImage);
                        }
                    });
                }
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
                ((PdfListActivity) mContext).finishByposition(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return pdfManager.getPageCount();
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
