package com.taperay.android.preview;

import java.util.HashMap;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class ServerObject {

	protected HashMap<String, String> propertyHash;
	protected String tag = "ServerObject";

	protected void readFromNode(Node node) {
		NodeList properties = node.getChildNodes();

		for (int j = 0; j < properties.getLength(); j++){
			Node property = properties.item(j);
			String propName = property.getNodeName();
			String propValue = null;

			if (property.getFirstChild() != null)
				propValue = property.getFirstChild().getNodeValue();

			//Log.v(tag, "name " + propName + " value " + propValue);

			if (propName != null && propValue != null)
				propertyHash.put(propName, propValue);
		}
	}

	ServerObject() {
		propertyHash = new HashMap<String, String>();
	}
}
