package com.taperay.android.preview;

import com.taperay.android.preview.R;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.FrameLayout;

public class ShowPreviewActivity extends Activity {
	private static ImagePreview imagePreview;
	private ContentManager contentManager;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.preview);

    	TapeRayApplication app = (TapeRayApplication) getApplication();
    	contentManager = app.getContentManager();

        // add imagepreview
        imagePreview = new ImagePreview(this);
        ((FrameLayout) findViewById(R.id.preview)).addView(imagePreview);

        // add touch controller
        final TouchImageView imageView = new TouchImageView(this, null);
        ((FrameLayout) findViewById(R.id.preview)).addView(imageView);
        
	    new Thread(new Runnable() {
	        public void run() {
	        	final Artwork a = contentManager.getCurrentArtwork();
	        	Log.v("XXX", "CURRENT ARTWORK " + a.getTitle());
	        	a.getImageBitmap();
	            
	            imageView.post(new Runnable() {
	            	public void run() {
	            		imageView.setImageBitmap(a.getImageBitmap());
	    	        	//dialog.dismiss();
	            	}
	            });
	        }
	    }).start();
    }

	public static void setCameraDisplayOrientation(Activity activity,
	         int cameraId, android.hardware.Camera camera) {
	     int rotation = activity.getWindowManager().getDefaultDisplay()
	             .getRotation();
		imagePreview.setCameraDisplayOrientation(cameraId, camera, rotation);
	}
}
