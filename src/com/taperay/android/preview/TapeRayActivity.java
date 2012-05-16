package com.taperay.android.preview;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

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
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.default_menu, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {		
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.about_taperay:
			final TextView message = new TextView(TapeRayActivity.this);
			final SpannableString s = 
				new SpannableString(getResources().getString(R.string.about_taperay_message));
			Linkify.addLinks(s, Linkify.WEB_URLS);
			message.setText(s);
			message.setMovementMethod(LinkMovementMethod.getInstance());
			
    		AlertDialog.Builder builder = new AlertDialog.Builder(TapeRayActivity.this);  
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
}
