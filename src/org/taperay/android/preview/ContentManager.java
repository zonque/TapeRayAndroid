package org.taperay.android.preview;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import android.util.Log;

public class ContentManager {
	static final String baseURI = "http://taperay.com/";
	static final String tag = "ContentManager";
	DefaultHttpClient client;
	HttpGet request;
	HttpResponse resp;
	List<Category> categories;
	List<MaterialColor> materialColors;
	
	void retrieveCategories() {
		try {
			request.setURI(new URI(baseURI + "categories.xml"));
		} catch (URISyntaxException e) {
			e.printStackTrace();
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

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder;
		
		try {
			builder = factory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
			return;
		}
		
		Document xmlDoc;
		
		try {
			xmlDoc = builder.parse(resp.getEntity().getContent());
		} catch (IllegalStateException e) {
			e.printStackTrace();
			return;
		} catch (SAXException e) {
			e.printStackTrace();
			return;
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}

		Element root = xmlDoc.getDocumentElement();
        NodeList items = root.getElementsByTagName("category");
        
        for (int i = 0; i < items.getLength(); i++) {
        	Category c = new Category(items.item(i));
        	categories.add(c);
        }
	}
	
	void retrieveMaterialColors() {
		try {
			request.setURI(new URI(baseURI + "material_colors.xml"));
		} catch (URISyntaxException e) {
			e.printStackTrace();
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

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder;
		
		try {
			builder = factory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
			return;
		}
		
		Document xmlDoc;
		
		try {
			xmlDoc = builder.parse(resp.getEntity().getContent());
		} catch (IllegalStateException e) {
			e.printStackTrace();
			return;
		} catch (SAXException e) {
			e.printStackTrace();
			return;
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}

		Element root = xmlDoc.getDocumentElement();
        NodeList items = root.getElementsByTagName("material_color");
        
        for (int i = 0; i < items.getLength(); i++) {
        	MaterialColor mc = new MaterialColor(items.item(i));
        	materialColors.add(mc);
        	Log.v(tag, "GOT COLOR: " + mc.getTitle());
        }
	}
	
	ContentManager() {
		client = new DefaultHttpClient();
		request = new HttpGet();
		
		categories = new ArrayList<Category>();
		materialColors = new ArrayList<MaterialColor>();

		retrieveCategories();
		retrieveMaterialColors();
	}
	
	List<MaterialColor> getMaterialColors() {
		return materialColors;
	}
	
	List<Category> getCategories() {
		return categories;
	}
}
