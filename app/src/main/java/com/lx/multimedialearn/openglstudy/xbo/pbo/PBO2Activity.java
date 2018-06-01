package com.lx.multimedialearn.openglstudy.xbo.pbo;

import android.graphics.Bitmap;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.lx.multimedialearn.R;
import com.lx.multimedialearn.utils.GlUtil;
import com.lx.multimedialearn.utils.ToastUtils;

public class PBO2Activity extends AppCompatActivity {

    private GLSurfaceView mGLSurfaceView;
    private ImageView mImgView;
    private DoublePBORender mRender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pbo2);
        mGLSurfaceView = (GLSurfaceView) findViewById(R.id.glsurface_pbo_player);
        mImgView = (ImageView) findViewById(R.id.img_pbo_show);
        if (!GlUtil.checkGLEsVersion_2(this)) {
            ToastUtils.show(this, "不支持OpenGL 2.0");
            finish();
            return;
        }
        mGLSurfaceView.setEGLContextClientVersion(2);
        mRender = new DoublePBORender(this);
        mGLSurfaceView.setRenderer(mRender);
        mGLSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        mRender.setListener(new PBORender.onBitmapUpdateListener() {
            @Override
            public void update(final Bitmap bitmap) {
                runOnUiThread(new Runnable() { //切回主线程
                    @Override
                    public void run() {
                        if (bitmap != null) {
                            mImgView.setImageBitmap(bitmap);
                        }
                    }
                });
            }
        });
        mGLSurfaceView.requestRender();
    }
}

