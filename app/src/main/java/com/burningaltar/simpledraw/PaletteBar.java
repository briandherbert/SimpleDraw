package com.burningaltar.simpledraw;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.GradientDrawable.Orientation;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

public class PaletteBar extends RelativeLayout {
    public static final String TAG = "PaletteBar";
    public static final int DEFAULT_COLOR_MARGIN_DP = 4;
    static final int GRAY = Color.rgb(128, 128, 128);
    static final int BROWN = Color.rgb(128, 64, 0);
    static final int RED = Color.rgb(255, 0, 0);
    static final int YELLOW = Color.rgb(255, 255, 0);
    static final int GREEN = Color.rgb(0, 255, 0);
    static final int TEAL = Color.rgb(128, 255, 255);
    static final int BLUE = Color.rgb(0, 0, 255);
    static final int VIOLET = Color.rgb(255, 0, 255);
    static final GradientDrawable.Orientation LR_ORIENTATION;
    static final GradientDrawable[] COLOR_GRADIENTS;
    static final GradientDrawable[] TINT_GRADIENTS;
    private int mPaletteWidth;
    private int mPaletteHeight;
    private int mColorMargin;
    private boolean mShowColorInMargin;
    private int mCurrentColor;
    private PaletteBar.PaletteBarListener mListener;
    private OnTouchListener mTouchListener;

