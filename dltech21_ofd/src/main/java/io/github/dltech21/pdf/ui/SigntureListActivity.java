package io.github.dltech21.pdf.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.artifex.mupdf.viewer.MuPDFCore;

import io.github.dltech21.ofd.R;
import io.github.dltech21.pdf.model.SignatureInformation;

import java.util.ArrayList;

public class SigntureListActivity extends AppCompatActivity {
    public final static int RETURN_LIST = 1;
    private RecyclerView rvShow;
    private SigntureAdapter adapter;
    private MuPDFCore signCore;

    public static void open(Context mContext, ArrayList<SignatureInformation> list, String path) {
        Intent intent = new Intent(mContext, SigntureListActivity.class)
                .putExtra("list", list).putExtra("path", path);
        ((Activity) mContext).startActivityForResult(intent, RETURN_LIST);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signture_list);

        initView();
    }

    public void initView() {
        initToolbar();
        rvShow = (RecyclerView) findViewById(R.id.rv_show);
        rvShow.setLayoutManager(new LinearLayoutManager(SigntureListActivity.this));
        initData();
    }

    private void initData() {
        ArrayList<SignatureInformation> list = (ArrayList<SignatureInformation>) getIntent().getSerializableExtra("list");
        String path = getIntent().getStringExtra("path");
        signCore = new MuPDFCore(path);
        if (signCore != null) {
            setData(list);
        }
    }

    private void setData(ArrayList<SignatureInformation> result) {
        adapter = new SigntureAdapter(SigntureListActivity.this, result);
        adapter.setListener(new SigntureAdapter.ItemListener() {
            @Override
            public void onItemClick(int position) {
                signCore.getSignatureInformationByCoordinate(adapter.getList().get(position));
                adapter.notifyDataSetChanged();
                ElectronicSignResultActivity.open(SigntureListActivity.this, adapter.getList().get(position));
            }
        });
        rvShow.setAdapter(adapter);
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
}
