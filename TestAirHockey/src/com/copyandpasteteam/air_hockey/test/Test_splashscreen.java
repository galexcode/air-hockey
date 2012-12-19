package com.copyandpasteteam.air_hockey.test;

import android.test.ActivityInstrumentationTestCase2;
import android.view.View;

import com.copyandpasteteam.air_hockey.SplashScreen;
import com.jayway.android.robotium.solo.Solo;

public class Test_splashscreen extends ActivityInstrumentationTestCase2<SplashScreen> {

	private Solo solo;

	public Test_splashscreen() {
		super(SplashScreen.class);
	}

	protected void setUp() throws Exception {
	    super.setUp();
	    solo = new Solo(getInstrumentation(), getActivity());
	}



	public void test_splash() throws Exception {

		View view= solo.getView(com.copyandpasteteam.air_hockey.R.id.SplashImageView);
		solo.clickOnView(view); 
	
	}
	public void test_app()
	{

	solo.assertCurrentActivity("Wrong activity", SplashScreen.class);
		
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