    static {
        LR_ORIENTATION = GradientDrawable.Orientation.LEFT_RIGHT;
        COLOR_GRADIENTS = new GradientDrawable[]{new GradientDrawable(LR_ORIENTATION, new int[]{GRAY, BROWN}), new GradientDrawable(LR_ORIENTATION, new int[]{BROWN, RED}), new GradientDrawable(LR_ORIENTATION, new int[]{RED, YELLOW}), new GradientDrawable(LR_ORIENTATION, new int[]{YELLOW, GREEN}), new GradientDrawable(LR_ORIENTATION, new int[]{GREEN, TEAL}), new GradientDrawable(LR_ORIENTATION, new int[]{TEAL, BLUE}), new GradientDrawable(LR_ORIENTATION, new int[]{BLUE, VIOLET})};
        TINT_GRADIENTS = new GradientDrawable[]{new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, new int[]{-1, 0}), new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP, new int[]{-16777216, 0})};
    }

    public PaletteBar(Context context) {
        this(context, (AttributeSet)null);
    }

    public PaletteBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PaletteBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mPaletteWidth = 0;
        this.mPaletteHeight = 0;
        this.mColorMargin = -1;
        this.mShowColorInMargin = true;
        this.mCurrentColor = -16777216;
        this.mTouchListener = new OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                float x = event.getX();
                float y = event.getY();
                if(x < (float)PaletteBar.this.mColorMargin) {
                    x = (float)PaletteBar.this.mColorMargin;
                } else if(x >= (float)(PaletteBar.this.mPaletteWidth + PaletteBar.this.mColorMargin)) {
                    x = (float)(PaletteBar.this.mPaletteWidth + PaletteBar.this.mColorMargin - 1);
                }

                if(y < (float)PaletteBar.this.mColorMargin) {
                    y = (float)PaletteBar.this.mColorMargin;
                } else if(y >= (float)(PaletteBar.this.mPaletteHeight + PaletteBar.this.mColorMargin)) {
                    y = (float)(PaletteBar.this.mPaletteHeight + PaletteBar.this.mColorMargin - 1);
                }

                PaletteBar.this.mCurrentColor = PaletteBar.this.getColorFromCoords(x, y);
                if(action == 1 && PaletteBar.this.mListener != null) {
                    PaletteBar.this.mListener.onColorSelected(PaletteBar.this.mCurrentColor);
                } else if(PaletteBar.this.mShowColorInMargin && (action == 0 || action == 2)) {
                    PaletteBar.this.setBackgroundColor(PaletteBar.this.mCurrentColor);
                }

                return true;
            }
        };
        this.init(context);
    }

    public void setColorMarginPx(int colorMarginPx) {
        this.mColorMargin = colorMarginPx;
        if(this.getContext() != null) {
            this.init(this.getContext());
            this.invalidate();
        }

    }

    public void init(Context context) {
        this.removeAllViews();

        LinearLayout linHues = new LinearLayout(context);
        LinearLayout linTints = new LinearLayout(context);
        LayoutParams lpHuesAndTint = new LayoutParams(-1, -1);
        if(this.mColorMargin < 0) {
            this.mColorMargin = (int)((double)(context.getResources().getDisplayMetrics().density * 4.0F) + 0.5D);
        }

        lpHuesAndTint.setMargins(this.mColorMargin, this.mColorMargin, this.mColorMargin, this.mColorMargin);
        linHues.setLayoutParams(lpHuesAndTint);
        linTints.setLayoutParams(lpHuesAndTint);
        android.widget.LinearLayout.LayoutParams lpGradients = new android.widget.LinearLayout.LayoutParams(0, -1);
        lpGradients.weight = 1.0F;

        for(int lpTints = 0; lpTints < COLOR_GRADIENTS.length; ++lpTints) {
            View i = new View(context);
            i.setLayoutParams(lpGradients);
            i.setBackgroundDrawable(COLOR_GRADIENTS[lpTints]);
            linHues.addView(i);
        }

        this.addView(linHues);
        linTints.setOrientation(LinearLayout.VERTICAL);
        android.widget.LinearLayout.LayoutParams var9 = new android.widget.LinearLayout.LayoutParams(-1, 0);
        var9.weight = 1.0F;

        for(int var10 = 0; var10 < TINT_GRADIENTS.length; ++var10) {
            View view = new View(context);
            view.setLayoutParams(var9);
            view.setBackgroundDrawable(TINT_GRADIENTS[var10]);
            linTints.addView(view);
        }

        this.addView(linTints);
        this.setBackgroundColor(this.mCurrentColor);
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        this.mPaletteWidth = this.getWidth() - this.mColorMargin * 2;
        this.mPaletteHeight = this.getHeight() - this.mColorMargin * 2;
    }

    public int getColorFromCoords(float x, float y) {
        x -= (float)this.mColorMargin;
        y -= (float)this.mColorMargin;
        float gradientSize = (float)this.mPaletteWidth / (float)COLOR_GRADIENTS.length;
        float gradientIdx = (float)((int)(x / gradientSize));
        float mantissa = x / gradientSize % 1.0F;
        float r = 0.0F;
        float g = 0.0F;
        float b = 0.0F;
        if(gradientIdx == 0.0F) {
            r = 127.0F;
            g = 127.0F - mantissa * 63.0F;
            b = 127.0F - mantissa * 127.0F;
        } else if(gradientIdx == 1.0F) {
            r = 127.0F + mantissa * 127.0F;
            g = 63.0F - mantissa * 63.0F;
            b = 0.0F;
        } else if(gradientIdx == 2.0F) {
            r = 255.0F;
            g = mantissa * 255.0F;
            b = 0.0F;
        } else if(gradientIdx == 3.0F) {
            r = 255.0F - mantissa * 255.0F;
            g = 255.0F;
            b = 0.0F;
        } else if(gradientIdx == 4.0F) {
            r = 0.0F;
            g = 255.0F;
            b = mantissa * 255.0F;
        } else if(gradientIdx == 5.0F) {
            r = 0.0F;
            g = 255.0F - mantissa * 255.0F;
            b = 255.0F;
        } else if(gradientIdx >= 6.0F) {
            r = mantissa * 255.0F;
            g = 0.0F;
            b = 255.0F;
        }

        gradientSize = (float)(this.mPaletteHeight / 2);
        gradientIdx = (float)((int)(y / gradientSize));
        mantissa = y / gradientSize % 1.0F;
        float blackness;
        if(gradientIdx == 0.0F) {
            blackness = 255.0F - mantissa * 255.0F;
            r = Math.min(255.0F, r + blackness);
            g = Math.min(255.0F, g + blackness);
            b = Math.min(255.0F, b + blackness);
        } else {
            blackness = mantissa * 255.0F;
            r = Math.max(r - blackness, 0.0F);
            g = Math.max(g - blackness, 0.0F);
            b = Math.max(b - blackness, 0.0F);
        }

        return Color.argb(255, (int)r, (int)g, (int)b);
    }

    public void setListener(PaletteBar.PaletteBarListener listener) {
        this.mListener = listener;
        if(listener == null) {
            this.setOnTouchListener((OnTouchListener)null);
        } else {
            this.setOnTouchListener(this.mTouchListener);
            this.mListener.onColorSelected(this.mCurrentColor);
        }

    }

    public interface PaletteBarListener {
        void onColorSelected(int var1);
    }
}
