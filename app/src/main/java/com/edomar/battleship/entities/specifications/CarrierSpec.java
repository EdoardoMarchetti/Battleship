package com.edomar.battleship.entities.specifications;

import android.graphics.PointF;

public class CarrierSpec extends ObjectSpec {

    private static final String tag = "Ship";
    private static final String gridTag = "Ca";
    private static final String bitmapName = "ship_carrier";
    private static final float speed = 0;
    private static final PointF blocksOccupied= new PointF(1,4);
    private static final  String[] components = new String[]{"ShipGraphicsComponent",
            "ShipSpawnComponent",
            "ShipInputComponent",
    };

    public CarrierSpec() {
            super(tag, gridTag, bitmapName, speed,blocksOccupied, components);
    }
}
