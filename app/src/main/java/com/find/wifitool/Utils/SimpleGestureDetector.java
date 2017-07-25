package com.find.wifitool.Utils;


import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;

import static android.content.ContentValues.TAG;

public class SimpleGestureDetector extends GestureDetector.SimpleOnGestureListener {
    //handle 'swipe left' action only
    private GestureCallBack gestureCallBack;

    public SimpleGestureDetector(GestureCallBack gc){
        gestureCallBack = gc;
    }
    @Override
    public boolean onFling(MotionEvent event1, MotionEvent event2,
                           float velocityX, float velocityY) {
         /*
         Toast.makeText(getBaseContext(),
          event1.toString() + "\n\n" +event2.toString(),
          Toast.LENGTH_SHORT).show();
         */

        if(event1.getY() < event2.getY()){
            Log.w(TAG, "onFling: Sliding");
            gestureCallBack.GesCallBack();
        }

        return true;
    }

    public interface GestureCallBack{
        public void GesCallBack();
    }
}