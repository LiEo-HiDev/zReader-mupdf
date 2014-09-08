package com.artifex.mupdfdemo;
import java.io.File;
import java.util.ArrayList;

import com.zreader.main.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;

public class MuPDFCore
{
	/* load our native library */
	static {
		System.loadLibrary("mupdf");
	}
	
	//Custom Code//
	public static final int PAPER_NORMAL = 0;
	public static final int PAPER_INVERT = 1;
	public static final int PAPER_GRAY = 2;
	public static final int PAPER_GRAY_INVERT = 3;
	public static final int PAPER_OPACIT_100 = 100;
	public static final int PAPER_OPACIT_90 = 90;
	public static final int PAPER_OPACIT_80 = 80;
	public static final int PAPER_OPACIT_70 = 70;
	public static final int PAPER_OPACIT_60 = 60;
	public static final int PAPER_OPACIT_50 = 50;
	public static final int PAPER_OPACIT_40 = 40;
	public int themeMode = PAPER_NORMAL;
	
	public static final int SINGLE_PAGE_MODE = 0;
	public static final int DOUBLE_PAGE_MODE = 1;
	public static final int AUTO_PAGE_MODE = 2;
	
	private Context context;
	private String filePath;
	private boolean doubleMode = false;
	private boolean coverPageMode = true;
	private boolean reflow = false;

	/* Readable members */
	private int numPages = -1;
	private float pageWidth;
	private float pageHeight;
	private long globals;
	private byte fileBuffer[];
	private String file_format;

	/* The native functions */
	private native long openFile(String filename);
	private native long openBuffer();
	private native String fileFormatInternal();
	private native int countPagesInternal();
	private native void gotoPageInternal(int localActionPageNum);
	private native float getPageWidth();
	private native float getPageHeight();
	private native void drawPage(Bitmap bitmap,
			int pageW, int pageH,
			int patchX, int patchY,
			int patchW, int patchH, int filterMode,
			int background, int opacity);
	private native void updatePageInternal(Bitmap bitmap,
			int page,
			int pageW, int pageH,
			int patchX, int patchY,
			int patchW, int patchH,
			int mode, int bgValue, int opacity);
	private native RectF[] searchPage(String text);
	private native TextChar[][][][] text();
	private native byte[] textAsHtml();
	private native void addMarkupAnnotationInternal(PointF[] quadPoints, int type);
	private native void addInkAnnotationInternal(PointF[][] arcs);
	private native void deleteAnnotationInternal(int annot_index);
	private native int passClickEventInternal(int page, float x, float y);
	private native void setFocusedWidgetChoiceSelectedInternal(String [] selected);
	private native String [] getFocusedWidgetChoiceSelected();
	private native String [] getFocusedWidgetChoiceOptions();
	private native int getFocusedWidgetSignatureState();
	private native String checkFocusedSignatureInternal();
	private native boolean signFocusedSignatureInternal(String keyFile, String password);
	private native int setFocusedWidgetTextInternal(String text);
	private native String getFocusedWidgetTextInternal();
	private native int getFocusedWidgetTypeInternal();
	private native LinkInfo [] getPageLinksInternal(int page);
	private native RectF[] getWidgetAreasInternal(int page);
	private native Annotation[] getAnnotationsInternal(int page);
	private native OutlineItem [] getOutlineInternal();
	private native boolean hasOutlineInternal();
	private native boolean needsPasswordInternal();
	private native boolean authenticatePasswordInternal(String password);
	private native MuPDFAlertInternal waitForAlertInternal();
	private native void replyToAlertInternal(MuPDFAlertInternal alert);
	private native void startAlertsInternal();
	private native void stopAlertsInternal();
	private native void destroying();
	private native boolean hasChangesInternal();
	private native void saveInternal();

	public native boolean javascriptSupported();

	public MuPDFCore(Context context, String filename) throws Exception
	{
		this.context = context;
		this.filePath = filename;
		globals = openFile(filename);
		if (globals == 0)
		{
			throw new Exception(String.format(context.getString(R.string.cannot_open_file_Path), filename));
		}
		file_format = fileFormatInternal();
	}

	public MuPDFCore(Context context, byte buffer[]) throws Exception
	{
		this.context = context;
		fileBuffer = buffer;
		globals = openBuffer();
		if (globals == 0)
		{
			throw new Exception(context.getString(R.string.cannot_open_buffer));
		}
		file_format = fileFormatInternal();
	}

	public  int countDocumentPages()
	{
		if (numPages < 0)
			numPages = countPagesSynchronized();

		return numPages;
	}
	
