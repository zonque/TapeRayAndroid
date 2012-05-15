package com.taperay.android.preview;

import com.taperay.android.preview.R;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

public class ShowPreviewActivity extends Activity {
	private static ImagePreview imagePreview;
	private ContentManager contentManager;
	private Artwork artwork;
	private TouchImageView imageView;
	
	void reloadImage() {
        final ProgressDialog dialog = ProgressDialog.show(this, "", 
                "Loading artwork, please wait...", true);

        new Thread(new Runnable() {
        	public void run() {
        		final Bitmap b = artwork.getImageBitmap();
        		artwork.setBitmapColor(contentManager.getCurrentColor());
        		
        		imageView.post(new Runnable() {
        			public void run() {
        				imageView.setImageBitmap(b);
            	        dialog.dismiss();
        			}
        		});
        	}
        }).start();
	}
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.preview);

    	TapeRayApplication app = (TapeRayApplication) getApplication();
    	contentManager = app.getContentManager();

        // add imagepreview
        imagePreview = new ImagePreview(this);
        ((FrameLayout) findViewById(R.id.preview)).addView(imagePreview);

        // add touch controller
        imageView = new TouchImageView(this, null);
        ((FrameLayout) findViewById(R.id.preview)).addView(imageView);
        
        final Button button = (Button) findViewById(R.id.order_button);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(artwork.getURL()));
                startActivity(browserIntent);
            }
        });

        artwork = contentManager.getCurrentArtwork();
        setTitle(artwork.getTitle());

        reloadImage();
    }
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.preview_menu, menu);
	    
	    return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle item selection
	    switch (item.getItemId()) {
	        case R.id.change_color:
	        	Intent i = new Intent(ShowPreviewActivity.this, ShowColorsActivity.class);
				startActivity(i);
	        	return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		reloadImage();
	}
}
