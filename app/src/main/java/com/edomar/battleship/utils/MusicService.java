package com.edomar.battleship.utils;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.widget.Toast;

import com.edomar.battleship.R;

import androidx.annotation.Nullable;

/**
 * Classe di utilità per la gestionde del service per attivare la musica di sottofondo
 */

public class MusicService extends Service implements MediaPlayer.OnErrorListener {

    private final IBinder mBinder = new ServiceBinder();
    MediaPlayer mPlayer;
    private int length = 0;
    private boolean isPlaying = false;

    public MusicService() {
    }

    ;

    public class ServiceBinder extends Binder {
        public MusicService getService() {
            return MusicService.this;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mPlayer = MediaPlayer.create(this, R.raw.background_music);
        mPlayer.setOnErrorListener(this);

        if (mPlayer != null) {
            mPlayer.setLooping(true);
            mPlayer.setVolume(50, 50);
        }

        mPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
                onError(mPlayer, i, i1);
                return true;
            }
        });
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (mPlayer != null) {
            mPlayer.start();
            isPlaying = true;
        }
        return START_NOT_STICKY;
    }

    public void pauseMusic() {
        if(mPlayer != null){
            if(mPlayer.isPlaying()){
                mPlayer.pause();
                length = mPlayer.getCurrentPosition();
                isPlaying = false;
            }
        }
    }

    public void resumeMusic(){
        if(mPlayer != null){
            if(!mPlayer.isPlaying()){
                mPlayer.seekTo(length);
                mPlayer.start();
            }
        }
    }

    public void startMusic(){
        mPlayer = MediaPlayer.create(this, R.raw.background_music);
        mPlayer.setOnErrorListener(this);
        if(mPlayer != null){
            mPlayer.setLooping(true);
            mPlayer.setVolume(50,50);
            mPlayer.start();
        }
    }

    public void stopMusic() {
        if (mPlayer != null) {
            mPlayer.stop();
            mPlayer.release();
            mPlayer = null;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mPlayer != null){
            try {
                mPlayer.stop();
                mPlayer.release();
            }finally {
                mPlayer = null;
            }
        }
    }

    public boolean onError(MediaPlayer mp, int what, int extra) {

        Toast.makeText(this, "Music player failed", Toast.LENGTH_SHORT).show();
        if (mPlayer != null) {
            try {
                mPlayer.stop();
                mPlayer.release();
            } finally {
                mPlayer = null;
            }
        }
        return false;
    }

    public boolean getIsPlaying(){
        return isPlaying;
    }
}
