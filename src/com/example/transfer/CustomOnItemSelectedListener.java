package com.example.transfer;

import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Toast;
 
public class CustomOnItemSelectedListener implements OnItemSelectedListener {
 
  Search myparent = null;
	
  public CustomOnItemSelectedListener(Search in_parent){
	  myparent = in_parent;
  }
  
  public void onItemSelected(AdapterView<?> parent, View view, int pos,long id) {
	Toast.makeText(parent.getContext(), 
		 parent.getItemAtPosition(pos).toString(),
		Toast.LENGTH_SHORT).show();
		myparent.mapResults(view);
  }
 
  @Override
  public void onNothingSelected(AdapterView<?> arg0) {
	// TODO Auto-generated method stub
  }
 
}
