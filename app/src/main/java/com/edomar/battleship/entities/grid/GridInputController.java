package com.edomar.battleship.entities.grid;

import android.graphics.RectF;
import android.view.MotionEvent;

import com.edomar.battleship.engines.AmmoSpawner;
import com.edomar.battleship.engines.BattleFieldBroadcaster;
import com.edomar.battleship.entities.InputObserver;
import com.edomar.battleship.entities.gameObject.GameObject;
import com.edomar.battleship.entities.levels.LevelManager;

import java.util.ArrayList;

public class GridInputController implements InputObserver {

    private AmmoSpawner mAS;

    public GridInputController(BattleFieldBroadcaster b, AmmoSpawner a){
        b.addObserver(this);
        mAS = a;
    }

    @Override
    public void handleInput(MotionEvent event, Grid grid, LevelManager levelManager, int gameState, int notifyNumber) {
        //Prima di tutto controllare se sia è in preMatch o durante una partita
        //PreMatch -> l'input non è mai rivolta alla griglia
        //Match -> l'input è sempre rivolto ala griglia -> verificare se si ha già fatto quel colpo
        //                                              -> far partire l'animazione del razzo
        //                                              -> definire se si è colpito una nave o meno
        //                                              -> indicare a Grid il colpo effettuato


        if(gameState == 1 && notifyNumber == 0 && !levelManager.getObjects().get(LevelManager.MISSILE).checkActive()) {
            //Ottengo informazioni sull'evento
            int i = event.getActionIndex();
            int x = (int) event.getX(i);
            int y = (int) event.getY(i);

            int row;
            int column;
            String[][] gridConfiguration = grid.getGridConfiguration();


            int eventType = event.getAction() & MotionEvent.ACTION_MASK;

            switch (eventType){

                //Con Action_down o Action_move aggiorno la cella selezionata per animazione "radar"
                case MotionEvent.ACTION_DOWN:
                case MotionEvent.ACTION_MOVE:
                    row = (int) (y / grid.getBlockDimension());
                    column = (int) (x / grid.getBlockDimension());
                    if(row >= 0 && row < 10 && column >= 0 && column < 10)
                        grid.setCurrentCoordinatesSelected(row, column);
                    else{
                        grid.setCurrentCoordinatesSelected(-2,-2);
                    }
                    break;

                //Con action_up attivo l'animazione del razzo quando è necessario
                case MotionEvent.ACTION_UP:
                    row = grid.getCurrentCoordinatesSelected().x;
                    column = grid.getCurrentCoordinatesSelected().y;

                    //Verifica se il tocco è avvenuto in coordinate ammesse e se il colpo era già stato fatto
                    if (!gridConfiguration[row][column].equals("S") && !gridConfiguration[row][column].equals("X")) {

                        //Se si è nell'if le coordinate scelte sono disponibili
                        ArrayList<GameObject> objects = levelManager.getObjects();
                        boolean hit = false;
                        int j = 0;

                        //Verifica se è stata colpita una nave
                        while (!hit && j < levelManager.getNumShipsInLevel()) {
                            RectF objectCollider = objects.get(j) //nave
                                    .getTransform()               //transform
                                    .getCollider();               //collider

                            if (objectCollider.contains(x, y)) {
                                //nave colpita
                                hit = true;
                            }

                            j++;
                        }

                        //se al termine del ciclo for hit=false nessuna nave è stata colpita e quindi sulla griglia verrà disegnata
                        // una X quando l'animazione del razzo terminerà
                        grid.setLastHit(row, column, hit);

                        //fai partire l'animazione del razzo
                        mAS.spawnAmmo(row, column); //a spawnAmmo devono essere passate le coordinate colpite per posizionare il missile

                    }
            }
        }
    }
}
