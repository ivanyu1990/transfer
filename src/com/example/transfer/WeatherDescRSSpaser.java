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

import android.util.Log;

/*Give url to RSSpaser constructor then read then getResult - arraylist */


public class WeatherDescRSSpaser {
	
	private ArrayList<String> mStrings = new ArrayList<String>();
	private URL url;
	private DocumentBuilder builder;
	private String weathertype;
	
	
	public WeatherDescRSSpaser(){
		try {
			builder = DocumentBuilderFactory.newInstance()
					.newDocumentBuilder();
			url = new URL("http://rss.weather.gov.hk/rss/LocalWeatherForecast_uc.xml");
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public WeatherDescRSSpaser(String in_url){
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
			Log.i("buglog","yoyooyyo2");
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
			
			String[] wording = {"天晴", "多雲", "天陰", "密雲","陽光充沛","部分時間有陽光","短暫時間有陽光","天色明朗","雨","驟雨或陣雨","間中有驟雨","零散驟雨","局部地區性驟雨","狂風","狂風驟雨","雷雨","毛毛雨"};
			Log.i("buglog","yoyooyyo3");
			temp = mStrings.get(0).split("本港地區");
			Log.i("buglog","yoyooyyo3");
			boolean pass = false;
			Log.i("mmlog",temp[1]);
			String[] temp2 = temp[1].split("，");
			Log.i("mmlog2",temp2[0]);
			for(int count = 0; count < temp2.length ; count++){
				for(int i = 0; i < wording.length ; i++){
					if(temp2[count].contains(wording[i])){
						weathertype = wording[i];
						Log.i("mmlog3",weathertype);
						pass = true;
						break;
					}	
				}
				if(pass){
					break;
				}
			}
			Log.i("buglog","yoy"+weathertype);
			
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
	
	public String getResult(){
		if(weathertype != null)
			return weathertype;
		return null;
	} 
	
}
