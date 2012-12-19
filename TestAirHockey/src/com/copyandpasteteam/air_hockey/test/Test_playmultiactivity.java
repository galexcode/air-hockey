package com.copyandpasteteam.air_hockey.test;

import android.test.ActivityInstrumentationTestCase2;

import com.copyandpasteteam.air_hockey.PlayMultiActivity;
import com.jayway.android.robotium.solo.Solo;

public class Test_playmultiactivity extends ActivityInstrumentationTestCase2<PlayMultiActivity> {

	private Solo solo;
	
	public Test_playmultiactivity() {
		super(PlayMultiActivity.class);
	}

	protected void setUp() throws Exception {
	    super.setUp();
	    solo = new Solo(getInstrumentation(), getActivity());
	}
	
	public void testApp()
	{
			solo.assertCurrentActivity("Wrong activity", PlayMultiActivity.class);
	}

}
