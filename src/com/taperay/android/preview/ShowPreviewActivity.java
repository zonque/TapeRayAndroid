package com.taperay.android.preview;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import com.taperay.android.preview.R;

import android.app.AlertDialog;
import android.app.ProgressDialog;
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
					
					BitmapFactory.Options options=new BitmapFactory.Options();
					options.inSampleSize = 8;

					Bitmap videoBitmap = BitmapFactory.decodeByteArray(data, 0, data.length, options);

					int degrees = 0;
					switch (getWindowManager().getDefaultDisplay().getRotation()) {
					case Surface.ROTATION_0: degrees = 0; break;
					case Surface.ROTATION_90: degrees = 90; break;
					case Surface.ROTATION_180: degrees = 180; break;
					case Surface.ROTATION_270: degrees = 270; break;
					}

					int rotation;
					android.hardware.Camera.CameraInfo info =
							new android.hardware.Camera.CameraInfo();
					android.hardware.Camera.getCameraInfo(0, info);

					if (info.facing == android.hardware.Camera.CameraInfo.CAMERA_FACING_FRONT) {
						rotation = (info.orientation + degrees) % 360;
						rotation = (360 - rotation) % 360;  // compensate the mirror
					} else {  // back-facing
						rotation = (info.orientation - degrees + 360) % 360;
					}

					imageView.setDrawingCacheEnabled(true);
					Bitmap rootBitmap = imageView.getDrawingCache();
					
					//videoBitmap.recycle();
					
					int w, h;
					if (rotation % 180 == 0) {
						h = videoBitmap.getHeight();
						w = videoBitmap.getWidth();
					} else {
						w = videoBitmap.getHeight();
						h = videoBitmap.getWidth();
					}
					
					float factor = (float) w / getDisplayWidth();
					
					Bitmap finalBitmap = Bitmap.createBitmap(w, h, videoBitmap.getConfig());
					Log.v("XXX", "ROTATION " + rotation + " video " + videoBitmap.getWidth() + "x" + videoBitmap.getHeight() + " --> " + w + "x" + h);
					Canvas canvas = new Canvas(finalBitmap);
					Matrix mtx = new Matrix();
					mtx.setTranslate(w, 0);
					mtx.preRotate(90.0f); //, videoBitmap.getWidth() / 2, videoBitmap.getHeight() / 2);
					canvas.drawBitmap(videoBitmap, mtx, null);
					
					
					mtx.reset();
					//mtx.preConcat(imageView.getImageMatrix());
					
					float[] v = new float[9];
					imageView.getImageMatrix().getValues(v);
					//Log.v("XXX", "MATRIX: " + v[0] + " " + v[1] + " " + v[2] + " " + v[3] + " " + v[4] + " " + v[5] + " " + v[6] + " " + v[7] + " " + v[8]);
					Log.v("XXX", "factor --> " + factor);
					
					mtx.setTranslate(factor * v[Matrix.MTRANS_X], factor * v[Matrix.MTRANS_Y]);
					mtx.preScale(factor * v[Matrix.MSCALE_X], factor * v[Matrix.MSCALE_Y]);
					canvas.drawBitmap(bitmap, mtx, null);
//					canvas.rotate(rotation);

					//canvas.save();

					/*
					ImageView view = new ImageView(ShowPreviewActivity.this);
					view.setDrawingCacheEnabled(true);
					view.draw(canvas);
					*/
					
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

					Log.d("xxx", "onPictureTaken - jpeg");
					
					Intent intent = new Intent();
					intent.setAction(Intent.ACTION_VIEW);
					intent.setDataAndType(Uri.parse("file://" + myPath.getAbsolutePath()), "image/*");
					startActivity(intent);

					
					//imagePreview.camera.startPreview();
				}
		    };

		    //imagePreview.camera.takePicture(null, null, takePictureCallback);
		    return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	private float getDisplayWidth() {
		Display display = getWindowManager().getDefaultDisplay();
		Point size = new Point();
		return display.getWidth();
		//return size.x;
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
		imageView = new TouchImageView(this, null);
		layout.addView(imageView);
		
        // Un-comment below lines to specify the size.
        //previewLayoutParams.height = 500;
        //previewLayoutParams.width = 500;

        // Un-comment below line to specify the position.
        //mPreview.setCenterPosition(270, 130);
		
		loadImage();
	}
}
