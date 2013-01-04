package com.copyandpasteteam.air_hockey.test;
import android.test.ActivityInstrumentationTestCase2;
import android.view.View;

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
  
/*
public void testMulti() throws Throwable {
      
	  View button = solo.getView(com.copyandpasteteam.air_hockey.R.id.MultiPlayerStartButton);
	  solo.clickOnView(button);
	  solo.goBack();

  }*/
  
  public void testHelp() throws Exception {
	    
		  View button = solo.getView(com.copyandpasteteam.air_hockey.R.id.HelpButton);
		  solo.clickOnView(button);
		  solo.clickOnText("Close");		 
  }
  
  public void testInfo() throws Exception {
        
		  View button = solo.getView(com.copyandpasteteam.air_hockey.R.id.InfoButton);
		  solo.clickOnView(button);  
		  solo.clickOnText("Close");
	 
  }
  
  public void testExit() throws Exception {
      
		  View button = solo.getView(com.copyandpasteteam.air_hockey.R.id.ExitButton);
		  solo.clickOnView(button); 
		  
	 
  }
  
  public void test1()
  {
		solo.goBack();
  }
  
  public void test_text_multi() throws Exception {
      
	  solo.clickOnText("Multiplayer");
 
  }
  
  public void test_text_help() throws Exception {
      
	  solo.clickOnText("Help");
	  solo.goBack();
 
  }
  
  public void test_text_about() throws Exception {
      
	  solo.clickOnText("About");
	  solo.goBack();
 
  }
  
  public void test_text_exit() throws Exception {
      
	  solo.clickOnText("Exit Game");
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