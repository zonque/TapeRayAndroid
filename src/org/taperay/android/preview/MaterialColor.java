package org.taperay.android.preview;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.graphics.Color;

public class MaterialColor {
	
	String title;
	int color;

	MaterialColor(Node node) {
		NodeList properties = node.getChildNodes();

		for (int j = 0; j < properties.getLength(); j++){
			Node property = properties.item(j);
			String name = property.getNodeName();

			if (name.equalsIgnoreCase("rgb_hex"))
				Color.parseColor("#" + property.getFirstChild().getNodeValue());
			
			if (name.equalsIgnoreCase("title_de"))
				title = new String(property.getFirstChild().getNodeValue());
		}
	}
	
    String getTitle() {
    	return title;
    }
    
    int getColor() {
    	return color;
    }
}
