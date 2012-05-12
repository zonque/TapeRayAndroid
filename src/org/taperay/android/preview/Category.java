package org.taperay.android.preview;

import java.util.List;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class Category {

	String title;
	int category_id;

	Category(Node node) {
        NodeList properties = node.getChildNodes();

        for (int j = 0; j < properties.getLength(); j++){
            Node property = properties.item(j);
            String name = property.getNodeName();
            
            if (name.equalsIgnoreCase("id"))
            	category_id = Integer.parseInt(property.getFirstChild().getNodeValue());

            if (name.equalsIgnoreCase("title_en"))
            	title = new String(property.getFirstChild().getNodeValue());
        }
	}
	
	String getTitle() {
		return title;
	}

	/*
	List<Artwork> getArtworks() {
		list = new List<Artwork>;
		
		return list;
	}
	*/
}
