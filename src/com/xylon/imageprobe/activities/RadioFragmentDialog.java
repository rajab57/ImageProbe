package com.xylon.imageprobe.activities;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.xylon.imageprobe.R;
import com.xylon.imageprobe.model.Filters;
import com.xylon.imageprobe.utils.SharedPreferencesUtils;

public class RadioFragmentDialog extends DialogFragment {

	String[] array;
	String title;
	String selValue;
	RadioGroup radioGroup;
	RadioGroup radioGroup2;
	Filters searchFilters;

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		int arryId = getArguments().getInt("array");
		title = getArguments().getString("title");
		searchFilters = (Filters) getArguments().getSerializable("filters");
		final Dialog dialog = new Dialog(getActivity());
		//dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.fragment_radio);
		dialog.setTitle(getArguments().getString("navTitle"));
		array = getActivity().getResources().getStringArray(arryId);
		LinearLayout layout = (LinearLayout) dialog.findViewById(R.id.radioll);
		Button saveButton = (Button) dialog.findViewById(R.id.saveBtn);
		Button cancelButton = (Button) dialog.findViewById(R.id.cancelBtn);
		if (array.length > 10) {
			// Create 2 radio groups
			createTwoRadioGroups(layout);
			loadPreferencesFromFile(radioGroup,title);
			loadPreferencesFromFile(radioGroup2,title);
		} else {
			createOneRadioGroup(layout);
			loadPreferencesFromFile(radioGroup,title);
		}

		saveButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				int selectedId;
				RadioButton rb = null;
				// get selected radio button from radioGroup
				int chkId1 = radioGroup.getCheckedRadioButtonId();
				if (array.length > 10) {

					int chkId2 = radioGroup2.getCheckedRadioButtonId();
					selectedId = chkId1 == -1 ? chkId2 : chkId1;
					if (chkId1 == -1 )
						rb = (RadioButton) radioGroup2.findViewById(chkId2);
					else
						rb = (RadioButton) radioGroup.findViewById(chkId1);	
				} else {
					selectedId = chkId1;
					if (selectedId != -1) {
						// find the radiobutton by returned id
						rb = (RadioButton) radioGroup
								.findViewById(selectedId);
					}
				}

				if (rb != null) {
					selValue = rb.getText().toString();
					// Save the preset values to persist
					SharedPreferencesUtils.SavePreferences(getActivity(),
							title, selValue);
				}

				// update filter object for next search
				if (selValue != null && !selValue.equals("")) {
					if (title.equals("color"))
						searchFilters.setImgColor(selValue);
					else if (title.equals("size"))
						searchFilters.setImgSize(selValue);
					else if (title.equals("type"))
						searchFilters.setImgType(selValue);
					else if (title.equals("site"))
						searchFilters.setSiteToSearch(selValue);
				}

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

	private void createOneRadioGroup(LinearLayout layout) {
		radioGroup = new RadioGroup(getActivity());
		LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.FILL_PARENT,
				LinearLayout.LayoutParams.WRAP_CONTENT);
		layout.addView(radioGroup, p);
		int count = 0;
		for (String item : array) {
			RadioButton radioButtonView = new RadioButton(getActivity());
			radioButtonView.setId(count++);
			radioButtonView.setText(item);
			radioGroup.addView(radioButtonView, p);
		}

	}

	private void createTwoRadioGroups(LinearLayout layout) {
		radioGroup = new RadioGroup(getActivity());
		radioGroup2 = new RadioGroup(getActivity());

		LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.WRAP_CONTENT,
				LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
		layout.addView(radioGroup, p);
		layout.addView(radioGroup2, p);
		int count = 0;
		for (String item : array) {
			RadioButton radioButtonView = new RadioButton(getActivity());
			radioButtonView.setId(count);
			radioButtonView.setText(item);
			if (count < array.length / 2)
				radioGroup.addView(radioButtonView, p);
			else
				radioGroup2.addView(radioButtonView, p);
			++count;
		}

		radioGroup.setOnCheckedChangeListener(listener1);
		radioGroup2.setOnCheckedChangeListener(listener2);

	}
	
	// initial values on the dialog
	private void loadPreferencesFromFile(RadioGroup rg, String key) {
		String prevSelValue = SharedPreferencesUtils.LoadPreferences(getActivity(), key);
		for (int i = 0; i < rg.getChildCount(); i++) {
			RadioButton rd = ((RadioButton) rg.getChildAt(i));
			String rdVal  = rd.getText().toString();
			if (rdVal.equals(prevSelValue)) {
				radioGroup.check(rd.getId());
			}
		}
	}

	private OnCheckedChangeListener listener1 = new OnCheckedChangeListener() {

		@Override
		public void onCheckedChanged(RadioGroup group, int checkedId) {
			if (checkedId != -1) {
				radioGroup2.setOnCheckedChangeListener(null); // remove the
																// listener
																// before
																// clearing so
																// we don't
																// throw that
																// stackoverflow
																// exception(like
																// Vladimir
																// Volodin
																// pointed out)
				radioGroup2.clearCheck(); // clear the second RadioGroup!
				radioGroup2.setOnCheckedChangeListener(listener2); // reset the
																	// listener
				Log.e("XXX2", "do the work");
			}
		}
	};

	private OnCheckedChangeListener listener2 = new OnCheckedChangeListener() {

		@Override
		public void onCheckedChanged(RadioGroup group, int checkedId) {
			if (checkedId != -1) {
				radioGroup.setOnCheckedChangeListener(null);
				radioGroup.clearCheck();
				radioGroup.setOnCheckedChangeListener(listener1);
				Log.e("XXX1", "do the work");
			}
		}
	};

}
