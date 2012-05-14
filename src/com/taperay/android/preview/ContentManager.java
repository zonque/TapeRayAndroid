package com.taperay.android.preview;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import android.util.Log;

public class ContentManager {
	static final String tag = "ContentManager";

	private ArrayList<Category> categories;
	private ArrayList<MaterialColor> materialColors;
	private Artwork currentArtwork;
	private ArrayList<Artwork> currentArtworks;
	
	private void retrieveCategories() {
		RestClient restClient = new RestClient("categories");
		Element root = restClient.index();
        NodeList items = root.getElementsByTagName("category");
        
        for (int i = 0; i < items.getLength(); i++) {
        	Category c = new Category(items.item(i));
        	categories.add(c);
        }
	}
	
	private void retrieveMaterialColors() {
		RestClient restClient = new RestClient("material_colors");
		Element root = restClient.index();
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

	public void selectArtwork(int index) {
		currentArtwork = currentArtworks.get(index);
	}
	
	ContentManager() {
		categories = new ArrayList<Category>();
		materialColors = new ArrayList<MaterialColor>();
    	currentArtwork = new Artwork("weaver-freeride-flying");
	}
}
