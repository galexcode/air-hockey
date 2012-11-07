package com.copyandpasteteam.air_hockey;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;

public class SplashScreen extends Activity {
    
    private Thread mSplashThread;    

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash); //Uzycie layoutu z splash.xml

        final SplashScreen sPlashScreen = this;   
        
        mSplashThread =  new Thread(){
            @Override
            public void run(){
                try {
                    synchronized(this){
                        wait(2000); //Czas wyœwietlania splashscreena
                    }
                }
                catch(InterruptedException ex){                    
                }

                finish();
                

                Intent intent = new Intent();
                intent.setClass(sPlashScreen, MainActivity.class);
                startActivity(intent);
                                    
            }
        };
        
        mSplashThread.start();        
    }
        

    @Override
    public boolean onTouchEvent(MotionEvent evt) //Wy³¹czenie splashscreena po dotkniêciu ekranu
    {
        if(evt.getAction() == MotionEvent.ACTION_DOWN)
        {
            synchronized(mSplashThread){
                mSplashThread.notifyAll();
            }
        }
        return true;
    }    
} 