package com.xylon.imageprobe.model;

import java.io.Serializable;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ImageResult implements Serializable {

	private static final long serialVersionUID = -8714265520693038953L;
	private String fullUrl;
	private String thumbUrl;
	private int fullHeight = 0;
	private int fullWidth = 0;
	private int thumbHeight = 0;
	private int thumbWidth = 0;
	private String titleNoFormatting ;
	private String contentNoFormatting;
	private String contextUrl;
		
	public ImageResult(JSONObject json) {
		
		try {
			this.fullUrl = json.getString("url");
			this.thumbUrl = json.getString("tbUrl");
			this.fullHeight = json.getInt("height");
			this.fullWidth = json.getInt("width");
			this.thumbHeight = json.getInt("tbHeight");
			this.thumbWidth = json.getInt("tbWidth");
			if (!json.isNull("contentNoFormatting"))
				this.contentNoFormatting = json.getString("contentNoFormatting");
			if(!json.isNull("titleNoFormatting"))
				this.titleNoFormatting = json.getString("titleNoFormatting");
			if(!json.isNull("originalContextUrl"))
				this.contextUrl = json.getString("originalContextUrl");
		} catch (JSONException e) {
			this.fullUrl = null;
			this.thumbUrl = null;
			
		}
	}
	public String getFullUrl() {
		return fullUrl;
	}
	public void setFullUrl(String fullUrl) {
		this.fullUrl = fullUrl;
	}
	public String getThumbUrl() {
		return thumbUrl;
	}
	public void setThumbUrl(String thumbUrl) {
		this.thumbUrl = thumbUrl;
	}
	public static ArrayList<ImageResult> fromJSONArray(JSONArray imageJsonResults) {
		ArrayList<ImageResult> results = new ArrayList<ImageResult>();
		for ( int i = 0; i < imageJsonResults.length();++i) {
			try {
				results.add(new ImageResult(imageJsonResults.getJSONObject(i)));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return results;
		
	}
	
	public String toString() {
		String out = fullUrl + "::" + thumbUrl + "::" +titleNoFormatting;
		return out;
	}
	public int getFullHeight() {
		return fullHeight;
	}
	public void setFullHeight(int fullHeight) {
		this.fullHeight = fullHeight;
	}
	public int getFullWidth() {
		return fullWidth;
	}
	public void setFullWidth(int fullWidth) {
		this.fullWidth = fullWidth;
	}
	public int getThumbHeight() {
		return thumbHeight;
	}
	public void setThumbHeight(int thumbHeight) {
		this.thumbHeight = thumbHeight;
	}
	public int getThumbWidth() {
		return thumbWidth;
	}
	public void setThumbWidth(int thumbWidth) {
		this.thumbWidth = thumbWidth;
	}
	public String getTitleNoFormatting() {
		return titleNoFormatting;
	}
	public void setTitleNoFormatting(String titleNoFormatting) {
		this.titleNoFormatting = titleNoFormatting;
	}
	public String getContentNoFormatting() {
		return contentNoFormatting;
	}
	public void setContentNoFormatting(String contentNoFormatting) {
		this.contentNoFormatting = contentNoFormatting;
	}
	public String getContextUrl() {
		return contextUrl;
	}
	public void setContextUrl(String contextUrl) {
		this.contextUrl = contextUrl;
	}

}
