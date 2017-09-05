package com.parsin.bletool.Utils;

/**
 * Created by hadi on 9/3/17.
 */
import android.os.Handler;
import android.os.Looper;

public class Timer {
    private Handler handler;
    private boolean paused;

    private int interval;

    private Runnable task = new Runnable () {
        @Override
        public void run() {
            if (!paused) {
                Timer.this.runnable.run ();
                Timer.this.handler.postDelayed (this, interval);
            }
        }
    };

    private Runnable runnable;

    public int getInterval() {
        return interval;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }

    public void startTimer () {
        paused = false;
        handler.postDelayed (task, interval);
    }

    public void stopTimer () {
        paused = true;
    }

    public Timer (Runnable runnable, String threadName, int interval, boolean started) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                handler = new Handler ();
                Looper.loop();
            }
        }, threadName).start();
        this.runnable = runnable;
        this.interval = interval;
        if (started)
            startTimer ();
    }
}