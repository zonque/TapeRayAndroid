package org.taperay.android.preview;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

public class Artwork {
	Bitmap bitmap;
	String name;
	Document xmlDoc;
	String imageURL;
	
	static final String baseURI = "http://taperay.com/artworks/";
	static final String tag = "Artwork";


	private void retrieve() {
		DefaultHttpClient client = new DefaultHttpClient();
		HttpGet request = new HttpGet();
		try {
			request.setURI(new URI(baseURI + name + ".xml"));
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		HttpResponse resp;
		try {
			resp = client.execute(request);
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}

		StatusLine status = resp.getStatusLine();
		if (status.getStatusCode() != 200) {
		    Log.d(tag, "HTTP error, invalid server status code: " + resp.getStatusLine());  
		}

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder;
		try {
			builder = factory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
		
		
		try {
			xmlDoc = builder.parse(resp.getEntity().getContent());
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
		
		Element root = xmlDoc.getDocumentElement();
        NodeList items = root.getElementsByTagName("artwork");
        Node artwork = items.item(0);
        NodeList properties = artwork.getChildNodes();

        for (int j = 0; j < properties.getLength(); j++){
            Node property = properties.item(j);
            String name = property.getNodeName();
            
            if (name.equalsIgnoreCase("image_url")) {
            	imageURL = new String(property.getFirstChild().getNodeValue());
            }
//            Log.d(tag, "NAME::: " + name);
        }
        
        Log.d(tag, "URL::: " + imageURL);
        
        // retrieve image

		try {
			request.setURI(new URI(imageURL));
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			resp = client.execute(request);
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
		
		status = resp.getStatusLine();
		if (status.getStatusCode() != 200) {
		    Log.d(tag, "HTTP error, invalid server status code: " + resp.getStatusLine());  
		}

		try {
			BufferedHttpEntity bufferedHttpEntity = new BufferedHttpEntity(resp.getEntity());
			InputStream is = bufferedHttpEntity.getContent();
	        bitmap = BitmapFactory.decodeStream(is);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	Artwork(String _name) {
		name = _name;
		retrieve();
	}
	
	Bitmap getImageBitmap() {
		return bitmap;
	}
	
}
