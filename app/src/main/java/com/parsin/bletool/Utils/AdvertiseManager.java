package com.parsin.bletool.Utils;

import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.parsin.bletool.Model.Advertisement;
import com.parsin.bletool.Model.Section;
import com.parsin.bletool.R;
import com.parsin.bletool.Utils.Server.SendOptionEnum;
import com.parsin.bletool.internal.Constants;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by hadi on 10/18/17.
 */

public class AdvertiseManager {
    private static final int ADS_REGION_THRESH = 3;
    private String url = "";
    private static AdvertiseManager ourInstance = null;
    private final String TAG = AdvertiseManager.class.getSimpleName();
    private ArrayList<Advertisement> advertisements;
    private ArrayList<Advertisement> shownAds;


    public static AdvertiseManager getInstance() {
        if (ourInstance == null)
            ourInstance = new AdvertiseManager();
        return ourInstance;
    }

    private AdvertiseManager() {
        advertisements = new ArrayList<>();
        shownAds = new ArrayList<>();
    }

    public void init(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE);
        url = sharedPreferences.getString(Constants.ADVERTISE_URL_NAME, Constants.ADVERTISE_URL_DEFAULT);
        url = url + SendOptionEnum.Get_Advertisement.url();
        getAdvJSON(url);
    }

    public void checkAndShowAds(String currLocation, Context context) {
        new Thread(() -> {
            float x_y[] = getIntXY(currLocation);
            for (Advertisement ads : advertisements) {
                if (inSquare(x_y[0], x_y[1], ads) && !shownAds.contains(ads)) {
                    if (ads.getRegionCounter() >= ADS_REGION_THRESH){
                        sendNotification(ads.getId(), ads.getTitle(), ads.getText(), ads.getImg(), context);
                        shownAds.add(ads);
                    }else {
                        ads.setRegionCounter(ads.getRegionCounter() + 1);
                    }
                }
            }
        }).start();
    }

    private void getAdvJSON(String url) {
        OkHttpClient client = new OkHttpClient();
        // GET request
        Request request = new Request.Builder()
                .url(url)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                final String mMessage = request.toString();
                Log.e("resJSONError1", mMessage);
                Log.e("getAdvJSON", "3");
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                String resJSONreq = response.body().string();
                Log.d("advertise: ", resJSONreq);
                try {
                    JSONArray advertisementsArray = new JSONObject(resJSONreq).getJSONArray("advertisements");
                    for (int i = 0; i < advertisementsArray.length(); i++) {
                        JSONObject advertisementJson = advertisementsArray.getJSONObject(i);
//                        Advertisement advertisementObj;
                        String advrName = advertisementJson.getString("name");
                        String advrText = advertisementJson.getString("text");
                        String advrImg = advertisementJson.getString("image");
                        int advrId = advertisementJson.getInt("id");
                        JSONArray advrSections = advertisementJson.getJSONArray("sections");
                        Advertisement advertisement = new Advertisement(advrId, advrName, advrText, advrImg);
                        for (int j = 0; j < advrSections.length(); j++) {
                            JSONObject sectionJson = advrSections.getJSONObject(j);
                            String sectionName = sectionJson.getString("section_name");
                            float x1 = Float.parseFloat(sectionJson.getString("t_x"));
                            float y1 = Float.parseFloat(sectionJson.getString("t_y"));
                            float x2 = Float.parseFloat(sectionJson.getString("b_x"));
                            float y2 = Float.parseFloat(sectionJson.getString("b_y"));
                            Section section = new Section(sectionName, x1, y1, x2, y2);
                            advertisement.addSection(section);
                        }
                        advertisements.add(advertisement);
                    }
                } catch (Exception e) {
                    Log.e("JSONParsing", e.getMessage());
                }
            }
        });
    }

    private float[] getIntXY(String xyStr) {
        float x_y[] = new float[2];
        x_y[0] = Float.parseFloat(xyStr.substring(xyStr.indexOf(",") + 1, xyStr.length()));
        x_y[1] = Float.parseFloat(xyStr.substring(0, xyStr.indexOf(",")));
        Log.d("XXXX:", xyStr.substring(xyStr.indexOf(",") + 1, xyStr.length()));
        Log.d("YYYY:", xyStr.substring(0, xyStr.indexOf(",")));
        return x_y;
    }

    private boolean inSquare(float dotX, float dotY, Advertisement advertisement) {
        for (Section section : advertisement.getSections()) {
            if (section.getT_x() <= dotX && dotX <= section.getB_x() && section.getB_y() <= dotY && dotY <= section.getT_y())
                return true;
        }
        Log.e("inSquare", "dotX : " + String.valueOf(dotX) + " dotY :" + String.valueOf(dotY));
        Log.e("inSquare", "False");
        return false;
    }

    private void sendNotification(int advId, String title, String message, String imgUrl, Context context) {
        new Thread(() -> {
            String text = message;
            String defaultImg = "/media/default/advertise.jpg";
            Bitmap bitmap = null;
            try {
                URL url;
                if (imgUrl.contains("http"))
                    url = new URL(imgUrl);
                else {
                    SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE);
                    url = new URL(sharedPreferences.getString(Constants.ADVERTISE_URL_NAME, Constants.ADVERTISE_URL_DEFAULT) + imgUrl);
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

}

