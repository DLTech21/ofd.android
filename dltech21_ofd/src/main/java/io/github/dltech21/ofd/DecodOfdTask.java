package io.github.dltech21.ofd;

import android.os.AsyncTask;

import com.blankj.utilcode.util.FileUtils;

import io.github.dltech21.OFD_Native;
import io.github.dltech21.ofd.listener.OfdTaskListener;


public class DecodOfdTask extends AsyncTask<Void, Void, String> {
    private int pageNum;
    private OfdManager ofdManager;
    private OfdTaskListener taskListener;

    public DecodOfdTask(int pageNum, OfdManager ofdManager) {
        this.pageNum = pageNum;
        this.ofdManager = ofdManager;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if (taskListener != null) {
            taskListener.onBefor();
        }
    }

    @Override
    protected String doInBackground(Void... params) {
        boolean exist = FileUtils.isFileExists(ofdManager.getCacheDir() + "/test" + pageNum + ".png");
        if (exist) {
            return ofdManager.getCacheDir() + "/test" + pageNum + ".png";
        }
        OFD_Native.drawPage(ofdManager.getOfdPtr(), pageNum, ofdManager.getCacheDir(), FontManager.getInstance().getFontMap());
        return ofdManager.getCacheDir() + "/test" + pageNum + ".png";
    }

    @Override
    protected void onPostExecute(String path) {
        super.onPostExecute(path);
        if (taskListener != null) {
            taskListener.onComplete(path);
        }
    }

    public OfdTaskListener getTaskListener() {
        return taskListener;
    }

    public void setTaskListener(OfdTaskListener taskListener) {
        this.taskListener = taskListener;
    }
}
