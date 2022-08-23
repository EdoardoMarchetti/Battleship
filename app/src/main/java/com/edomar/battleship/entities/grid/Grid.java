package com.edomar.battleship.entities.grid;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.Log;


import com.edomar.battleship.R;

import java.util.List;

import androidx.core.content.res.ResourcesCompat;

public class Grid  {
    private static final String TAG = "Grid";

    private final float mGridDimension;             //Dimensione griglia in pixel
    private final float mBlockDimension;            //Dimensione di una cella in pixel

    private static int textDimension;               //Dimensione testo per le coordinate
    private int strokeWidth;                        //Spessore linee

    private String[][] mGridConfiguration;          //Configurazione della griglia

    private Point mLastHit= new Point();            //Coordinate dell'ultimo colpo effettuato
    private boolean mLastHitResult = false;         //Esito dell'ultimo colpo effettuato
    private int currentRow;                         //Riga corrente premuta
    private int currentColumn;                      //Colonna corrente premuta
    private boolean pressed = false;                //Flag per indicare che il giocatore sta premendo sullo schermo ( true => disegno il puntatore sulla griglia)


    public Grid(float gridDimension) {
        mGridDimension = gridDimension;
        mBlockDimension = gridDimension / 10;
        strokeWidth = (int) gridDimension / 175;

        mGridConfiguration = new String[10][10];

        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                mGridConfiguration[i][j] ="0";
            }
        }

        textDimension = strokeWidth * 15;

    }

    /**
     * drawGrid
     * Disegna le linee che definiscono la griglia e il puntatore se necessario
     * @param canvas
     * @param paint
     */
    public void drawGrid (Canvas canvas, Paint paint){

        paint.setAntiAlias(true);
        paint.setDither(true);
        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(strokeWidth);

        //Horizontal lines
        for (int i = 0; i < 11; i++) {
            canvas.drawLine(0, mBlockDimension * i, mGridDimension , mBlockDimension * i, paint);
        }

        //Vertical lines
        for (int i = 0; i < 11; i++) {
            canvas.drawLine(mBlockDimension * i, 0, mBlockDimension * i, mGridDimension, paint);
        }

        //Selected coordinates
        if(pressed){
            Paint previousState = paint;
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(Color.WHITE);
            paint.setAlpha(100);

            canvas.drawRect(currentColumn * mBlockDimension, 0, mBlockDimension *(currentColumn+1), mGridDimension, paint);
            canvas.drawRect(0, currentRow * mBlockDimension, mGridDimension, mBlockDimension *(currentRow+1),  paint);

            paint = previousState;
        }

    }

    /**
     * drawCoordinates
     * Disegna le coordinate intorno alla griglia
     * @param letters
     * @param numbers
     * @param size
     * @param c
     */
    public static void drawCoordinates(Bitmap letters, Bitmap numbers, Point size, Context c){

        final Rect textBounds = new Rect();

        //First draw letters
        Canvas canvas = new Canvas(letters);
        Paint paint = new Paint();
        Point unit = new Point();


        unit.x = size.x/10;
        unit.y = size.y;
        paint.setColor(Color.WHITE);
        Typeface tf = ResourcesCompat.getFont(c, R.font.bad_crunge);
        paint.setTypeface(tf);




        //draw letters
        String[] lettersSymbols = new String[]{"A", "B", "C", "D", "E", "F", "G", "H", "I", "J"};

        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(textDimension);
        for (int i = 0; i < 10; i++) {

            paint.getTextBounds(lettersSymbols[i], 0, lettersSymbols[i].length(), textBounds);
            canvas.drawText(lettersSymbols[i], (float) (unit.x * (i+ 0.5)), unit.y / 2 - textBounds.exactCenterY(), paint);
        }

        //Now draw numbers
        canvas.setBitmap(numbers);

        String[] numbersSymbol = new String[]{"1", "2", "3", "4", "5", "6", "7", "8", "9", "10"};
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(textDimension);
        for (int i = 0; i < 10; i++) {

            paint.getTextBounds(numbersSymbol[i], 0, numbersSymbol[i].length(), textBounds);
            canvas.drawText(numbersSymbol[i], unit.x / 2 , (float) (unit.y * (i+ 0.5))- textBounds.exactCenterY(),paint);
        }


    }

    /**
     * drawHitCells
     * Disegna gli HitMarker sulle celle già colpite
     * @param canvas
     * @param paint
     * @param gameState indicazione su come devono essere disegnati
     */
    public void drawHitCells(Canvas canvas, Paint paint, int gameState){
        //Draw hit cell
        if(gameState!=0){
            Paint previousStyle = paint;

            for(int i = 0; i<10; i++){
                for(int j = 0; j<10; j++){
                    if(gameState == 1){ // In match => disegno X dove è stata colpita l'acqua e l'HitMarker rosso per le navi colpite
                        if(mGridConfiguration[j][i].equals("X")){
                            paint.setStyle(Paint.Style.FILL);
                            paint.setColor(Color.WHITE);
                            canvas.drawLine((float) (mBlockDimension*i +mBlockDimension * 0.1), (float)(mBlockDimension*j+mBlockDimension * 0.1), (float) (mBlockDimension*(i+1)-mBlockDimension * 0.1), (float)(mBlockDimension*(j+1)-mBlockDimension * 0.1), paint);
                            canvas.drawLine((float) (mBlockDimension*i+mBlockDimension * 0.1), (float)(mBlockDimension*(j+1)-mBlockDimension * 0.1), (float)(mBlockDimension*(i+1)-mBlockDimension * 0.1), (float)(mBlockDimension*j+mBlockDimension * 0.1), paint);
                        }else if(mGridConfiguration[j][i].equals("S")){
                            paint.setStyle(Paint.Style.STROKE);
                            paint.setColor(Color.RED);
                            canvas.drawCircle((float) (mBlockDimension*(i+ 0.5)), (float) (mBlockDimension*(j+ 0.5)), (float) (mBlockDimension/2.5), paint);
                            paint.setStyle(Paint.Style.FILL);
                            canvas.drawCircle((float) (mBlockDimension*(i+ 0.5)), (float) (mBlockDimension*(j+ 0.5)), (float) (mBlockDimension/4.5), paint);
                        }
                    }else if(gameState == 2){ // In post match => disegno X dove è stata colpita l'acqua, l'HitMarker rosso per le navi colpite e l'HitMarker verde per le navi non colpite
                        if(mGridConfiguration[j][i].equals("X")){
                            paint.setStyle(Paint.Style.FILL);
                            paint.setColor(Color.WHITE);
                            canvas.drawLine((float) (mBlockDimension*i +mBlockDimension * 0.1), (float)(mBlockDimension*j+mBlockDimension * 0.1), (float) (mBlockDimension*(i+1)-mBlockDimension * 0.1), (float)(mBlockDimension*(j+1)-mBlockDimension * 0.1), paint);
                            canvas.drawLine((float) (mBlockDimension*i+mBlockDimension * 0.1), (float)(mBlockDimension*(j+1)-mBlockDimension * 0.1), (float)(mBlockDimension*(i+1)-mBlockDimension * 0.1), (float)(mBlockDimension*j+mBlockDimension * 0.1), paint);
                        }else if(mGridConfiguration[j][i].equals("S")){
                            paint.setStyle(Paint.Style.STROKE);
                            paint.setColor(Color.RED);
                            canvas.drawCircle((float) (mBlockDimension*(i+ 0.5)), (float) (mBlockDimension*(j+ 0.5)), (float) (mBlockDimension/2.5), paint);
                            paint.setStyle(Paint.Style.FILL);
                            canvas.drawCircle((float) (mBlockDimension*(i+ 0.5)), (float) (mBlockDimension*(j+ 0.5)), (float) (mBlockDimension/4.5), paint);
                        }else if(!mGridConfiguration[j][i].equals("0")){
                            paint.setStyle(Paint.Style.STROKE);
                            paint.setColor(Color.GREEN);
                            canvas.drawCircle((float) (mBlockDimension*(i+ 0.5)), (float) (mBlockDimension*(j+ 0.5)), (float) (mBlockDimension/2.5), paint);
                            paint.setStyle(Paint.Style.FILL);
                            canvas.drawCircle((float) (mBlockDimension*(i+ 0.5)), (float) (mBlockDimension*(j+ 0.5)), (float) (mBlockDimension/4.5), paint);
                        }
                    }

                }
            }
            paint = previousStyle;
        }

    }

    //Reimposta tutta la griglia a zero
    public void clearGrid(){
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                mGridConfiguration[i][j] ="0";
            }
        }
    }

    //posiziona la nave in griglia
    public void positionShip(int startRow, int startColumn, int blockOccupied, boolean isVertical, String gridTag){
        if(isVertical){

            for (int i = startRow; i < (startRow+blockOccupied) ; i++) {

                mGridConfiguration[i][startColumn] = gridTag;
            }
        }else{

            for (int j = startColumn; j < (startColumn+blockOccupied) ; j++) {

                mGridConfiguration[startRow][j] = gridTag;
            }
        }
    }

    /** Getters **/
    public Point getCurrentCoordinatesSelected() {
        pressed = false;
        return new Point(currentRow, currentColumn);
    }

    public String[][] getGridConfiguration() {
        return mGridConfiguration;
    }

    public float getGridDimension(){
        return mGridDimension;
    }

    public float getBlockDimension() {
        return mBlockDimension;
    }

    public Point getLastHitCoordinates(){ return  mLastHit;}

    public boolean getLastHitResult(){ return mLastHitResult;}



    /** Setters **/
    public void setDispositionInGrid(List<String[]> gridRows){
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                if(gridRows.size() == 0){ //Non ci sono righe da copiare in griglia quindi si mette tutto a zero
                    mGridConfiguration[i][j] ="0";
                }else{//E' stata passata una configurazione letta da qualche sorgente(file o database)
                    mGridConfiguration[i][j] = (gridRows.get(i))[j];
                }
            }
        }
    }

    public void setHit(int row, int column, boolean shipHit){
        if(shipHit){//Nave colpita
            mGridConfiguration[row][column] = "S";
        }else{//Acqua
            mGridConfiguration[row][column] = "X";
        }
    }


    public void setLastHit(int row, int column, boolean hit) {
        mLastHit.x = row;
        mLastHit.y = column;
        mLastHitResult= hit;
    }

    //Quando la navi non possono essere adiacenti,
    //una volta affondata una nave segno come colpite le celle adiacenti
    public void setHitForDistance(RectF shipCollider) {
        int left = (int) (shipCollider.left / mBlockDimension);
        int top = (int) (shipCollider.top / mBlockDimension);
        int right = (int) (shipCollider.right / mBlockDimension);
        int bottom = (int) (shipCollider.bottom / mBlockDimension);

        for(int i = top-1; i<=bottom; i++){
            if(i>=0 && i<10) {
                if(left-1 >= 0){
                    setHit(i, left - 1, false);
                }
                if(right<10){
                    setHit(i, right, false);
                }
            }
        }
        for(int j = left-1; j<=right; j++){
            if(j>=0  && j<10){
                if(top-1 >= 0){
                    setHit(top-1, j, false);
                }
                if(bottom<10){
                    setHit(bottom, j, false);
                }
            }
        }
    }

    //Metodo per impostare le celle selezionate e fare l'animazione di "radar" nella griglia
    public void setCurrentCoordinatesSelected(int row, int column) {
        currentRow = row;
        currentColumn = column;
        if(!pressed && currentRow != -2 && currentColumn != -2){
            pressed = true;
        }
    }


}
