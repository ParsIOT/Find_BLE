package com.parsin.bletool.View;

import android.net.Uri;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import com.parsin.bletool.Controller.OnFragmentInteractionListener;
import com.parsin.bletool.R;
import com.parsin.bletool.View.Fragment.TrackFragment;
import com.parsin.bletool.Utils.SimpleGestureDetector;


public class MapActivity extends AppCompatActivity implements SimpleGestureDetector.GestureCallBack, OnFragmentInteractionListener {
    private static final String TAG = "MapActivity";
    private GestureDetectorCompat detector;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_map);
        overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);

        detector = new GestureDetectorCompat(this,new SimpleGestureDetector(this));

        TrackFragment trackFragment = new TrackFragment();


        // In case this activity was started with special instructions from an
        // Intent, pass the Intent's extras to the fragment as arguments
        trackFragment.setArguments(getIntent().getExtras());

        // Add the fragment to the 'fragment_container' FrameLayout
        getSupportFragmentManager().beginTransaction()
                .add(R.id.track_fragment_container, trackFragment).commit();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_out_bottom, R.anim.slide_in_bottom);
        finish();
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();

        final View view = getWindow().getDecorView();
        final WindowManager.LayoutParams lp = (WindowManager.LayoutParams) view.getLayoutParams();
        lp.gravity = Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM;

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);


        lp.width = displayMetrics.widthPixels;
        lp.height = (displayMetrics.heightPixels / 20) * 17;
        getWindowManager().updateViewLayout(view, lp);

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        this.detector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    @Override
    public void GesCallBack() {
        finish();
        overridePendingTransition(R.anim.slide_out_bottom, R.anim.slide_in_bottom);

    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}


