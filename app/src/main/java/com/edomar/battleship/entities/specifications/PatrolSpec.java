package com.edomar.battleship.entities.specifications;

import android.graphics.PointF;

public class PatrolSpec extends ObjectSpec {

    private static final String tag = "Ship";
    private static final String gridTag = "P";
    private static final String bitmapName = "ship_patrol";
    private static final float speed = 0;
    private static final PointF blocksOccupied= new PointF(1,2);
    private static final  String[] components = new String[]{"ShipGraphicsComponent",
            "ShipSpawnComponent",
            "ShipInputComponent",
    };
    public PatrolSpec() {
        super(tag, gridTag, bitmapName, speed,blocksOccupied, components);
    }
}
