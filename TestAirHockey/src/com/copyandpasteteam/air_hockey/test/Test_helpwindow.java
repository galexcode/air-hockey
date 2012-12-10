package com.copyandpasteteam.air_hockey.test;

import com.copyandpasteteam.air_hockey.HelpWindow;
import com.copyandpasteteam.air_hockey.MainActivity;
import com.copyandpasteteam.air_hockey.SplashScreen;
import com.jayway.android.robotium.solo.Solo;

import android.app.Instrumentation;
import android.test.ActivityInstrumentationTestCase2;
import android.test.TouchUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;

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
		solo.assertCurrentActivity("ASD", HelpWindow.class);
		
	}
	
	public void test1(){
		solo.goBack();
	}
	
	public void test2(){
		
		solo.clickOnView(solo.getView(com.copyandpasteteam.air_hockey.R.id.HelpText));
		
	}

	  public void test3() throws Exception {
	         
		    Instrumentation instrumentation = getInstrumentation();
		    HelpWindow activity = getActivity();
		    Button view = (Button) activity.findViewById(com.copyandpasteteam.air_hockey.R.id.HelpBackButton);  
		    TouchUtils.clickView(this, view);
		 
	 }
}
