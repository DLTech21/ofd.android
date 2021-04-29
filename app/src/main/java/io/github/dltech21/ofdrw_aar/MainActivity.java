package io.github.dltech21.ofdrw_aar;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.blankj.utilcode.util.PathUtils;
import com.blankj.utilcode.util.ResourceUtils;
import com.github.barteksc.pdfviewer.PDFView;

import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;


import java.io.File;
import java.util.List;

import io.github.dltech21.ofd.PdfConverter;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        System.setProperty("org.xml.sax.driver", "org.xmlpull.v1.sax2.Driver");
        setContentView(R.layout.activity_main);
        PDFView pdfView = findViewById(R.id.pdfView);
        findViewById(R.id.bt_choose).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AndPermission.with(MainActivity.this)
                        .permission(Permission.Group.STORAGE)
                        .onGranted(new Action() {
                            @Override
                            public void onAction(List<String> permissions) {
                                File file0 = null;
                                String dirPath = PathUtils.getCachePathExternalFirst() + System.getProperty("file.separator") + "test";
                                boolean ret = ResourceUtils.copyFileFromAssets("test", dirPath);
                                Log.e("copy asset", ret+"");
                                file0 = new File(dirPath, "z.ofd");
                                Log.e("file", file0.exists()+"");

                                byte[] pdfByte = PdfConverter.toPdf(file0.getAbsolutePath(), MainActivity.this);
                                pdfView.fromBytes(pdfByte).load();

                            }
                        })
                        .start();
            }
        });
    }

}