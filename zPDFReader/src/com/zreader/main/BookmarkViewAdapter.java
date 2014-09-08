package com.zreader.main;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.androidquery.AQuery;
import com.artifex.mupdfdemo.AsyncTask;
import com.artifex.mupdfdemo.MuPDFActivity;
import com.artifex.mupdfdemo.MuPDFCore;
import com.squareup.picasso.Picasso;
import com.zreader.database.BookmarkData;
import com.zreader.main.ThumbnailViewAdapter.AsyncDrawable;
import com.zreader.main.ThumbnailViewAdapter.BitmapWorkerTask;
import com.zreader.utils.PreferencesReader;

public class BookmarkViewAdapter extends BaseAdapter {

	private Context mContext;
	private MuPDFCore mCore;
	private String mPath;
	private int currentlyViewing;
	private Drawable pd;
	private Bitmap mLoadingBitmap;
	private int setWidth = 0;
	private float pageWidth, pageHeight;
	private float fixHeight, fixWidth;
	private ArrayList<BookmarkData> bmDatas;

	public BookmarkViewAdapter(Context context, MuPDFCore core, ArrayList<BookmarkData> bookmark) {
		mContext = context;
		bmDatas = bookmark;
		mCore = core;
		mPath = /*core.getFileDirectory()*/PreferencesReader.getDataDir(mContext) + "/thumbnail";
		File mCacheDirectory = new File(mPath);
		if (!mCacheDirectory.exists())
			mCacheDirectory.mkdirs();
		// mLoadingBitmap =
		// BitmapFactory.decodeResource(mContext.getResources(),
		// R.drawable.ic_ebook_pdf);
		mLoadingBitmap = null;
		pd = new BitmapDrawable(mContext.getResources(), mLoadingBitmap);

		PointF point = mCore.getSinglePageSize(0);
		pageWidth = point.x;
		pageHeight = point.y;
	}

	@Override
	public int getCount() {
		return bmDatas.size();
	}

	@Override
	public Object getItem(int position) {
		return bmDatas.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.thumbnail_item, parent, false);
		}

//		int mPage = mCore.getDocumentPage(((MuPDFActivity) mContext).mCurrentPage);

		AQuery aq = new AQuery(convertView);
		aq.id(R.id.ThumbnailNumber).text("" + (bmDatas.get(position).page + 1));

//		if (mPage == position) {
//			aq.id(R.id.ThumbnailLayoutView).background(R.color.accent_color);
//			aq.id(R.id.ThumbnailNumber).textColorId(android.R.color.secondary_text_light);
//		} else {
//			aq.id(R.id.ThumbnailLayoutView).background(R.drawable.button);
//			aq.id(R.id.ThumbnailNumber).textColorId(android.R.color.secondary_text_dark);
//		}

		convertView.setTag(bmDatas.get(position).page);

