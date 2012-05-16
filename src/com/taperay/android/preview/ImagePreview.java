package com.taperay.android.preview;

import java.io.IOException;
import java.util.List;

import android.content.Context;
import android.content.res.Configuration;

import android.hardware.Camera;
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

		if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
			parameters.set("orientation", "portrait");
			camera.setDisplayOrientation(90);
		}

		if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
			parameters.set("orientation", "landscape");
		}

		camera.setParameters(parameters);
		camera.startPreview();
	}
}
