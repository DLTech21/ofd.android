package com.artifex.mupdf.viewer;

import android.graphics.Bitmap;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.Log;

import com.artifex.mupdf.fitz.Cookie;
import com.artifex.mupdf.fitz.DisplayList;
import com.artifex.mupdf.fitz.Document;
import com.artifex.mupdf.fitz.Link;
import com.artifex.mupdf.fitz.Matrix;
import com.artifex.mupdf.fitz.Outline;
import com.artifex.mupdf.fitz.Page;
import com.artifex.mupdf.fitz.Quad;
import com.artifex.mupdf.fitz.Rect;
import com.artifex.mupdf.fitz.RectI;
import com.artifex.mupdf.fitz.android.AndroidDrawDevice;

import java.util.ArrayList;

import io.github.dltech21.pdf.model.SignatureInformation;
import io.github.dltech21.pdf.model.WidgetArea;

public class MuPDFCore
{
	private int resolution;
	private Document doc;
	private Outline[] outline;
	private int pageCount = -1;
	private int currentPage;
	private Page page;
	private float pageWidth;
	private float pageHeight;
	private DisplayList displayList;

	/* Default to "A Format" pocket book size. */
	private int layoutW = 312;
	private int layoutH = 504;
	private int layoutEM = 10;

	public MuPDFCore(String filename) {
		doc = Document.openDocument(filename);
		doc.layout(layoutW, layoutH, layoutEM);
		pageCount = doc.countPages();
		resolution = 160;
		currentPage = -1;
	}

	public MuPDFCore(byte buffer[], String magic) {
		doc = Document.openDocument(buffer, magic);
		doc.layout(layoutW, layoutH, layoutEM);
		pageCount = doc.countPages();
		resolution = 160;
		currentPage = -1;
	}

	public String getTitle() {
		return doc.getMetaData(Document.META_INFO_TITLE);
	}

	public int countPages() {
		return pageCount;
	}

	public synchronized boolean isReflowable() {
		return doc.isReflowable();
	}

	public synchronized int layout(int oldPage, int w, int h, int em) {
		if (w != layoutW || h != layoutH || em != layoutEM) {
			System.out.println("LAYOUT: " + w + "," + h);
			layoutW = w;
			layoutH = h;
			layoutEM = em;
			long mark = doc.makeBookmark(doc.locationFromPageNumber(oldPage));
			doc.layout(layoutW, layoutH, layoutEM);
			currentPage = -1;
			pageCount = doc.countPages();
			outline = null;
			try {
				outline = doc.loadOutline();
			} catch (Exception ex) {
				/* ignore error */
			}
			return doc.pageNumberFromLocation(doc.findBookmark(mark));
		}
		return oldPage;
	}

	private synchronized void gotoPage(int pageNum) {
		/* TODO: page cache */
		if (pageNum > pageCount-1)
			pageNum = pageCount-1;
		else if (pageNum < 0)
			pageNum = 0;
		if (pageNum != currentPage) {
			currentPage = pageNum;
			if (page != null)
				page.destroy();
			page = null;
			if (displayList != null)
				displayList.destroy();
			displayList = null;
			page = doc.loadPage(pageNum);
			Rect b = page.getBounds();
			pageWidth = b.x1 - b.x0;
			pageHeight = b.y1 - b.y0;
		}
	}

	public synchronized PointF getPageSize(int pageNum) {
		gotoPage(pageNum);
		return new PointF(pageWidth, pageHeight);
	}

	public synchronized void onDestroy() {
		if (displayList != null)
			displayList.destroy();
		displayList = null;
		if (page != null)
			page.destroy();
		page = null;
		if (doc != null)
			doc.destroy();
		doc = null;
	}

	public synchronized void drawPage(Bitmap bm, int pageNum,
                                      int pageW, int pageH,
                                      int patchX, int patchY,
                                      int patchW, int patchH,
                                      Cookie cookie) {
		gotoPage(pageNum);

		if (displayList == null)
			displayList = page.toDisplayList();

		float zoom = resolution / 72;
		Matrix ctm = new Matrix(zoom, zoom);
		RectI bbox = new RectI(page.getBounds().transform(ctm));
		float xscale = (float)pageW / (float)(bbox.x1-bbox.x0);
		float yscale = (float)pageH / (float)(bbox.y1-bbox.y0);
		ctm.scale(xscale, yscale);

		AndroidDrawDevice dev = new AndroidDrawDevice(bm, patchX, patchY);
		displayList.run(dev, ctm, cookie);
		dev.close();
		dev.destroy();
	}

	public synchronized void updatePage(Bitmap bm, int pageNum,
                                        int pageW, int pageH,
                                        int patchX, int patchY,
                                        int patchW, int patchH,
                                        Cookie cookie) {
		drawPage(bm, pageNum, pageW, pageH, patchX, patchY, patchW, patchH, cookie);
	}

