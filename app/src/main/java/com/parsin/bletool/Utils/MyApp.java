package com.parsin.bletool.Utils;

/**
 * Created by root on 7/19/17.
 */

import android.app.Application;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import com.parsin.bletool.Model.DaoMaster;
import com.parsin.bletool.Model.DaoSession;
import com.splunk.mint.Mint;

import java.io.File;
import java.io.IOException;

import static android.content.ContentValues.TAG;

public class MyApp extends Application {


    private DaoSession mDaoSession;
    private Context gContext;


    @Override
    public void onCreate() {
        super.onCreate();
        gContext = getApplicationContext();
        Mint.initAndStartSession(this, "577120a0");
        FontsOverride.setDefaultFont(this, "MONOSPACE", "Fonts/IRRoya.ttf");
        FontsOverride.setDefaultFont(this, "DEFAULT", "Fonts/IRRoya.ttf");
        //FontsOverride.setDefaultFont(this, "NORMAL", "Fonts/IRRoya.ttf");
        FontsOverride.setDefaultFont(this, "SERIF", "Fonts/IRRoya.ttf");
        FontsOverride.setDefaultFont(this, "SANS_SERIF", "Fonts/IRRoya.ttf");
        //FontsOverride.setDefaultFont(this, "SANS", "Fonts/IRRoya.ttf");
        Log.e("APP", "SALAM");

        mDaoSession = new DaoMaster(
                new DaoMaster.DevOpenHelper(this, "green.db").getWritableDb()).newSession();


    }

    public DaoSession getDaoSession() {
        return mDaoSession;
    }

    public Context getAppContext(){
        return this.gContext;
    }


    public static void logging() {

        if (isExternalStorageWritable()) {

            File appDirectory = new File(Uri.parse(Environment.getExternalStorageDirectory().toURI().toString()) + "/MyPersonalAppFolder/");
            File logDirectory = new File(appDirectory + "/log//");
            File logFile = new File(logDirectory, "Parsin" + System.currentTimeMillis() + ".txt");

            // create app folder
            if (!appDirectory.exists()) {
                appDirectory.mkdir();
                Log.e(TAG, "onCreate: create app folder");

            }

            // create log folder
            if (!logDirectory.exists()) {
                logDirectory.mkdir();
                Log.e(TAG, "onCreate: create log folder");
            }

            // clear the previous logcat and then write the new one to the file
            try {
                Process process = Runtime.getRuntime().exec("logcat -c");
                process = Runtime.getRuntime().exec("logcat -f " + logFile + " *:S MyActivity:D MyActivity2:D");
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else if (isExternalStorageReadable()) {
            // only readable
            Log.e(TAG, "onCreate: only readable");

        } else {
            // not accessible
            Log.e(TAG, "onCreate: not accessible");
        }


    }


    /* Checks if external storage is available for read and write */
    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    /* Checks if external storage is available to at least read */
    public static boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }

    @Override
    public void onTerminate() {
        Log.e(TAG, "onTerminate: ");
        super.onTerminate();
    }
}
