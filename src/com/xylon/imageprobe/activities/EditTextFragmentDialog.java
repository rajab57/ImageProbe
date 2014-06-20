package com.xylon.imageprobe.activities;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.xylon.imageprobe.R;
import com.xylon.imageprobe.model.Filters;
import com.xylon.imageprobe.utils.SharedPreferencesUtils;

public class EditTextFragmentDialog extends DialogFragment {

	String title;
	String textName;
	EditText etText;
	Filters searchFilters;

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		title = getArguments().getString("title");
		searchFilters = (Filters) getArguments().getSerializable("filters");
		final Dialog dialog = new Dialog(getActivity());
		dialog.setContentView(R.layout.fragment_edittext);
		dialog.setTitle(getArguments().getString("navTitle"));
		Button saveButton = (Button) dialog.findViewById(R.id.saveBtn);
		Button cancelButton = (Button) dialog.findViewById(R.id.cancelBtn);
		etText = (EditText)dialog.findViewById(R.id.etInput);
		loadPreferencesFromFile(etText,title);
		saveButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				textName = etText.getText().toString();
				if ( textName != null && !(textName.equals("")))
					SharedPreferencesUtils.SavePreferences(getActivity(),title, textName);
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
	private void loadPreferencesFromFile(EditText et, String key) {
		String prevSelValue = SharedPreferencesUtils.LoadPreferences(getActivity(), key);
		if (prevSelValue != null && !prevSelValue.equals(""))
			etText.setText(prevSelValue);
	}
}
