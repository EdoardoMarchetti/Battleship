package com.edomar.battleship.entities.specifications;

import android.graphics.PointF;

public class MissileSpec extends ObjectSpec{

    private static final String tag = "Ammo";
    private static final String gridTag = "";
    private static final String bitmapName = "missile_one";
    private static final float speed = .65f;
    private static final PointF blocksOccupied= new PointF(1,1);
    private static final  String[] components = new String[]{"AmmoGraphicsComponent",
            "AmmoSpawnComponent",
            "AmmoUpdateComponent"};



    public MissileSpec() {
        super(tag, gridTag,bitmapName, speed, blocksOccupied, components);
    }


}
