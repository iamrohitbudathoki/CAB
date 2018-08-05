package com.geetapoojakaran.cab;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        Thread thread=new Thread(){

        @Override
                public void run()
        {

            try {
                sleep(7000);
            }
            catch (Exception e){
                e.printStackTrace();
            }
            finally {
                Intent welcomeintent=new Intent(SplashActivity.this, WelcomeActivity.class);
                startActivity(welcomeintent);
            }
            }
        };
        thread.start();

    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }
}
