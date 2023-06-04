package io.github.dltech21.pdf.ui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AbsListView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;

import io.github.dltech21.ofd.R;
import io.github.dltech21.ofd.ViewUtils;
import io.github.dltech21.pdf.PdfManager;
import io.github.dltech21.view.RecyclerViewGridDivider;

import java.io.File;

/**
 * Created by Donal on 2017/6/19.
 */

public class PdfListActivity extends AppCompatActivity {
    public final static int REQ_CODE = 3400;
    private RecyclerView rvPhoto;
    private PdfListAdapter mPdfListAdapter;
    private int currentPosition;
    private String path;
    private String password;
    private PdfManager pdfManager;

    public static void open(Activity context, int position, String path, int count, String password) {
        Intent intent = new Intent(context, PdfListActivity.class);
        intent.putExtra("position", position);
        intent.putExtra("path", path);
        intent.putExtra("count", count);
        intent.putExtra("password", password);
        context.startActivityForResult(intent, REQ_CODE);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf_thumb);
        currentPosition = getIntent().getIntExtra("position", 0);
        path = getIntent().getStringExtra("path");
        password = getIntent().getStringExtra("password");
        pdfManager = new PdfManager();
        if (path != null) {
            try {
                pdfManager.init(PdfListActivity.this, new File(path), password);
                initView();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        pdfManager.close();
    }

    public void initView() {
        initToolbar();
        rvPhoto = findViewById(R.id.rv_photo);
        rvPhoto.setLayoutManager(new GridLayoutManager(PdfListActivity.this, 3));
        rvPhoto.addItemDecoration(new RecyclerViewGridDivider(3, ViewUtils.dip2px(PdfListActivity.this, 48), ViewUtils.dip2px(PdfListActivity.this, 15)));
        mPdfListAdapter = new PdfListAdapter(PdfListActivity.this, currentPosition, pdfManager, getIntent().getIntExtra("count", 0));
        rvPhoto.setAdapter(mPdfListAdapter);
        rvPhoto.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    pdfManager.setType(PdfManager.TYPE_FIRST);
                }
            }
        });

        RecyclerView.SmoothScroller smoothScroller = new LinearSmoothScroller(PdfListActivity.this) {
            @Override
            protected int getVerticalSnapPreference() {
                return LinearSmoothScroller.SNAP_TO_START;
            }
        };
        smoothScroller.setTargetPosition(currentPosition);
        rvPhoto.getLayoutManager().startSmoothScroll(smoothScroller);
    }

    private void initToolbar() {
        try {
//            findViewById(R.id.toolbar).setBackgroundColor(Color.parseColor(SdkPdfManager.barColor));
//            ((TextView) findViewById(R.id.tv_title)).setTextColor(Color.parseColor(SdkPdfManager.titleColor));
        } catch (Exception e) {
            e.printStackTrace();
        }
        findViewById(R.id.rl_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    public void finishByposition(int position) {
        setResult(RESULT_OK, new Intent().putExtra("position", position));
        finish();
    }

    @Override
    protected void onStop() {
        super.onStop();
        pdfManager.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        pdfManager.onResume();
    }

}