//		if (pageWidth == 100 && pageHeight == 100) {
//			PointF point = mCore.getSinglePageSize(0);
//			pageWidth = point.x;
//			pageHeight = point.y;
//		}
		if (fixHeight == 0) {
			fixHeight = (float) aq.id(R.id.ThumbnailPageImageView).getImageView().getMeasuredHeight();
		}
		fixWidth = pageWidth / pageHeight * fixHeight;
		if (fixWidth != setWidth) {
			setWidth = (int) fixWidth;
		}

		if (setWidth != 0) {
			aq.id(R.id.ThumbnailPageImageView).width(setWidth, false);
		}

		drawPageImageView(aq.id(R.id.ThumbnailPageImageView).getImageView(), bmDatas.get(position).page);
		return convertView;
	}

	private void drawPageImageView(ImageView v, int position) {
		if (cancelPotentialWork(v, position)) {

			final BitmapWorkerTask task = new BitmapWorkerTask(v, position);
			final AsyncDrawable asyncDrawable = new AsyncDrawable(mContext.getResources(), mLoadingBitmap, task);
			v.setImageDrawable(asyncDrawable);
			task.execute();
		}
	}

	public static boolean cancelPotentialWork(ImageView v, int position) {
		final BitmapWorkerTask bitmapWorkerTask = getBitmapWorkerTask(v);

		if (bitmapWorkerTask != null) {
			final int bitmapPosition = bitmapWorkerTask.position;
			if (bitmapPosition != position) {
				bitmapWorkerTask.cancel(true);
			} else {
				return false;
			}
		}
		return true;
	}

	class BitmapWorkerTask extends AsyncTask<Integer, Void, File> {

		private final WeakReference<ImageView> viewHolderReference;
		private int position;
		private ImageView imgview;
		private Bitmap bmp = null;
		private AQuery aq;
		private Drawable d = null;

		public BitmapWorkerTask(ImageView v, int position) {
			viewHolderReference = new WeakReference<ImageView>(v);
			this.position = position;
			imgview = v;
			aq = new AQuery(mContext);
		}

		@Override
		protected File doInBackground(Integer... params) {
			File fi = getPageThumbnail(position);
			return fi;
		}

		@Override
		protected void onPostExecute(File file) {
			if (isCancelled()) {
				file = null;
			}
			if (viewHolderReference != null && file != null) {
				final ImageView imageview = viewHolderReference.get();
				if (imageview != null) {
					final BitmapWorkerTask bitmapWorkerTask = getBitmapWorkerTask(imageview);
					if (this == bitmapWorkerTask && imageview != null) {

						Picasso.with(mContext).load(file)
						// .placeholder(R.drawable.ic_loading)
						// .error(R.drawable.ic_no_picture)
								.into(imageview);

					}
				}
			}
		}

	}

	static class AsyncDrawable extends BitmapDrawable {
		private final WeakReference<BitmapWorkerTask> bitmapWorkerTaskReference;

		public AsyncDrawable(Resources res, Bitmap bitmap, BitmapWorkerTask bitmapWorkerTask) {
			super(res, bitmap);
			bitmapWorkerTaskReference = new WeakReference<BitmapWorkerTask>(bitmapWorkerTask);
		}

		public BitmapWorkerTask getBitmapWorkerTask() {
			return bitmapWorkerTaskReference.get();
		}
	}

	private static BitmapWorkerTask getBitmapWorkerTask(ImageView imageView) {
		if (imageView != null) {
			final Drawable drawable = imageView.getDrawable();
			if (drawable instanceof AsyncDrawable) {
				final AsyncDrawable asyncDrawable = (AsyncDrawable) drawable;
				return asyncDrawable.getBitmapWorkerTask();
			}
		}
		return null;
	}

	public Bitmap getPageThumbnailBitmap(int position) {
		Bitmap bmp = null;
		PointF pageSize = mCore.getPageSize(position);
		int sizeY = (int) convertDpToPixel(130, mContext);
		if (sizeY == 0)
			sizeY = 120;
		int sizeX = (int) (pageSize.x / pageSize.y * sizeY);
		Point newSize = new Point(sizeX, sizeY);
		bmp = Bitmap.createBitmap(newSize.x, newSize.y, Bitmap.Config.ARGB_8888);
		mCore.drawThumbnailPage(bmp, position, newSize.x, newSize.y, 0, 0, newSize.x, newSize.y);
		return bmp;
	}

	public File getPageThumbnail(int position) {
		String mCachedBitmapFilePath = mPath + "/" + mCore.getFilePathReplace() + "_" + position;
		File mCachedBitmapFile = new File(mCachedBitmapFilePath);
		Bitmap bmp = null;
		try {
			if (mCachedBitmapFile.exists() && mCachedBitmapFile.canRead()) {
				// bmp = BitmapFactory.decodeFile(mCachedBitmapFilePath);
				return mCachedBitmapFile;
			}
		} catch (Exception e) {
			e.printStackTrace();
			mCachedBitmapFile.delete();
			bmp = null;
		}

		PointF pageSize = mCore.getSinglePageSize(position);
		int sizeY = (int) convertDpToPixel(130, mContext);
		if (sizeY == 0)
			sizeY = 120;
		int sizeX = (int) (pageSize.x / pageSize.y * sizeY);
		Point newSize = new Point(sizeX, sizeY);
		bmp = Bitmap.createBitmap(newSize.x, newSize.y, Bitmap.Config.ARGB_8888);
		mCore.drawThumbnailPage(bmp, position, newSize.x, newSize.y, 0, 0, newSize.x, newSize.y);
		try {
			bmp.compress(CompressFormat.JPEG, 75, new FileOutputStream(mCachedBitmapFile));
		} catch (FileNotFoundException e) {
			mCachedBitmapFile.delete();
			e.printStackTrace();
		}
		return mCachedBitmapFile;
	}

	private static float convertDpToPixel(float dp, Context context) {
		Resources resources = context.getResources();
		DisplayMetrics metrics = resources.getDisplayMetrics();
		float px = dp * (metrics.densityDpi / 160f);
		return px;
	}

	public int getCurrentlyViewing() {
		return currentlyViewing;
	}

	public void setCurrentlyViewing(int currentlyViewing) {
		this.currentlyViewing = currentlyViewing;
		notifyDataSetChanged();
	}

	public void setCurrentlyViewingNoRefresh(int currentlyViewing) {
		this.currentlyViewing = currentlyViewing;
	}

}
