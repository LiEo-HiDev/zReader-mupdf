package com.zreader.main;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.androidquery.AQuery;
import com.artifex.mupdfdemo.MuPDFActivity;
//import com.google.android.gms.ads.AdRequest;
//import com.google.android.gms.ads.AdView;
import com.se_ed.current.PaperActivity;
import com.zreader.database.DBRecentBooks;
import com.zreader.utils.LIstDocumentFile;
import com.zreader.utils.ZReaderUtils;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.GridView;
import android.widget.Toast;

public class ShowAllEBooksFragment extends Fragment {

	private AQuery aq;
	private AsyncTask<Void, Void, List<File>> scanFileAsync;
	private SmallCardAdapter adapter;
	private ArrayList<File> listFiles = new ArrayList<File>();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		Bundle args = getArguments();
		String title = args.getString("title");
		ActionBar actionbar = getActivity().getActionBar();
		actionbar.setTitle(title);

		final View rootView = inflater.inflate(R.layout.pager_gridview_layout, container, false);
		aq = new AQuery(rootView);
		scanFileAsync = new AsyncTask<Void, Void, List<File>>() {

			@Override
			protected List<File> doInBackground(Void... arg0) {
				String searchPaht;
				File storageUri = getActivity().getApplicationContext().getDatabasePath("/storage");
				if (storageUri != null && storageUri.exists()) {
					searchPaht = storageUri.getPath();
				} else {
					searchPaht = Environment.getExternalStorageDirectory().getPath();
				}
				List<File> files = ZReaderUtils.searchFileFrom(searchPaht, ".pdf");
				
				//Sorting File//
				List<LIstDocumentFile> listFile = new ArrayList<LIstDocumentFile>();
				for(File file : files) {
					listFile.add(new LIstDocumentFile(file, file.getName()));
				}
				
				Collections.sort(listFile, new Comparator<LIstDocumentFile>() {

					@Override
					public int compare(LIstDocumentFile lhs, LIstDocumentFile rhs) {
						// TODO Auto-generated method stub
						return lhs.fileName.compareToIgnoreCase(rhs.fileName);
					}
				});
				
				List<File> returnFiles = new ArrayList<File>();
				for(LIstDocumentFile lFile : listFile) {
					returnFiles.add(lFile.file);
				}
				return returnFiles;
			}

			@Override
			protected void onPostExecute(List<File> result) {
				if (result != null) {
					listFiles.addAll(result);
					adapter = new SmallCardAdapter(getActivity(), result);
					aq.id(R.id.progressBarBook).gone();
					if (result.size() == 0) {
						aq.id(R.id.textViewNoItem).visible();
					} else
						aq.id(R.id.textViewNoItem).gone();

					aq.id(R.id.PagerGridView).getGridView().setOnItemLongClickListener(new OnItemLongClickListener() {

						@Override
						public boolean onItemLongClick(AdapterView<?> arg0, View view, int arg2, long arg3) {	
							return false;
						}
					});
					
					aq.id(R.id.PagerGridView).adapter(adapter).itemClicked(new OnItemClickListener() {

						@Override
						public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
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
					
					
				}

			}

		};

		aq.id(R.id.progressBarBook).visible();
		scanFileAsync.execute();
		
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
	public void onDestroyView() {
		if (scanFileAsync != null)
			scanFileAsync.cancel(true);
		super.onDestroyView();
	}
	
	
//	@Override
//	public boolean onContextItemSelected(MenuItem item) {
//		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
//		GridView grid = aq.id(R.id.PagerGridView).getGridView();
//		File file = (File) grid.getItemAtPosition(info.position);
//		
//		if(item.getItemId() == R.id.file_open) {
//			if (file != null) {
//				DBRecentBooks db = new DBRecentBooks(getActivity()).open();
//				db.addOrUpdateRecentBook(file.getPath(), file.getName(), System.currentTimeMillis());
//				db.close();
//
//				Uri uri = Uri.fromFile(file);
//				Intent intent = new Intent(getActivity(), MuPDFActivity.class);
//				intent.setAction(Intent.ACTION_VIEW);
//				intent.setData(uri);
//				startActivity(intent);
//			}
//			return true;
//		}else if(item.getItemId() == R.id.file_open) {
//			return true;
//		}else if(item.getItemId() == R.id.file_rename) {
//			return true;
//		}else if(item.getItemId() == R.id.file_duplicate) {
//			return true;
//		}else if(item.getItemId() == R.id.file_move) {
//			return true;
//		}else if(item.getItemId() == R.id.file_delete) {
//			return true;
//		}else if(item.getItemId() == R.id.file_properties) {
//			return true;
//		}else if(item.getItemId() == R.id.file_share) {
//			return true;
//		}else 
//			return super.onContextItemSelected(item);
//	}

}
