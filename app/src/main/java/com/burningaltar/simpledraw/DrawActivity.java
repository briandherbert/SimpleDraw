package com.burningaltar.simpledraw;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

public class DrawActivity extends AppCompatActivity implements PaletteBar.PaletteBarListener, DrawSurface.DrawListener {
    PaletteBar mPalleteBar;
    DrawSurface mDrawSurface;
    TextView mLblErase;

    boolean mHasSeenEraseMessage = false;

    static final int ERASE_TAPS_NEEDED = 3;
    int mEraseCount = ERASE_TAPS_NEEDED;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_draw);

        mPalleteBar = (PaletteBar) findViewById(R.id.palettebar);
        mPalleteBar.setColorMarginPx(getResources().getDimensionPixelOffset(R.dimen.palette_color_border));

        mPalleteBar.setListener(this);

        mDrawSurface = (DrawSurface) findViewById(R.id.drawsurface);
        mDrawSurface.setListener(this);

        mLblErase = (TextView) findViewById(R.id.lbl_erase);
        mLblErase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEraseCount--;

                if (!mHasSeenEraseMessage) {
                    mHasSeenEraseMessage = true;
                    String message = getResources().getString(R.string.erase_message, ERASE_TAPS_NEEDED - 1);
                    Toast.makeText(DrawActivity.this, message, Toast.LENGTH_LONG).show();
                }

                if (mEraseCount == 0) {
                    mDrawSurface.clearCanvas();
                    mEraseCount = ERASE_TAPS_NEEDED;
                }

                mLblErase.setText(Integer.toString(mEraseCount));
            }
        });
    }

    @Override
    public void onColorSelected(int color) {
        if (mDrawSurface == null) return;

        mDrawSurface.setDrawingColor(color);

        mEraseCount = ERASE_TAPS_NEEDED;
        mLblErase.setText(Integer.toString(mEraseCount));
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {

            View decorView = getWindow().getDecorView();

            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }

    @Override
    public void onLineDrawn() {
        mEraseCount = ERASE_TAPS_NEEDED;
        mLblErase.setText(Integer.toString(mEraseCount));
    }
}
