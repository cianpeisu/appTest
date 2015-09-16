package com.example.sucianpei.mapscale;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.DisplayMetrics;
import android.util.FloatMath;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;


public class ImageViewHelper {
    private ImageView imageView;
    private Matrix matrix = new Matrix();
    private Matrix savedMatrix = new Matrix();
    private Bitmap bitmap;

    private float minScaleR;

    private static final int NONE = 0;//initial state
    private static final int DRAG = 1;
    private static final int ZOOM = 2;
    private int mode = NONE;

    private PointF prev = new PointF();
    private PointF mid = new PointF();
    private float dist = 1f;
    private DisplayMetrics dm;
    private ImageButton zoomInButton;
    private ImageButton zoomOutButton;
    public ImageViewHelper(DisplayMetrics dm,ImageView imageView,Bitmap bitmap, ImageButton zoomInButton, ImageButton zoomOutButton){
        this.dm = dm;
        this.imageView = imageView;
        this.zoomInButton = zoomInButton;
        this.zoomOutButton = zoomOutButton;
        this.bitmap = bitmap;
        setImageSize();
        minZoom();
        center();
        imageView.setImageMatrix(matrix);

    }
    public Matrix getMatrix(){
        return matrix;
    }
    public void setZoomIn(){

        minScaleR = Math.min(
                (float) dm.widthPixels / (float) bitmap.getWidth(),
                (float) dm.heightPixels / (float) bitmap.getHeight());
        if (minScaleR < 1.0) {
            matrix.postScale(minScaleR+0.5f, minScaleR+0.5f);
            Log.e("minScaleR", minScaleR + "");
            Log.e("minScaleR", bitmap.getWidth() + "");
            Log.e("minScaleR",dm.widthPixels+"");

            Log.e("if", "zoom in");
        }
        else{
            matrix.postScale(minScaleR, minScaleR);
            Log.e("else","zoom in");
        }
    }
    public void setZoomOut(){
        minScaleR = Math.max(
                (float) dm.widthPixels / (float) bitmap.getWidth(),
                (float) dm.heightPixels / (float) bitmap.getHeight());
        if (minScaleR > 1.0) {
            matrix.postScale((minScaleR-0.2f),(minScaleR-0.2f));
        }
        else{
            matrix.postScale(minScaleR, minScaleR);
        }
    }

   //get the min ratio, if pic is larger than screen then ratio<1
    public void minZoom() {
        minScaleR = Math.min(
                (float) dm.widthPixels / (float) bitmap.getWidth(),
                (float) dm.heightPixels / (float) bitmap.getHeight());
        if (minScaleR <= 1.0) {
            matrix.postScale(minScaleR, minScaleR);
        }
        else{
            matrix.postScale(1.5f, 1.5f);
        }
    }

    public void center() {
        center(true, true);
    }

    public void center(boolean horizontal, boolean vertical) {

        Matrix m = new Matrix();
        m.set(matrix);
        RectF rect = new RectF(0, 0, bitmap.getWidth(), bitmap.getHeight());
        m.mapRect(rect);

        float height = rect.height();
        float width = rect.width();

        float deltaX = 0, deltaY = 0;

        if (vertical) {
        //if larger than screen and there's space on the top then move upward
            int screenHeight = dm.heightPixels;
            if (height < screenHeight) {
                deltaY = (screenHeight - height) / 2 - rect.top;
            } else if (rect.top > 0) {
                deltaY = -rect.top;
            } else if (rect.bottom < screenHeight) {
                deltaY = imageView.getHeight() - rect.bottom;
            }
        }

        if (horizontal) {
            int screenWidth = dm.widthPixels;
            if (width < screenWidth) {
                deltaX = (screenWidth - width) / 2 - rect.left;
            } else if (rect.left > 0) {
                deltaX = -rect.left;
            } else if (rect.right < screenWidth) {
                deltaX = screenWidth - rect.right;
            }
        }
        matrix.postTranslate(deltaX, deltaY);
    }
    //distance
    public float spacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return FloatMath.sqrt(x * x + y * y);
    }


    public void midPoint(PointF point, MotionEvent event) {
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        point.set(x / 2, y / 2);
    }
    public void setImageSize(){
        zoomInButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                setZoomIn();
                center();
                imageView.setImageMatrix(matrix);
            }

        });
        zoomOutButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                setZoomOut();
                center();
                imageView.setImageMatrix(matrix);
            }

        });
        //MotionEvent:Object used to report movement (mouse, pen, finger, trackball) events.
        imageView.setOnTouchListener(new View.OnTouchListener(){

            @Override
            public boolean onTouch(View arg0, MotionEvent event) {
                switch (event.getAction() & MotionEvent.ACTION_MASK) {

                    case MotionEvent.ACTION_DOWN://user's finger press the screen
                        savedMatrix.set(matrix);
                        prev.set(event.getX(), event.getY());
                        mode = DRAG;
                        break;

                    case MotionEvent.ACTION_POINTER_DOWN://an other finger press
                        dist = spacing(event);
                        if (spacing(event) > 10f) {
                            savedMatrix.set(matrix);
                            midPoint(mid, event);
                            mode = ZOOM;
                        }
                        break;
                    case MotionEvent.ACTION_UP://fingers leave
                    case MotionEvent.ACTION_POINTER_UP:
                        mode = NONE;
                        break;
                    case MotionEvent.ACTION_MOVE:
                        if (mode == DRAG) {
                            matrix.set(savedMatrix);
                            matrix.postTranslate(event.getX() - prev.x, event.getY()
                                    - prev.y);
                        } else if (mode == ZOOM) {
                            float newDist = spacing(event);
                            if (newDist > 10f) {
                                matrix.set(savedMatrix);
                                float tScale = newDist / dist;
                                matrix.postScale(tScale, tScale, mid.x, mid.y);

                            }

                        }
                        break;
                }
                imageView.setImageMatrix(matrix);
                center();
                return true;
            }

        });
    }
}
