package com.artifex.mupdfdemo;

import java.io.UnsupportedEncodingException;

import com.zreader.main.R;

import android.content.Context;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.RectF;
import android.os.Handler;
import android.util.Base64;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class MuPDFReflowView extends WebView implements MuPDFView {
	private final MuPDFCore mCore;
	private final Handler mHandler;
	private final Point mParentSize;
	private int mPage;
	private float mScale;
	private int mContentHeight;
	AsyncTask<Void,Void,byte[]> mLoadHTML;

	public MuPDFReflowView(Context c, MuPDFCore core, Point parentSize) {
		super(c);
		mHandler = new Handler();
		mCore = core;
		mParentSize = parentSize;
		mScale = 1.0f;
		mContentHeight = parentSize.y;
		getSettings().setJavaScriptEnabled(true);
		addJavascriptInterface(new Object(){
			public void reportContentHeight(String value) {
				mContentHeight = (int)Float.parseFloat(value);
				mHandler.post(new Runnable() {
					public void run() {
						requestLayout();
					}
				});
			}
		}, "HTMLOUT");
		setWebViewClient(new WebViewClient() {
			@Override
			public void onPageFinished(WebView view, String url) {
				setScale(mScale);
			}
		});
		setBackgroundColor(MuPDFActivity.backGroundPage);
	}

	private void requestHeight() {
		// Get the webview to report the content height via the interface setup
		// above. Workaround for getContentHeight not working
		loadUrl("javascript:elem=document.getElementById('content');window.HTMLOUT.reportContentHeight("+mParentSize.x+"*elem.offsetHeight/elem.offsetWidth)");
	}

	public void setPage(int page, PointF size) {
		mPage = page;
		if (mLoadHTML != null) {
			mLoadHTML.cancel(true);
		}
		mLoadHTML = new AsyncTask<Void,Void,byte[]>() {
			@Override
			protected byte[] doInBackground(Void... params) {
				return mCore.html(mPage);
			}
			@Override
			protected void onPostExecute(byte[] result) {
				String str;
				try {
					str = new String(result, "UTF-8");
					
					if(mCore.isNoghtMode()){
						str = str.replace("background-color:white", "background-color:black");
					}else {
						str = str.replace("background-color:black", "background-color:white");
					}
					
					loadData(str, "text/html", "UTF-8");
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
				
//				String b64 = Base64.encodeToString(result, Base64.DEFAULT);
//				loadData(b64, "text/html; charset=utf-8", "base64");
			}
		};
		mLoadHTML.execute();
	}

	public int getPage() {
		return mPage;
	}

	public void setScale(float scale) {
		mScale = scale;
		loadUrl("javascript:document.getElementById('content').style.zoom=\""+(int)(mScale*100)+"%\"");
		requestHeight();
		
		if(mCore.isNoghtMode()){
			toNightMode();
		}else {
			toDayMode();
		}
	}
	
	public void toNightMode() {
		loadUrl("javascript:document.getElementById('content').style.color='f2f2f2'");
	}
	
	public void toDayMode() {
		loadUrl("javascript:document.getElementById('content').style.color='black'");
	}

	public void blank(int page) {
	}

	public Hit passClickEvent(float x, float y) {
		return Hit.Nothing;
	}

	public LinkInfo hitLink(float x, float y) {
		return null;
	}

	public void selectText(float x0, float y0, float x1, float y1) {
	}

	public void deselectText() {
	}

	public boolean copySelection() {
		return false;
	}

	public boolean markupSelection(Annotation.Type type) {
		return false;
	}

	public void startDraw(float x, float y) {
	}

	public void continueDraw(float x, float y) {
	}

	public void cancelDraw() {
	}

	public boolean saveDraw() {
		return false;
	}

	public void setSearchBoxes(RectF[] searchBoxes) {
	}

	public void setLinkHighlighting(boolean f) {
	}

	public void deleteSelectedAnnotation() {
	}

	public void deselectAnnotation() {
	}

	public void setChangeReporter(Runnable reporter) {
	}

	public void update() {
	}

	public void updateHq(boolean update) {
	}

	public void removeHq() {
	}

	public void releaseResources() {
		if (mLoadHTML != null) {
			mLoadHTML.cancel(true);
			mLoadHTML = null;
		}
	}

	public void releaseBitmaps() {
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int x, y;
		switch(View.MeasureSpec.getMode(widthMeasureSpec)) {
		case View.MeasureSpec.UNSPECIFIED:
			x = mParentSize.x;
			break;
		default:
			x = View.MeasureSpec.getSize(widthMeasureSpec);
		}
		switch(View.MeasureSpec.getMode(heightMeasureSpec)) {
		case View.MeasureSpec.UNSPECIFIED:
			y = mContentHeight;
			break;
		default:
			y = View.MeasureSpec.getSize(heightMeasureSpec);
		}

		setMeasuredDimension(x, y);
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		// TODO Auto-generated method stub
		return false;
	}
}
