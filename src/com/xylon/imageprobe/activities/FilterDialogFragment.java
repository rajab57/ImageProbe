package com.xylon.imageprobe.activities;



import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.xylon.imageprobe.R;
import com.xylon.imageprobe.model.Filters;
import com.xylon.imageprobe.utils.SharedPreferencesUtils;

public class FilterDialogFragment extends DialogFragment {
	
	String selColor;
	String selType;
	String selSize;
	String siteName;
	Filters searchFilters;
	EditText etSite;
	
	Spinner colorSpinner;
	Spinner typeSpinner;
	Spinner sizeSpinner;
	
	 /** Declaring the interface, to invoke a callback function in the implementing activity class */
    AlertPositiveListener mAlertPositiveListener;
 
    /** An interface to be implemented in the hosting activity for "OK" button click listener */
    interface AlertPositiveListener {
        public void onPositiveClick();
    }
    
    
 
	
	public FilterDialogFragment(Filters searchFilterSettings) {
		this.searchFilters = searchFilterSettings;	
	}
 	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		final Dialog dialog = new Dialog(getActivity());
		dialog.setTitle("Advanced Filters");
		dialog.setContentView(R.layout.set_filter);
		dialog.getWindow().setBackgroundDrawable(new ColorDrawable(R.color.theme_color));
		
		dialog.show();
		Spinner colorSpinner = (Spinner)dialog.findViewById(R.id.colorSpinner);
		Spinner typeSpinner = (Spinner)dialog.findViewById(R.id.typeSpinner);
		Spinner sizeSpinner = (Spinner)dialog.findViewById(R.id.sizeSpinner);
		Button saveButton = (Button)dialog.findViewById(R.id.saveBtn);
		Button cancelButton = (Button)dialog.findViewById(R.id.cancelBtn);
		etSite = (EditText)dialog.findViewById(R.id.siteEntry);

		loadPreferencesFromFile(colorSpinner, "color");
		loadPreferencesFromFile(typeSpinner,"type");
		loadPreferencesFromFile(sizeSpinner, "size");
		etSite.setText(SharedPreferencesUtils.LoadPreferences(getActivity(),"site") );
		
//		colorSpinner.setOnItemSelectedListener(this);
//		typeSpinner.setOnItemSelectedListener(this);
//		sizeSpinner.setOnItemSelectedListener(this);
		
		mAlertPositiveListener = (AlertPositiveListener) getActivity();
		
		colorSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int pos,long id) {
				selColor = parent.getItemAtPosition(pos).toString();
		}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		
		typeSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int pos,long id) {
				selType = parent.getItemAtPosition(pos).toString();
		}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		
		sizeSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int pos,long id) {
				selSize = parent.getItemAtPosition(pos).toString();
		}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		
		
		
		saveButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				
				// Get the site name if entered.
				siteName = etSite.getText().toString();
				// Save the preset values to persist
				SharedPreferencesUtils.SavePreferences(getActivity(), "size",selSize);		
				SharedPreferencesUtils.SavePreferences(getActivity(), "color",selColor);
				SharedPreferencesUtils.SavePreferences(getActivity(), "type",selType);
				SharedPreferencesUtils.SavePreferences(getActivity(), "site",siteName);
				
				// update filter object for next search
				searchFilters.setImgColor(selColor);
				searchFilters.setImgSize(selSize);
				searchFilters.setImgType(selType);
				searchFilters.setSiteToSearch(siteName);
				mAlertPositiveListener.onPositiveClick();
				dismiss();
			}
		});

		cancelButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dismiss();
			}
		});
		return dialog;

	}
	
	// initial values on the dialog
	private void loadPreferencesFromFile(Spinner spinner, String key) {
		String prevSel = SharedPreferencesUtils.LoadPreferences(getActivity(),
				key);
		if (!prevSel.equals("")) {
			ArrayAdapter myAdap = (ArrayAdapter) spinner.getAdapter();
			int spinnerPosition = myAdap.getPosition(prevSel);
			// set the default according to value
			spinner.setSelection(spinnerPosition);
		}
	}
	
//	@Override
//	  public void onItemSelected(AdapterView<?> parent, View view, int pos,long id) {
//			Toast.makeText(parent.getContext(), 
//				"OnItemSelectedListener : " + parent.getItemAtPosition(pos).toString(),Toast.LENGTH_SHORT).show();
//			switch(parent.getId()) {
//			case R.id.colorSpinner:
//				selColor = parent.getItemAtPosition(pos).toString();
//				break;
//			case R.id.sizeSpinner:
//				selSize = parent.getItemAtPosition(pos).toString();
//				break;
//			case R.id.typeSpinner:
//				selType = parent.getItemAtPosition(pos).toString();
//				break;
//			}
//				
//		  }
//
//
//	@Override
//	public void onNothingSelected(AdapterView<?> arg0) {
//		// TODO Auto-generated method stub
//		
//	}


}
