package com.taperay.android.preview;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class ShowColorNumbersActivity extends TapeRayActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		int numColors = getIntent().getExtras().getInt("numColors");

		setContentView(R.layout.artworks);
		String items[] = new String[numColors];
		
		for (int i = 0; i < numColors; i++)
			items[i] = getResources().getString(R.string.color) + " #" + (i + 1);

		setTitle(getResources().getString(R.string.select_color_to_change));

		final ListView listView = (ListView) findViewById(R.id.list);
		final ArrayAdapter<String> adapter = new ArrayAdapter<String>(ShowColorNumbersActivity.this,
				android.R.layout.simple_list_item_1, android.R.id.text1, items);

		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent i = new Intent(ShowColorNumbersActivity.this, ShowColorsActivity.class);
				i.putExtra("colorIndex", position);
				startActivity(i);
				finish();
			}
		});
		
		listView.setAdapter(adapter);
	}
}
