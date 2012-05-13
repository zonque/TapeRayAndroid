package com.taperay.android.preview;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

public class ShowArtworksActivity extends Activity {
	
	ContentManager contentManager;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TapeRayApplication app = ((TapeRayApplication) getApplicationContext());
        contentManager = app.getContentManager();

        Log.v("XXX", "---------->");
    }

}
