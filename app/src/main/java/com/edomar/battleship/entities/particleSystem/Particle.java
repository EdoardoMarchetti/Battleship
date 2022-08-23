package com.edomar.battleship.entities.particleSystem;


import android.graphics.PointF;

public class Particle {

    public PointF mVelocity;
    public PointF mPosition;

    public Particle(PointF direction) {
        this.mVelocity = new PointF();
        this.mPosition = new PointF();

        //Determine the direction
        mVelocity.x = direction.x;
        mVelocity.y = direction.y;
    }

    public void update(){
        //Move the particle
        mPosition.x += mVelocity.x;
        mPosition.y += mVelocity.y;
    }

    public void setPosition(PointF position){
        mPosition.x = position.x;
        mPosition.y = position.y;
    }

    public PointF getPosition() {
        return mPosition;
    }
}
