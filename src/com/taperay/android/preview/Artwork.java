package com.taperay.android.preview;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.w3c.dom.Node;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;

public class Artwork extends ServerObject {
	private Bitmap masksBitmap;
	private MaterialColor[] color;
	private int numColors;
	String name;

	static final String tag = "Artwork";

	private void retrieveBitmap() throws ClientProtocolException, IOException {
		HttpGet request = new HttpGet();
		DefaultHttpClient client = new DefaultHttpClient();
		HttpResponse resp;

		try {
			String imageURL = propertyHash.get("masks_url");
			request.setURI(new URI(imageURL));
		} catch (URISyntaxException e) {
			e.printStackTrace();
			return;
		}

		resp = client.execute(request);

		StatusLine status = resp.getStatusLine();
		if (status.getStatusCode() != 200) {
			Log.d(tag, "HTTP error, invalid server status code: " + resp.getStatusLine());  
		}

		try {
			BufferedHttpEntity bufferedHttpEntity = new BufferedHttpEntity(resp.getEntity());
			InputStream is = bufferedHttpEntity.getContent();
			masksBitmap = BitmapFactory.decodeStream(is);
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
	}

	/*
	private void retrieve() {
		Element root = restClient.get(propertyHash.get("id"));

		NodeList items = root.getElementsByTagName("artwork");
		Node artwork = items.item(0);
		readFromNode(artwork);
	}

	Artwork(String _name) {
		name = _name;
		bitmap = null;
		//		retrieve();
	}
	 */

	public Artwork(Node node) {
		ContentManager contentManager = TapeRayApplication.getInstance().getContentManager();
		masksBitmap = null;
		readFromNode(node);

		numColors = Integer.parseInt(propertyHash.get("num_colors"));
		color = new MaterialColor[numColors];

		for (int i = 0; i < numColors; i++) {
			int id = Integer.parseInt(propertyHash.get("default_color_" + i));
			MaterialColor mc = contentManager.getMaterialColorById(id);
			color[i] = mc;
		}

		//		retrieve();
	}

	public String getTitle() {
		return propertyHash.get("title");
	}

	public void setMaterialColor(int index, MaterialColor mc) {
		color[index] = mc;
	}

	public Bitmap getImageBitmap() throws ClientProtocolException, IOException {
		if (masksBitmap == null)
			retrieveBitmap();

		// find the outmost pixels, so we can generate a canvas that uses
		// the biggest possible area

		int startX = masksBitmap.getWidth();
		int endX = 0;
		int startY = masksBitmap.getHeight();
		int endY = 0;

		int heightPerColor = masksBitmap.getHeight() / numColors;

		for (int c = 0; c < numColors; c++)
			for (int x = 0; x < masksBitmap.getWidth(); x++)
				for (int y = 0; y < heightPerColor; y++) {
					int alpha = masksBitmap.getPixel(x, y + (c * heightPerColor));
					if (alpha != 0) {
						if (startX > x)
							startX = x;
						if (endX < x)
							endX = x;
						if (startY > y)
							startY = y;
						if (endY < y)
							endY = y;
					}
				}

		Bitmap alphaMask = masksBitmap.extractAlpha();

		Bitmap dest = Bitmap.createBitmap(endX - startX,
										  endY - startY,
										  Bitmap.Config.ARGB_8888);

		Canvas canvas = new Canvas(dest);
		Paint paint = new Paint();
		Rect dst = new Rect(0, 0, dest.getWidth(), dest.getHeight());

		for (int c = 0; c < numColors; c++) {
			int r = color[c].getRed();
			int g = color[c].getGreen();
			int b = color[c].getBlue();

			Rect src = new Rect(startX, (heightPerColor * c) + startY,
								endX, (heightPerColor * (c)) + endY);

			paint.setARGB(255, r, g, b);
			canvas.drawBitmap(alphaMask, src, dst, paint);
		}

		dest.prepareToDraw();
		return dest;
	}

	public String getURL() {
		return propertyHash.get("url");
	}

	public String getShortURL() {
		return propertyHash.get("short_url");
	}

	public String getMinSize() {
		return propertyHash.get("min_width") + "cm x " +
				propertyHash.get("min_height") + "cm";
	}

	public String getArtistName() {
		return propertyHash.get("artist_name");
	}

	public CharSequence getPublishedOn() {
		return propertyHash.get("published_at");
	}

	public String getPrice() {
		return String.format("%.2f €", Float.parseFloat(propertyHash.get("price")) / 100.0f);
	}

	public String getCategoryName() {
		return propertyHash.get("category");
	}

	public int getNumColors() {
		return numColors;
	}
}
