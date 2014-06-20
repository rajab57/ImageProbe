package com.xylon.imageprobe.activities;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.Toast;

import com.codepath.examples.navdrawerdemo.FragmentNavigationDrawer;
import com.xylon.imageprobe.R;
import com.xylon.imageprobe.activities.FilterDialogFragment.AlertPositiveListener;
import com.xylon.imageprobe.model.Filters;
import com.xylon.imageprobe.utils.NetworkingUtils;

public class SearchActivity extends FragmentActivity implements
		OnQueryTextListener,AlertPositiveListener{

	private static String TAG = SearchActivity.class.getSimpleName();

	FragmentNavigationDrawer dlDrawer;
	SearchView searchView;
	SearchResultFragment resultsFragment;

	String query;
	boolean mHasRequestedMore = false;
	Filters searchFilterSettings;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);
		searchFilterSettings = new Filters(this);

		// Create the main fragment
		resultsFragment = SearchResultFragment.newInstance(this);

		dlDrawer = (FragmentNavigationDrawer) findViewById(R.id.drawer_layout);
		// Setup drawer view
		dlDrawer.setupDrawerConfiguration(
				(ListView) findViewById(R.id.lvDrawer),
				R.layout.drawer_nav_item, R.id.content_frame);
		// Add nav items
		createRadioFilter(R.array.size,        "size" , "Size Filter" , searchFilterSettings );
		createRadioFilter(R.array.colorfilter, "color", "Color Filter" , searchFilterSettings );
		createRadioFilter(R.array.type,        "type" , "Type Filter" , searchFilterSettings );
		createEditTextFilter("site" , "Site Filter", searchFilterSettings);

	}
	
	private void createRadioFilter(int arrayId, String titleName, String navName, Filters filter ) {
		Bundle bundle = new Bundle();
		bundle.putInt("array", arrayId);
		bundle.putString("title", titleName);
		bundle.putSerializable("filters", filter);
		bundle.putString("navTitle", navName);
		dlDrawer.addNavItem(navName,  navName,RadioFragmentDialog.class, bundle);
		
	}
	
	private void createEditTextFilter(String titleName, String navName, Filters filter) {
		Bundle bundle = new Bundle();
		bundle.putString("title", titleName);
		bundle.putString("navTitle",navName);
		dlDrawer.addNavItem("Site Filter", navName, EditTextFragmentDialog.class, bundle);		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.search, menu);
		searchView = (SearchView) menu.findItem(R.id.action_search)
				.getActionView();
		searchView.setOnQueryTextListener(this);
		return true;
	}

	@Override
	protected void onResume() {
		super.onResume();
		System.out.println("ON RESUME called");
		if (resultsFragment != null)
			resultsFragment.toggleDimScreen(false);
	}

	// The following callbacks are called for the
	// SearchView.OnQueryChangeListener
	public boolean onQueryTextChange(String newText) {
		if (resultsFragment != null)
			resultsFragment.toggleDimScreen(true);
		// bac_dim_layout.setVisibility(View.VISIBLE);
		return false;
	}

	public boolean onQueryTextSubmit(String queryStr) {
		this.query = queryStr;
		// perform query here
		viewSearchResults();
		return true;
	}
	
	private void viewSearchResults() {
		if (query != null && !query.equals("")) {
			// open the SearchFragment to show the results of the search query
			FragmentTransaction transaction = getSupportFragmentManager()
					.beginTransaction();
			transaction.replace(R.id.content_frame, resultsFragment);
			transaction.addToBackStack(null);
			// Commit the transaction
			transaction.commit();
			resultsFragment.toggleDimScreen(false);
			resultsFragment.init(); // clear results here
			// Check for network connectivity
			boolean isInternetConnected = NetworkingUtils
					.isNetworkAvailable(getApplicationContext());
			if (isInternetConnected == false)
				Toast.makeText(this, "Not connected to the Internet",
						Toast.LENGTH_SHORT).show();
			else {
				resultsFragment.customLoadMoreDataFromApi(0);
				Toast.makeText(this, "Searching for " + query,
						Toast.LENGTH_SHORT).show();
			}
		}
	}

	// Make the search icon invisible when the drawer opens
	// and make it visible when the drawer closes
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		// If the nav drawer is open, hide action items related to the content
		if (dlDrawer.isDrawerOpen()) {
			menu.findItem(R.id.action_search).setVisible(false);
			searchView.setFocusable(false);
			searchView.setClickable(false);
			menu.findItem(R.id.action_settings).setVisible(false);
		}
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// The action bar home/up action should open or close the drawer.
		// ActionBarDrawerToggle will take care of this.
		if (dlDrawer.getDrawerToggle().onOptionsItemSelected(item)) {
			return true;
		} else {
			// Open the main fragment
			FragmentTransaction transaction = getSupportFragmentManager()
					.beginTransaction();
			transaction.replace(R.id.content_frame, resultsFragment);
			transaction.addToBackStack(null);
			// Commit the transaction
			transaction.commit();
			if (item.getItemId() == R.id.action_settings) {

				DialogFragment dialogFragment = new FilterDialogFragment(
						searchFilterSettings);
				dialogFragment
						.show(getSupportFragmentManager(), "filterdialog");
				return true;
			}

		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		dlDrawer.getDrawerToggle().syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		// Pass any configuration change to the drawer toggles
		dlDrawer.getDrawerToggle().onConfigurationChanged(newConfig);
	}

	// call back from FilterFragment Dialog
	// adjust the results based on the filter settings on the prev search
	@Override
	public void onPositiveClick() {
		if (query != null && !query.equals("")) {
			viewSearchResults();

		}
	}

}
