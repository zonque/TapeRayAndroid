package com.taperay.android.preview;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.text.Html;
import android.text.method.LinkMovementMethod;
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
			final String s = getResources().getString(R.string.about_taperay_message);

			message.setText(Html.fromHtml(s), TextView.BufferType.SPANNABLE);
			message.setMovementMethod(LinkMovementMethod.getInstance());
			message.setPadding(15, 10, 15, 10);

			AlertDialog.Builder builder = new AlertDialog.Builder(TapeRayActivity.this);  
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

		case R.id.search:
			onSearchRequested();
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}
}