	public int countDisplayPage(){
		if (numPages < 0)
			numPages = countPagesSynchronized();
		
		//Single
		if(!doubleMode) return numPages;	
		
		//Double
		if(coverPageMode) {
			return (numPages - 1) % 2 == 1 ? (numPages / 2) + 1 : ((numPages - 1) / 2) + 1;
		}else{
			return numPages % 2 == 1 ? (numPages + 1) / 2 : numPages / 2;
		}
	}
	
	public int getDocumentPage(int page) {
		if (!doubleMode || page == 0) {
			return page;
		} else {
			if (!coverPageMode) {
				return page * 2;
			} else {
				return (page * 2) - 1;
			}
		}
	}

	public boolean isLastSinglePage(int displayPage){
		if(coverPageMode) {
			int lastDisplay = (numPages+1) / 2;
			if(lastDisplay != displayPage) return false;
			return (numPages - 1) % 2 == 1 ? true : false;
		}else{
			int lastDisplay = numPages / 2;
			if(lastDisplay != displayPage) return false;
			return numPages % 2 == 1 ? true : false;
		}
	}
	
	
	
//	public int getDocumentPage(int disPlayPage){
//		if(coverPageMode) {
//			return (disPlayPage * 2) - 1;
//		}else{
//			return disPlayPage * 2;
//		}
//	}

	public String fileFormat()
	{
		return file_format;
	}

	public String getFilePath(){
		return filePath;
	}
	
	public String getFileDirectory() {
		return (new File(filePath)).getParent();
	}
	
	public String getFilePathReplace() {
		File file = new File(filePath);
		return file.getName().replace("/", "_").replace(".", "_").replace(" ", "_");
	}
	
	private synchronized int countPagesSynchronized() {
		return countPagesInternal();
	}

	/* Shim function */
	private void gotoPage(int page)
	{
		if (page > numPages-1)
			page = numPages-1;
		else if (page < 0)
			page = 0;
		gotoPageInternal(page);
		this.pageWidth = getPageWidth();
		this.pageHeight = getPageHeight();
	}

//	public synchronized PointF getPageSize(int page) {
//		gotoPage(page);
//		return new PointF(pageWidth, pageHeight);
//	}
	
	public synchronized PointF getDocumentPageSize(int page) {
		gotoPage(page);
		return new PointF(pageWidth, pageHeight);
	}
	
	public synchronized PointF getPageSize(int displayPage) {
		if (!doubleMode) {
			gotoPage(displayPage);
			return new PointF(pageWidth, pageHeight);
		} else {
			if (coverPageMode && displayPage == 0) {
				gotoPage(displayPage);
				return new PointF(pageWidth, pageHeight);
			}
			if (isLastSinglePage(displayPage)) {
				int page = 0;
				if (coverPageMode) {
					page = (displayPage * 2) - 1;
				} else
					page = displayPage * 2;
				gotoPage(page);
				return new PointF(pageWidth, pageHeight);
			}

			int page = 0;
			if (coverPageMode) {
				page = (displayPage * 2) - 1;
			} else {
				page = displayPage * 2;
			}
			gotoPage(page);
			float leftWidth = pageWidth;
			float leftHeight = pageHeight;
			gotoPage(page + 1);
			float screenWidth = leftWidth + pageWidth;
			float screenHeight = Math.max(leftHeight, pageHeight);
			return new PointF(screenWidth, screenHeight);

		}
	}

	public MuPDFAlert waitForAlert() {
		MuPDFAlertInternal alert = waitForAlertInternal();
		return alert != null ? alert.toAlert() : null;
	}

	public void replyToAlert(MuPDFAlert alert) {
		replyToAlertInternal(new MuPDFAlertInternal(alert));
	}

	public void stopAlerts() {
		stopAlertsInternal();
	}

	public void startAlerts() {
		startAlertsInternal();
	}

	public synchronized void onDestroy() {
		destroying();
		globals = 0;
	}

//	public synchronized void drawPage(Bitmap bm, int page,
//			int pageW, int pageH,
//			int patchX, int patchY,
//			int patchW, int patchH) {
//		gotoPage(page);
//		drawPage(bm, pageW, pageH, patchX, patchY, patchW, patchH);
//	}
	
	public int getDisplayPage(int page) {
		if (!doubleMode || page == 0) {
			return page;
		} else {
			if (coverPageMode) {
				return (page + 1) / 2;
			} else {
				return page / 2;
			}
		}
	}
		
