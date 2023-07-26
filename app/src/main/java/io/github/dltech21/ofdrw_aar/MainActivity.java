package io.github.dltech21.ofdrw_aar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import com.blankj.utilcode.util.PathUtils;
import com.blankj.utilcode.util.ResourceUtils;
import com.blankj.utilcode.util.StringUtils;
import com.leon.lfilepickerlibrary.LFilePicker;
import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;

import org.ofdrw.core.attachment.CT_Attachment;
import org.ofdrw.reader.OFDReader;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.github.dltech21.ReaderManager;
import io.github.dltech21.ofd.FontManager;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
                                Log.e("copy asset", ret + "");

                                FontManager.getInstance().copyFontFromAssets("fonts");
                                String fontPath = FontManager.getInstance().getFontDir();
                                Map<String, String> fontMap = new HashMap<>();
                                fontMap.put("宋体", fontPath + "/simsun.ttf");
                                fontMap.put("simsun", fontPath + "/simsun.ttf");
                                fontMap.put("楷体", fontPath + "/simkai.ttf");
                                fontMap.put("kaiti", fontPath + "/simkai.ttf");
                                fontMap.put("kaiti_gb2312", fontPath + "/simkai.ttf");
                                fontMap.put("楷体_gb2312", fontPath + "/simhei.ttf");
                                fontMap.put("courier new", fontPath + "/cour.ttf");
                                fontMap.put("仿宋", fontPath + "/SIMFANG.TTF");
                                fontMap.put("仿宋_gb2312", fontPath + "/SIMFANG.TTF");
                                fontMap.put("小标宋体", fontPath + "/方正小标宋简体.ttf");
                                fontMap.put("方正小标宋简体", fontPath + "/方正小标宋简体.ttf");
                                fontMap.put("latha", fontPath + "/Latha.ttf");
                                fontMap.put("tahoma", fontPath + "/Tahoma.ttf");
                                fontMap.put("times new roman", fontPath + "/times.ttf");
                                fontMap.put("timesnewromanpsmt", fontPath + "/times.ttf");
                                fontMap.put("arial", fontPath + "/arial.ttf");
                                fontMap.put("calibri", fontPath + "/calibri.ttf");
                                fontMap.put("symbol", fontPath + "/symbol.ttf");
                                fontMap.put("stcaiyun", fontPath + "/STCAIYUN.TTF");
                                fontMap.put("mongolian baiti", fontPath + "/monbaiti.ttf");
                                fontMap.put("wingdings", fontPath + "/wingding.ttf");
                                FontManager.getInstance().setFontMap(fontMap);

//                                file0 = new File(dirPath, "GMT0031-2014.pdf");
//                                Log.e("file", file0.exists() + "");
//
//                                SdkOfdManager.openFile(MainActivity.this, file0.getAbsolutePath());

                                int REQUESTCODE_FROM_ACTIVITY = 1000;
                                new LFilePicker()
                                        .withActivity(MainActivity.this)
                                        .withRequestCode(REQUESTCODE_FROM_ACTIVITY)
                                        .withMutilyMode(false)
                                        .withFileFilter(new String[]{".ofd", ".pdf"})
                                        .withStartPath(dirPath)
                                        .start();
                            }
                        })
                        .start();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 1000) {
                List<String> list = data.getStringArrayListExtra("paths");
                ReaderManager.openFile(MainActivity.this, list.get(0));
                try {
                    test( list.get(0));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    private void test(String intPath) throws IOException {
        Path srcP = Paths.get(intPath);
        OFDReader reader = new OFDReader(srcP);
        List<CT_Attachment> attachmentList = reader.getAttachmentList();
        for (CT_Attachment attachment : attachmentList) {
            Path attFile = reader.getAttachmentFile(attachment);
            byte[] fileBytes = Files.readAllBytes(attFile);
            String fileName = attFile.getFileName().toString();
            final String attachmentName = attachment.getAttachmentName();
            String displayFileName = StringUtils.isEmpty(attachmentName) ? fileName :
                    attachmentName.concat(fileName.contains(".") ?
                            fileName.substring(fileName.lastIndexOf(".")) : "");
            Log.e("CT_Attachment", displayFileName);
        }
    }

}