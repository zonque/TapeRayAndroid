package com.taperay.android.preview;

import com.taperay.android.preview.R;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
        
        final Button button = (Button) findViewById(R.id.order_button);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	Artwork a = contentManager.getCurrentArtwork();
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(a.getURL()));
                startActivity(browserIntent);
            }
        });

        final Artwork a = contentManager.getCurrentArtwork();
        setTitle(a.getTitle());

        final ProgressDialog dialog = ProgressDialog.show(this, "", 
                "Loading artwork, please wait...", true);

        new Thread(new Runnable() {
        	public void run() {
        		final Bitmap b = a.getImageBitmap();
        		a.setBitmapColor(contentManager.getCurrentColor());
        		
        		imageView.post(new Runnable() {
        			public void run() {
        				imageView.setImageBitmap(b);
            	        dialog.dismiss();
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
