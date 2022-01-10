package io.github.dltech21;

import android.util.Log;

import java.nio.ByteBuffer;
import java.util.Map;


public class OFD_Native {

	static {
		try {
			System.loadLibrary("ofdParser");
			Log.e("io.github.dltech21.OFD_Native", "load ofdParser library success");
		} catch (Exception e) {
			Log.e("io.github.dltech21.OFD_Native", "load ofdParser library failed:"+ e.getMessage());
		}
	}


	/* Buffer helpers */

	public static native ByteBuffer newBuffer(int size);

	public static native void fillBuffer(byte[] bytes, ByteBuffer buffer, int length);

	public static native void deleteBuffer(ByteBuffer buffer);

    public static native long readOFD(ByteBuffer data, int length);

    public static native void drawPage(long ofd, int pageIndex, String dir, Map<String, String> fontMap);

}