	public synchronized Bitmap drawPage(Bitmap bitmap, final int disPlayPage, int pageW, int pageH, int patchX, int patchY,
			int patchW, int patchH) {
		Canvas canvas = null;
		try {
			canvas = new Canvas(bitmap);
			canvas.drawColor(Color.TRANSPARENT);

			// Single mode and first single
			if (!doubleMode || (coverPageMode && disPlayPage == 0)) {
				gotoPage(disPlayPage);
				drawPage(bitmap, pageW, pageH, patchX, patchY, patchW, patchH, themeMode, 0xff, PAPER_OPACIT_100);
				return bitmap;

				// Last single in Double mode
			} else if (isLastSinglePage(disPlayPage)) {
				int page = 0;
				if (coverPageMode) {
					page = (disPlayPage * 2) - 1;
				} else
					page = disPlayPage * 2;

				gotoPage(page);
				drawPage(bitmap, pageW, pageH, patchX, patchY, patchW, patchH, themeMode, 0xff, PAPER_OPACIT_100);
				return bitmap;
			} else {
				int drawPage = 0;
				if (coverPageMode) {
					drawPage = (disPlayPage == 0) ? 0 : (disPlayPage * 2) - 1;
				} else {
					drawPage = (disPlayPage == 0) ? 0 : disPlayPage * 2;
				}

				int leftPageW = pageW / 2;
				int rightPageW = pageW - leftPageW;
				int leftBmWidth = Math.min(leftPageW, leftPageW - patchX);
				leftBmWidth = (leftBmWidth < 0) ? 0 : leftBmWidth;
				int rightBmWidth = patchW - leftBmWidth;

				Paint paint = new Paint(Paint.FILTER_BITMAP_FLAG);
				if (leftBmWidth > 0) {
					Bitmap leftBm = Bitmap.createBitmap(leftBmWidth, patchH, getBitmapConfig());
					gotoPage(drawPage);
					drawPage(leftBm, leftPageW, pageH, patchX, patchY, leftBmWidth, patchH, themeMode, 0xff,
							PAPER_OPACIT_100);
					canvas.drawBitmap(leftBm, 0, 0, paint);
					leftBm.recycle();
				}
				if (rightBmWidth > 0) {
					Bitmap rightBm = Bitmap.createBitmap(rightBmWidth, patchH, getBitmapConfig());
					gotoPage(drawPage + 1);
					drawPage(rightBm, rightPageW, pageH, (leftBmWidth == 0) ? patchX - leftPageW : 0, patchY,
							rightBmWidth, patchH, themeMode, 0xff, PAPER_OPACIT_100);

					canvas.drawBitmap(rightBm, (float) leftBmWidth, 0, paint);
					rightBm.recycle();
				}
				return bitmap;
			}
		} catch (OutOfMemoryError e) {
			if (canvas != null)
				canvas.drawColor(Color.TRANSPARENT);
			return bitmap;
		}
	}
	
	private Config getBitmapConfig() {
		return Config.ARGB_8888;
	}
	
	public synchronized Bitmap drawThumbnailPage(Bitmap bm, int page,
			int pageW, int pageH, int patchX, int patchY, int patchW, int patchH) {
		gotoPage(page);
		drawPage(bm, pageW, pageH, patchX, patchY, patchW, patchH, PAPER_NORMAL,
				0xff, PAPER_OPACIT_100);
		return bm;
	}

//	public synchronized void updatePage(Bitmap bm, int page,
//			int pageW, int pageH,
//			int patchX, int patchY,
//			int patchW, int patchH) {
//		updatePageInternal(bm, page, pageW, pageH, patchX, patchY, patchW, patchH);
//	}
	
