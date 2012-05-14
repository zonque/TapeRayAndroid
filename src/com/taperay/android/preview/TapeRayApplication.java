package com.taperay.android.preview;

import android.app.Application;

public class TapeRayApplication extends Application {
	
	private static TapeRayApplication singleton;
	private ContentManager contentManager;
	public static TapeRayApplication getInstance() {
        return singleton;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        singleton = this;
        contentManager = new ContentManager();
    }

	public ContentManager getContentManager() {
		return contentManager;
	}
}
