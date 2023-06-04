package io.github.dltech21.pdf;

import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.AsyncTask;

import com.artifex.mupdf.fitz.Cookie;

import io.github.dltech21.pdf.listener.PdfTaskListener;


public class DecodPdfTask extends AsyncTask<Void, Void, Bitmap> {
    private int pageNum;
    private PdfTaskListener taskListener;
    private PdfManager pdfManager;
    private Point requestSize;
    private Rect drawSize;

    public DecodPdfTask(int pageNum, Point requestSize, Rect drawSize, PdfManager pdfManager) {
        this.pageNum = pageNum;
        this.pdfManager = pdfManager;
        this.requestSize = requestSize;
        this.drawSize = drawSize;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if (taskListener != null) {
            taskListener.onBefor();
        }
    }

    @Override
    protected Bitmap doInBackground(Void... params) {
        Bitmap bitmap = Bitmap.createBitmap(drawSize.width(), drawSize.height(),
                Bitmap.Config.ARGB_8888);
        Cookie cookie = new Cookie();
        pdfManager.getCore().drawPage(bitmap, pageNum, requestSize.x, requestSize.y, drawSize.left, drawSize.top, drawSize.width(), drawSize.height(), cookie);
        return bitmap;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        super.onPostExecute(bitmap);
        if (taskListener != null) {
            taskListener.onComplete(bitmap);
        }
    }

    public PdfTaskListener getTaskListener() {
        return taskListener;
    }

    public void setTaskListener(PdfTaskListener taskListener) {
        this.taskListener = taskListener;
    }
}
