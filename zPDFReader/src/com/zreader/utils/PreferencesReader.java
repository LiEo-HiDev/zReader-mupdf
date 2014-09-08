package com.zreader.utils;

import java.io.File;

import com.artifex.mupdfdemo.MuPDFCore;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Environment;

public class PreferencesReader {
	
	
	public static String rePlaceString(String str){
		return str.replace("/", "_").replace(".", "_").replace(" ", "_");
	}
	
	public static String getDataDir(Context context) {
		String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
		return extStorageDirectory + "/Android/data/" + context.getPackageName();
	}
	
	public static void saveThemeMode(Activity act, int theme){
		SharedPreferences prefs = act.getPreferences(Context.MODE_PRIVATE);
		SharedPreferences.Editor edit = prefs.edit();
		edit.putInt("thememode", theme);
		edit.commit();
	}
	
	public static int getThemeMode(Activity act) {
		SharedPreferences prefs = act.getPreferences(Context.MODE_PRIVATE);
		return prefs.getInt("thememode", MuPDFCore.PAPER_NORMAL);
	}
	
	public static void savePageMode(Activity act, int pageMode) {
		SharedPreferences prefs = act.getPreferences(Context.MODE_PRIVATE);
		SharedPreferences.Editor edit = prefs.edit();
		edit.putInt("pagemode", pageMode);
		edit.commit();
	}
	
	public static int getPageMode(Activity act) {
		SharedPreferences prefs = act.getPreferences(Context.MODE_PRIVATE);
		return prefs.getInt("pagemode", MuPDFCore.AUTO_PAGE_MODE);
	}
	
	public static void saveShowCoverPageMode(Activity act, boolean showCover){
		SharedPreferences prefs = act.getPreferences(Context.MODE_PRIVATE);
		SharedPreferences.Editor edit = prefs.edit();
		edit.putBoolean("showcoverpage", showCover);
		edit.commit();
	}
	
	public static boolean isShowCoverPageMode(Activity act) {
		SharedPreferences prefs = act.getPreferences(Context.MODE_PRIVATE);
		return prefs.getBoolean("showcoverpage", true);
	}
	
	public static void saveReflowMode(Activity act, boolean reflow) {
		SharedPreferences prefs = act.getPreferences(Context.MODE_PRIVATE);
		SharedPreferences.Editor edit = prefs.edit();
		edit.putBoolean("reflowmode", reflow);
		edit.commit();
	}
	
	public static boolean isReflow(Activity act) {
		SharedPreferences prefs = act.getPreferences(Context.MODE_PRIVATE);
		return prefs.getBoolean("reflowmode", false);
	}
	
	public static boolean isFirstTime(Activity act){
		SharedPreferences prefs = act.getPreferences(Context.MODE_PRIVATE);
		boolean isFirst = prefs.getBoolean("isfirsttime", true);
		if(isFirst) {
			SharedPreferences.Editor edit = prefs.edit();
			edit.putBoolean("isfirsttime", false);
			edit.commit();
			return true;
		}
		return false;
	}
	
	public static String getCurrentPathBrowse(Activity act) {
		SharedPreferences prefs = act.getPreferences(Context.MODE_PRIVATE);
		return prefs.getString("currentpathbrowse", "");
	}
	
	public static void saveCurrentPathBrowse(Activity act, String path) {
		SharedPreferences prefs = act.getPreferences(Context.MODE_PRIVATE);
		SharedPreferences.Editor edit = prefs.edit();
		edit.putString("currentpathbrowse", path);
		edit.commit();
	}
	
	public static boolean getRemoveAdsPurchased(Activity act){
		SharedPreferences prefs = act.getPreferences(Context.MODE_PRIVATE);
		return prefs.getBoolean("removeadspurchased", false);
	}
	
	public static void saveRemoveAdsPurchased(Activity act, boolean purchased) {
		SharedPreferences prefs = act.getPreferences(Context.MODE_PRIVATE);
		SharedPreferences.Editor edit = prefs.edit();
		edit.putBoolean("removeadspurchased", purchased);
		edit.commit();
	}

}
