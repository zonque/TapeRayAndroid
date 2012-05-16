package com.taperay.android.preview;

import org.w3c.dom.Node;

public class MaterialColor extends ServerObject {

	byte r, g, b;

	MaterialColor(Node node) {
		readFromNode(node);
		
		String color = propertyHash.get("rgb_hex");
		r = (byte) (Integer.parseInt(color.substring(0, 2), 16));
		g = (byte) (Integer.parseInt(color.substring(2, 4), 16));
		b = (byte) (Integer.parseInt(color.substring(4, 6), 16));
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
