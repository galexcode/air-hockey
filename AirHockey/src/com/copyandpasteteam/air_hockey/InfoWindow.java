package com.copyandpasteteam.air_hockey;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.copyandpasteteam.air_hockey.R;

public class InfoWindow extends Activity {
    @Override
    
    public void onCreate(Bundle savedInstanceState) {
    	  super.onCreate(savedInstanceState);
          setContentView(R.layout.activity_infowindow); //Uzycie layoutu z activity_infowindow.xml
          

    }
    
    public void endInfo(View view){
    	
    	finish();
    	
    }
}