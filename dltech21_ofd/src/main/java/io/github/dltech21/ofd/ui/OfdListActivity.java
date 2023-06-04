package io.github.dltech21.ofd.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.AbsListView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;

import io.github.dltech21.ofd.OfdManager;
import io.github.dltech21.ofd.R;
import io.github.dltech21.ofd.ViewUtils;
import io.github.dltech21.view.RecyclerViewGridDivider;

public class OfdListActivity extends AppCompatActivity {
    public final static int REQ_CODE = 3400;
    private RecyclerView rvPhoto;
    private OfdListAdapter ofdListAdapter;
    private int currentPosition;
    private String path;
    private String password;
    private OfdManager ofdManager;

    public static void open(Activity context, int position, String path, int count, String password) {
        Intent intent = new Intent(context, OfdListActivity.class);
        intent.putExtra("position", position);
        intent.putExtra("path", path);
        intent.putExtra("count", count);
        intent.putExtra("password", password);
        context.startActivityForResult(intent, REQ_CODE);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ofd_thumb);
        currentPosition = getIntent().getIntExtra("position", 0);
        path = getIntent().getStringExtra("path");
        password = getIntent().getStringExtra("password");
        ofdManager = new OfdManager();
        if (path != null) {
            try {
                ofdManager.init(this, new File(path));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        rvPhoto = findViewById(R.id.rv_photo);
        rvPhoto.setLayoutManager(new GridLayoutManager(this, 3));
        rvPhoto.addItemDecoration(new RecyclerViewGridDivider(3, ViewUtils.dip2px(this, 48), ViewUtils.dip2px(this, 15)));
        ofdListAdapter = new OfdListAdapter(this, currentPosition, ofdManager, getIntent().getIntExtra("count", 0));
        rvPhoto.setAdapter(ofdListAdapter);
        rvPhoto.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    ofdManager.setType(ofdManager.TYPE_FIRST);
                }
            }
        });

        RecyclerView.SmoothScroller smoothScroller = new LinearSmoothScroller(this) {
            @Override
            protected int getVerticalSnapPreference() {
                return LinearSmoothScroller.SNAP_TO_START;
            }
        };
        smoothScroller.setTargetPosition(currentPosition);
        rvPhoto.getLayoutManager().startSmoothScroll(smoothScroller);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ofdManager.close();
    }


    public void finishByposition(int position) {
        setResult(Activity.RESULT_OK, new Intent().putExtra("position", position));
        finish();
    }

    @Override
    protected void onStop() {
        super.onStop();
        ofdManager.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        ofdManager.onResume();
    }
}
