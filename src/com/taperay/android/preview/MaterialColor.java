package com.taperay.android.preview;

import org.w3c.dom.Node;

public class MaterialColor extends ServerObject {

	private int r, g, b, id;

	MaterialColor(Node node) {
		readFromNode(node);

		String color = propertyHash.get("rgb_hex");
		r = Integer.parseInt(color.substring(0, 2), 16);
		g = Integer.parseInt(color.substring(2, 4), 16);
		b = Integer.parseInt(color.substring(4, 6), 16);
		id = Integer.parseInt(propertyHash.get("id"));
	}

	public int getRed() {
		return r;
	}

	public int getGreen() {
		return g;
	}

	public int getBlue() {
		return b;
	}

	public int getId() {
		return id;
	}
}
