package com.taperay.android.preview;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Iterator;
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
	private DefaultHttpClient client;
	private HttpGet request;
	private HttpResponse resp;
	private ArrayList<Category> categories;
	private ArrayList<MaterialColor> materialColors;
	private Artwork currentArtwork;
	private ArrayList<Artwork> currentArtworks;
	
	private void retrieveCategories() {
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
	
	private void retrieveMaterialColors() {
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
	

	public List<MaterialColor> getMaterialColors() {
		return materialColors;
	}
	
	public List<Category> getCategories() {
		return categories;
	}
	
	public Artwork getCurrentArtwork() {
		return currentArtwork;
	}

	public void loadData() {
		retrieveCategories();
		retrieveMaterialColors();		
	}
	
	public String[] getCategoryTitles() {
		String[] list = new String[categories.size()];
		int pos = 0;
		
		for(Iterator<Category> i = categories.iterator(); i.hasNext();) {
			Category c = (Category) i.next();
			list[pos++] = c.getTitle();
		}
		
		return list;
	}

	public String[] getArtworkTitles() {
		String[] list = new String[currentArtworks.size()];
		int pos = 0;
		
		for(Iterator<Artwork> i = currentArtworks.iterator(); i.hasNext();) {
			Artwork a = (Artwork) i.next();
			list[pos++] = a.getTitle();
		}
		
		return list;
	}
	
	public void selectCategory(int index) {
		Category c = categories.get(index);
		currentArtworks = c.getArtworks();
		currentArtwork = null;
	}
	
	ContentManager() {
		client = new DefaultHttpClient();
		request = new HttpGet();
		categories = new ArrayList<Category>();
		materialColors = new ArrayList<MaterialColor>();
    	currentArtwork = new Artwork("weaver-freeride-flying");
	}
}
