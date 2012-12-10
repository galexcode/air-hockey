package com.copyandpasteteam.air_hockey.test;

import java.util.ArrayList;

import com.copyandpasteteam.air_hockey.MainActivity;
import com.copyandpasteteam.air_hockey.SplashScreen;
import com.jayway.android.robotium.solo.Solo;

import android.app.Activity;
import android.test.ActivityInstrumentationTestCase2;
import android.test.TouchUtils;
import android.view.View;
import android.widget.ImageView;

public class Test_splashscreen extends ActivityInstrumentationTestCase2<SplashScreen> {

	private Solo solo;
	
	public Test_splashscreen() {
		super(SplashScreen.class);
	}

	protected void setUp() throws Exception {
	    super.setUp();
	    solo = new Solo(getInstrumentation(), getActivity());
	}
	
	public void testApp()
	{
		solo.assertCurrentActivity("ASD", SplashScreen.class);
		solo.goBack();
	}
	
	public void test1() throws Exception {
				
		Activity activity = solo.getCurrentActivity();
		ImageView view = (ImageView) activity.findViewById(com.copyandpasteteam.air_hockey.R.id.SplashImageView); 
		TouchUtils.clickView(this, view);	
	}

}
