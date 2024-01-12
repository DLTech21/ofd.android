package io.github.dltech21.pdf;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import androidx.collection.ArrayMap;

import com.artifex.mupdf.fitz.Cookie;
import com.artifex.mupdf.viewer.MuPDFCore;
import com.blankj.utilcode.util.EncryptUtils;
import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.PathUtils;

import io.github.dltech21.ofd.ViewUtils;
import io.github.dltech21.pdf.listener.OnPdfResultListener;
import io.github.dltech21.pdf.listener.PdfTaskListener;
import io.github.dltech21.pdf.model.PdfItem;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;


public class PdfManager {
    public static final String PDF_NOTIFIY = "ACTION_TYPE_SERVICE";
    public static final int TYPE_FIRST = 1;
    public static final int TYPE_LAST = 2;

    private int type = TYPE_LAST;
    private String fileHash;

    private Timer timer = null;
    private final Object progressTimerSync = new Object();

    private File src;
    private Context mContext;
    private MuPDFCore core;
    private int pageCount = -1;
    private boolean isClose = false;
    private boolean isStop = false;
    private Point screenSize;
    private String cacheDir;
    private ArrayMap<String, Point> array = new ArrayMap<>();
    private LinkedList<PdfItem> list = new LinkedList();
    private boolean isCheckSign = false;

    private boolean needPassword = false;
    private boolean correctPassowrd = false;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                PdfItem item = (PdfItem) msg.obj;
                if (item.getListener() != null) {
                    item.getListener().onRenderFinish(item.getPath(), item.getBitmap());
                }
            }
        }
    };

    public void init(Context context, File src, String password) throws Exception {
        this.mContext = context;
        this.src = src;
        fileHash = EncryptUtils.encryptMD5File2String(src);
        cacheDir = PathUtils.getCachePathExternalFirst() + System.getProperty("file.separator") + "pdf" + System.getProperty("file.separator") + fileHash;
        FileUtils.createOrExistsDir(cacheDir);
        screenSize = ViewUtils.getScreenMetrics(mContext);
        core = new MuPDFCore(src.getAbsolutePath());
        if (needPassword = core.needsPassword()) {
            if (password == null) {
                throw new Exception("null of password");
            }
            correctPassowrd = core.authenticatePassword(password);
            if (!correctPassowrd) {
                throw new Exception("error password");
            }
        }
    }

    public void startPdfDecode(int num, Point requestSize, Rect drawSize, PdfTaskListener listener) {
        DecodPdfTask task = new DecodPdfTask(num, requestSize, drawSize, this);
        if (listener != null) {
            task.setTaskListener(listener);
        }
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public void putTask(Context context, int pageNum, OnPdfResultListener listener) {
        if (checkCacheBitmap(fileHash, pageNum)) {
            listener.onRenderFinish(getRenderPath(fileHash, pageNum), null);
            return;
        }
        String key = getKey(fileHash, pageNum);
        Point point = array.get(key);
        if (point != null) {
            synchronized (list) {
                for (PdfItem item : list) {
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
            list.add(new PdfItem(pageNum, fileHash, listener));
        } else {
            list.addFirst(new PdfItem(pageNum, fileHash, listener));
        }
        if (timer == null) {
            startRender();
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
                                    if (core != null) {
                                        core.onDestroy();
                                        core = null;
                                    }
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
                                PdfItem item = null;
                                synchronized (list) {
                                    item = list.removeFirst();
                                }
                                array.remove(item.getFileHash() + item.getPage());
                                if (item.getFileHash() != fileHash) {
                                    continue;
                                }

                                PointF pageSize = core.getPageSize(item.getPage());
                                Log.e("pageSize", pageSize.x + ",,,,,," + pageSize.y);
                                Point request = null;
                                double mSourceScale = Math.min(screenSize.x * 1.0 / pageSize.x, screenSize.y * 1.0 / pageSize.y);
                                Point newSize = new Point((int) (pageSize.x * mSourceScale), (int) (pageSize.y * mSourceScale));
                                request = newSize;

                                Bitmap bitmap = Bitmap.createBitmap(request.x, request.y,
                                        Bitmap.Config.ARGB_8888);
                                Cookie cookie = new Cookie();
                                core.drawPage(bitmap, item.getPage(), request.x, request.y, 0, 0, request.x, request.y, cookie);
                                item.setBitmap(bitmap);
                                saveImage(fileHash + "_" + item.getPage(), bitmap);
                                item.setPath(getRenderPath(fileHash, item.getPage()));
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

    public Point getPageScreen(int pageNum) {
        if (core == null) {
            return null;
        }
        PointF pointF = core.getPageSize(pageNum);
        return new Point((int) pointF.x, (int) pointF.y);
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public void close() {
        isClose = true;
        list.clear();
        array.clear();
        core.onDestroy();
    }

    public String getFileHash() {
        return fileHash;
    }

    private String getKey(String hash, int pageNum) {
        return hash + pageNum;
    }

    public File getSrc() {
        return src;
    }

    public int getPageCount() {
        if (pageCount == -1) {
            if (core != null) {
                pageCount = core.countPages();
            }
        }
        return pageCount;
    }

    public MuPDFCore getCore() {
        return core;
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

    public boolean isCheckSign() {
        return isCheckSign;
    }

    public void setCheckSign(boolean checkSign) {
        isCheckSign = checkSign;
    }

    public boolean isNeedPassword() {
        return needPassword;
    }

    public boolean isCorrectPassowrd() {
        return correctPassowrd;
    }

    private void saveImage(String key, Bitmap bmp) {
        String fileName = key + ".png";
        File file = new File(cacheDir, fileName);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getRenderPath(String fileHash, int pageNum) {
        return new File(cacheDir, fileHash + "_" + pageNum + ".png").getAbsolutePath();
    }

    public boolean checkCacheBitmap(String fileHash, int pageNum) {
        if (new File(cacheDir, fileHash + "_" + pageNum + ".png").exists()) {
            return true;
        }
        return false;
    }
}
