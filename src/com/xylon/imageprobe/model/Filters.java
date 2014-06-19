package com.xylon.imageprobe.model;

import java.io.Serializable;

import android.content.Context;
import android.net.Uri;

import com.xylon.imageprobe.utils.SharedPreferencesUtils;

public class Filters implements Serializable {

	private static final long serialVersionUID = -7817459236475759717L;


	// TODO  2 copies of the array - one for the UI ,ane one here.
	// Merge both
	public static enum  Colors{
		any, black, blue, brown, gray, green, orange,pink,purple,red,
		teal,white,yellow 	
	}
	public static enum ImageSize {
		any, small, medium, large, extralarge
	}
	
	public static enum ImageType {
		any, faces, photo, clipart, lineart
	}
	String  siteToSearch;
	Colors imgColor;
	boolean color; // can be either color or black/white
	ImageSize imgSize;
	ImageType imgType;
	
	public Filters(Context context) {
		imgColor = Colors.any;
		imgSize = ImageSize.any;
		imgType = ImageType.any;
		String color = SharedPreferencesUtils.LoadPreferences(context, "color");
		if ( !color.equals("")) setImgColor(color);
		String size = SharedPreferencesUtils.LoadPreferences(context, "size");
		if (!size.equals("")) setImgSize(size);
		String type = SharedPreferencesUtils.LoadPreferences(context, "type");
		if (!type.equals("")) setImgType(type);
		siteToSearch = SharedPreferencesUtils.LoadPreferences(context, "site");
	}
	
	public String getSiteToSearch() {
		return siteToSearch;
	}
	public void setSiteToSearch(String siteToSearch) {
		this.siteToSearch = siteToSearch;
	}
	public Colors getImgColor() {
		return imgColor;
	}
	public void setImgColor(String imgColor) {
		this.imgColor = Colors.valueOf(imgColor);
	}
	public boolean isColor() {
		return color;
	}
	public void setColor(boolean color) {
		this.color = color;
	}
	public ImageSize getImgSize() {
		return imgSize;
	}
	public void setImgSize(String imgSize) {
		this.imgSize = ImageSize.valueOf(imgSize);
	}
	public ImageType getImgType() {
		return imgType;
	}
	public void setImgType(String imgType) {
		this.imgType = ImageType.valueOf(imgType);
	}
	
	
	//TODO how to handle empty searchTerms
	public String formQuery(String searchTerm, int start, int totalItemsCount) {
		StringBuffer queryStr = new StringBuffer();
		queryStr.append("https://ajax.googleapis.com/ajax/services/search/images?"
				+ "v=1.0&&rsz="
				+ totalItemsCount
				+ "&start="
				+ start
				+ "&q="
				+ Uri.encode(searchTerm));
		if (imgType != ImageType.any) { //type
			queryStr.append("&imgtype=" + imgType.name());
		}
		if (imgSize != ImageSize.any) { //size
			if (imgSize == ImageSize.small)
				queryStr.append("&imgsz=icon");
			else if (imgSize == ImageSize.medium)
				queryStr.append("&imgsz=" + imgSize.name());
			else if (imgSize == ImageSize.large)
				queryStr.append("&imgsz=xxlarge");
			else if (imgSize == ImageSize.extralarge)
				queryStr.append("&imgsz=huge");
		}
		if (imgColor != Colors.any) { // color
			queryStr.append("&imgcolor=" + imgColor.name());
		}
		// add site to search
		if (siteToSearch != null && !siteToSearch.equals("") ) {
			queryStr.append("&as_sitesearch="+siteToSearch);
		}
		return queryStr.toString();
	}

}
