package com.taperay.android.preview;

import java.util.ArrayList;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import android.util.Log;

public class Category extends ServerObject {
	private RestClient restClient;
	
	Category(Node node) {
		tag = "Category";

		restClient = new RestClient("categories");
		readFromNode(node);
	}
	
	public void retrieve() {
		
	}
	
	String getTitle() {
		return propertyHash.get("title_de");
	}
	
	ArrayList<Artwork> getArtworks() {
		ArrayList<Artwork> artworks = new ArrayList<Artwork>();

		Element root = restClient.get(propertyHash.get("id"));
        NodeList items = root.getElementsByTagName("artwork");
        
        for (int i = 0; i < items.getLength(); i++) {
        	Artwork a = new Artwork(items.item(i));
        	
        	if (a.getTitle() != null)
        		artworks.add(a);
        }
		
		return artworks;
	}
}
