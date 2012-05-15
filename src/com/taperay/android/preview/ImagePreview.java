package com.taperay.android.preview;

import java.io.IOException;
import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.hardware.Camera;
import android.hardware.Camera.PreviewCallback;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;

public class ImagePreview extends SurfaceView implements Callback {
	private static final String TAG = "Preview";
	SurfaceHolder mHolder;
	public Camera camera;

	ImagePreview(Context context) {
		super(context);

		// Install a SurfaceHolder.Callback so we get notified when the
		// underlying surface is created and destroyed.
		mHolder = getHolder();
		mHolder.addCallback(this);
		mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	}

	public void surfaceCreated(SurfaceHolder holder) {
		// The Surface has been created, acquire the camera and tell it where
		// to draw.
		camera = Camera.open();
		try {
			camera.setPreviewDisplay(holder);

			camera.setPreviewCallback(new PreviewCallback() {

				public void onPreviewFrame(byte[] data, Camera arg1) {
					/*
					FileOutputStream outStream = null;
					try {
						outStream = new FileOutputStream(String.format(
								"/sdcard/%d.jpg", System.currentTimeMillis()));
						outStream.write(data);
						outStream.close();
						Log.d(TAG, "onPreviewFrame - wrote bytes: "
								+ data.length);
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					} finally {
					}
					ImagePreview.this.invalidate();
					 */
				}
			});
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void surfaceDestroyed(SurfaceHolder holder) {
		// Surface will be destroyed when we return, so stop the preview.
		// Because the CameraDevice object is not a shared resource, it's very
		// important to release it when the activity is paused.
		camera.stopPreview();
		camera.setPreviewCallback(null);
		camera.release();
		camera = null;
	}

	public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
		// Now that the size is known, set up the camera parameters and begin
		// the preview.
		Camera.Parameters parameters = camera.getParameters();
		List<Camera.Size> previewSizes = parameters.getSupportedPreviewSizes();
		Camera.Size previewSize = previewSizes.get(0);
		parameters.setPreviewSize(previewSize.width, previewSize.height);
		camera.setParameters(parameters);
		camera.setDisplayOrientation(90);
		camera.startPreview();
	}
	
	public void setCameraDisplayOrientation(int cameraId, android.hardware.Camera camera, int rotation) {
	     android.hardware.Camera.CameraInfo info =
	             new android.hardware.Camera.CameraInfo();
	     android.hardware.Camera.getCameraInfo(cameraId, info);

	     int degrees = 0;
	     switch (rotation) {
	         case Surface.ROTATION_0: degrees = 0; break;
	         case Surface.ROTATION_90: degrees = 90; break;
	         case Surface.ROTATION_180: degrees = 180; break;
	         case Surface.ROTATION_270: degrees = 270; break;
	     }

	     int result;
	     if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
	         result = (info.orientation + degrees) % 360;
	         result = (360 - result) % 360;  // compensate the mirror
	     } else {  // back-facing
	         result = (info.orientation - degrees + 360) % 360;
	     }
	     camera.setDisplayOrientation(result);
	}
	
	@Override
	public void draw(Canvas canvas) {
		super.draw(canvas);
		Paint p = new Paint(Color.RED);
		Log.d(TAG, "draw");
		canvas.drawText("PREVIEW", canvas.getWidth() / 2,
				canvas.getHeight() / 2, p);
	}
}
