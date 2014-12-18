package com.zreader.main;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

import com.androidquery.AQuery;
import com.artifex.mupdfdemo.AsyncTask;
import com.artifex.mupdfdemo.MuPDFCore;
import com.artifex.mupdfdemo.SearchTaskResult;
import com.squareup.picasso.Picasso;
import com.zreader.utils.PreferencesReader;

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

public class searchItemAdapter  extends BaseAdapter{

	private ArrayList<SearchTaskResult> searchItem = new ArrayList<SearchTaskResult>();
	private Context mContext;
	private MuPDFCore mCore;
	private String mPath;
	private int setWidth = 0;
	private float pageWidth, pageHeight;
	private float fixHeight, fixWidth;
	
	public searchItemAdapter(Context context, MuPDFCore core, ArrayList<SearchTaskResult> result) {
		mContext = context;
		mCore = core;
		searchItem.addAll(result);
		mPath = /*core.getFileDirectory()*/PreferencesReader.getDataDir(mContext) + "/thumbnail";
		File mCacheDirectory = new File(mPath);
		if (!mCacheDirectory.exists())
			mCacheDirectory.mkdirs();
		
		PointF point = mCore.getSinglePageSize(0);
		pageWidth = point.x;
		pageHeight = point.y;
	}
	
	@Override
	public int getCount() {
		return searchItem.size();
	}

	@Override
	public Object getItem(int position) {
		return searchItem.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		AQuery aq;
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(R.layout.search_item, parent, false);
			aq = new AQuery(convertView);
		} else {
			aq = new AQuery(convertView);
		}
		
		aq.id(R.id.searchResultTitle).text("Page "+(searchItem.get(position).pageNumber+1));
		aq.id(R.id.searchResultDetail).text(searchItem.get(position).searchBoxes.length + " Results");
		
		if (fixHeight == 0) {
			fixHeight = (float) aq.id(R.id.searchThumbnail).getImageView().getMeasuredHeight();
		}
		fixWidth = pageWidth / pageHeight * fixHeight;
		if (fixWidth != setWidth) {
			setWidth = (int) fixWidth;
		}

		if (setWidth != 0) {
			aq.id(R.id.searchThumbnail).width(setWidth, false);
		}
		
		drawPageImageView(aq.id(R.id.searchThumbnail).getImageView(), searchItem.get(position).pageNumber);
		return convertView;
	}
	
	private void drawPageImageView(ImageView v, int position) {
		if (cancelPotentialWork(v, position)) {

			final BitmapWorkerTask task = new BitmapWorkerTask(v, position);
			final AsyncDrawable asyncDrawable = new AsyncDrawable(mContext.getResources(), null, task);
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

}
