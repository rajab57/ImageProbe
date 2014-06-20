package com.codepath.examples.navdrawerdemo;

import java.util.ArrayList;

import android.app.ActionBar;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.xylon.imageprobe.R;

public class FragmentNavigationDrawer extends DrawerLayout {
	private ActionBarDrawerToggle drawerToggle;
	private ListView lvDrawer;
	private ArrayAdapter<String> drawerAdapter;
	private ArrayList<FragmentNavItem> drawerNavItems;
	private int drawerContainerRes;

	public FragmentNavigationDrawer(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
	}

	public FragmentNavigationDrawer(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public FragmentNavigationDrawer(Context context) {
		super(context);
	}

	// setupDrawerConfiguration((ListView) findViewById(R.id.lvDrawer),
	// R.layout.drawer_list_item, R.id.flContent);
	public void setupDrawerConfiguration(ListView drawerListView,
			int drawerItemRes, int drawerContainerRes) {
		// Setup navigation items array
		drawerNavItems = new ArrayList<FragmentNavigationDrawer.FragmentNavItem>();
		// Set the adapter for the list view
		drawerAdapter = new ArrayAdapter<String>(getActivity(), drawerItemRes,
				new ArrayList<String>());
		this.drawerContainerRes = drawerContainerRes;
		// Setup drawer list view and related adapter
		lvDrawer = drawerListView;
		lvDrawer.setAdapter(drawerAdapter);
		lvDrawer.setDescendantFocusability(FOCUS_BLOCK_DESCENDANTS);
		// Setup item listener
		lvDrawer.setOnItemClickListener(new FragmentDrawerItemListener());
		// ActionBarDrawerToggle ties together the the proper interactions
		// between the sliding drawer and the action bar app icon
		drawerToggle = setupDrawerToggle();
		setDrawerListener(drawerToggle);
		// set a custom shadow that overlays the main content when the drawer
		setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
		// Setup action buttons
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);
	}

	// ** Modified by raji - Addded Bundle argumment
	// addNavItem("First", "First Fragment", FirstFragment.class)
	public void addNavItem(String navTitle, String windowTitle,
			Class<? extends Fragment> fragmentClass, Bundle bundle) {
		drawerAdapter.add(navTitle);
		drawerNavItems.add(new FragmentNavItem(windowTitle, fragmentClass,
				bundle));
	}

	public void addNavItem(String navTitle, String windowTitle,
			Class<? extends Fragment> fragmentClass) {
		drawerAdapter.add(navTitle);
		drawerNavItems.add(new FragmentNavItem(windowTitle, fragmentClass));
	}

	// ** Modified by raji to support DialogFragment
	// TODO better way
	/** Swaps fragments in the main content view */
	public void selectDrawerItem(int position) {
		// Create a new fragment and specify the planet to show based on
		// position
		FragmentNavItem navItem = drawerNavItems.get(position);
		FragmentManager fragmentManager = getActivity()
				.getSupportFragmentManager();

		if (navItem.fragmentClass.getSuperclass() == DialogFragment.class) {
			// Handle DialogFragment
			DialogFragment fragment = null;
			try {
				fragment = (DialogFragment) navItem.getFragmentClass()
						.newInstance(); // hack
				Bundle args = navItem.getFragmentArgs();
				if (args != null) {
					fragment.setArguments(args);
				}
				// Remove prev DialogFragment if any
				// TODO ? How to get the tag ?
				Fragment prev = fragmentManager.findFragmentByTag("filter");
				if (prev != null)
					fragmentManager.beginTransaction().remove(prev);
				// Clear up the parent as well
				Fragment content = fragmentManager.findFragmentById(drawerContainerRes);
				if (content != null)
					fragmentManager.beginTransaction().remove(content);
				setTitle(navItem.getTitle());
				fragmentManager.beginTransaction().addToBackStack(null);
				fragment.show(fragmentManager.beginTransaction(), "filter");
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			// Should support ListFragment and Preferences Fragment as well
			Fragment fragment = null;
			try {
				fragment = navItem.getFragmentClass().newInstance();
				Bundle args = navItem.getFragmentArgs();
				if (args != null) {
					fragment.setArguments(args);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			// Insert the fragment by replacing any existing fragment;
			fragmentManager.beginTransaction().replace(drawerContainerRes, fragment).commit();
		}

		// Highlight the selected item, update the title, and close the drawer
		lvDrawer.setItemChecked(position, true);

		if (navItem.fragmentClass.getSuperclass() != DialogFragment.class)
			closeDrawer(lvDrawer);
	}

	public ActionBarDrawerToggle getDrawerToggle() {
		return drawerToggle;
	}

	private FragmentActivity getActivity() {
		return (FragmentActivity) getContext();
	}

	private ActionBar getActionBar() {
		return getActivity().getActionBar();
	}

	// TODO handling dialog Fragment raji
	private void setTitle(CharSequence title) {
		getActionBar().setTitle(title);
	}

	private class FragmentDrawerItemListener implements
			ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			selectDrawerItem(position);
		}
	}

	private class FragmentNavItem {
		private Class<? extends Fragment> fragmentClass;
		private String title;
		private Bundle fragmentArgs;

		public FragmentNavItem(String title,
				Class<? extends Fragment> fragmentClass) {
			this(title, fragmentClass, null);
		}

		public FragmentNavItem(String title,
				Class<? extends Fragment> fragmentClass, Bundle args) {
			this.fragmentClass = fragmentClass;
			this.fragmentArgs = args;
			this.title = title;
		}

		public Class<? extends Fragment> getFragmentClass() {
			return fragmentClass;
		}

		public String getTitle() {
			return title;
		}

		public Bundle getFragmentArgs() {
			return fragmentArgs;
		}

	}

	private ActionBarDrawerToggle setupDrawerToggle() {
		return new ActionBarDrawerToggle(getActivity(), /* host Activity */
		this, /* DrawerLayout object */
		R.drawable.ic_drawer, /* nav drawer image to replace 'Up' caret */
		R.string.drawer_open, /* "open drawer" description for accessibility */
		R.string.drawer_close /* "close drawer" description for accessibility */
		) {
			public void onDrawerClosed(View view) {
				// setTitle(getCurrentTitle());
				getActivity().invalidateOptionsMenu(); // call
														// onPrepareOptionsMenu()
			}

			public void onDrawerOpened(View drawerView) {
				// setTitle("Navigate");

				getActivity().invalidateOptionsMenu(); // call
														// onPrepareOptionsMenu()
			}
		};
	}

	public boolean isDrawerOpen() {
		return isDrawerOpen(lvDrawer);
	}
}
