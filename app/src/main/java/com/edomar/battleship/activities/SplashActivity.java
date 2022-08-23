package com.edomar.battleship.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.edomar.battleship.R;
import com.edomar.battleship.utils.SoundEffectsManager;

import java.util.Locale;

public class SplashActivity extends AppCompatActivity {

    private static final String SPLASH_ACTIVITY = SplashActivity.class.getSimpleName();

    private final int SPLASH_TIMEOUT = 1000;



    private SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        sp = getSharedPreferences(getString(R.string.configuration_preference_key), MODE_PRIVATE);


        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                if(initData(getBaseContext())){
                    /* Create an Intent that will start the Menu-Activity. */
                    Intent mainIntent = new Intent(SplashActivity.this , MainActivity.class);
                    SplashActivity.this.startActivity(mainIntent);
                    SplashActivity.this.finish();
                }else{
                    Toast.makeText(SplashActivity.this, R.string.launch_error, Toast.LENGTH_LONG).show();
                    SplashActivity.this.finish();
                }
            }
        }, SPLASH_TIMEOUT);

    }

    private boolean initData(Context c) {
        /**init Sound Engine**/
        SoundEffectsManager.getInstance(c);

        /** Language Config **/
        Log.d(SPLASH_ACTIVITY, "setLocale: start");
        String lang = sp.getString(getString(R.string.language_key), "English");
        String languageToLoad = "";

        switch (lang) {
            case "English":
            case "Inglese":
                languageToLoad = "en";
                break;
            case "Italian":
            case "Italiano":
                languageToLoad = "it";
                break;
        }
        Log.d(SPLASH_ACTIVITY, "setLocale: SELECTED LANGUAGE= "+ languageToLoad);
        Locale locale = new Locale(languageToLoad);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.setLocale(locale);
        this.getBaseContext().getResources().updateConfiguration(config,
                this.getBaseContext().getResources().getDisplayMetrics());
        //end language configuration

        Locale current = getResources().getConfiguration().locale;

        return true;
    }


}