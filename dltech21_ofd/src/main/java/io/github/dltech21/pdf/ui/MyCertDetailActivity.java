package io.github.dltech21.pdf.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.blankj.utilcode.util.StringUtils;

import io.github.dltech21.ofd.R;
import io.github.dltech21.pdf.model.SignatureInformation;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Donal
 * @date 2017/4/20
 */

public class MyCertDetailActivity extends AppCompatActivity {

    private TextView tvName;
    private TextView tvAuthor;
    private TextView tvSerial;
    private TextView tvTime;
    private TextView tvSignAlgorithm;

    private SignatureInformation info;

    public static void open(Context context, SignatureInformation info) {
        context.startActivity(new Intent(context, MyCertDetailActivity.class).putExtra("info", info));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mycert_info);
        info = (SignatureInformation) getIntent().getSerializableExtra("info");
        initView();
        initData(info);
    }

    private void initData(SignatureInformation info) {
        tvAuthor.setText(info.getIssuer() != null ? info.getIssuer() : "");
        tvSerial.setText(info.getSerial() != null ? info.getSerial() : "");
        tvName.setText(info.getSigner() != null ? info.getSigner() : "");
        tvTime.setText(String.format(getString(R.string.text_data_range), formData(info.getStartTime()), formData(info.getEndTime())));
        if (!StringUtils.isEmpty(info.getAlg())) {
            tvSignAlgorithm.setText(info.getAlg());
            findViewById(R.id.ll_signAlgorithm).setVisibility(View.VISIBLE);
            if (info.getAlg().equals("1.2.156.10197.1.501")) {
                tvSignAlgorithm.setText("SM2");
            } else if (info.getAlg().toLowerCase().contains("rsa")) {
                tvSignAlgorithm.setText("RSA");
            }
        }
        if (!StringUtils.isEmpty(info.getIssuer())) {
            findViewById(R.id.yinzhang_iv).setVisibility(info.getIssuer().toLowerCase().contains("cinfsec") ? View.VISIBLE : View.INVISIBLE);
        }
    }

    private String formData(String time) {
        if (StringUtils.isEmpty(time)) {
            return "";
        }
        time = time.replaceAll(" ", "");
        SimpleDateFormat bFormat = new SimpleDateFormat("yyyy-MM-dd:HH:mm:ss");
        SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd");
        try {
            if (StringUtils.isEmpty(time)) {
                return "";
            }
            Date d = bFormat.parse(time);
            return format.format(d);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return time;
    }

    public void initView() {
        initToolbar();
        tvName = findViewById(R.id.tv_name);
        tvAuthor = findViewById(R.id.tv_author);
        tvSerial = findViewById(R.id.tv_serial);
        tvTime = findViewById(R.id.tv_time);
        tvSignAlgorithm = findViewById(R.id.tv_signAlgorithm);
    }

    private void initToolbar() {
        try {
//            findViewById(R.id.toolbar).setBackgroundColor(Color.parseColor(SdkPdfManager.barColor));
//            ((TextView) findViewById(R.id.tv_title)).setTextColor(Color.parseColor(SdkPdfManager.titleColor));
            findViewById(R.id.layoutback).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
