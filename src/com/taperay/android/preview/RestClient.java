package com.taperay.android.preview;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

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
import org.xml.sax.SAXException;

import android.util.Log;

public class RestClient {

	private static final String tag = "RestClient";
	private static final String baseURI = "http://taperay.com/";
	private String name;
	private DefaultHttpClient client;
	private HttpGet request;
	private HttpResponse resp;
	
	RestClient(String _name) {
		name = _name;
		client = new DefaultHttpClient();
		request = new HttpGet();
	}
	
	public Element index() {
		return get(null);
	}
	
	public Element get(String id) {
		try {
			String fetch_name = name;
			
			if (id != null)
				fetch_name += "/" + id;

			request.setURI(new URI(baseURI + fetch_name + ".xml"));
			Log.v("XXX", "GET " + baseURI + fetch_name + ".xml");
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}

		try {
			resp = client.execute(request);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
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
			return null;
		}
		
		Document xmlDoc;
		
		try {
			xmlDoc = builder.parse(resp.getEntity().getContent());
		} catch (IllegalStateException e) {
			e.printStackTrace();
			return null;
		} catch (SAXException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}

		return xmlDoc.getDocumentElement();		
	}
}
