package com.copyandpasteteam.air_hockey;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.copyandpasteteam.air_hockey.R;

public class HelpWindow extends Activity {
    @Override
    
    public void onCreate(Bundle savedInstanceState) {
    	  super.onCreate(savedInstanceState);
          setContentView(R.layout.activity_helpwindow); //Uzycie layoutu z activity_helpwindow.xml


    }
    
    public void endInfo(View view){
    	
    	finish();
    	
    }
}