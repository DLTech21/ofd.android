package io.github.dltech21.pdf.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
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
 * Created by donal on 2018/1/30.
 */

public class ElectronicSignResultActivity extends AppCompatActivity {
    private SignatureInformation info;

    private TextView tvSigner, tvSignTime, tvConclusion, tvFileStatus;
    private ImageView ivFileStatus;

    public static void open(Context mContext, SignatureInformation info) {
        Intent intent = new Intent(mContext, ElectronicSignResultActivity.class).putExtra("info", info);
        mContext.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_electrosign_result);
        info = (SignatureInformation) getIntent().getSerializableExtra("info");
        initToolbar();
        initView();
    }

    public void initView() {
        tvSigner = findViewById(R.id.tv_signer);
        tvSignTime = findViewById(R.id.tv_sign_time);
        tvConclusion = findViewById(R.id.tv_conclusion);
        tvFileStatus = findViewById(R.id.tv_file_status);
        ivFileStatus = findViewById(R.id.iv_file_status);
        if (info != null) {
            tvSigner.setText(info.getSigner() != null ? info.getSigner() : "");
//            StringBuffer sb = new StringBuffer();
            if (!StringUtils.isEmpty(info.getSignTime())) {
//                int start = info.getSignTime().indexOf(":") + 1;
//                sb.append(info.getSignTime().substring(start, start + 4)).append(".")
//                        .append(info.getSignTime().substring(start + 4, start + 6)).append(".")
//                        .append(info.getSignTime().substring(start + 6, start + 8)).append(" ")
//                        .append(info.getSignTime().substring(start + 8, start + 10)).append(":")
//                        .append(info.getSignTime().substring(start + 10, start + 12)).append(":")
//                        .append(info.getSignTime().substring(start + 12, start + 14));
                tvSignTime.setText(info.getSignTime());
            }
            if (info.isSignatureValid()) {
                tvFileStatus.setText(getString(R.string.text_signture_stauts_valid));
                ivFileStatus.setImageResource(R.drawable.youxiao);
                String t = null;
                if (!StringUtils.isEmpty(info.getTsTime())) {
                    t = getTTime(info.getTsTime());
                    tvConclusion.setText(String.format(getString(R.string.text_signture_valid_detail), t, t));
                } else {
                    tvConclusion.setText(getString(R.string.text_signture_valid_detail_s));
                }
            } else {
                tvFileStatus.setText(getString(R.string.text_signture_stauts_unvalid));
                ivFileStatus.setImageResource(R.drawable.wuxiao);
                tvConclusion.setText(getString(R.string.text_signture_unvalid_detail));
            }
            findViewById(R.id.bt_check_cert).setEnabled(isCertCheckEnable());
            findViewById(R.id.bt_check_cert).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MyCertDetailActivity.open(ElectronicSignResultActivity.this, info);
                }
            });
        }
    }

    private boolean isCertCheckEnable() {
        if (info.isSignatureValid()) {
            return true;
        }
        if (StringUtils.isEmpty(info.getSubject()) && StringUtils.isEmpty(info.getIssuer()) && StringUtils.isEmpty(info.getSerial())) {
            return false;
        }
        return true;
    }

    private String getTTime(String tsTime) {
        StringBuffer sb = new StringBuffer();
        sb.append(tsTime.substring(0, 4)).append(".")
                .append(tsTime.substring(4, 6)).append(".")
                .append(tsTime.substring(6, 8)).append(" ")
                .append(tsTime.substring(8, 10)).append(":")
                .append(tsTime.substring(10, 12)).append(":")
                .append(tsTime.substring(12, 14));
        SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
        try {
            Date d = format.parse(sb.toString());
            long l = d.getTime();
            d = new Date(l + 8 * 1000 * 60 * 60);
            return format.format(d);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return sb.toString();
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
