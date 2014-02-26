package com.example.transfer;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.CharacterData;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/*Give url to RSSpaser constructor then read then getResult - arraylist */


public class WeatherRSSpaser {
	
	private ArrayList<String> mStrings = new ArrayList<String>();
	private URL url;
	private DocumentBuilder builder;
	
	
	public WeatherRSSpaser(){
		try {
			url = new URL("http://rss.weather.gov.hk/rss/CurrentWeather_uc.xml");
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public WeatherRSSpaser(String in_url){
		try {
			builder = DocumentBuilderFactory.newInstance()
					.newDocumentBuilder();
			url = new URL(in_url);
			
			
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void readRSS(){
		String temp[];
		//String tmparray[];
		Document doc;
		try {
			doc = builder.parse(url.openStream());
			NodeList nodes = doc.getElementsByTagName("item");
			//NodeList itemnodes = doc.getElementsByTagName("item");
			
			Element element = (Element) nodes.item(0);
			mStrings.add(getElementValue(element,"description"));
			
			/*
			for(int count = 0; count < itemnodes.getLength() ; count++){
				String str = "";
				element = (Element) itemnodes.item(count);
				mStrings.add(getElementValue(element,"title"));
				temp = getElementValue(element,"pubDate").split(" ");
				for(int i = 0; i < temp.length-1 ; i++)
					str += temp[i] + " ";
				mStrings.add(str);
			}*/
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	protected String getElementValue(Element parent, String label) {
		return getCharacterDataFromElement((Element) parent
				.getElementsByTagName(label).item(0));
	}
	
	private String getCharacterDataFromElement(Element e) {
		try {
			Node child = e.getFirstChild();
			if (child instanceof CharacterData) {
				CharacterData cd = (CharacterData) child;
				return cd.getData();
			}
		} catch (Exception ex) {

		}
		return "";
	}
	
	public ArrayList<String> getResult(){
		if(mStrings.size() > 0)
			return mStrings;
		return null;
	} 
	
}
