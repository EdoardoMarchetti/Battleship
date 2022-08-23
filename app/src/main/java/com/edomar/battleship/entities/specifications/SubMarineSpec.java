package com.edomar.battleship.entities.specifications;

import android.graphics.PointF;

public class SubMarineSpec extends ObjectSpec {

    private static final String tag = "Ship";
    private static final String gridTag = "Su";
    private static final String bitmapName = "ship_sub_marine";
    private static final float speed = 0;
    private static final PointF blocksOccupied= new PointF(1,5);
    private static final  String[] components = new String[]{"ShipGraphicsComponent",
            "ShipSpawnComponent",
            "ShipUpdateComponent"
    };

    public SubMarineSpec() {
        super(tag, gridTag,bitmapName, speed,blocksOccupied, components);
    }
}
