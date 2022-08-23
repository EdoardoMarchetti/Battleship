package com.edomar.battleship.engines;

import android.graphics.Point;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ArtificialPlayer {


    private List<String> explored;
    private List<String> frontier;
    private List<String> unexplored;
    private List<String> shipsPosition;


    private  Random random;

    public ArtificialPlayer(){

        explored = new ArrayList<String>(); //Celle già colpite
        frontier = new ArrayList<String>(); //Celle da colpire con alta priorità
        unexplored = new ArrayList<String>(); //Celle non colpite
        shipsPosition = new ArrayList<String>();

        random = new Random();

        //Aggiungo tutte le celle alla lista unexplored
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10 ; j++) {
                unexplored.add(i+","+j);
            }
        }

    }

    /**
     * shot
     * Metodo con cui l'intelligenza artificiale sceglie dove sparare
     * @return la coppia (riga;colonna) in cui sparare
     */
    public Point shot(){

        if(frontier.isEmpty()){                                                         //Non ci sono celle in cui sparare con più alta priorità
            int indice = random.nextInt(unexplored.size());                             //Scelgo una cella da unexplored in maniera casuale
            String[] coordinates = unexplored.remove(indice).split(",");          //Ottengo le coordinate
            int row = Integer.parseInt(coordinates[0]);
            int column = Integer.parseInt(coordinates[1]);
            return new Point(row, column);
        }else {
            int indice = random.nextInt(frontier.size());                               //Scelgo una cella in maniera randomica da frontier
            String[] coordinates = frontier.get(indice).split(",");               //Ottengo le coordinate
            frontier.remove(indice);                                                    //rimuovo la cella dalla lista

            int row = Integer.parseInt(coordinates[0]);
            int column = Integer.parseInt(coordinates[1]);
            return new Point(row, column);
        }
    }


    /**
     * updateList
     * Aggiorno le tre liste
     * @param row riga in cui si è sparato
     * @param column colonna in cui si è sparato
     * @param good esito del colpo (true => nave colpita)
     */
    public void updateList(int row, int column, boolean good){
        String coordinates = row+","+column;
        explored.add(coordinates);             //aggiungo ad explored
        unexplored.remove(row+","+column); //rimuovo da unexplored


        if(good) { //Se l'esito è positivo aggiungo i frontier (4 celle adiacenti)
            shipsPosition.add(coordinates);

            if (row - 1 >= 0 && !explored.contains(row-1+","+column)) {
                frontier.add((row - 1) + "," + column);
            }

            if (row + 1 < 10 && !explored.contains(row+1+","+column)) {
                frontier.add((row + 1) + "," + column);
            }

            if (column - 1 >= 0 && !explored.contains(row+","+(column-1))) {
                frontier.add(row + "," + (column - 1));
            }

            if (column + 1 < 10 && !explored.contains(row+","+column+1)) {
                frontier.add(row + "," + (column + 1));
            }
        }
    }

    /**updateListForDistance
     * Aggiorno le liste dopo aver affondato una nave
     * in modo da cancellare le celle intorno alla nave quando il livello lo richiede
     * @param row riga in cui si è sparato
     * @param column colonna in cui si è sparato
     */
    public void updateListForDistance(int row, int column){
        String coordinates = row+","+column;
        List<String> currentShip = new ArrayList<String>();
        currentShip.add(coordinates);


        int nextRow = -1;
        int nextColumn = -1;
        boolean firstCycle = true;
        int currentRow = 0;
        int currentColumn = 0;

        //La casella (row,column) l'ho già aggiornata in updateList
        while(!(nextRow==currentRow && nextColumn==currentColumn)) {
            if(firstCycle){
                currentRow = row;
                currentColumn = column;
                firstCycle = false;
            }else {
                currentRow = nextRow;
                currentColumn = nextColumn;
            }

            for (int i = currentRow - 1; i <= currentRow + 1; i++) {
                for (int j = currentColumn - 1; j <= currentColumn + 1; j++) {

                    if (!(i == currentRow && j == currentColumn)) {

                        if (shipsPosition.contains(i + "," + j) && !currentShip.contains(i + "," + j)) {
                            nextRow = i;
                            nextColumn = j;
                            currentShip.add(i + "," + j);

                        }

                        explored.add(i + "," + j);
                        frontier.remove(i + "," + j);
                        unexplored.remove(i + "," + j);

                    }

                }
            }

        }



    }


}
