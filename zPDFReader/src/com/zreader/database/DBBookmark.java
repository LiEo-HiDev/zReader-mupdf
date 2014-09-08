package com.zreader.database;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;



public class DBBookmark {
	
	public static final String DATABASE_NAME = "zReaderBookmark.db";
	public static final String KEY_ID = "_id";
	public static final String KEY_PATH = "path";
	public static final String KEY_BOOKNAME = "bookname";
	public static final String KEY_PAGE = "page";
	public static final String KEY_MARK_NAME = "markname";
	public static final String KEY_ADDTIME = "addtime";

	private static final int DB_VERSION = 1;
	private static final String TABLE_BOOKMARK = "tablebookmark";
	
	private static final String DATABASE_CREATE = "CREATE TABLE IF NOT EXISTS " + TABLE_BOOKMARK + " ("
			+ KEY_ID + " INTEGER PRIMARY KEY, " 
			+ KEY_PATH + " TEXT, "
			+ KEY_BOOKNAME+ " TEXT, " 
			+ KEY_PAGE + " INTEGER, "
			+ KEY_MARK_NAME + " TEXT, "
			+ KEY_ADDTIME + " TEXT)";
	
	private final Context context;
	private DatabaseHelper DBHelper;
	private SQLiteDatabase db;
	
	public DBBookmark(Context ctx) {
		this.context = ctx;
		DBHelper = new DatabaseHelper(context);
	}

	public DBBookmark open() throws SQLException {
		db = DBHelper.getWritableDatabase();
		return this;
	}

	public void close() {
		DBHelper.close();
	}
	
	public void addBookmark(String path, String bookName, int page, String markName, String addTime){
		ContentValues cv = new ContentValues();
		cv.put(KEY_PATH, path);
		cv.put(KEY_BOOKNAME, bookName);
		cv.put(KEY_PAGE, page);
		cv.put(KEY_MARK_NAME, markName);
		cv.put(KEY_ADDTIME, addTime);
		db.insert(TABLE_BOOKMARK, null, cv);
	}
	
	public void deleteBookmarkFromPath(String path, int page) {
		db.delete(TABLE_BOOKMARK, KEY_PATH + "= ? AND " + KEY_PAGE + "=" + page, new String[] {path});
	}
	
	public void deleteBookmarkFromBookName(String bookName, int page) {
		db.delete(TABLE_BOOKMARK, KEY_BOOKNAME + "= ? AND " + KEY_PAGE + "=" + page, new String[] {bookName});
	}
	
	public void deleteAllFormPath(String path) {
		db.delete(TABLE_BOOKMARK,  KEY_PATH + "= ?", new String[] {path});
	}
	
	public void deleteAllFormBookName(String bookName) {
		db.delete(TABLE_BOOKMARK,  KEY_BOOKNAME + "= ?", new String[] {bookName});
	}
	
	public ArrayList<BookmarkData> getAllFormPath(String path) {
		ArrayList<BookmarkData> bookmarks = new ArrayList<BookmarkData>();
		Cursor cur = db.query(true, TABLE_BOOKMARK, new String[] { KEY_PATH, KEY_BOOKNAME, KEY_PAGE, KEY_MARK_NAME,
				KEY_ADDTIME }, KEY_PATH + "= ?", new String[] {path}, null, null, KEY_PAGE, null);
		if (cur != null) {
			if (cur.moveToFirst()) {
				do {
					BookmarkData bm = new BookmarkData();
					bm.filePath = cur.getString(0);
					bm.bookName = cur.getString(1);
					bm.page = cur.getInt(2);
					bm.markName = cur.getString(3);
					bm.addTime = cur.getString(4);
					bookmarks.add(bm);
				} while (cur.moveToNext());
			}
		}
		return bookmarks;
	}
	
	public ArrayList<BookmarkData> getAllFormBookName(String bookName) {
		ArrayList<BookmarkData> bookmarks = new ArrayList<BookmarkData>();
		Cursor cur = db.query(true, TABLE_BOOKMARK, new String[] { KEY_PATH, KEY_BOOKNAME, KEY_PAGE, KEY_MARK_NAME,
				KEY_ADDTIME }, KEY_BOOKNAME + "= ?", new String[] {bookName}, null, null, KEY_PAGE, null);
		if (cur != null) {
			if (cur.moveToFirst()) {
				do {
					BookmarkData bm = new BookmarkData();
					bm.filePath = cur.getString(0);
					bm.bookName = cur.getString(1);
					bm.page = cur.getInt(2);
					bm.markName = cur.getString(3);
					bm.addTime = cur.getString(4);
					bookmarks.add(bm);
				} while (cur.moveToNext());
			}
		}
		return bookmarks;
	}
	
	public boolean isMarkFormPath(String path, int page) {
		Cursor cur = db.query(true, TABLE_BOOKMARK, new String[] { KEY_PAGE }, KEY_PATH + "= ?", new String[] {path},
				null, null, null, null);
		if (cur != null) {
			if (cur.moveToFirst()) {
				do {
					if (page == cur.getInt(0)) {
						return true;
					}
				} while (cur.moveToNext());
			}
			cur.close();
		}
		return false;
	}
	
	public boolean isMarkFormBookName(String bookName, int page) {
		Cursor cur = db.query(true, TABLE_BOOKMARK, new String[] { KEY_PAGE }, KEY_BOOKNAME + "= ?", new String[] {bookName},
				null, null, null, null);
		if (cur != null) {
			if (cur.moveToFirst()) {
				do {
					if (page == cur.getInt(0)) {
						return true;
					}
				} while (cur.moveToNext());
			}
			cur.close();
		}
		return false;
	}

	private static class DatabaseHelper extends SQLiteOpenHelper {

		DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DB_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(DATABASE_CREATE);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		}

	}
}
