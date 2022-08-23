package com.edomar.battleship.entities.particleSystem;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;


import java.util.ArrayList;
import java.util.Random;

public class ParticleSystem {



    public float mDuration;

    public ArrayList<Particle> mParticles;
    public Random random = new Random();
    public boolean mIsRunning = false;
    public boolean mShipHit = false;

    public void init (int numParticles){

        mParticles = new ArrayList<>();
        //Create the particles
        for (int i = 0; i < numParticles; i++) {
            float angle = (random.nextInt(360));
            angle = angle * 3.14f / 180.0f;
            float speed = (random.nextInt(10)+1);

            PointF direction;

            direction= new PointF((float) Math.cos(angle) * speed,
                    (float) Math.sin(angle) * speed);

            mParticles.add(new Particle(direction));
        }
    }

    public void update (long fps){
        mDuration -= (1f/fps);

        for (Particle p:mParticles
             ) {
            p.update();
        }

        if(mDuration< 0){
            mIsRunning = false;

        }


    }

    public void emitParticles(PointF startPosition){
        mIsRunning = true;
        mDuration = 0.75f;

        for (Particle p:mParticles) {
            p.setPosition(startPosition);
        }
    }

    public void draw (Canvas canvas, Paint paint){

        for (Particle p : mParticles
             ) {

            int red=0;
            int green=0;
            int blue = 0;


            if(mShipHit) {
                red = 255;
                green = random.nextInt(215) + 1;
            }else{
                red = random.nextInt((125-100)+1)+100;
                green = random.nextInt((180-125)+1)+125;
                blue = random.nextInt((256-230)+1)+230;

            }


            paint.setARGB(255,
                    red,
                    green,
                    blue);

            canvas.drawCircle(p.getPosition().x,
                    p.getPosition().y,
                    10,
                     paint);

        }
    }

    public void stop(){
        mIsRunning = false;
    }

    public boolean isRunning(){
        return mIsRunning;
    }

}
