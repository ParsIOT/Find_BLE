package com.find.wifitool.Utils;

import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.find.wifitool.Model.Advertisement;
import com.find.wifitool.Model.Section;
import com.find.wifitool.R;
import com.find.wifitool.internal.Constants;

import java.io.IOException;
import java.net.URL;
import java.util.concurrent.Semaphore;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by root on 4/19/17.
 */

public class Utils {

    public static Semaphore semaphore = new Semaphore(1);


    public static float[] getIntXY(String xyStr) {
        float x_y[] = new float[2];
        String x, y;
        x_y[0] = Float.parseFloat(xyStr.substring(xyStr.indexOf(",") + 1, xyStr.length()));
        x_y[1] = Float.parseFloat(xyStr.substring(0, xyStr.indexOf(",")));
        Log.d("XXXX:", xyStr.substring(xyStr.indexOf(",") + 1, xyStr.length()));
        Log.d("YYYY:", xyStr.substring(0, xyStr.indexOf(",")));
        return x_y;
    }

    public static boolean inSquare(float dotX, float dotY, Advertisement advertisement) {
        for (Section section : advertisement.getSections()) {
            if (section.getT_x() <= dotX && dotX <= section.getB_x() && section.getB_y() <= dotY && dotY <= section.getT_y())
                return true;
        }
        Log.e("inSquare", "dotX : " + String.valueOf(dotX) + " dotY :" + String.valueOf(dotY));
        Log.e("inSquare", "False");
        return false;
    }

    public static void notifyThis(String title, String message, Context context) {
        new Thread(() -> {
            String imageUrl = "";
            String text = "";
            String pattern = "~~(.+)~~.*url:(.+)";
            // Create a Pattern object
            Pattern r = Pattern.compile(pattern);
            // Now create matcher object.
            Matcher m = r.matcher(message);
            if (m.find()) {
                text = m.group(1);
                imageUrl = m.group(2);
            } else {
                Log.e("REGEX", "NO MATCH");
                Log.e("REGEX", message);
            }
            Bitmap bitmap = null;
            try {
                URL url;
                if (imageUrl.contains("http"))
                    url = new URL(imageUrl);
                else {
                    SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.PREFS_NAME,0);
                    StaticObjects.ParsinServerIp = sharedPreferences.getString(StaticObjects.PARSIN_SERVER_NAME, StaticObjects.ParsinServerIp);
                    url = new URL(StaticObjects.ParsinServerIp + imageUrl);
                }
                bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
            NotificationCompat.Builder b = new NotificationCompat.Builder(context);
            b.setAutoCancel(true)
                    .setDefaults(NotificationCompat.DEFAULT_ALL)
                    .setWhen(System.currentTimeMillis())
                    .setSmallIcon(R.drawable.ic_action_map)
                    .setTicker("{your tiny message}")
                    .setPriority(android.support.v7.app.NotificationCompat.PRIORITY_HIGH)
                    .setContentTitle(title)
                    .setContentInfo("");

            if (bitmap != null && !imageUrl.equals("") && !text.equals("")) {
                NotificationCompat.BigPictureStyle s = new NotificationCompat.BigPictureStyle().bigPicture(bitmap);
                s.setSummaryText(text);
                b.setContentText(text);
                b.setStyle(s);
            }else
                b.setContentText(message);
            NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            nm.notify(0, b.build());
        }).start();
    }
}
