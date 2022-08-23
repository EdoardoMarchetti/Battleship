package com.edomar.battleship.entities.specifications;

import android.graphics.PointF;

public class BattleshipSpec extends ObjectSpec {

    private static final String tag = "Ship";
    private static final String gridTag = "B";
    private static final String bitmapName = "ship_battleship";
    private static final float speed = 0;
    private static final PointF blocksOccupied= new PointF(1,5);
    private static final  String[] components = new String[]{"ShipGraphicsComponent",
            "ShipSpawnComponent",
            "ShipInputComponent",
    };

    public BattleshipSpec() {
        super(tag, gridTag, bitmapName, speed,blocksOccupied, components);
    }
}
