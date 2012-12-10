package com.copyandpasteteam.air_hockey.test;


import com.copyandpasteteam.air_hockey.InfoWindow;
import com.jayway.android.robotium.solo.Solo;

import android.app.Instrumentation;
import android.test.ActivityInstrumentationTestCase2;
import android.test.TouchUtils;
import android.widget.Button;

public class Test_infowindow extends ActivityInstrumentationTestCase2<InfoWindow> {

	private Solo solo;
	
	public Test_infowindow() {
		super(InfoWindow.class);
	}

	protected void setUp() throws Exception {
	    super.setUp();
	    solo = new Solo(getInstrumentation(), getActivity());
	}
	
	public void testApp()
	{
		solo.assertCurrentActivity("ASD", InfoWindow.class);
		
	}
	
	public void test1(){
		solo.goBack();
	}
	
	public void test2(){
		
		solo.clickOnView(solo.getView(com.copyandpasteteam.air_hockey.R.id.InfoText));
		
	}

	  public void test3() throws Exception {
	         
		    Instrumentation instrumentation = getInstrumentation();
		    InfoWindow activity = getActivity();
		    Button view = (Button) activity.findViewById(com.copyandpasteteam.air_hockey.R.id.InfoBackButton);  
		    TouchUtils.clickView(this, view);
		 
	 }

}
