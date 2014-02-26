package com.example.transfer;

import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.content.Intent;
import android.graphics.Point;

public class GovActivity extends Activity {
	private Button n1;
	private Button a;
	public final static String EXTRA_MESSAGE = "com.example.myfirstapp.MESSAGE";
    @Override
    protected void onCreate(Bundle savedInstanceState) {	
    	requestWindowFeature(Window.FEATURE_NO_TITLE); 
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gov_act);
        Log.i("i", getIntent().getExtras().get("temp").toString());
        Log.i("i", getIntent().getExtras().get("district").toString());
        TextView tv = (TextView)findViewById(R.id.textView2);
        tv.setText(getIntent().getExtras().get("temp").toString() + "¡C");
        TextView tv1 = (TextView)findViewById(R.id.textView1);
        tv1.setText(getIntent().getExtras().get("district").toString() + "\n" + getIntent().getExtras().get("address").toString());
        // tv1.setText(getIntent().getExtras().get("district").toString() );
        n1 = (Button)findViewById(R.id.button_n1);
        a = (Button)findViewById(R.id.button_a);
        n1.setOnClickListener(buttonAddOnClickListener);
        a.setOnClickListener(buttonAddOnClickListener);
    }

    Button.OnClickListener buttonAddOnClickListener  = new Button.OnClickListener(){
        @Override
        public void onClick(View arg0) {
            //Switch statement so you don't have to use a lot of click listeners
            
        	switch (arg0.getId()) {
                case R.id.button_n1:
                	//Intent intent = new Intent(arg0.getContext(), DisplayMessageActivity.class);
                	//String msg = "yoooo";
                	//intent.putExtra(EXTRA_MESSAGE, msg);
                	//arg0.getContext().startActivity(intent);
                	//n1.setText("n1 pressed");
                	break;
                  //  doSomething();
                case R.id.button_a:
                	Intent intent = new Intent(arg0.getContext(), Reminder.class);
                	startActivity(intent);
                	//arg0.getContext().startActivity(intent);
                	//a.setText("A pressed");
                	break;
                  //  doSomethingElse();
            }
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
    
    /*public void sendMessage(View view) {
    	Intent intent = new Intent(this, DisplayMessageActivity.class);
    	
//    	if(view.getId()==R.id.button_a){
    		Button A = (Button) findViewById(R.id.button_b);
    		String msg = "pressed";
    		intent.putExtra(EXTRA_MESSAGE, msg);
    		startActivity(intent);
        
//    	}
    	
    	
        // Do something in response to button
//    	Intent intent = new Intent(this, DisplayMessageActivity.class);
//        EditText editText = (EditText) findViewById(R.id.edit_message);
//        String message = editText.getText().toString();
//        intent.putExtra(EXTRA_MESSAGE, message);
//        startActivity(intent);
    }*/
    
    public void clickButtonB(View v){
    	startActivity(new Intent(this, Search.class));
    }
    
    public void clickButtonC(View v){
    	Intent i = new Intent(this, Sildermenu.class);
    	i.putExtra("district", getIntent().getExtras().get("district").toString());
    	startActivity(i);
    }
}