	public synchronized Link[] getPageLinks(int pageNum) {
		gotoPage(pageNum);
		return page.getLinks();
	}

	public synchronized int resolveLink(Link link) {
		return doc.pageNumberFromLocation(doc.resolveLink(link));
	}

	public synchronized Quad[][] searchPage(int pageNum, String text) {
		gotoPage(pageNum);
		return page.search(text);
	}

	public synchronized boolean hasOutline() {
		if (outline == null) {
			try {
				outline = doc.loadOutline();
			} catch (Exception ex) {
				/* ignore error */
			}
		}
		return outline != null;
	}

//	private void flattenOutlineNodes(ArrayList<OutlineActivity.Item> result, Outline list[], String indent) {
//		for (Outline node : list) {
//			if (node.title != null) {
//				int page = doc.pageNumberFromLocation(doc.resolveLink(node));
//				result.add(new OutlineActivity.Item(indent + node.title, page));
//			}
//			if (node.down != null)
//				flattenOutlineNodes(result, node.down, indent + "    ");
//		}
//	}
//
//	public synchronized ArrayList<OutlineActivity.Item> getOutline() {
//		ArrayList<OutlineActivity.Item> result = new ArrayList<OutlineActivity.Item>();
//		flattenOutlineNodes(result, outline, "");
//		return result;
//	}

	public synchronized boolean needsPassword() {
		return doc.needsPassword();
	}

	public synchronized boolean authenticatePassword(String password) {
		return doc.authenticatePassword(password);
	}

	public ArrayList<SignatureInformation>  getAllSignature() {
		ArrayList<SignatureInformation> signs = new ArrayList<>();
		byte[] signers = new byte[10000 * 8];
		int[] signNum = new int[1];
		int[] pageNo = new int[1000];
		int[] valids = new int[1000];
		float[] rect = new float[1000 * 4];
		int ret = doc.getAllSignature(signNum, signers, pageNo, valids, rect);
		if (ret == 1) {
			signers = byteToNewByte(signers);
			String[] signerArr = (new String(signers)).split(":");
			for (int i = 0; i < signNum[0]; i++) {
				SignatureInformation signatureInformation = new SignatureInformation();
				signatureInformation.setSigner(i >= signerArr.length ? "" : signerArr[i]);
				float singleRect[] = new float[4];
				singleRect[0] = rect[0 + i * 4];
				singleRect[1] = rect[1 + i * 4];
				singleRect[2] = rect[2 + i * 4];
				singleRect[3] = rect[3 + i * 4];
				signatureInformation.setSignatureValid(valids[i]==0);
				signatureInformation.setCheck(true);
				signatureInformation.setRect(singleRect);
				signatureInformation.setPageNo(pageNo[i]);
				signs.add(signatureInformation);
			}
		}
		return signs;
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

	public WidgetArea getWidgetAreas(int page) {
		float[] rect = new float[1000 * 4];
		RectF[] boundRect = doc.getWidgetAreas(page, rect);
		RectF[] realRect = new RectF[boundRect.length];
		for (int i = 0; i < boundRect.length; i++) {
			RectF rectF = new RectF(rect[i * 4], rect[1 + i * 4], rect[2 + i * 4], rect[3 + i * 4]);
			realRect[i] = rectF;
		}
		return new WidgetArea(boundRect, realRect);
	}

	public SignatureInformation getSignatureInformationByCoordinate(int pageNo, float[] rect) {
		SignatureInformation signatureInformation = new SignatureInformation();
		signatureInformation.setPageNo(pageNo);
		signatureInformation.setRect(rect);
		return getSignatureInformationByCoordinate(signatureInformation);
	}

	public SignatureInformation getSignatureInformationByCoordinate(SignatureInformation signatureInformation) {
		byte[] signer = new byte[256];
		byte[] signTime = new byte[256];
		int[] hasTs = new int[1];
		byte[] tsTime = new byte[256];
		int[] valids = new int[1];
		byte[] issuer = new byte[256];
		byte[] startTime = new byte[256];
		byte[] endTime = new byte[256];
		byte[] serial = new byte[256];
		byte[] alg = new byte[128];
		int ret0 = doc.getSignatureInformationByRect(signatureInformation.getPageNo(), signatureInformation.getRect(), signer, signTime, hasTs, tsTime, valids, issuer, startTime, endTime, serial, alg);
		if (ret0 == 1) {
			signer = byteToNewByte(signer);
			signTime = byteToNewByte(signTime);
			tsTime = byteToNewByte(tsTime);
			signatureInformation.setSigner(new String(signer));
			signatureInformation.setSignTime(new String(signTime));
			signatureInformation.setTsTime(new String(tsTime));
			signatureInformation.setHasTs(hasTs[0]);
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
