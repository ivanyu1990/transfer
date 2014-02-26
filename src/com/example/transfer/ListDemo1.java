package com.example.transfer;

import java.net.URL;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.CharacterData;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import android.annotation.SuppressLint;
import android.app.ListActivity;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

@SuppressLint("NewApi")
public class ListDemo1 extends ListActivity {
	/** Called when the activity is first created. */

	public ArrayList<String> mStrings = new ArrayList<String>();
	// private static RssFeed instance = null;
	private ArrayList<URL> myArrayList = new ArrayList<URL>();

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setListAdapter(new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, mStrings));
		// 啟用按鍵過濾功能
		getListView().setTextFilterEnabled(true);
	}
	@Override  
	protected void onListItemClick(ListView l, View v, int pos, long id) {  
	    super.onListItemClick(l, v, pos, id);
	    //
	    // TODO : Logic
	}  
	// public static String[] mStrings = new String[] {};
}
