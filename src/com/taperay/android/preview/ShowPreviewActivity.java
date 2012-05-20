package com.taperay.android.preview;

import com.taperay.android.preview.R;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.TextView;

public class ShowPreviewActivity extends TapeRayActivity {
	private static ImagePreview imagePreview;
	private ContentManager contentManager;
	private Artwork artwork;
	private TouchImageView imageView;
	private ProgressDialog dialog;
	private Bitmap bitmap;

	void loadImage() {
		if (dialog != null) {
			dialog.dismiss();
			dialog = null;
		}

		if (bitmap != null) {
			artwork.setBitmapColor(contentManager.getCurrentColor());
			imageView.setImageBitmap(bitmap);
			return;
		}

		dialog = ProgressDialog.show(this,
						getResources().getString(R.string.progress_dialog_header),
						getResources().getString(R.string.loading_artwork),
						true);

		new Thread(new Runnable() {
			public void run() {
				try {
					bitmap = artwork.getImageBitmap();
				} catch (Exception e) {
					displayNetworkErrorAndFinish();
					return;
				}
				artwork.setBitmapColor(contentManager.getCurrentColor());

				runOnUiThread(new Runnable() {
					public void run() {
						imageView.setImageBitmap(bitmap);
					}
				});
				
				if (dialog != null) {
					dialog.dismiss();
					dialog = null;
				}
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

		artwork = contentManager.getCurrentArtwork();
		setTitle(artwork.getTitle());

		loadImage();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.preview_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent i;
		
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.change_color:
			i = new Intent(ShowPreviewActivity.this, ShowColorsActivity.class);
			startActivity(i);
			return true;
		case R.id.visit_website:
			i = new Intent(Intent.ACTION_VIEW, Uri.parse(artwork.getURL()));
			startActivity(i);
			return true;
		case R.id.artwork_info:
			final TextView message = new TextView(ShowPreviewActivity.this);
			String s = getResources().getString(R.string.artwork_info_template);
			
			s = s.replace("##TITLE##", artwork.getTitle());
			s = s.replace("##ARTIST##", artwork.getArtistName());
			s = s.replace("##MIN_SIZE##", artwork.getMinSize());
			s = s.replace("##DATE##", artwork.getPublishedOn());
			s = s.replace("##PRICE##", artwork.getPrice());			
			
			message.setText(Html.fromHtml(s), TextView.BufferType.SPANNABLE);
			message.setMovementMethod(LinkMovementMethod.getInstance());
			message.setPadding(15, 10, 15, 10);

    		AlertDialog.Builder builder = new AlertDialog.Builder(ShowPreviewActivity.this);  
            builder.setTitle(getResources().getString(R.string.about_taperay));  
            builder.setView(message);
            builder.setNegativeButton("OK", new DialogInterface.OnClickListener() {  
                 @Override
                 public void onClick(DialogInterface dialog, int which) {  
                     dialog.cancel();  
                     finish();
                 }  
            });

            AlertDialog alert = builder.create(); 
            alert.show();

			
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	protected void onPause() {
		super.onPause();
		if (dialog != null) {
			dialog.dismiss();
			dialog = null;
		}
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		loadImage();
	}
}
