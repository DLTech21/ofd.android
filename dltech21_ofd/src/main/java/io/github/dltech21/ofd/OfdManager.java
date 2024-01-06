package io.github.dltech21.ofd;

import android.content.Context;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.RectF;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;

import androidx.collection.ArrayMap;

import com.blankj.utilcode.util.EncryptUtils;
import com.blankj.utilcode.util.FileIOUtils;
import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.PathUtils;

import java.io.File;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.LinkedList;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import io.github.dltech21.OFD_Native;
import io.github.dltech21.ofd.listener.OfdTaskListener;
import io.github.dltech21.ofd.listener.OnOfdResultListener;
import io.github.dltech21.ofd.model.OfdItem;
import io.github.dltech21.pdf.model.SignatureInformation;
import io.github.dltech21.pdf.model.WidgetArea;

public class OfdManager {
    public static final int TYPE_FIRST = 1;
    public static final int TYPE_LAST = 2;

    private int type = TYPE_LAST;
    private File src;
    private Context mContext;
    private String fileHash;
    private long ofdPtr;
    private String cacheDir;
    private int pageCount = -1;
    private ArrayMap<String, Point> array = new ArrayMap<>();
    private LinkedList<OfdItem> list = new LinkedList();

    private Timer timer = null;
    private final Object progressTimerSync = new Object();

    private boolean isStop = false;

    public long getOfdPtr() {
        return ofdPtr;
    }

    private boolean isClose = false;

