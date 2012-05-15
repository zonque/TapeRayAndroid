package com.taperay.android.preview;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.util.Log;

public class MaterialColor {
	
	String title;
	byte r, g, b;

	MaterialColor(Node node) {
		NodeList properties = node.getChildNodes();

		for (int j = 0; j < properties.getLength(); j++){
			Node property = properties.item(j);
			String name = property.getNodeName();

			if (name.equalsIgnoreCase("rgb_hex")) {
				String s = property.getFirstChild().getNodeValue();
				r = (byte) (Integer.parseInt(s.substring(0, 2), 16));
				g = (byte) (Integer.parseInt(s.substring(2, 4), 16));
				b = (byte) (Integer.parseInt(s.substring(4, 6), 16));
			}

			if (name.equalsIgnoreCase("title_de"))
				title = new String(property.getFirstChild().getNodeValue());
		}
	}
	
    public String getTitle() {
    	return title;
    }
    
    public Byte getRed() {
    	return r;
    }

    public Byte getGreen() {
    	return g;
    }

    public Byte getBlue() {
    	return b;
    }

}
