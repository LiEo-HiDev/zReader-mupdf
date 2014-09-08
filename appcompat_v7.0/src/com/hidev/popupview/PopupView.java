package com.hidev.popupview;


import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v7.appcompat.R;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.PopupWindow.OnDismissListener;

public class PopupView extends PopupWindows implements OnDismissListener {

	private ViewGroup mRootView;
	private int rootWidth = 0;
	private OnDismissListener mDismissListener;
	private Context ctx;

	public PopupView(Context context, View v) {
		super(context);
		ctx = context;
		LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mRootView = (ViewGroup) mInflater.inflate(R.layout.seed_popupview, null);
		mRootView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		mRootView.addView(v);
		setContentView(mRootView);
	}
	
	public PopupView(Context context, View v, boolean noBg) {
		super(context);
		ctx = context;
		LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		if(noBg){
			mRootView = (ViewGroup) mInflater.inflate(R.layout.seed_popupview_no_bg, null);
		}else mRootView = (ViewGroup) mInflater.inflate(R.layout.seed_popupview, null);
		mRootView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		mRootView.addView(v);
		setContentView(mRootView);
	}
	
	public ViewGroup getRootView(){
		return mRootView;
	}
	
	/**
	 * Show quickaction popup. 
	 * 
	 */
	public void show(View anchor, int x, int y, int width){
		preShow();	
		Drawable popupBackground = mRootView.getBackground();
		if (popupBackground != null) {
			Rect mTempRect = new Rect();
            popupBackground.getPadding(mTempRect);
            y -= mTempRect.top;
		}
		if(width != 0) mWindow.setWidth(width);
		mWindow.setAnimationStyle(R.style.Animations_PopDownMenu_Reflect);
		mWindow.showAtLocation(anchor, Gravity.NO_GRAVITY, x, y);
	}
	
	/**
	 * Show quickaction popup. 
	 * 
	 */
	public void show(View anchor, int x, int y, int width, int height){
		preShow();	
		Drawable popupBackground = mRootView.getBackground();
		if (popupBackground != null) {
			Rect mTempRect = new Rect();
            popupBackground.getPadding(mTempRect);
            y -= mTempRect.top;
		}
		if(width != 0) mWindow.setWidth(width);
		if(height != 0) mWindow.setHeight(height);
		mWindow.setAnimationStyle(R.style.Animations_PopDownMenu_Reflect);
		mWindow.showAtLocation(anchor, Gravity.NO_GRAVITY, x, y);
	}

	/**
	 * Show quickaction popup. Popup is automatically positioned, on top or
	 * bottom of anchor view.
	 * 
	 */
	public void show(View anchor) {
		preShow();

		int xPos, yPos;

		int[] location = new int[2];

		anchor.getLocationOnScreen(location);

		Rect anchorRect = new Rect(location[0], location[1], location[0] + anchor.getWidth(), location[1]
				+ anchor.getHeight());

		// mRootView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
		// LayoutParams.WRAP_CONTENT));

		mRootView.measure(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

		int rootHeight = mRootView.getMeasuredHeight();

		if (rootWidth == 0) {
			rootWidth = mRootView.getMeasuredWidth();
		}

		int screenWidth = mWindowManager.getDefaultDisplay().getWidth();
		int screenHeight = mWindowManager.getDefaultDisplay().getHeight();

		// automatically get X coord of popup (top left)
		if ((anchorRect.left + rootWidth) > screenWidth) {
			xPos = anchorRect.left - (rootWidth - anchor.getWidth());
			xPos = (xPos < 0) ? 0 : xPos;

		} else {
			if (anchor.getWidth() > rootWidth) {
				xPos = anchorRect.centerX() - (rootWidth / 2);
			} else {
				xPos = anchorRect.left;
			}

		}

		int dyTop = anchorRect.top;
		int dyBottom = screenHeight - anchorRect.bottom;

		boolean onTop = (dyTop > dyBottom) ? true : false;

		if (onTop) {
			yPos = anchorRect.top - rootHeight;
		} else {
			yPos = anchorRect.bottom;
		}

		mWindow.setAnimationStyle((onTop) ? R.style.Animations_PopUpMenu_Reflect
				: R.style.Animations_PopDownMenu_Reflect);
		mWindow.showAtLocation(anchor, Gravity.NO_GRAVITY, xPos, yPos);
	}

	/**
	 * Set listener for window dismissed. This listener will only be fired if
	 * the quicakction dialog is dismissed by clicking outside the dialog or
	 * clicking on sticky item.
	 */
	public void setOnDismissListener(PopupView.OnDismissListener listener) {
		setOnDismissListener(this);

		mDismissListener = listener;
	}

	@Override
	public void onDismiss() {
		if (mDismissListener != null) {
			mDismissListener.onDismiss();
		}
	}

	/**
	 * Listener for window dismiss
	 * 
	 */
	public interface OnDismissListener {
		public abstract void onDismiss();
	}

}