    public String getCacheDir() {
        return cacheDir;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                OfdItem item = (OfdItem) msg.obj;
                if (item.getListener() != null) {
                    item.getListener().onRenderFinish(item.getPath());
                }
            }
        }
    };

    public long init(Context context, File src) {
        this.mContext = context;
        this.src = src;
        fileHash = EncryptUtils.encryptMD5File2String(src);
        cacheDir = PathUtils.getCachePathExternalFirst() + System.getProperty("file.separator") + "ofd" + System.getProperty("file.separator") + fileHash;
        FileUtils.createOrExistsDir(cacheDir);
        byte[] a = FileIOUtils.readFile2BytesByStream(src);
        ByteBuffer buffer = OFD_Native.newBuffer(a.length);
        buffer.order(ByteOrder.nativeOrder());
        buffer.limit(buffer.position() + a.length);
        OFD_Native.fillBuffer(a, buffer, a.length);
        long[] ptr = new long[1];
        long ret = OFD_Native.readOFD(buffer, buffer.remaining(), ptr);
        if (ret == 0) {
            ofdPtr = ptr[0];
        }
        return ret;
    }

    public void close() {
        isClose = true;
        list.clear();
        array.clear();
    }

    public void onStop() {
        isStop = true;
    }

    public void onResume() {
        isStop = false;
    }

    public boolean isStop() {
        return isStop;
    }

    public void startOfdDecode(int num, OfdTaskListener listener) {
        DecodOfdTask task = new DecodOfdTask(num, this);
        if (listener != null) {
            task.setTaskListener(listener);
        }
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public int getPageCount() {
        if (pageCount == -1) {
            pageCount = OFD_Native.getNumberOfPages(ofdPtr);
        }
        return pageCount;
    }

    public void drawPdf() {
        OFD_Native.drawPdf(ofdPtr, cacheDir, FontManager.getInstance().getFontMap());
    }

    public PointF getPageSize(int pageIndex) {
        return OFD_Native.getPageSize(ofdPtr, pageIndex);
    }

    private String getKey(String hash, int pageNum) {
        return hash + pageNum;
    }


    public void putTask(Context context, int pageNum, OnOfdResultListener listener) {
        boolean exist = FileUtils.isFileExists(cacheDir + "/page" + pageNum + ".png");
        if (exist) {
            listener.onRenderFinish(cacheDir + "/page" + pageNum + ".png");
            return;
        }
        String key = getKey(fileHash, pageNum);
        Point point = array.get(key);
        if (point != null) {
            synchronized (list) {
                for (OfdItem item : list) {
                    if (key == getKey(item.getFileHash(), item.getPage())) {
                        list.remove(item);
                        break;
                    }
                }
            }
        } else {
            array.put(key, new Point());
        }
        if (TYPE_LAST == type) {
            list.add(new OfdItem(pageNum, fileHash, listener));
        } else {
            list.addFirst(new OfdItem(pageNum, fileHash, listener));
        }
        if (timer == null) {
            startRender();
        }
    }

    public void stop() {
        synchronized (progressTimerSync) {
            if (timer != null) {
                try {
                    timer.cancel();
                    timer = null;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void startRender() {
        synchronized (progressTimerSync) {
            if (timer == null) {
                timer = new Timer();
                timer.schedule(new TimerTask() {

                    @Override
                    public void run() {
                        if (array.size() == 0) {
                            if (isClose) {
                                try {
                                    OFD_Native.closeOFD(ofdPtr);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                stop();
                            }
                        } else {
                            while (list.size() > 0) {
                                if (isStop) {
                                    return;
                                }
                                OfdItem item = null;
                                synchronized (list) {
                                    item = list.removeFirst();
                                }
                                array.remove(item.getFileHash() + item.getPage());
                                if (item.getFileHash() != fileHash) {
                                    continue;
                                }
                                OFD_Native.drawPage(ofdPtr, item.getPage(), cacheDir, FontManager.getInstance().getFontMap());
                                item.setPath(cacheDir + "/page" + item.getPage() + ".png");
                                Message message = Message.obtain();
                                message.what = 1;
                                message.obj = item;
                                handler.sendMessage(message);
                            }
                        }
                    }
                }, 0, 400);
            }
        }
    }

    public WidgetArea getWidgetAreas(int page) {
        byte[] signId = new byte[4000];
        RectF[] boundRect = OFD_Native.getWidgetAreas(ofdPtr, page, signId);
        signId = byteToNewByte(signId);
        String[] signerArr = (new String(signId)).split(":");
        String[] signIds = new String[boundRect.length];
        for (int i = 0; i < boundRect.length; i++) {
            signIds[i] = signerArr[i];
        }
        return new WidgetArea(boundRect, signIds);
    }

    private byte[] byteToNewByte(byte[] bytes) {
        int desLen = 1;
        for (int i = 0; i < bytes.length; i++) {
            if (bytes[i] == (byte) 0) {
                desLen = i;
                break;
            }
        }
        byte des[] = new byte[desLen];
        for (int i = 0; i < desLen; i++) {
            des[i] = bytes[i];
        }
        return des;
    }

    public SignatureInformation getSignatureInformationBySid(int pageNo, String id) {
        SignatureInformation signatureInformation = new SignatureInformation();
        signatureInformation.setPageNo(pageNo);
        signatureInformation.setFieldName(id);
        return getSignatureInformationBySid(signatureInformation);
    }

    public SignatureInformation getSignatureInformationBySid(SignatureInformation signatureInformation) {
        byte[] signer = new byte[256];
        byte[] signTime = new byte[256];
        int[] valids = new int[1];
        byte[] issuer = new byte[256];
        byte[] startTime = new byte[256];
        byte[] endTime = new byte[256];
        byte[] serial = new byte[256];
        byte[] alg = new byte[128];
        int ret0 = OFD_Native.getSignatureInformationBySid(ofdPtr, signatureInformation.getPageNo(), signatureInformation.getFieldName(), signer, signTime, valids, issuer, startTime, endTime, serial, alg);
        if (ret0 == 1) {
            signer = byteToNewByte(signer);
            signTime = byteToNewByte(signTime);
            signatureInformation.setSigner(new String(signer));
            signatureInformation.setSignTime(new String(signTime));
            signatureInformation.setSignatureValid(valids[0] == 0);
            signatureInformation.setCheck(true);
            issuer = byteToNewByte(issuer);
            startTime = byteToNewByte(startTime);
            endTime = byteToNewByte(endTime);
            serial = byteToNewByte(serial);
            alg = byteToNewByte(alg);
            signatureInformation.setSubject(new String(signer));
            signatureInformation.setIssuer(new String(issuer));
            signatureInformation.setStartTime(new String(startTime));
            signatureInformation.setEndTime(new String(endTime));
            signatureInformation.setSerial(new String(serial));
            signatureInformation.setAlg(new String(alg));
        }
        return signatureInformation;
    }
}
