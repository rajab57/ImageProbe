package com.xylon.imageprobe.activities;


import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.RelativeLayout;

import com.etsy.android.grid.StaggeredGridView;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.xylon.imageprobe.R;
import com.xylon.imageprobe.adapters.ImageResultsStaggeredViewAdapter;
import com.xylon.imageprobe.listeners.EndlessScrollListener;
import com.xylon.imageprobe.model.ImageResult;
import com.xylon.imageprobe.utils.NetworkingUtils;

public class SearchResultFragment extends Fragment {
	
	private static String TAG = SearchResultFragment.class.getSimpleName();
	StaggeredGridView gvResults;
	RelativeLayout bac_dim_layout;
	SearchActivity parentActivity;
	
	
	ArrayList<ImageResult> imageResults = new ArrayList<ImageResult>();
	ImageResultsStaggeredViewAdapter staggeredImageAdapter;

	boolean mHasRequestedMore = false;
	private static int MAX_SIZE = 8;
	
	   public static SearchResultFragment newInstance(SearchActivity parent) {
	    	SearchResultFragment mFrgment = new SearchResultFragment(parent);
	    	
	        return mFrgment;
	    }
	   
	  private SearchResultFragment(SearchActivity parent) {
		  this.parentActivity = parent;
	  }
	  
	  public void init() {
		  mHasRequestedMore = false;
		  imageResults.clear();
	  }

	    @Override
	    public View onCreateView(LayoutInflater inflater, ViewGroup container,
	            Bundle savedInstanceState) {
	        
	    	System.out.println("Fragment view is created ");
	        View v = inflater.inflate(R.layout.fragment_main, null);
			setupViews(v);

			// init universal image loader
			ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(parentActivity));

			staggeredImageAdapter = new ImageResultsStaggeredViewAdapter(parentActivity,imageResults);
			gvResults.setAdapter(staggeredImageAdapter);
			
			//listeners for scrolling and onItemClick(detail view of image)
			setupListeners();
	 
	        return v;
	    }
	    
		public void setupViews(View v) {
			System.out.println("setting up views");
			gvResults = (StaggeredGridView) v.findViewById(R.id.gvResults);
			bac_dim_layout = (RelativeLayout) v.findViewById(R.id.bac_dim_layout);

		}
		
		public void setupListeners() {
			// Load more images on scrolling
			gvResults.setOnScrollListener(new EndlessScrollListener() {
				@Override
				public void onLoadMore(int page, int totalItemsCount) {
					// Triggered only when new data needs to be appended to the list
					// Add whatever code is needed to append new items to your
					// AdapterView
					Log.d(TAG, "onLoadMore");
					customLoadMoreDataFromApi(page);
					// or customLoadMoreDataFromApi(totalItemsCount);
				}
			});

			// See image full-screen on item click
			gvResults.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> adapter, View parent,
						int position, long arg3) {
					System.out.println("activity onclick");
					Intent i = new Intent(getActivity().getApplicationContext(),
							ImageDisplayActivity.class);
					ImageResult imageResult = imageResults.get(position);
					i.putExtra("result", imageResult);
					startActivity(i);
				}
			});		
		}
		
		public void customLoadMoreDataFromApi(int start) {
			// TODO: how to pass data to fragments ?
			String query = parentActivity.query;
			if (query != null && !query.equals("")) {
				AsyncHttpClient client = new AsyncHttpClient();
				String url = parentActivity.searchFilterSettings.formQuery(query, start, MAX_SIZE);
				Log.d(TAG, url);
				client.get(url, new JsonHttpResponseHandler() {
					@Override
					public void onSuccess(JSONObject response) {
						JSONArray imageJsonResultsArray = null;
						if (!response.isNull("responseData")) {
							try {
								int responseStatus = NetworkingUtils
										.checkResponseStatus(response, TAG);
								if (responseStatus == 200) {
									JSONObject imageJsonResults = response
											.getJSONObject("responseData");
									if (!imageJsonResults.isNull("results")) {
										imageJsonResultsArray = imageJsonResults
												.getJSONArray("results");
										staggeredImageAdapter.addAll(ImageResult
												.fromJSONArray(imageJsonResultsArray));
									}
								}
							} catch (JSONException e) {
								Log.d(TAG,e.toString());
							}
						}
					}
				});
			}
			mHasRequestedMore = false;
		}
		
	public void toggleDimScreen(boolean visible) {
		if (bac_dim_layout != null) {
			System.out.println("after view creation bac_dim not empty");
			if (visible)
				bac_dim_layout.setVisibility(View.VISIBLE);
			else
				bac_dim_layout.setVisibility(View.GONE);
		}
	}
	
		public void clearResults() {
			imageResults.clear();
		}

}
