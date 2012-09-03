package com.taperay.android.preview;

import java.util.List;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class ShowColorsActivity extends TapeRayActivity {

	private ContentManager contentManager;
	private String[] colorTitles;
	private int colorIndex;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		colorIndex = getIntent().getExtras().getInt("colorIndex");

		setContentView(R.layout.artworks);

		TapeRayApplication app = (TapeRayApplication) getApplication();
		contentManager = app.getContentManager();
		colorTitles = contentManager.getColorTitles();
		final List<MaterialColor> materialColors = contentManager.getMaterialColors();

		final ListView listView = (ListView) findViewById(R.id.list);
		final ArrayAdapter<String> adapter = new ArrayAdapter<String>(ShowColorsActivity.this,
				android.R.layout.simple_list_item_1, android.R.id.text1, colorTitles);

		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Artwork artwork = contentManager.getCurrentArtwork();
				artwork.setMaterialColor(colorIndex, materialColors.get(position));
				Log.v(" !!! ", "SET COLOR");
				finish();
			}
		});

		listView.setAdapter(adapter);
	}
}
