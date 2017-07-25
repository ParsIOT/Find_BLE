package com.find.wifitool;

import java.util.concurrent.Semaphore;

/**
 * Created by root on 6/1/17.
 */

public class Test {
    public static void main(String[] args) {
        Semaphore semaphore = new Semaphore(1);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    semaphore.acquire();


                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    semaphore.acquire();


                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
