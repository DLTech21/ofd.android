package io.github.dltech21;

import android.graphics.PointF;
import android.graphics.RectF;
import android.util.Log;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;


public class OFD_Native {

	static {
		try {
			System.loadLibrary("mupdf_java");
			System.loadLibrary("ofdParser");
			Log.e("OFD_Native", "load ofdParser library success");
		} catch (Exception e) {
			Log.e("OFD_Native", "load ofdParser library failed:"+ e.getMessage());
		}
	}


	/* Buffer helpers */

	public static native ByteBuffer newBuffer(int size);

	public static native void fillBuffer(byte[] bytes, ByteBuffer buffer, int length);

	public static native void deleteBuffer(ByteBuffer buffer);

	public static native long readOFDByPath(String path);
    public static native long readOFD(ByteBuffer data, int length);

    public static native void drawPage(long ofd, int pageIndex, String dir, Map<String, String> fontMap);

	public static native void drawPdf(long ofd, String dir, Map<String, String> fontMap);

	public static native void closeOFD(long ofd);

	public static native int getNumberOfPages(long ofd);

	public static native RectF[] getWidgetAreas(long ofd, int pageIndex, byte[] signId);

	public static native int getPageSize(long ofd, int pageNum, int[] width, int[] height);

	public static synchronized PointF getPageSize(long ofd, int pageNum) {
		int[] pageWidth = new int[1];
		int[] pageHeight = new int[1];
		int res = getPageSize(ofd, pageNum, pageWidth, pageHeight);
		if (res == 1) {
			return new PointF(pageWidth[0], pageHeight[0]);
		} else {
			return new PointF(210, 297);
		}
	}

	public static native int getSignatureInformationBySid(long ofd, int page, String id, byte[] signers, byte[] signTime, int[] valids, byte[] issuer, byte[] startTime, byte[] endTime, byte[] serial, byte[] alg);
}