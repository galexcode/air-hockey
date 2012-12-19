package com.copyandpasteteam.air_hockey.test;

import android.test.ActivityInstrumentationTestCase2;
import android.view.View;

import com.copyandpasteteam.air_hockey.HelpWindow;
import com.jayway.android.robotium.solo.Solo;

public class Test_helpwindow extends ActivityInstrumentationTestCase2<HelpWindow> {

	private Solo solo;
	
	public Test_helpwindow() {
		super(HelpWindow.class);
	}

	protected void setUp() throws Exception {
	    super.setUp();
	    solo = new Solo(getInstrumentation(), getActivity());
	}
	
	public void testApp()
	{
		solo.assertCurrentActivity("Wrong acitivity", HelpWindow.class);
		
	}
	
	public void test1(){
		solo.goBack();
	}
	
	public void test2(){
		
		solo.clickOnView(solo.getView(com.copyandpasteteam.air_hockey.R.id.HelpText));
		
	}

	  public void test3() throws Exception {
	         
			  View button = solo.getView(com.copyandpasteteam.air_hockey.R.id.HelpBackButton);
			  solo.clickOnView(button);
		 
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
