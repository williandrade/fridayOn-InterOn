package me.williandrade.util;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.RectF;
import android.media.MediaPlayer;
import android.os.Build;
import android.util.AttributeSet;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;

import me.williandrade.R;

public class IntroVideoSurfaceView extends SurfaceView implements SurfaceHolder.Callback {

    private MediaPlayer mp;
    private boolean has_started = false;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public IntroVideoSurfaceView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        if (!isInEditMode()) {
            init();
        }

    }

    public IntroVideoSurfaceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (!isInEditMode()) {
            init();
        }

    }

    public IntroVideoSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (!isInEditMode()) {
            init();
        }
    }

    public IntroVideoSurfaceView(Context context) {
        super(context);
        if (!isInEditMode()) {
            init();
        }
    }

    private void init() {
        mp = new MediaPlayer();
        getHolder().addCallback(this);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        AssetFileDescriptor afd = getResources().openRawResourceFd(R.raw.back_ground);
        try {
            if (!has_started) {
                has_started = true;
                mp.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getDeclaredLength());

                mp.prepare();
                android.view.ViewGroup.LayoutParams lp = getLayoutParams();
                lp.height = getHeight();
                lp.width = getWidth();

                mp.setVideoScalingMode(2);

                setLayoutParams(lp);
                mp.setDisplay(getHolder());
                mp.setLooping(true);
                mp.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        mp.stop();
        mp.reset();
    }
}

