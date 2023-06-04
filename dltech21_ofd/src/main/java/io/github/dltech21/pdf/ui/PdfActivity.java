package io.github.dltech21.pdf.ui;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.drawable.AnimationDrawable;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.viewpager.widget.ViewPager;

import com.artifex.mupdf.viewer.MuPDFCore;
import com.blankj.utilcode.util.EncryptUtils;

import io.github.dltech21.ofd.R;
import io.github.dltech21.pdf.PdfManager;
import io.github.dltech21.pdf.model.SignatureInformation;
import io.github.dltech21.pdf.model.VailResult;
import io.github.dltech21.pdf.view.PdfView;
import io.github.dltech21.view.HackyViewPager;

import java.io.File;
import java.util.ArrayList;

public class PdfActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback {

    private PdfPagerAdapter adapter;
    private HackyViewPager viewPager;

    private TextView tvCurrent;
    private TextView tvAll;
    private ImageView iv_status;
    private TextView tv_status;

    private MuPDFCore signCore;
    private ArrayList<SignatureInformation> signtures = null;
    private PdfManager pdfManager;
    private String filePath;
    private String password;

    private AnimationDrawable checkDrawable;
    private boolean isChecking = true;
    private int currentPosition;

    private boolean isOpenPdf = false;
    private boolean canCheckSign = true;

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1 && !isFinishing()) {
                ArrayList<SignatureInformation> signs = (ArrayList<SignatureInformation>) msg.obj;
                isChecking = false;
                signtures = signs;
                checkDrawable.stop();
                pdfManager.setCheckSign(false);
                displayFileValid();
            }
        }
    };

    public static void open(Context mContext, String filePath, String password) {
        Intent intent = new Intent(mContext, PdfActivity.class);
        intent.putExtra("path", filePath);
        intent.putExtra("password", password);
        mContext.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf);

        filePath = getIntent().getStringExtra("path");
        password = getIntent().getStringExtra("password");
        initView();
        pdfManager = new PdfManager();
        if (filePath != null) {
//            boolean isSupport = PermissionUtils.checkPermissions(PdfActivity.this, PermissionUtils.FILE_REQUESTCODE, PermissionUtils.filePermissions);
//            if (isSupport) {
                openPDF();
//            }
        }
    }

    public void initView() {
        initToolbar();
        iv_status = findViewById(R.id.iv_status);
        tv_status = findViewById(R.id.tv_status);
        viewPager = findViewById(R.id.vp_show);

        findViewById(R.id.rl_fenye).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (filePath != null) {
                    PdfListActivity.open(PdfActivity.this, currentPosition, filePath, pdfManager.getPageCount(), password);
                }
            }
        });
        findViewById(R.id.rl_detail).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isChecking) {
                    if (signtures == null || signtures.size() == 0) {
                        Toast.makeText(PdfActivity.this, "该文档没有签名", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    ArrayList<SignatureInformation> current = new ArrayList<SignatureInformation>();
                    current.addAll(signtures);
                    SigntureListActivity.open(PdfActivity.this, current, filePath);
                }
            }
        });
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (pdfManager.getType() == PdfManager.TYPE_LAST) {
                    pdfManager.setType(PdfManager.TYPE_FIRST);
                }

                currentPosition = position;
                tvCurrent.setText((position + 1) + "");
                adapter.getPrimaryItem().callOnClick();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void openPDF() {
        findViewById(R.id.rl_fenye).setVisibility(View.VISIBLE);

        try {
            pdfManager.init(PdfActivity.this, new File(filePath), password);//PdfUtils.getInstance().init(this, file);
            isOpenPdf = true;
            initPdfData();
        } catch (Exception e) {
            e.printStackTrace();
//            if (pdfManager.isNeedPassword() && (password == null || !pdfManager.isCorrectPassowrd())) {
//                showAlertDialog(mContext, "密码错误，请输入正确的密码", getString(R.string.text_sure), new SmartDialogClickListener() {
//                    @Override
//                    public void ok() {
//                        finish();
//                    }
//                });
//            } else {
//                showAlertDialog(mContext, "文件打开失败，请查看原文件", getString(R.string.text_sure), new SmartDialogClickListener() {
//                    @Override
//                    public void ok() {
//                        finish();
//                    }
//                });
//            }
        }
    }

    private void initPdfData() {
        pdfManager.setType(PdfManager.TYPE_LAST);
//        if (!isOpenPdf) {
//            showAlertDialog(mContext, "文件打开失败，请查看原文件", getString(R.string.text_sure), new SmartDialogClickListener() {
//                @Override
//                public void ok() {
//                    finish();
//                }
//            });
//            return;
//        }
        tvAll.setText(" / " + pdfManager.getPageCount());
        tvCurrent.setText("1");
        adapter = new PdfPagerAdapter(this, pdfManager.getSrc().getAbsolutePath(), pdfManager);
        viewPager.setAdapter(adapter);

        checkDrawable = (AnimationDrawable) iv_status.getDrawable();
        checkDrawable.start();
        pdfManager.setCheckSign(true);
        new Thread(new Runnable() {
            @Override
            public void run() {
                ArrayList<SignatureInformation> signs = pdfManager.getCore().getAllSignature();
                int size = signs.size();
                if (size > 0) {
                    if (canCheckSign) {
                        Message msg = Message.obtain();
                        msg.what = 1;
                        msg.obj = signs;
                        mHandler.sendMessageDelayed(msg, 2000);
                    }
                } else {
                    Message msg = new Message();
                    msg.what = 1;
                    msg.obj = signs;
                    mHandler.sendMessage(msg);
                }
            }
        }).start();
    }

    private void displayFileValid() {
        ArrayList<VailResult> results = new ArrayList<>();
        ArrayList<VailResult> current = new ArrayList<>();
        boolean isAllCheck = true;
        boolean isVaild = true;
        for (SignatureInformation info : signtures) {
//            if (!info.isCheck()) {
//                isAllCheck = false;
//            } else {
                if (!info.isSignatureValid()) {
                    isVaild = false;
                    VailResult result = new VailResult();
                    Point point = pdfManager.getPageScreen(info.getPageNo());
                    if (point == null) {
                        return;
                    }
                    result.key = EncryptUtils.encryptMD5ToString(info.toKeyId());
                    result.page = info.getPageNo();
                    result.rectF = new RectF(info.getRect()[0], point.y - info.getRect()[1], info.getRect()[2], point.y - info.getRect()[3]);
                    result.parent = new RectF(0, 0, point.x, point.y);

                    if (info.getPageNo() >= viewPager.getCurrentItem() - 1 && info.getPageNo() <= viewPager.getCurrentItem() + 1) {
                        current.add(result);
                    } else {
                        results.add(result);
                    }
                }
//            }
        }
        adapter.addVailLogo(results);
        if (current.size() > 0) {
            for (VailResult item : current) {
                PdfView pdfView = viewPager.findViewWithTag(item.page);
                if (pdfView.isRenderFinish()) {
                    pdfView.addVaildLogo(item);
                }
                adapter.addVailLogo(item);
            }
        }
        if (isAllCheck) {
            iv_status.setImageResource(isVaild ? R.drawable.youxiao : R.drawable.wuxiao);
            tv_status.setText(isVaild ? R.string.text_stauts_valid : R.string.text_stauts_unvalid);
            tv_status.setTextColor(Color.parseColor(isVaild ? "#5FB336" : "#E83535"));
        }
    }

    @Override
    public void onBackPressed() {
        if (isOpenPdf) {
            canCheckSign = false;
        }
        super.onBackPressed();
    }

    private void initToolbar() {
        try {
//            findViewById(R.id.toolbar).setBackgroundColor(Color.parseColor(SdkPdfManager.barColor));
            tvAll = findViewById(R.id.tv_all);
            tvCurrent = findViewById(R.id.tv_current);
//            tvAll.setTextColor(Color.parseColor(SdkPdfManager.titleColor));
//            tvCurrent.setTextColor(Color.parseColor(SdkPdfManager.titleColor));
        } catch (Exception e) {

        }
        findViewById(R.id.rl_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isOpenPdf) {
                    canCheckSign = false;
                }
                finish();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        if (PermissionUtils.FILE_REQUESTCODE == requestCode) {
//            for (int result : grantResults) {
//                if (result != PackageManager.PERMISSION_GRANTED) {
//                    showAlertDialog(PdfActivity.this, getString(R.string.dialog_title), getString(R.string.tip_file_permisson), getString(R.string.text_cancel), getString(R.string.text_sure), new SmartDialogClickListener() {
//                        @Override
//                        public void ok() {
//                            ActivityUtils.getAppDetailSettingIntent(PdfActivity.this);
//                            finish();
//                        }
//
//                        @Override
//                        public void cancel() {
//                            finish();
//                        }
//                    });
//                    return;
//                }
//            }
//            openPDF();
//        }
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacksAndMessages(null);
        if (isOpenPdf) {
            pdfManager.close();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (RESULT_OK == resultCode && requestCode == PdfListActivity.REQ_CODE) {
            int position = data.getIntExtra("position", 0);
            viewPager.setCurrentItem(position, false);
        }
    }
}
