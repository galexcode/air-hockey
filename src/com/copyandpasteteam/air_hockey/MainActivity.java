package com.copyandpasteteam.air_hockey;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import com.copyandpasteteam.air_hockey.R;

public class MainActivity extends Activity {

    private static final String EXTRA_MESSAGE = null;

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    
    public void playMulti(View view) {
        
    	Intent intent = new Intent(this, PlayMultiActivity.class);
        startActivity(intent);
    	
    }
    
    public void infoWindow(View view) {
        
    	Intent intent = new Intent(this, InfoWindow.class);
        startActivity(intent);
    	
    }
    
    public void helpWindow(View view) {
        
    	Intent intent = new Intent(this, HelpWindow.class);
        startActivity(intent);
    	
    }
    
    public void exitButton(View view) {
        
    	MainActivity.this.finish();
    	
    }
}
