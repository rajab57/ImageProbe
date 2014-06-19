package com.xylon.imageprobe.utils;


import android.content.Context;
import android.content.SharedPreferences;
import android.widget.RadioButton;

public class SharedPreferencesUtils {

	public static void SavePreferences(Context context, String key, String value) {
		//String key = context.getResources().getResourceName(resid);
		SharedPreferences sharedPreferences = context.getSharedPreferences(
				"MY_SHARED_PREF", Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putString(key, value);
		editor.commit();
	}

	
	public static String LoadPreferences(final Context context, final String key ) {
		//String resName = context.getResources().getResourceName(resId);
		SharedPreferences sharedPreferences = context.getSharedPreferences(
				"MY_SHARED_PREF", Context.MODE_PRIVATE);
		String value = sharedPreferences.getString(key, "");
		return value;
	}
}
