package com.edomar.battleship.entities.levels;

import android.content.Context;
import android.graphics.RectF;


import com.edomar.battleship.engines.BattleFieldBroadcaster;
import com.edomar.battleship.entities.gameObject.GameObject;
import com.edomar.battleship.entities.gameObject.GameObjectFactory;
import com.edomar.battleship.entities.specifications.BattleshipSpec;
import com.edomar.battleship.entities.specifications.CarrierSpec;
import com.edomar.battleship.entities.specifications.CruiserSpec;
import com.edomar.battleship.entities.specifications.DestroyerSpec;
import com.edomar.battleship.entities.specifications.MissileSpec;
import com.edomar.battleship.entities.specifications.PatrolSpec;
import com.edomar.battleship.entities.specifications.RescueSpec;
import com.edomar.battleship.entities.specifications.SubMarineSpec;
import com.edomar.battleship.entities.transforms.Collider;
import com.edomar.battleship.utils.Utilities;

import java.util.ArrayList;



public class LevelManager {



    public static int MISSILE;
    public static int sNumShipsInLevel;

    private ArrayList<GameObject> objects;
    private Level currentLevel;
    private GameObjectFactory factory;
    private int mNumShipsRemains;

    private boolean[] shipsInError; //Array di navi che sono in posizioni non valide

    public LevelManager(Context context, float gridSize, BattleFieldBroadcaster bf, String level){

        objects = new ArrayList<>();
        factory = new GameObjectFactory(context, gridSize, bf);

        setCurrentLevel(level);
        buildGameObject();
        shipsInError = new boolean[sNumShipsInLevel];
    }

    //Imposto il livello corrente che deve essere visualizzato sul Battlefield
    public void setCurrentLevel(String level) {
        level = Utilities.translateScenario(level);
        switch (level){
            case "russian":
                currentLevel = new LevelRussian();
                break;
            case "classic":
                currentLevel = new LevelClassic();
                break;
            case "standard":
                currentLevel = new LevelStandard();
                break;
            case "test":
                currentLevel = new LevelTest();
                break;
            default:

                break;

        }
    }

    //Costruisco i gameObjects necessari per il livello ricavando la lista definita nella classe LevelX
    public void buildGameObject(){

        objects.clear();
        ArrayList<String> objectsToLoad = currentLevel.getLevelObjects();

        for(int i = 0; i < objectsToLoad.size(); i++){

            switch (objectsToLoad.get(i)){

                case "battleship":
                    objects.add(factory.create(new BattleshipSpec()));
                    break;
                case "carrier":
                    objects.add(factory.create(new CarrierSpec()));
                    break;
                case "cruiser":
                    objects.add(factory.create(new CruiserSpec()));
                    break;
                case "destroyer":
                    objects.add(factory.create(new DestroyerSpec()));
                    break;
                case "patrol":
                    objects.add(factory.create(new PatrolSpec()));
                    break;
                case "rescue":
                    objects.add(factory.create(new RescueSpec()));
                    break;
                case "submarine":
                    objects.add(factory.create(new SubMarineSpec()));
                    break;
                case "missile":
                    objects.add(factory.create(new MissileSpec()));
                    MISSILE = i;
                    sNumShipsInLevel = i;
                    mNumShipsRemains = i;
                    break;
                default:
                    break;
            }

        }


    }

    /**---Getters---**/
    public ArrayList<GameObject> getObjects() {
        return objects;
    }

    public Level getCurrentLevel(){
        return currentLevel;
    }

    public int getNumShipsInLevel(){return sNumShipsInLevel;}

    public int getShipsRemains() {
        return  mNumShipsRemains;
    }

    public boolean needDistance() {
        return currentLevel.mDistance;
    }

    public boolean[] getShipsInError(){
        return shipsInError;
    }

    /**--Altri metodi--**/
    /**
     * Verifica se le navi rispettano le regole del livello sulla griglia
     * @return true => disposizione valida
     */
    public boolean checkCorrectFleetConfiguration() {

        boolean goodPosition = true;
        float blockSize = objects.get(0).getTransform().getBlockDimension();

        if(currentLevel.mDistance){//NON devono essere VICINE
            RectF collider1Expanded = new RectF();
            RectF collider2Expanded = new RectF();

            //Verifico l'incrocio per ogni coppia di navi
            for (int i = 0; i < sNumShipsInLevel; i++) {

                Collider collider1= objects.get(i)
                        .getTransform().getCollider();


                collider1Expanded.top = collider1.top - (blockSize/2);
                collider1Expanded.left = collider1.left - (blockSize/2);
                collider1Expanded.bottom = collider1.bottom + (blockSize/2);
                collider1Expanded.right = collider1.right + (blockSize/2);



                boolean collides = false;

                for (int j = 0; j < sNumShipsInLevel; j++) {
                    if(i != j) { //Non verifico quando la prima e la seconda nave sono la stessa

                        Collider collider2 = objects.get(j)
                                .getTransform().getCollider();


                        collider2Expanded.top = collider2.top - (blockSize/10);
                        collider2Expanded.left = collider2.left - (blockSize/10);
                        collider2Expanded.bottom = collider2.bottom + (blockSize/10);
                        collider2Expanded.right = collider2.right + (blockSize/10);

                        if (RectF.intersects(collider1Expanded, collider2Expanded)) {
                            collides = true;
                            goodPosition = false;

                        }
                    }

                }

                //Coloro il collider della nave principale di verde o di rosso a seconda della necessità
                if(collides){
                    collider1.setColorRed();
                    shipsInError[i] = true;
                }else{
                    collider1.setColorGreen();
                    shipsInError[i] = false;
                }
            }


        }else{//NON devono essere SOVRAPPOSTE

            for (int i = 0; i < sNumShipsInLevel; i++) {

                Collider collider1= objects.get(i)
                        .getTransform().getCollider();

                boolean collides = false;

                //Verifico per ogni coppia di navi
                for (int j = 0; j < sNumShipsInLevel; j++) {
                    if(i != j) { //Non verifico quando la prima e la seconda nave sono la stessa
                        Collider collider2 = objects.get(j)
                                .getTransform().getCollider();

                        if (RectF.intersects(collider1, collider2)) {
                            collides = true;
                            goodPosition = false;
                        }
                    }

                }

                if(collides){
                    collider1.setColorRed();
                    shipsInError[i] = true;
                }else{
                    collider1.setColorGreen();
                    shipsInError[i] = false;
                }
            }

        }

        return goodPosition;
    }

    //Riduco il numero di navi ancora in gioco
    public void shipLost() {
        mNumShipsRemains--;
    }



}
