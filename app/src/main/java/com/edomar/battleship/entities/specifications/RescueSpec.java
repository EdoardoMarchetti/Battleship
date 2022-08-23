package com.edomar.battleship.entities.specifications;

import android.graphics.PointF;

public class RescueSpec extends ObjectSpec {

    private static final String tag = "Ship";
    private static final String gridTag = "R";
    private static final String bitmapName = "ship_rescue";
    private static final float speed = 0;
    private static final PointF blocksOccupied= new PointF(1,3);
    private static final  String[] components = new String[]{"ShipGraphicsComponent",
            "ShipSpawnComponent",
            "ShipInputComponent",
    };
    public RescueSpec() {
        super(tag, gridTag,bitmapName, speed,blocksOccupied, components);
    }
}
