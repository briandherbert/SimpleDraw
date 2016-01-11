package com.burningaltar.simpledraw;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * A surface to draw on
 *
 * @author bherbert
 *
 */
public class DrawSurface extends SurfaceView implements SurfaceHolder.Callback {
    public static final String TAG = DrawSurface.class.getSimpleName();

    private Path mCurrentPath = new Path();

    public static final int STROKE_WIDTH = 20;

    private SurfaceHolder mSurfaceHolder;

    private Paint mPaint = new Paint();

    /** This is just so we don't have to create a new Paint obj every time we update the surface */
    private Paint mBmpPaint = new Paint();

    /** The viewport bmp which will be drawn on **/
    private Bitmap mBmpDraw = null;

    /** The canvas for which {@link #mBmpDraw} is the source */
    private Canvas mCanvas = null;

    /** Used to track a quick down-up, with no finger moving for drawing **/
    boolean mHasDrawn = false;

    boolean mHasBeenDestroyed = false;

    DrawListener mListener;

    int w = 0;
    int h = 0;

    public DrawSurface(Context context, AttributeSet attrs) {
        super(context, attrs);

        getHolder().addCallback(this);
        initDrawingPaint();
    }

    public interface DrawListener {
        public void onLineDrawn();
    }

    public void setListener(DrawListener listener) {
        mListener = listener;
    }

    public void initDrawingPaint() {
        mPaint.setColor(Color.BLACK);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(STROKE_WIDTH);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setAntiAlias(true);

        mPaint.setDither(true);                    // set the dither to true
        mPaint.setStrokeJoin(Paint.Join.ROUND);    // set the join to round you want
        mPaint.setPathEffect(new CornerPathEffect(10) );   // set the path effect when they join.
        mPaint.setAntiAlias(true);

        mBmpPaint.setColor(Color.WHITE);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.v(TAG, "surface created");
        mSurfaceHolder = holder;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        if (mCurrentPath == null) {
            mCurrentPath = new Path();
        }

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mCurrentPath.moveTo(x, y);
                break;

            case MotionEvent.ACTION_MOVE:
                mCurrentPath.lineTo(x, y);
                updateCanvas();
                mHasDrawn = true;

                break;

            case MotionEvent.ACTION_OUTSIDE:
            case MotionEvent.ACTION_UP:
                if (!mHasDrawn) {
                    mCurrentPath.addCircle(x, y, STROKE_WIDTH / 3, Path.Direction.CCW);
                    updateCanvas();
                }

                mHasDrawn = false;

                mCanvas.save();
                mCanvas.drawPath(mCurrentPath, mPaint);
                mCanvas.restore();

                mCurrentPath = null;

                if (mListener != null) mListener.onLineDrawn();
                break;
        }

        return true;
    }

    public void clearCanvas() {
        Log.v(TAG, "clear canvas");
        mCanvas.drawRect(0, 0, w, h, mBmpPaint);
        updateCanvas();
    }

    private void updateCanvas() {
        if (mSurfaceHolder == null) return;

        Canvas c = mSurfaceHolder.lockCanvas();

        if (c != null && mBmpDraw != null) {

            c.drawBitmap(mBmpDraw, 0, 0, mBmpPaint);

            if (mCurrentPath != null && !mCurrentPath.isEmpty()) {
                c.drawPath(mCurrentPath, mPaint);
            }

            mSurfaceHolder.unlockCanvasAndPost(c);
        }
    }

    public void setDrawingColor(int color) {
        mPaint.setColor(color);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Log.v(TAG, "surface changed, width " + width + " height " + height);
        if (width == 0 || height == 0) return;

        if (w == width && h == height && !mHasBeenDestroyed) return;

        w = width;
        h = height;

        Log.v(TAG, "w: " + w + " h: " + h);

        if (!mHasBeenDestroyed || mBmpDraw == null) {
            mBmpDraw = Bitmap.createBitmap(w, h, Bitmap.Config.RGB_565);
            mCanvas = new Canvas(mBmpDraw);
            mCanvas.drawRect(0, 0, w, h, mBmpPaint);
        }

        updateCanvas();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.v(TAG, "surface destroyed");
        mHasBeenDestroyed = true;
    }
}
