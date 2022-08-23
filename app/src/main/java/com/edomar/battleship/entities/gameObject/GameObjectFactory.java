package com.edomar.battleship.entities.gameObject;

import android.content.Context;
import android.graphics.PointF;


import com.edomar.battleship.engines.BattleFieldBroadcaster;
import com.edomar.battleship.entities.components.AmmoGraphicsComponent;
import com.edomar.battleship.entities.components.AmmoSpawnComponent;
import com.edomar.battleship.entities.components.AmmoUpdateComponent;
import com.edomar.battleship.entities.components.ShipGraphicsComponent;
import com.edomar.battleship.entities.components.ShipInputComponent;
import com.edomar.battleship.entities.components.ShipSpawnComponent;
import com.edomar.battleship.entities.specifications.ObjectSpec;
import com.edomar.battleship.entities.transforms.AmmoTransform;
import com.edomar.battleship.entities.transforms.ShipTransform;

/**
 * Questa classe permette di costruire un oggetto tramite la spec passata al metodo create
 */

public class GameObjectFactory {



    private Context mContext;
    private float mGridSize;
    private float mBlockSize;
    private BattleFieldBroadcaster mBattlefieldReference;

    /**
     * Costruttore
     *
     * @param c
     * @param gridSize dimensione della surfaceView (Battlefield) in cui l'oggetto verrà posizionato
     * @param bf referenza del campo da gioco in cui l'oggetto verrà impiegato
     */
    public GameObjectFactory (Context c, float gridSize, BattleFieldBroadcaster bf){
        this.mContext = c;
        this.mGridSize = gridSize;
        this.mBlockSize = gridSize / 10;
        mBattlefieldReference = bf;
    }

    /**
     * create
     * Permette di costruire l'oggetto relativo alla spec passata come paramentro
     * @param spec specification di riferimento
     * @return oggetto costruito
     */
    public GameObject create(ObjectSpec spec){

        GameObject object = null;

        int numComponents = spec.getComponents().length;


        final float HIDDEN = -2000f;

        PointF objectSize = new PointF(mBlockSize * spec.getBlocksOccupied().x,
                mBlockSize * spec.getBlocksOccupied().y);

        PointF location = new PointF(HIDDEN, HIDDEN);

        // Associo al GameObject
        // il transform corretto
        switch(spec.getTag()){ //Crea il transform in base al tag dell'oggetto
            case "Ship":
                //Crea oggetto nave
                object = new GameObject();
                object.setGridTag(spec.getGridTag());
                object.setTransform(new ShipTransform(objectSize.x, objectSize.y, location, mGridSize));
                break;
            case "Ammo":
                //Crea oggetto missile
                object = new GameObject();
                object.setTag(spec.getTag());
                float speed = mGridSize / spec.getSpeed();
                object.setTransform(new AmmoTransform(speed, objectSize.x, objectSize.y, location, mGridSize));
                break;
            default:
                break;
        }




        //attacca i component all'oggetto
        for(int i=0 ; i < numComponents ; i++){
            switch (spec.getComponents()[i]){
                //Ships' component
                case "ShipGraphicsComponent":
                    object.setGraphics(new ShipGraphicsComponent(),
                            mContext, spec, objectSize);
                    break;

                case "ShipSpawnComponent":
                    object.setSpawner(new ShipSpawnComponent());
                    break;

                case "ShipInputComponent":
                    object.setInput(new ShipInputComponent(mBattlefieldReference));
                    break;



                //Ammos' components
                case "AmmoGraphicsComponent":
                    object.setGraphics(new AmmoGraphicsComponent(), mContext, spec, objectSize);
                    break;

                case "AmmoSpawnComponent":
                    object.setSpawner(new AmmoSpawnComponent());
                    break;

                case "AmmoUpdateComponent":
                    object.setUpdater(new AmmoUpdateComponent());
            }
        }


        return object;
    }

}
