package com.edomar.battleship.entities.transforms;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;

public class Collider extends RectF {

    private int mColor = Color.GREEN;


    public void draw (Canvas canvas, Paint paint){
        paint.setColor(mColor);
        canvas.drawRect(this, paint);
    }

    public void setColorRed(){
        mColor = Color.RED;
    }

    public void setColorGreen(){
        mColor = Color.GREEN;
    }

    public String getColor(){
        if(mColor == Color.GREEN){
            return "green";
        }else {
            return "red";
        }
    }
}
