package com.edomar.battleship.utils;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

/**
 * Classe di utilità per far partire e femrare il service della musica di background
 */

public class MusicServiceManager {

    private static MusicServiceManager sInstance;
    private Activity mActivity;

    private MusicServiceManager() {
    }

    public  static MusicServiceManager getInstance(){
        if(sInstance == null){
            sInstance = new MusicServiceManager();
        }
        return sInstance;
    }


    /**--------------------------------------
     METHODS FOR BACKGROUND MUSIC
     -------------------------------------**/
    private boolean mIsBound = false;
    private MusicService mServ;

    private ServiceConnection Scon =new ServiceConnection(){

        public void onServiceConnected(ComponentName name, IBinder
                binder) {
            mServ = ((MusicService.ServiceBinder)binder).getService();
        }

        public void onServiceDisconnected(ComponentName name) {
            mServ = null;
        }
    };

    public void doBindService(Activity activity){
        activity.bindService(new Intent(activity,MusicService.class),
                Scon, Context.BIND_AUTO_CREATE);
        mIsBound = true;
    }

    public void doUnbindService(Activity activity) {
        if(mIsBound)
        {
            activity.unbindService(Scon);
            mIsBound = false;
        }
    }


    public ComponentName startService(Activity activity, Intent service) {
        return activity.startService(service);
    }


    public boolean stopService(Activity activity) {
        Intent music = new Intent();
        music.setClass(activity, MusicService.class);
        return activity.stopService(music);

    }

    public void startService(Activity activity) {
        Intent music = new Intent();
        music.setClass(activity, MusicService.class);
        activity.startService(music);
    }

    public void pauseMusic(Activity activity){
        if(mServ != null){
            mServ.pauseMusic();
        }
        
    }
    //end METHODS FOR BACKGROUND MUSIC
}
