package com.zreader.main;

import java.io.File;
import java.util.ArrayList;

import com.androidquery.AQuery;
import com.artifex.mupdfdemo.AsyncTask;
import com.artifex.mupdfdemo.MuPDFActivity;
//import com.google.android.gms.ads.AdRequest;
//import com.google.android.gms.ads.AdView;
import com.se_ed.current.PaperActivity;
import com.zreader.database.DBRecentBooks;
import com.zreader.database.RecentBooksData;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class RecentlyFragment extends Fragment {

	private AQuery aq;
	private SmallCardAdapter adapter;

	private AsyncTask<Void, Void, ArrayList<File>> taskRecent;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		setHasOptionsMenu(true);
		Bundle args = getArguments();
		String title = args.getString("title");
		ActionBar actionbar = getActivity().getActionBar();
		actionbar.setTitle(title);

		final View rootView = inflater.inflate(R.layout.pager_gridview_layout, container, false);
		aq = new AQuery(rootView);
		aq.id(R.id.PagerGridView).itemClicked(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
				File file = (File) aq.id(R.id.PagerGridView).getGridView().getItemAtPosition(position);
				if (file != null) {
					DBRecentBooks db = new DBRecentBooks(getActivity()).open();
					db.addOrUpdateRecentBook(file.getPath(), file.getName(), System.currentTimeMillis());
					db.close();

					Uri uri = Uri.fromFile(file);
					Intent intent = new Intent(getActivity(), MuPDFActivity.class);
					intent.setAction(Intent.ACTION_VIEW);
					intent.setData(uri);
					startActivity(intent);
				}
			}
		});

		taskRecent = new AsyncTask<Void, Void, ArrayList<File>>() {

			@Override
			protected ArrayList<File> doInBackground(Void... params) {
				DBRecentBooks db = new DBRecentBooks(getActivity()).open();
				ArrayList<File> recents = db.getAllFileRecentBooks();
				db.close();
				ArrayList<File> existing = new ArrayList<File>();
				for (File book : recents) {
					if (book.exists())
						existing.add(book);
				}
				return existing;
			}

			@Override
			protected void onPostExecute(ArrayList<File> result) {
				adapter = new SmallCardAdapter(getActivity(), result);
				adapter.isRecentsFragment(true);
				aq.id(R.id.PagerGridView).adapter(adapter);
				if (result.size() == 0) {
					aq.id(R.id.textViewNoItem).visible();
				} else
					aq.id(R.id.textViewNoItem).gone();
			}

		};
		taskRecent.execute();

		//Ads  
//		AdRequest.Builder adBuilder = new AdRequest.Builder();
//		adBuilder.addTestDevice("4BD85A785116AD9259A63E9FB48EDFE5");			
//		AdRequest adRequest = adBuilder.build();
//		AdView adView = (AdView) rootView.findViewById(R.id.adView);
//		adView.loadAd(adRequest);
		//Ads
//		LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
//		LocationListener locationListener = new LocationListener() {
//
//			@Override
//			public void onLocationChanged(Location location) {
//				//Ads  
//				AdRequest.Builder adBuilder = new AdRequest.Builder();
//				adBuilder.setLocation(location);
//				adBuilder.addTestDevice("4BD85A785116AD9259A63E9FB48EDFE5");			
//				AdRequest adRequest = adBuilder.build();
//				AdView adView = (AdView) rootView.findViewById(R.id.adView);
//				adView.loadAd(adRequest);
//				//Ads
//			}
//
//			@Override
//			public void onProviderDisabled(String arg0) {
//				// TODO Auto-generated method stub
//				
//			}
//
//			@Override
//			public void onProviderEnabled(String arg0) {
//				// TODO Auto-generated method stub
//				
//			}
//
//			@Override
//			public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
//				// TODO Auto-generated method stub
//				
//			}
//			
//		};
//		locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
		
		return rootView;
	}

	@Override
	public void onResume() {
		super.onResume();
		if (adapter != null && adapter.getCount() > 0)
			adapter.notifyDataSetChanged();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (taskRecent != null) {
			taskRecent.cancel(true);
			taskRecent = null;
		}
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.main_menu, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if(item.getItemId() == R.id.main_menu_clear_all_recents) {
			if(adapter != null && adapter.getCount() >0 ){
				DBRecentBooks db = new DBRecentBooks(getActivity()).open();
				db.deleteAll();
				db.close();
				adapter.clearAll();
				adapter.notifyDataSetChanged();
				aq.id(R.id.textViewNoItem).visible();
			}			
			return true;
		}else
			return super.onOptionsItemSelected(item);
	}

}
