package com.edomar.battleship.engines;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.ImageView;

import com.edomar.battleship.R;
import com.edomar.battleship.entities.gameObject.GameObject;
import com.edomar.battleship.entities.grid.Grid;
import com.edomar.battleship.entities.particleSystem.ParticleSystem;

import java.util.ArrayList;

public class Renderer  {

    private static final String TAG = "Renderer";


    private Canvas mCanvas;
    private SurfaceHolder mSurfaceHolder;
    private Paint mPaint;
    private Context mContext;

    /**Costruttore**/
    public Renderer(SurfaceView sh, Context c) {
        this.mSurfaceHolder = sh.getHolder();
        this.mPaint = new Paint();
        mContext = c;
    }

    /**
     * Richiamo il metodo draw delle varie entità del gioco
     * @param grid istanza della griglia da disegnare
     * @param objects lista di oggetti da disegnare
     * @param ps istanza di particleSystem da disegnare
     * @param gameState
     */
    public void draw(Grid grid, ArrayList<GameObject> objects, ParticleSystem ps, int gameState) {

        if (mSurfaceHolder.getSurface().isValid()) {


            mCanvas = mSurfaceHolder.lockCanvas();
            Drawable d = mContext.getResources().getDrawable(R.drawable.battleship_shape, null);
            d.setBounds(0,0, (int)grid.getGridDimension(), (int)grid.getGridDimension());
            d.draw(mCanvas);


            /**Grid**/
            grid.drawGrid(mCanvas, mPaint);

            /** Objects **/
            if(objects != null){
                for (GameObject object: objects) {
                    if(object.checkActive()) {

                        object.draw(mCanvas, mPaint);
                    }
                }
            }


            /**Hit Marker**/
            grid.drawHitCells(mCanvas, mPaint, gameState);

            /**Particles**/
            if(ps != null && ps.mIsRunning){
                ps.draw(mCanvas, mPaint);
            }

            mSurfaceHolder.unlockCanvasAndPost(mCanvas);
        }
    }

    public void drawGridCoordinates(ImageView letters, ImageView numbers, Point size){
        //DRAW THE COORDINATES
        Bitmap lettersBitmap = Bitmap.createBitmap(size.x, size.y, Bitmap.Config.ARGB_8888);
        Bitmap numbersBitmap = Bitmap.createBitmap(size.y, size.x, Bitmap.Config.ARGB_8888);
        Grid.drawCoordinates(lettersBitmap, numbersBitmap, size, mContext);
        letters.setImageBitmap(lettersBitmap);
        numbers.setImageBitmap(numbersBitmap);
    }
}
