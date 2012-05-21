package com.taperay.android.preview;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import com.taperay.android.preview.R;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Picture;
import android.graphics.Point;
import android.hardware.Camera.PictureCallback;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Html;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ShowPreviewActivity extends TapeRayActivity {
	private static ImagePreview imagePreview;
	private ContentManager contentManager;
	private Artwork artwork;
	private TouchImageView imageView;
	private ProgressDialog dialog;
	private Bitmap bitmap;
	private FrameLayout layout;
	private MediaPlayer mediaPlayer;

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
						Log.v("XXX", "===> SET IMAGE!");
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


		TapeRayApplication app = (TapeRayApplication) getApplication();
		contentManager = app.getContentManager();

		layout = new FrameLayout(this);
		setContentView(layout);		
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
                 }  
            });

            AlertDialog alert = builder.create(); 
            alert.show();
			
			return true;
			
		case R.id.save_image:
			
		    /** Handles data for jpeg picture */
		    final PictureCallback takePictureCallback = new PictureCallback() {
				@Override
				public void onPictureTaken(byte[] data, android.hardware.Camera camera) {
					
					Log.v("XXX", "BYTES " + data.length);
					//shootSound();
					
					BitmapFactory.Options options=new BitmapFactory.Options();
					options.inSampleSize = 8;

					Bitmap videoBitmap = BitmapFactory.decodeByteArray(data, 0, data.length, options);

					int rotation = imagePreview.getCameraOrientation();

					imageView.setDrawingCacheEnabled(true);
					Bitmap rootBitmap = imageView.getDrawingCache();
					
					//videoBitmap.recycle();
					Matrix mtx = new Matrix();

					int w, h;
					
					switch (rotation) {
					case 0:
						h = videoBitmap.getHeight();
						w = videoBitmap.getWidth();
						break;
					case 90:
						w = videoBitmap.getHeight();
						h = videoBitmap.getWidth();
						mtx.setTranslate(w, 0);
						mtx.preRotate(90);
						break;
					case 180:
						h = videoBitmap.getHeight();
						w = videoBitmap.getWidth();
						mtx.setTranslate(w, h);
						mtx.preRotate(180);
						break;
					case 270:
						w = videoBitmap.getHeight();
						h = videoBitmap.getWidth();
						break;
					default:
						return;
					}
					
					float factorH = (float) h / imageView.getHeight();
					float factorW = (float) w / imageView.getWidth();
					
					Bitmap finalBitmap = Bitmap.createBitmap(w, h, videoBitmap.getConfig());
					Log.v("XXX", "ROTATION " + rotation + " video " + videoBitmap.getWidth() + "x" + videoBitmap.getHeight() + " --> " + w + "x" + h);
					Canvas canvas = new Canvas(finalBitmap);
					canvas.drawBitmap(videoBitmap, mtx, null);
					
					mtx.reset();
					//mtx.preConcat(imageView.getImageMatrix());
					
					float[] v = new float[9];
					imageView.getImageMatrix().getValues(v);
					//Log.v("XXX", "MATRIX: " + v[0] + " " + v[1] + " " + v[2] + " " + v[3] + " " + v[4] + " " + v[5] + " " + v[6] + " " + v[7] + " " + v[8]);
					Log.v("XXX", "factor --> " + factorW + " -- " + factorH);
					
					mtx.setTranslate(factorW * v[Matrix.MTRANS_X], factorH * v[Matrix.MTRANS_Y]);
					mtx.preScale(factorW * v[Matrix.MSCALE_X], factorH * v[Matrix.MSCALE_Y]);
					canvas.drawBitmap(bitmap, mtx, null);
					
					String extr = Environment.getExternalStorageDirectory().toString();
					File myPath = new File(extr, "blubb.jpg");
					FileOutputStream fos = null;
					try {
						fos = new FileOutputStream(myPath);
						finalBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
						fos.flush();
						fos.close();
						//MediaStore.Images.Media.insertImage(getContentResolver(), finalBitmap, "Screen", "screen");
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					Intent intent = new Intent();
					intent.setAction(Intent.ACTION_VIEW);
					intent.setDataAndType(Uri.parse("file://" + myPath.getAbsolutePath()), "image/*");
					startActivity(intent);

					imagePreview.mCamera.startPreview();
				}
		    };

		    imagePreview.mCamera.takePicture(null, null, takePictureCallback);
		    return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		if (dialog != null) {
			dialog.dismiss();
			dialog = null;
		}
		
		imagePreview.stop();
		
        layout.removeView(imagePreview);
        imagePreview = null;

        layout.removeView(imageView);
        imageView = null;
	}
	
	@Override
	protected void onResume() {
		super.onResume();

		artwork = contentManager.getCurrentArtwork();
		setTitle(artwork.getTitle());

		// add imagepreview
		imagePreview = new ImagePreview(this, 0, ImagePreview.LayoutMode.FitToParent);
		LayoutParams previewLayoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		layout.addView(imagePreview, 0, previewLayoutParams);

		// add touch controller
		imageView = new TouchImageView(this);
		layout.addView(imageView);
		
        // Un-comment below lines to specify the size.
        //previewLayoutParams.height = 500;
        //previewLayoutParams.width = 500;

        // Un-comment below line to specify the position.
        //mPreview.setCenterPosition(270, 130);
		
		loadImage();
	}
}
