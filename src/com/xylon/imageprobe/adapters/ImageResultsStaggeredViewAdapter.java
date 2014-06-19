package com.xylon.imageprobe.adapters;

import java.util.ArrayList;
import java.util.Random;

import android.content.Context;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.etsy.android.grid.util.DynamicHeightImageView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.xylon.imageprobe.R;
import com.xylon.imageprobe.model.ImageResult;

public class ImageResultsStaggeredViewAdapter extends ArrayAdapter<ImageResult> {
	private static final String TAG = ImageResultsStaggeredViewAdapter.class.getSimpleName();
	private final LayoutInflater mLayoutInflater;
	private final Random mRandom;
	private static final SparseArray<Double> sPositionHeightRatios = new SparseArray<Double>();

	public ImageResultsStaggeredViewAdapter(Context context, ArrayList<ImageResult> images) {
		super(context, android.R.layout.simple_list_item_1, images);
		this.mLayoutInflater = LayoutInflater.from(context);
		this.mRandom = new Random();
	}

	@Override
	public View getView(final int position, View convertView, final ViewGroup parent) {
		ViewHolder vh;
		if (convertView == null) {
			convertView = mLayoutInflater.inflate(R.layout.staggered_view_item_result, parent, false);
			vh = new ViewHolder();
			vh.imgView = (DynamicHeightImageView) convertView.findViewById(R.id.imgView);
			convertView.setTag(vh);
		} else {
			vh = (ViewHolder) convertView.getTag();
		}
		double positionHeight = getPositionRatio(position);
		vh.imgView.setHeightRatio(positionHeight);
		String url = getItem(position).getThumbUrl();
		// Using Universal Image Loader  for asynchronous image loading
		// from https://github.com/nostra13/Android-Universal-Image-Loader
		// Another one to try is Picasso
		// http://square.github.io/picasso/
		ImageLoader.getInstance().displayImage(url, vh.imgView);
		return convertView;
	}

	static class ViewHolder {
		DynamicHeightImageView imgView;
	}

	private double getPositionRatio(final int position) {
		double ratio = sPositionHeightRatios.get(position, 0.0);
		// if not yet done generate and stash the columns height
		// in our real world scenario this will be determined by
		// some match based on the known height and width of the image
		// and maybe a helpful way to get the column height!
		if (ratio == 0) {
			ratio = getRandomHeightRatio();
			sPositionHeightRatios.append(position, ratio);
			Log.d(TAG, "getPositionRatio:" + position + " ratio:" + ratio);
		}
		return ratio;
	}

	private double getRandomHeightRatio() {
		return (mRandom.nextDouble() / 2.0) + 1.0; // height will be 1.0 - 1.5
		// the width
	}
	
}
