package com.taperay.android.preview;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

public class Artwork extends ServerObject {
	private Bitmap bitmap;
	private RestClient restClient;
	String name;

	static final String tag = "Artwork";

	private void retrieveBitmap() {
		HttpGet request = new HttpGet();
		DefaultHttpClient client = new DefaultHttpClient();
		HttpResponse resp;

		try {
			String imageURL = propertyHash.get("image_url");
			request.setURI(new URI(imageURL));
		} catch (URISyntaxException e) {
			e.printStackTrace();
			return;
		}

		try {
			resp = client.execute(request);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			return;
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}

		StatusLine status = resp.getStatusLine();
		if (status.getStatusCode() != 200) {
			Log.d(tag, "HTTP error, invalid server status code: " + resp.getStatusLine());  
		}

		try {
			BufferedHttpEntity bufferedHttpEntity = new BufferedHttpEntity(resp.getEntity());
			InputStream is = bufferedHttpEntity.getContent();
			bitmap = BitmapFactory.decodeStream(is);
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
	}

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

	public Artwork(Node node) {
		restClient = new RestClient("artworks");
		bitmap = null;
		readFromNode(node);
		Log.v("title", "XXX:: " + propertyHash.get("title"));
		//		retrieve();
	}

	public String getTitle() {
		return propertyHash.get("title");
	}

	public Bitmap getImageBitmap() {
		if (bitmap == null)
			retrieveBitmap();

		return bitmap;
	}

	public void setBitmapColor(MaterialColor color) {
		if (bitmap == null)
			return;

		int size = bitmap.getHeight() * bitmap.getRowBytes();
		ByteBuffer buffer = ByteBuffer.allocate(size);
		buffer.order(ByteOrder.nativeOrder());

		bitmap.copyPixelsToBuffer(buffer);
		byte[] array = buffer.array();

		for (int i = 0; i < size; i += 4) {
			int alpha = (int) array[i + 3];

			if (alpha != 0) {
				array[i + 0] = color.getRed();
				array[i + 1] = color.getGreen();
				array[i + 2] = color.getBlue();
				array[i + 3] = (byte) 0xff;
			}
		}

		buffer.rewind();
		bitmap.copyPixelsFromBuffer(buffer);
		bitmap.prepareToDraw();
	}

	public String getURL() {
		return propertyHash.get("url");
	}
}
