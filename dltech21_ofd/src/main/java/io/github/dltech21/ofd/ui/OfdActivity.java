package io.github.dltech21.ofd.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.viewpager.widget.ViewPager;


import java.io.File;

import io.github.dltech21.ofd.OfdManager;
import io.github.dltech21.ofd.R;
import io.github.dltech21.view.HackyViewPager;


public class OfdActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback {

    private OfdPagerAdapter adapter;
    private HackyViewPager viewPager;

    private TextView tvCurrent;
    private TextView tvAll;
    private ImageView iv_status;
    private TextView tv_status;

//    private ArrayList<SignatureInformation> signtures = null;
    private OfdManager ofdManager;
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
//            if (msg.what == 1 && !isFinishing()) {
//                ArrayList<SignatureInformation> signs = (ArrayList<SignatureInformation>) msg.obj;
//                isChecking = false;
//                signtures = signs;
//                checkDrawable.stop();
//                pdfManager.setCheckSign(false);
//                displayFileValid();
//            }
        }
    };

    public static void open(Context mContext, String filePath, String password) {
        Intent intent = new Intent(mContext, OfdActivity.class);
        intent.putExtra("path", filePath);
        intent.putExtra("password", password);
        mContext.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ofd);

        filePath = getIntent().getStringExtra("path");
        password = getIntent().getStringExtra("password");
        initView();
        ofdManager = new OfdManager();
        if (filePath != null) {
//            boolean isSupport = PermissionUtils.checkPermissions(PdfActivity.this, PermissionUtils.FILE_REQUESTCODE, PermissionUtils.filePermissions);
//            if (isSupport) {
                openPDF();
//            }
        }
    }

    public void initView() {
        initToolbar();
//        iv_status = findViewById(R.id.iv_status);
//        tv_status = findViewById(R.id.tv_status);
        viewPager = findViewById(R.id.vp_show);

        findViewById(R.id.rl_fenye).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (filePath != null) {
                    OfdListActivity.open(OfdActivity.this, currentPosition, filePath, ofdManager.getPageCount(), password);
                }
            }
        });
//        findViewById(R.id.rl_detail).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (!isChecking) {
//                    if (signtures == null || signtures.size() == 0) {
//                        Toast.makeText(mContext, "该文档没有签名", Toast.LENGTH_SHORT).show();
//                        return;
//                    }
//                    ArrayList<SignatureInformation> current = new ArrayList<SignatureInformation>();
//                    current.addAll(signtures);
//                    SigntureListActivity.open(mContext, current, signCore.getPath());
//                }
//            }
//        });
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (ofdManager.getType() == OfdManager.TYPE_LAST) {
                    ofdManager.setType(OfdManager.TYPE_FIRST);
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
            ofdManager.init(OfdActivity.this, new File(filePath));
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
        ofdManager.setType(OfdManager.TYPE_LAST);
        if (!isOpenPdf) {
//            showAlertDialog(mContext, "文件打开失败，请查看原文件", getString(R.string.text_sure), new SmartDialogClickListener() {
//                @Override
//                public void ok() {
//                    finish();
//                }
//            });
            return;
        }
        tvAll.setText(" / " + ofdManager.getPageCount());
        tvCurrent.setText("1");
        adapter = new OfdPagerAdapter(this, ofdManager);
        viewPager.setAdapter(adapter);

//        checkDrawable = (AnimationDrawable) iv_status.getDrawable();
//        checkDrawable.start();
//        pdfManager.setCheckSign(true);
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                if (signCore == null) {
//                    return;
//                }
//                ArrayList<SignatureInformation> signs = signCore.getAllSignatures();
//                int size = signs.size();
//                if (size > 0) {
//                    for (int i = 0; i < size; i++) {
//                        SignatureInformation item = signs.get(i);
//                        if (!canCheckSign) {
//                            break;
//                        }
//                        signCore.checkSignatureValid(item);
//                    }
//                    if (canCheckSign) {
//                        Message msg = Message.obtain();
//                        msg.what = 1;
//                        msg.obj = signs;
//                        mHandler.sendMessageDelayed(msg, 3000);
//                    }
//                } else {
//                    Message msg = new Message();
//                    msg.what = 1;
//                    msg.obj = signs;
//                    mHandler.sendMessage(msg);
//                }
//            }
//        }).start();
    }

//    private void displayFileValid() {
//        ArrayList<VailResult> results = new ArrayList<>();
//        ArrayList<VailResult> current = new ArrayList<>();
//        boolean isAllCheck = true;
//        boolean isVaild = true;
//        for (SignatureInformation info : signtures) {
//            if (!info.isCheck()) {
//                isAllCheck = false;
//            } else {
//                if (!info.isSignatureValid()) {
//                    isVaild = false;
//                    VailResult result = new VailResult();
//                    Point point = pdfManager.getPageScreen(info.getPageNo());
//                    if (point == null) {
//                        return;
//                    }
//                    result.key = EncryptUtils.encryptMD5ToString(info.toKeyId());
//                    result.page = info.getPageNo();
//                    result.rectF = new RectF(info.getRect()[0], point.y - info.getRect()[1], info.getRect()[2], point.y - info.getRect()[3]);
//                    result.parent = new RectF(0, 0, point.x, point.y);
//
//                    if (info.getPageNo() >= viewPager.getCurrentItem() - 1 && info.getPageNo() <= viewPager.getCurrentItem() + 1) {
//                        current.add(result);
//                    } else {
//                        results.add(result);
//                    }
//                }
//            }
//        }
//        adapter.addVailLogo(results);
//        if (current.size() > 0) {
//            for (VailResult item : current) {
//                PdfView pdfView = viewPager.findViewWithTag(item.page);
//                if (pdfView.isRenderFinish()) {
//                    pdfView.addVaildLogo(item);
//                }
//                adapter.addVailLogo(item);
//            }
//        }
//        if (isAllCheck) {
//            iv_status.setImageResource(isVaild ? R.drawable.youxiao : R.drawable.wuxiao);
//            tv_status.setText(isVaild ? R.string.text_stauts_valid : R.string.text_stauts_unvalid);
//            tv_status.setTextColor(Color.parseColor(isVaild ? "#5FB336" : "#E83535"));
//        }
//    }

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

//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
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
//    }

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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacksAndMessages(null);
        if (isOpenPdf) {
            ofdManager.close();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (Activity.RESULT_OK == resultCode && requestCode == OfdListActivity.REQ_CODE) {
            int position = data.getIntExtra("position", 0);
            viewPager.setCurrentItem(position, false);
        }
    }
}
