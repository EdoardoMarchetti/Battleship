package com.edomar.battleship.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.util.Log;

import com.edomar.battleship.R;

import java.io.IOException;

public class SoundEffectsManager {

    private static final String TAG = "Sound Engine";

    /**Instance*/
    private static SoundEffectsManager sInstance;


    private static SoundPool mSP;
    public static boolean isSoundEffectOn = false;

    /**Sound ids**/
    private static int mExplosion_ID = -1;
    private static int mSplash_ID = -1;

    SharedPreferences sp;
    SharedPreferences.Editor editor;
    Context context;

    /*
    Constructor
     */
    private SoundEffectsManager(Context c){
        sp = c.getSharedPreferences(c.getString(R.string.configuration_preference_key), Context.MODE_PRIVATE);
        editor = sp.edit();
        context = c;

        //initialize the SoundPool
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            AudioAttributes audioAttributes =
                    new AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                            .build();

            mSP = new SoundPool.Builder()
                    .setMaxStreams(5)
                    .setAudioAttributes(audioAttributes)
                    .build();

        } else {
            mSP = new SoundPool(5, AudioManager.STREAM_MUSIC,0);
        }

        try{
            AssetManager assetManager = c.getAssets();
            AssetFileDescriptor descriptor;

            descriptor = assetManager.openFd("explosion.ogg");
            mExplosion_ID = mSP.load(descriptor, 0);

            descriptor = assetManager.openFd("splash.ogg");
            mSplash_ID = mSP.load(descriptor, 0);


        } catch (IOException e) {
            e.printStackTrace();
        }

        isSoundEffectOn = sp.getBoolean(c.getString(R.string.animation_sound_key), true);
    }

    public  static SoundEffectsManager getInstance(Context c){
        if(sInstance == null){
            sInstance = new SoundEffectsManager(c);
        }
        return sInstance;
    }



    public static void playExplosion(){
        if(isSoundEffectOn){
            mSP.play(mExplosion_ID, 1,1,1,0,1);
            Log.d("Sound check", "playExplosion: ");
        }
    }

    public static void playSplash() {
        if(isSoundEffectOn){
            mSP.play(mSplash_ID, 1,1,1,0,1);
            Log.d("Sound check", "playExplosion: ");
        }
    }


    public static void enableSoundEffect(){
        isSoundEffectOn = true;
    }

    public static void disableSoundEffect(){
        isSoundEffectOn = false;
    }


}
