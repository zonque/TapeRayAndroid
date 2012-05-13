package org.taperay.android.preview;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import org.taperay.android.preview.ImagePreview;

import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;
import android.view.View;

public class TapeRayActivity extends Activity {
	
	private static ImagePreview imagePreview;
	private static ContentManager contentManager;
	
	private void displayCategories() {
		runOnUiThread(new Runnable(){
			public void run() {
				//setContentView(R.layout.list);
			}
		});
		
		final String[] categoryTitles = contentManager.getCategoryTitles();
		final ListView listView = (ListView) findViewById(R.id.list);
		final ArrayAdapter<String> adapter = new ArrayAdapter<String>(TapeRayActivity.this,
				android.R.layout.simple_list_item_1, android.R.id.text1, categoryTitles);

		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
				int position, long id) {
				Toast.makeText(getApplicationContext(),
					"Click ListItem Number " + position, Toast.LENGTH_LONG)
					.show();
			}
		});
		
		listView.post(new Runnable() {
			public void run() {
	    		listView.setAdapter(adapter);
			}
		});
	}
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        contentManager = new ContentManager();

        setContentView(R.layout.list);

        /*
        // add imagepreview
        imagePreview = new ImagePreview(this);
        ((FrameLayout) findViewById(R.id.preview)).addView(imagePreview);

        // add touch controller
        final TouchImageView imageView = new TouchImageView(this, null);
        ((FrameLayout) findViewById(R.id.preview)).addView(imageView);
         */
        
		final ProgressDialog dialog = ProgressDialog.show(this, "", 
                "Loading data, please wait...", true);
					
	    new Thread(new Runnable() {
	        public void run() {
	    		contentManager.loadData();
	        	final Artwork a = new Artwork("weaver-freeride-flying");
	        	dialog.dismiss();
	            
	        	/*
	            imageView.post(new Runnable() {
	            	public void run() {
	            		imageView.setImageBitmap(a.getImageBitmap());
	            	}
	            });
	            */
	        	

	    		displayCategories();

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