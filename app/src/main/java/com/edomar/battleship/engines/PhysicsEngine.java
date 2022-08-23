package com.edomar.battleship.engines;


import android.graphics.PointF;


import com.edomar.battleship.entities.particleSystem.ParticleSystem;
import com.edomar.battleship.entities.gameObject.GameObject;
import com.edomar.battleship.entities.grid.Grid;
import com.edomar.battleship.entities.levels.LevelManager;
import com.edomar.battleship.entities.transforms.ShipTransform;
import com.edomar.battleship.utils.SoundEffectsManager;

import java.util.ArrayList;

public class PhysicsEngine {

    private LevelManager mLevelMan;
    private ArrayList<GameObject> objects;

    public PhysicsEngine(LevelManager lvlMan) {
        mLevelMan = lvlMan;
        objects = mLevelMan.getObjects();
    }




    /**
     * update
     * Aggiorna tutti gli elementi dinamici all'interno della griglia
     * @param fps frame per second
     * @param ps istanza del ParicleSystem
     * @param grid istanza della griglia
     * @param bf istanza del motore di gestione del campo di battaglia
     */
    public void update(long fps, ParticleSystem ps,
                      Grid grid,
                      AbstractBattleFieldEngine bf
                          ){



        for (GameObject o: objects) {
            if(o.getUpdateComponent() != null) {
                o.update(fps);
            }
        }


        if(ps.mIsRunning){ //Se un particleSystem è attivo aggiornalo
            ps.update(fps);
        }

        if(objects.get(LevelManager.MISSILE).checkActive()){ //Se un misile è attivo gestisci la collisione quando è necessario
            checkMissileDrop(grid, ps, bf);
        }



    }

    /**
     *  checkMissileDrop
     *  se l'oggetto missile è attivo verifica se è arrivato alle coordinate di destinazione
     * @param grid istanza della griglia per verificare se il missile è arrivato a destinazione
     * @param ps istanza del ParticleSystem per attivare l'effetto di splash o esplosione
     * @param bf istanza di un motore di gestione del campo per impostare il notify number
     *           // 0 allora colpo non eseguito o missile ancora in volo
     *           // 1 allora missile caduto (nave colpita o acqua)
     *           // 2 allora gioco finito (affondata l'ultima nave)
     *           // 3 allora nave colpita e affondata
     */
    private void checkMissileDrop(Grid grid, ParticleSystem ps, AbstractBattleFieldEngine bf){


        //Prima faccio precipitare il razzo nell'ultimo colpo eseguito
        int row = grid.getLastHitCoordinates().x;
        int column = grid.getLastHitCoordinates().y;
        boolean result = grid.getLastHitResult();

        GameObject missile = objects.get(LevelManager.MISSILE);

        if(missile.getTransform().getLocation().x >= grid.getBlockDimension()*(column) && missile.checkActive()
               ){//Il missile è arrivato a destinazione

            bf.notifyNumber = 1;
            missile.setInactive(); //faccio scomparire il missile

            float cooX = (float)(grid.getBlockDimension()*(column+0.5));
            float cooY = (float) (grid.getBlockDimension()*(row+0.5));

            ps.emitParticles(
                    new PointF(
                            (float)(grid.getBlockDimension()*(column+0.5)),
                            (float) (grid.getBlockDimension()*(row+0.5)))

            );

            if(result){ //Se il colpo è andato a segno verifica se la nave è affondata

                ps.mShipHit = true;
                boolean found = false;

                for(int i = 0; i < mLevelMan.getNumShipsInLevel() && !found ; i++){
                    GameObject ship = objects.get(i);
                    ShipTransform shipTransform = (ShipTransform) ship.getTransform();

                    if(shipTransform.getCollider().contains(cooX, cooY)){
                        shipTransform.shipHit();

                        if(shipTransform.getLives() == 0){
                            ship.setActive();
                            bf.notifyNumber = 3;
                            if(mLevelMan.needDistance()){
                                grid.setHitForDistance(shipTransform.getCollider());
                            }
                            mLevelMan.shipLost();
                            if(mLevelMan.getShipsRemains() == 0){
                                bf.notifyNumber = 2;
                            }
                        }
                        found = true;
                    }
                }

            }else {
                ps.mShipHit = false;
            }

            //Modifica la griglia in base al risultato
            grid.setHit(row, column, result);

            //Attiva effetto sonoro
            if(ps.mShipHit){
                SoundEffectsManager.playExplosion();
            }else{
                SoundEffectsManager.playSplash();
            }

        }


    }
}