	public synchronized Bitmap updatePage(Bitmap bitmap, int disPlayPage, int pageW,
			int pageH, int patchX, int patchY, int patchW, int patchH) {
		
		// updatePageInternal(bitmap, page, pageW, pageH, patchX, patchY,
		// patchW, patchH);
		Canvas canvas = null;

		try {
			canvas = new Canvas(bitmap);
			canvas.drawColor(Color.TRANSPARENT);
			
			if (!doubleMode || (coverPageMode && disPlayPage == 0)) {
				updatePageInternal(bitmap, disPlayPage, pageW, pageH, patchX, patchY,
						patchW, patchH, themeMode, 0xff, PAPER_OPACIT_100);
				return bitmap;
				// Last single in Double mode
			} else if (isLastSinglePage(disPlayPage)) {
				int page = 0;
				if (coverPageMode) {
					page = (disPlayPage * 2) - 1;
				} else
					page = disPlayPage * 2;

				updatePageInternal(bitmap, page, pageW, pageH, patchX, patchY, patchW, patchH, themeMode, 0xff, PAPER_OPACIT_100);
				return bitmap;
			} else {
				int drawPage = 0;
				if (coverPageMode) {
					drawPage = (disPlayPage == 0) ? 0 : (disPlayPage * 2) - 1;
				} else {
					drawPage = (disPlayPage == 0) ? 0 : disPlayPage * 2;
				}

				int leftPageW = pageW / 2;
				int rightPageW = pageW - leftPageW;
				int leftBmWidth = Math.min(leftPageW, leftPageW - patchX);
				leftBmWidth = (leftBmWidth < 0) ? 0 : leftBmWidth;
				int rightBmWidth = patchW - leftBmWidth;

				Paint paint = new Paint(Paint.FILTER_BITMAP_FLAG);
				if (leftBmWidth > 0) {
					Bitmap leftBm = Bitmap.createBitmap(leftBmWidth, patchH, getBitmapConfig());
					updatePageInternal(leftBm, drawPage, leftPageW, pageH, patchX, patchY, leftBmWidth, patchH, themeMode, 0xff,
							PAPER_OPACIT_100);
					canvas.drawBitmap(leftBm, 0, 0, paint);
					leftBm.recycle();
				}
				if (rightBmWidth > 0) {
					Bitmap rightBm = Bitmap.createBitmap(rightBmWidth, patchH, getBitmapConfig());
					updatePageInternal(rightBm,drawPage + 1 , rightPageW, pageH, (leftBmWidth == 0) ? patchX - leftPageW : 0, patchY,
							rightBmWidth, patchH, themeMode, 0xff, PAPER_OPACIT_100);

					canvas.drawBitmap(rightBm, (float) leftBmWidth, 0, paint);
					rightBm.recycle();
				}
				return bitmap;
			}
		} catch (OutOfMemoryError e) {
			if (canvas != null)
				canvas.drawColor(Color.TRANSPARENT);
			return bitmap;
		}
	}

	public synchronized PassClickResult passClickEvent(int page, float x, float y) {
		boolean changed = passClickEventInternal(page, x, y) != 0;

		switch (WidgetType.values()[getFocusedWidgetTypeInternal()])
		{
		case TEXT:
			return new PassClickResultText(changed, getFocusedWidgetTextInternal());
		case LISTBOX:
		case COMBOBOX:
			return new PassClickResultChoice(changed, getFocusedWidgetChoiceOptions(), getFocusedWidgetChoiceSelected());
		case SIGNATURE:
			return new PassClickResultSignature(changed, getFocusedWidgetSignatureState());
		default:
			return new PassClickResult(changed);
		}

	}

	public synchronized boolean setFocusedWidgetText(int page, String text) {
		boolean success;
		gotoPage(page);
		success = setFocusedWidgetTextInternal(text) != 0 ? true : false;

		return success;
	}

	public synchronized void setFocusedWidgetChoiceSelected(String [] selected) {
		setFocusedWidgetChoiceSelectedInternal(selected);
	}

	public synchronized String checkFocusedSignature() {
		return checkFocusedSignatureInternal();
	}

	public synchronized boolean signFocusedSignature(String keyFile, String password) {
		return signFocusedSignatureInternal(keyFile, password);
	}

//	public synchronized LinkInfo [] getPageLinks(int page) {
//		return getPageLinksInternal(page);
//	}
	
	public synchronized LinkInfo [] getPageLinks(int page) {
		if(!doubleMode){
			return getPageLinksInternal(page);
		}
		
		if(coverPageMode && page == 0){
			return getPageLinksInternal(page);
		}
		
		LinkInfo[] leftPageLinkInfo = new LinkInfo[0];
		LinkInfo[] rightPageLinkInfo = new LinkInfo[0];
		LinkInfo[] combinedLinkInfo;
		int combinedSize = 0;
		int rightPage = 0;
		if (coverPageMode) {
			rightPage = page * 2;
		} else
			rightPage = (page * 2) + 1;
		
		int leftPage = rightPage - 1;
		int count = countDisplayPage() * 2;
		if (leftPage >= 0) {
			LinkInfo[] leftPageLinkInfoInternal = getPageLinksInternal(leftPage);
			if (null != leftPageLinkInfoInternal) {
				leftPageLinkInfo = leftPageLinkInfoInternal;
				combinedSize += leftPageLinkInfo.length;
			}
		}
		if (rightPage < count) {
			LinkInfo[] rightPageLinkInfoInternal = getPageLinksInternal(rightPage);
			if (null != rightPageLinkInfoInternal) {
				rightPageLinkInfo = rightPageLinkInfoInternal;
				combinedSize += rightPageLinkInfo.length;
			}
		}

		combinedLinkInfo = new LinkInfo[combinedSize];
		for (int i = 0; i < leftPageLinkInfo.length; i++) {
			combinedLinkInfo[i] = leftPageLinkInfo[i];
		}

		LinkInfo temp;
		for (int i = 0, j = leftPageLinkInfo.length; i < rightPageLinkInfo.length; i++, j++) {
			temp = rightPageLinkInfo[i];
			temp.rect.left += pageWidth;
			temp.rect.right += pageWidth;
			combinedLinkInfo[j] = temp;
		}
		// for (LinkInfo linkInfo: combinedLinkInfo) {
		// if(linkInfo instanceof LinkInfoExternal)
		// Log.d(TAG, "return " + ((LinkInfoExternal)linkInfo).url);
		// }
		return combinedLinkInfo;
	}

