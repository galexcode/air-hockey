package com.copyandpasteteam.air_hockey.test;
import android.app.Instrumentation;
import android.test.ActivityInstrumentationTestCase2;
import android.test.TouchUtils;
import android.view.KeyEvent;
import android.widget.Button;


import com.copyandpasteteam.air_hockey.MainActivity;
import com.jayway.android.robotium.solo.Solo;


 
public class Test_mainactivity extends ActivityInstrumentationTestCase2<MainActivity> {
 
 private Solo solo;

  public Test_mainactivity() {
    super(MainActivity.class);
  }
  
  public void setUp() throws Exception {
	    super.setUp();
	    solo = new Solo(getInstrumentation(), getActivity());
  }
  
  public void testApp()
  {
		solo.assertCurrentActivity("ASD", MainActivity.class);
  }
  
  public void testMultiplayer() throws Exception {
         
    Instrumentation instrumentation = getInstrumentation();
    MainActivity activity = getActivity();
    Button view = (Button) activity.findViewById(com.copyandpasteteam.air_hockey.R.id.MultiPlayerStartButton);  
    TouchUtils.clickView(this, view);
    this.sendKeys(KeyEvent.KEYCODE_BACK);
    TouchUtils.clickView(this, view);
 
  }
  
  public void testHelp() throws Exception {
      
	    Instrumentation instrumentation = getInstrumentation();
	    MainActivity activity = getActivity();
	    Button view = (Button) activity.findViewById(com.copyandpasteteam.air_hockey.R.id.HelpButton);  
	    TouchUtils.clickView(this, view);
	    this.sendKeys(KeyEvent.KEYCODE_BACK);

		 
  }
  
  public void testInfo() throws Exception {
      
	    Instrumentation instrumentation = getInstrumentation();
	    MainActivity activity = getActivity();
	    Button view = (Button) activity.findViewById(com.copyandpasteteam.air_hockey.R.id.InfoButton);  
	    TouchUtils.clickView(this, view);
	    this.sendKeys(KeyEvent.KEYCODE_BACK);
	    
	 
  }
  
  public void testExit() throws Exception {
      
	    Instrumentation instrumentation = getInstrumentation();
	    MainActivity activity = getActivity();
	    Button view = (Button) activity.findViewById(com.copyandpasteteam.air_hockey.R.id.ExitButton);  
	    TouchUtils.clickView(this, view);
	 
  }
  
  public void test1()
  {
		solo.goBack();
  }
 

}