package com.ag.elisheva.ag_3d_rotatecard.view;

import android.content.Context;
import android.graphics.Canvas;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;

import com.ag.elisheva.ag_3d_rotatecard.app_data.LogoGLRenderer;
import com.ag.elisheva.ag_3d_rotatecard.demo.SceneLoader;

import static android.view.MotionEvent.INVALID_POINTER_ID;

public class LogoGLSurfaceView extends GLSurfaceView {
    private String tag = LogoGLSurfaceView.class.getSimpleName();
    public LogoGLRenderer logoGLRenderer;
    private final float TOUCH_SCALE_FACTOR = 180.0f / 320;
    private SceneLoader scene;
    private float mLastTouchX;
    private float mLastTouchY;
    private ScaleGestureDetector mScaleDetector;
    private float mScaleFactor = 1.f;
    private int mActivePointerId = INVALID_POINTER_ID;



    /**
     * setEGLContextClientVersion(2);
     * Set the Renderer for drawing on the GLSurfaceView
     * Render the view only when there is a change in the drawing data
     * @param context
     */
    public LogoGLSurfaceView(Context context){
        super(context);
        //
        setEGLContextClientVersion(2);
        logoGLRenderer = new LogoGLRenderer(this);
        setRenderer(logoGLRenderer);
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());
        logoGLRenderer.scaleXY =  .7f;

    }

    public void setScene(SceneLoader scene) {
        this.scene = scene;
        logoGLRenderer.setScene(scene);
    }
    @Override
    public boolean onTouchEvent(MotionEvent e) {
        // MotionEvent reports input details from the touch screen
        // and other input controls. In this case, you are only
        // interested in events where the touch position changed.
        mScaleDetector.onTouchEvent(e);
        final int action = e.getActionMasked();
        // float x = e.getX();
        // float y = e.getY();

        switch (action) {
            case MotionEvent.ACTION_DOWN: {
                logoGLRenderer.cardAction = "down";
                final int pointerIndex = e.getActionIndex();
                final float x = e.getX( pointerIndex);
                final float y = e.getY( pointerIndex);
                mLastTouchX = x;
                mLastTouchY = y;
                mActivePointerId = e.getPointerId(0);
                break;
            }
            case MotionEvent.ACTION_MOVE:{
                final int pointerIndex =
                        e.findPointerIndex(mActivePointerId);

                final float x = e.getX( pointerIndex);
                final float y = e.getY( pointerIndex);

                // Calculate the distance moved
                float dx = x - mLastTouchX;
                float dy = y - mLastTouchY;

                // reverse direction of rotation above the mid-line
                if (y > getHeight() / 2) {
                    dx = dx * -1 ;
                }

                // reverse direction of rotation to left of the mid-line
                if (x < getWidth() / 2) {
                    dy = dy * -1 ;
                }
                logoGLRenderer.cardAction = "rotate";
                logoGLRenderer.mAngle += (dx + dy) * TOUCH_SCALE_FACTOR;  // = 180.0f / 320
                requestRender();

                // Remember this touch position for the next move event
                mLastTouchX = x;
                mLastTouchY = y;

                break;
                /*
                float dx = x - mPreviousX;
                float dy = y - mPreviousY;

                // reverse direction of rotation above the mid-line
                if (y > getHeight() / 2) {
                    dx = dx * -1 ;
                }

                // reverse direction of rotation to left of the mid-line
                if (x < getWidth() / 2) {
                    dy = dy * -1 ;
                }
                cardGLRenderer.cardAction = "rotate";
                cardGLRenderer.mAngle += (dx + dy) * TOUCH_SCALE_FACTOR;  // = 180.0f / 320
                requestRender();*/
            }
            case MotionEvent.ACTION_UP: {
                logoGLRenderer.cardAction = "up";
                mActivePointerId = INVALID_POINTER_ID;
                break;
            }

            case MotionEvent.ACTION_CANCEL: {
                logoGLRenderer.cardAction = "cancel";
                mActivePointerId = INVALID_POINTER_ID;
                break;
            }
            case MotionEvent.ACTION_POINTER_UP: {
                logoGLRenderer.cardAction = "pointer_up";
                final int pointerIndex = e.getActionIndex();
                final int pointerId = e.getPointerId(pointerIndex);

                if (pointerId == mActivePointerId) {
                    // This was our active pointer going up. Choose a new
                    // active pointer and adjust accordingly.
                    final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
                    mLastTouchX = e.getX(newPointerIndex);
                    mLastTouchY = e.getY(newPointerIndex);
                    mActivePointerId = e.getPointerId(newPointerIndex);
                }
                break;
            }
        }

        return true;
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.save();
        canvas.scale(mScaleFactor, mScaleFactor);

        canvas.restore();
    }
    private class ScaleListener
            extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {

            logoGLRenderer.cardAction = "scale";
            mScaleFactor *= detector.getScaleFactor();

            // Don't let the object get too small or too large.
            mScaleFactor = Math.max(0.1f, Math.min(mScaleFactor, 10.0f));

            logoGLRenderer.scaleXY =  mScaleFactor;
            requestRender();

            return true;
        }
    }

    /**
     * to make sure i dont scale while rotate and vice versa
     * check
     * @return
     */
    private Boolean isActionFree() {
        return true;
    }
}
