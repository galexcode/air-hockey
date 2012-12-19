package com.copyandpasteteam.air_hockey.test;
import android.app.Instrumentation;
import android.test.ActivityInstrumentationTestCase2;
import android.test.TouchUtils;
import android.view.KeyEvent;
import android.view.View;
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
		solo.assertCurrentActivity("Wrong activity", MainActivity.class);
  }
  
  public void testMultiplayer() throws Exception {
         
	  View button = solo.getView(com.copyandpasteteam.air_hockey.R.id.MultiPlayerStartButton);
	  solo.clickOnView(button);
 
  }
  
  public void testHelp() throws Exception {
	    
		  View button = solo.getView(com.copyandpasteteam.air_hockey.R.id.HelpButton);
		  solo.clickOnView(button);

		 
  }
  
  public void testInfo() throws Exception {
        
		  View button = solo.getView(com.copyandpasteteam.air_hockey.R.id.InfoButton);
		  solo.clickOnView(button);    
	 
  }
  
  public void testExit() throws Exception {
      
		  View button = solo.getView(com.copyandpasteteam.air_hockey.R.id.ExitButton);
		  solo.clickOnView(button);    
	 
  }
  
  public void test1()
  {
		solo.goBack();
  }
  
  @Override
  public void tearDown() throws Exception {
    try {
      solo.finalize();
    } catch (Throwable e) {

      e.printStackTrace();
    }
    getActivity().finish();
    super.tearDown();
  }

}