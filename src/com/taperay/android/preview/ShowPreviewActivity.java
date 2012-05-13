package com.taperay.android.preview;

import com.taperay.android.preview.R;

import android.app.Activity;
import android.os.Bundle;
import android.widget.FrameLayout;

public class ShowPreviewActivity extends Activity {
	private static ImagePreview imagePreview;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.preview);

        // add imagepreview
        imagePreview = new ImagePreview(this);
        ((FrameLayout) findViewById(R.id.preview)).addView(imagePreview);

        // add touch controller
        final TouchImageView imageView = new TouchImageView(this, null);
        ((FrameLayout) findViewById(R.id.preview)).addView(imageView);
        
	    new Thread(new Runnable() {
	        public void run() {
	        	final Artwork a = new Artwork("weaver-freeride-flying");
	        	//dialog.dismiss();
	            
	            imageView.post(new Runnable() {
	            	public void run() {
	            		imageView.setImageBitmap(a.getImageBitmap());
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
