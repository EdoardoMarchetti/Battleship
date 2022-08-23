package com.edomar.battleship.entities.gameObject;

/**
 * La classe GameObject rappresenta un qualsiasi oggetto
 * che deve essere impiegato nel campo da gioco (Nave o Razzo)
 */


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;


import com.edomar.battleship.entities.components.interfaces.GraphicsComponent;
import com.edomar.battleship.entities.components.interfaces.InputComponent;
import com.edomar.battleship.entities.components.interfaces.SpawnComponent;
import com.edomar.battleship.entities.components.interfaces.UpdateComponent;
import com.edomar.battleship.entities.specifications.ObjectSpec;
import com.edomar.battleship.entities.transforms.Transform;

public class GameObject {

    private Transform mTransform;
    private boolean isActive = false;
    private String mTag;
    private String mGridTag;


    private GraphicsComponent graphicsComponent;
    private UpdateComponent updateComponent;
    private SpawnComponent spawnComponent;


    /** --------------------------------------------------------------------------------------------
     * ------------------------------------SETTERS--------------------------------------------------
     * ------------------------------------------------------------------------------------------**/
    /**setSpawner
     * Imposta lo spawnCompnent dell'oggetto
     * @param s
     */
    public void setSpawner(SpawnComponent s){
        spawnComponent = s;
    }

    /** setGraphics
     * Imposta il Graphics component dell'oggetto e lo inizializza
     * @param g componente grafico da usare
     * @param c context
     * @param spec specification dell'oggetto da rappresentare con tutte le sue caratteristiche
     * @param objectSize dimensione oggetto scalata ripsetto alla dimensione della cella
     */
    public void setGraphics(GraphicsComponent g, Context c,
                            ObjectSpec spec, PointF objectSize){
        //Set the graphics component and initialize it
        graphicsComponent = g;
        g.initialize(c, spec, objectSize);
    }

    /**setUpdater
     * Imposta l'UpdateComponent dell'oggetto
     * @param u
     */
    public void setUpdater(UpdateComponent u){
        updateComponent = u;
    }

    /**setInput
     * Imposta l'inputComponent
     * @param s
     */
    public void setInput(InputComponent s){
        s.setTransform(mTransform);
    }

    public void setTag(String tag){
        mTag = tag;
    }

    public void setGridTag(String gridTag){
        mGridTag = gridTag;
    }

    public void setTransform(Transform t){
        mTransform = t;
    }

    public void setActive() {
        isActive = true;
    }

    public void setInactive(){
        isActive = false;
    }



    /** --------------------------------------------------------------------------------------------
     * ------------------------------------GETTERS--------------------------------------------------
     * ------------------------------------------------------------------------------------------**/
    public Transform getTransform(){
        return mTransform;
    }

    public boolean checkActive() {
        return isActive;
    }

    public String getTag() {
        return mTag;
    }

    public String getGridTag() {
        return mGridTag;
    }

    public UpdateComponent getUpdateComponent() {
        return updateComponent;
    }

    /** --------------------------------------------------------------------------------------------
     * -------------------------METODI CHE INVOCANO I COMPONENTS------------------------------------
     * ------------------------------------------------------------------------------------------**/
    public void draw(Canvas canvas, Paint paint){
        graphicsComponent.draw(canvas, paint, mTransform);
    }

    public void update(long fps) {
        if (!(updateComponent.update(fps,
                mTransform))) {
            // Component returned false
            isActive = false;
        }

    }

    public boolean spawn(int row, int column, boolean show) {
        // Only spawnComponent if not already active
        if (!isActive) {
            spawnComponent.spawn(mTransform, row, column);
            if(show){
                isActive = true;
            }
            return true;
        }
        return false;
    }



}
