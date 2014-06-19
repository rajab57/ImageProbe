package com.xylon.imageprobe.activities;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.ShareActionProvider;
import android.widget.TextView;

import com.loopj.android.image.SmartImageTask.OnCompleteListener;
import com.oritz.touch.TouchImageView;
import com.oritz.touch.TouchImageView.OnTouchImageViewListener;
import com.xylon.imageprobe.R;
import com.xylon.imageprobe.model.ImageResult;

public class ImageDisplayActivity extends Activity {
	ImageResult result;
	TouchImageView ivImage;
	ShareActionProvider miShareAction;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_image_display);
		result = (ImageResult) getIntent().getSerializableExtra("result");
		ivImage = (TouchImageView) findViewById(R.id.ivResult);
		TextView tvTitle = (TextView)findViewById(R.id.tvTitle);
		TextView tvContent = (TextView)findViewById(R.id.tvContent);
		if(result.getTitleNoFormatting() != null )
			tvTitle.setText(result.getTitleNoFormatting());
		if(result.getContentNoFormatting() != null)
			tvContent.setText(result.getContentNoFormatting() + "\n" + result.getFullWidth() + " x " + result.getFullHeight());
		// TODO isOnCompleteListener available in TouchImageView ?
		// image_not_found - fall back resource when the image is not found.
		ivImage.setImageUrl(result.getFullUrl(),R.drawable.image_not_found, new OnCompleteListener() {
	        @Override
	        public void onComplete() {
	            // Setup share intent now that image has loaded
	            setupShareIntent();
	        }
	    });	
		//
		// Set the OnTouchImageViewListener which updates edit texts
		// with zoom and scroll diagnostics.
		//
		ivImage.setOnTouchImageViewListener(new OnTouchImageViewListener() {

			@Override
			public void onMove() {
				PointF point = ivImage.getScrollPosition();
				RectF rect = ivImage.getZoomedRect();
				float currentZoom = ivImage.getCurrentZoom();
				boolean isZoomed = ivImage.isZoomed();
				DecimalFormat df = new DecimalFormat("#.##");
				System.out.println("Scroll: x: " + df.format(point.x) + " y: " + df.format(point.y));
				System.out.println("Rectleft: " + df.format(rect.left) + " top: " + df.format(rect.top)
						+ "\nright: " + df.format(rect.right) + " bottom: " + df.format(rect.bottom));
				System.out.println("getCurrentZoom(): " + currentZoom + " isZoomed(): " + isZoomed);
			}
		});

	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.image_display, menu);
		MenuItem item = menu.findItem(R.id.menu_item_share);
		miShareAction = (ShareActionProvider)item.getActionProvider();
		return true;
	}

	
	public void setupShareIntent() {
		Intent shareIntent = sendEmail("");
		if ( shareIntent != null && miShareAction != null )
			miShareAction.setShareIntent(shareIntent);
	}
	
	public Intent sendEmail(String toEmailAddress) {
		Uri bmpUri = getLocalBitmapUri(ivImage);
		if (bmpUri != null ) {
			Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND); 
			emailIntent.setType("application/image");
			emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{toEmailAddress}); 
			emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,"Test Subject"); 
			emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, getApplication().getString(R.string.app_name));
			//emailIntent.putExtra(Intent.EXTRA_TEXT, result.getFullUrl());
			emailIntent.setType("image/*");
			emailIntent.putExtra(Intent.EXTRA_STREAM,bmpUri);
			// TODO How to share image from URL. Error seen - Couldn't send attachment
			// No option to access image directly from remote, write locally and share
			//emailIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(result.getThumbUrl()));
			//startActivity(Intent.createChooser(emailIntent, "Send image...")); 
			return emailIntent;
		}
		return null;

	}
	
	// Returns the URI path to the Bitmap displayed in specified ImageView
	public Uri getLocalBitmapUri(ImageView imageView) {
	    // Extract Bitmap from ImageView drawable
	    Drawable drawable = imageView.getDrawable();
	    Bitmap bmp = null;
	    if (drawable instanceof BitmapDrawable || drawable instanceof ColorDrawable){
	       bmp = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
	    } else {
	       return null;
	    }
	    // Store image to default external storage directory
	    Uri bmpUri = null;
	    try {
	        File file =  new File(Environment.getExternalStoragePublicDirectory(  
		        Environment.DIRECTORY_DOWNLOADS), "share_image_" + System.currentTimeMillis() + ".png");
	        file.getParentFile().mkdirs();
	        FileOutputStream out = new FileOutputStream(file);
	        bmp.compress(Bitmap.CompressFormat.PNG, 90, out);
	        out.close();
	        bmpUri = Uri.fromFile(file);
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	    return bmpUri;
	}
}
