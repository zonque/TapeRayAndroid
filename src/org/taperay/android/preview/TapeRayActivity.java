package org.taperay.android.preview;

import android.app.Activity;
import android.os.Bundle;
import org.taperay.android.preview.ImagePreview;
import android.widget.FrameLayout;

public class TapeRayActivity extends Activity {
	
	private static ImagePreview imagePreview;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.main);
        imagePreview = new ImagePreview(this);
        ((FrameLayout) findViewById(R.id.preview)).addView(imagePreview);   
    }
    
	public static void setCameraDisplayOrientation(Activity activity,
	         int cameraId, android.hardware.Camera camera) {
	     int rotation = activity.getWindowManager().getDefaultDisplay()
	             .getRotation();
		imagePreview.setCameraDisplayOrientation(cameraId, camera, rotation);
	}
}