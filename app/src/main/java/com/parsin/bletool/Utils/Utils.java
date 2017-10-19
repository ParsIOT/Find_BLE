package com.parsin.bletool.Utils;

import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.parsin.bletool.Model.Advertisement;
import com.parsin.bletool.Model.Section;
import com.parsin.bletool.R;
import com.parsin.bletool.internal.Constants;

import java.io.IOException;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Semaphore;

/**
 * Created by root on 4/19/17.
 */

public class Utils {
    private static final String TAG = "Utils";
    private static int news_item_dp = 110;

    public static HashMap<String, ArrayList<Integer>> hashMap = new HashMap<>();

    // Mohsen Tabasi
    public static String toPersianNum(String s, boolean horuf) {

        if (s.length() == 1 && horuf) {
            char ss = s.charAt(0);
            if (ss < '0' || ss > '9')
                return String.valueOf(s.charAt(0));
            else {
                String[] persianNumbers = new String[]{"صفر", "یک", "دو", "سه", "چهار", "پنج", "شش", "هفت", "هشت", "نه"};
                return persianNumbers[ss - '0'];
            }

        } else {

            String[] persianDigits = {"۰", "۱", "۲", "٣", "۴", "۵", "۶", "۷", "۸", "٩"};
            StringBuilder sb = new StringBuilder();

            for (int i = 0; i < s.length(); i++) {
                char ss = s.charAt(i);
                if (ss < '0' || ss > '9')
                    sb.append(s.charAt(i));
                else
                    sb.append(persianDigits[ss - '0']);
            }
            return sb.toString();
        }
    }


    public static void notifyThis(int advId, String title, String message, String imgUrl, Context context) {
        new Thread(() -> {
            String text = message;
            String defaultImg = "/media/default/advertise.jpg";
            Bitmap bitmap = null;
            try {
                URL url;
                if (imgUrl.contains("http"))
                    url = new URL(imgUrl);
                else {
                    SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.PREFS_NAME, 0);
                    StaticObjects.ParsinServerIp = sharedPreferences.getString(StaticObjects.PARSIN_SERVER_NAME, StaticObjects.ParsinServerIp);
                    url = new URL(StaticObjects.ParsinServerIp + imgUrl);
                }
                bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                Log.d("Utils", "Img url is :" + url);
            } catch (IOException e) {
                e.printStackTrace();
            }
            NotificationCompat.Builder b = new NotificationCompat.Builder(context);
            b.setAutoCancel(true)
                    .setDefaults(NotificationCompat.DEFAULT_ALL)
                    .setWhen(System.currentTimeMillis())
                    .setSmallIcon(R.drawable.ic_action_info)
                    .setTicker("{your tiny message}")
                    .setPriority(android.support.v7.app.NotificationCompat.PRIORITY_HIGH)
                    .setContentTitle(title)
                    .setContentInfo("");

            if (bitmap != null && !imgUrl.equals(defaultImg) && !text.equals("")) {
                NotificationCompat.BigPictureStyle s = new NotificationCompat.BigPictureStyle().bigPicture(bitmap);
                Log.d(TAG, "testNotif");
                s.setSummaryText(text);
                b.setContentText(text);
                b.setStyle(s);
            } else
                b.setContentText(message);
            NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            nm.notify(advId, b.build());
        }).start();
    }

    public static void checkInternet(AppCompatActivity activity) {

        new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    final InetAddress address = InetAddress.getByName(Constants.DEFAULT_SERVER);
//                    !address.toString().equals("");

                } catch (UnknownHostException e) {
                    e.printStackTrace();
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                            builder.setTitle("مشکل در اتصال به اینترنت");
                            builder.setMessage("برای بروزرسانی اطلاعات به اینترنت متصل شوید.");
                            builder.setPositiveButton("خب", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                            AlertDialog dialog = builder.show();
                            TextView textView = (TextView) dialog.findViewById(android.R.id.message);
                            if (textView != null) {
                                textView.setTypeface(Typeface.DEFAULT);
                            }
                        }
                    });
                }
            }
        }).start();


    }

    public static void expand(final View v) {
        v.measure(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        final int targtetHeight = v.getMeasuredHeight();
        int orginalSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, news_item_dp, v.getContext().getResources().getDisplayMetrics());

        int offsetHeight = targtetHeight - orginalSize;


        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {

                int newHeight;

                newHeight = (int) (offsetHeight * interpolatedTime);

//                newHeight = (int) (offsetHeight * (1 - interpolatedTime));

                //The new view height is based on start height plus the height increment
                v.getLayoutParams().height = newHeight + orginalSize;
                v.requestLayout();

              /*  v.getLayoutParams().height = interpolatedTime == 1
                        ? LinearLayout.LayoutParams.WRAP_CONTENT
                        : (int) (targtetHeight * interpolatedTime);

                v.requestLayout();*/
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        a.setDuration((int) (offsetHeight / v.getContext().getResources().getDisplayMetrics().density));
        v.startAnimation(a);
    }

    public static void collapse(final View v, int listSize) {
        final int initialHeight = v.getMeasuredHeight();

        int offsetHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, news_item_dp, v.getContext().getResources().getDisplayMetrics());
        int targetHeight = initialHeight - offsetHeight;

        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {

                int newHeight;

                newHeight = (int) (targetHeight * (1 - interpolatedTime));


                v.getLayoutParams().height = newHeight + offsetHeight;
                v.requestLayout();

                /*if (interpolatedTime == 1) {
                    v.getLayoutParams().height = initialHeight / listSize;
                    v.requestLayout();
                } else {
                    v.getLayoutParams().height = initialHeight - (int) (initialHeight * interpolatedTime);
                    v.requestLayout();
                }*/
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        a.setDuration((int) (targetHeight / v.getContext().getResources().getDisplayMetrics().density));
        v.startAnimation(a);
    }

    public static int getMedian(ArrayList<Integer> list) {

        if (list.size() == 1)
            return list.get(0);

        int res = 0;
        Collections.sort(list);
        int middle = list.size() / 2;
        if (middle % 2 == 0)
            res = (int) (list.get(middle) + list.get(middle + 1)) / 2;
        else
            res = list.get(middle);
        return res;
    }

    public static int getSum(int[] list) {
        int sum = 0;
        for (int i : list) sum = sum + i;
        return sum;
    }


}
