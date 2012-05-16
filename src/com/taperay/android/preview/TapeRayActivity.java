package com.taperay.android.preview;

import com.taperay.android.preview.R;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.view.View;

public class TapeRayActivity extends Activity {
	private static ContentManager contentManager;
	private String[] categoryTitles;

	private void displayCategories() {
		final ListView listView = (ListView) findViewById(R.id.list);
		final ArrayAdapter<String> adapter = new ArrayAdapter<String>(TapeRayActivity.this,
				android.R.layout.simple_list_item_1, android.R.id.text1, categoryTitles);

		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				final ProgressDialog dialog = ProgressDialog.show(TapeRayActivity.this, "", 
						"Loading artwork list, please wait...", true);
				final int index = position;

				new Thread(new Runnable() {
					public void run() {
						TapeRayApplication app = (TapeRayApplication) getApplication();
						ContentManager contentManager = app.getContentManager();
						contentManager.selectCategory(index);
						Intent i = new Intent(TapeRayActivity.this, ShowArtworksActivity.class);
						dialog.dismiss();
						startActivity(i);
					}
				}).start();

			}
		});

		listView.post(new Runnable() {
			public void run() {
				listView.setAdapter(adapter);
			}
		});
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.categories);
		setTitle("TapeRay > Categories");

		final ProgressDialog dialog = ProgressDialog.show(this, "", 
				"Loading data, please wait...", true);

		new Thread(new Runnable() {
			public void run() {
				TapeRayApplication app = (TapeRayApplication) getApplication();
				contentManager = app.getContentManager();
				contentManager.loadData();
				categoryTitles = contentManager.getCategoryTitles();
				dialog.dismiss();
				displayCategories();
			}
		}).start();

	}
}