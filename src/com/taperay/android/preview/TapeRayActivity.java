package com.taperay.android.preview;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;

public class TapeRayActivity extends Activity {

	protected void displayNetworkErrorAndFinish() {

        runOnUiThread(new Runnable() {
            public void run() {
        		AlertDialog.Builder builder = new AlertDialog.Builder(TapeRayActivity.this);  
                builder.setTitle(getResources().getString(R.string.network_error_title));  
                builder.setMessage(getResources().getString(R.string.network_error_message));  
                builder.setNegativeButton("OK", new DialogInterface.OnClickListener() {  
                     @Override
                     public void onClick(DialogInterface dialog, int which) {  
                         dialog.cancel();  
                         finish();
                     }  
                });

                AlertDialog alert = builder.create(); 
                alert.show();
            }
        });
	}
}