	public synchronized RectF [] getWidgetAreas(int page) {
		return getWidgetAreasInternal(page);
	}

	public synchronized Annotation [] getAnnoations(int page) {
		return getAnnotationsInternal(page);
	}

	public synchronized RectF [] searchPage(int page, String text) {
		gotoPage(page);
		return searchPage(text);
	}

	public synchronized byte[] html(int page) {
		gotoPage(page);
		return textAsHtml();
	}

	public synchronized TextWord [][] textLines(int page) {
		gotoPage(page);
		TextChar[][][][] chars = text();

		// The text of the page held in a hierarchy (blocks, lines, spans).
		// Currently we don't need to distinguish the blocks level or
		// the spans, and we need to collect the text into words.
		ArrayList<TextWord[]> lns = new ArrayList<TextWord[]>();

		for (TextChar[][][] bl: chars) {
			if (bl == null)
				continue;
			for (TextChar[][] ln: bl) {
				ArrayList<TextWord> wds = new ArrayList<TextWord>();
				TextWord wd = new TextWord();

				for (TextChar[] sp: ln) {
					for (TextChar tc: sp) {
						if (tc.c != ' ') {
							wd.Add(tc);
						} else if (wd.w.length() > 0) {
							wds.add(wd);
							wd = new TextWord();
						}
					}
				}

				if (wd.w.length() > 0)
					wds.add(wd);

				if (wds.size() > 0)
					lns.add(wds.toArray(new TextWord[wds.size()]));
			}
		}

		return lns.toArray(new TextWord[lns.size()][]);
	}

	public synchronized void addMarkupAnnotation(int page, PointF[] quadPoints, Annotation.Type type) {
		gotoPage(page);
		addMarkupAnnotationInternal(quadPoints, type.ordinal());
	}

	public synchronized void addInkAnnotation(int page, PointF[][] arcs) {
		gotoPage(page);
		addInkAnnotationInternal(arcs);
	}

	public synchronized void deleteAnnotation(int page, int annot_index) {
		gotoPage(page);
		deleteAnnotationInternal(annot_index);
	}

	public synchronized boolean hasOutline() {
		return hasOutlineInternal();
	}

	public synchronized OutlineItem [] getOutline() {
		return getOutlineInternal();
	}

	public synchronized boolean needsPassword() {
		return needsPasswordInternal();
	}

	public synchronized boolean authenticatePassword(String password) {
		return authenticatePasswordInternal(password);
	}

	public synchronized boolean hasChanges() {
		return hasChangesInternal();
	}

	public synchronized void save() {
		saveInternal();
	}
	
	public boolean isReflowMode() {
		return reflow;
	}
	
	public void setReflow(boolean reflow) {
		this.reflow = reflow;
	}
	
	public boolean getDoubleMode() {
		return doubleMode;
	}

	public boolean getCoverPageMode() {
		return coverPageMode;
	}

	public void setDoubleMode(boolean doubleMode) {
		this.doubleMode = doubleMode;
	}

	public void setCoverPageMode(boolean coverPageMode) {
		this.coverPageMode = coverPageMode;
	}
	
	public synchronized PointF getSinglePageSize(int page) {
		gotoPage(page);
		return new PointF(pageWidth, pageHeight);
	}
	
	public int countSinglePages() {
		return numPages;
	}
	
	public void setThemeMode(int theme) {
		themeMode = theme;
	}
	
	public boolean isNoghtMode(){
		switch (themeMode) {
		case PAPER_GRAY_INVERT:
			return true;

		default:
			return false;
		}
	}
	
}
