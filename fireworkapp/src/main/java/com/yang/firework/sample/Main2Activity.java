package com.yang.firework.sample;

import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;

public class Main2Activity extends AppCompatActivity implements GestureDetector.OnGestureListener {

    private GLSurfaceView view;
    private MyGLRender render;
    private GestureDetector mGestureDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        render = new MyGLRender();
        view = new GLSurfaceView(this);
        view.setRenderer(render);
        setContentView(view);

        // create the gesture detector to detect finger movement on screen
        mGestureDetector = new GestureDetector(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // ATTENTION!!! Bind the gesture detector!
        return mGestureDetector.onTouchEvent(event);
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {
    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
                            float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {
    }

    @Override
    // detect the movement of finger and inject force to SPH System
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                           float velocityY) {

        float width = (float) render.GetWindowWidth();
        float height = (float) render.GetWindowHeight();
        float px = e1.getX();
        float py = e1.getY();

        // x position of finger on screen
        float perX = px / width;
        // y position of finger on screen
        float perY = (height - py) / height;
        // force vector x
        float forceX = (e2.getX() - px) / width;
        // force vector y
        float forceY = ((height - e2.getY()) - (height - py)) / height;

        // inject external force from finger to SPH System
        render.GetSPH().Add_external_event(perX, perY, forceX, forceY);

        return false;
    }
}