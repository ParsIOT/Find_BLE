package com.parsin.bletool.Utils.Server;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.parsin.bletool.Controller.Observable;
import com.parsin.bletool.Controller.Observer;
import com.parsin.bletool.Model.Advertisement;
import com.parsin.bletool.Model.Section;
import com.parsin.bletool.Utils.StaticObjects;
import com.parsin.bletool.internal.Constants;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


/**
 * Created by root on 4/12/17.
 */

public class ParsinServer implements Observable {

    private final OkHttpClient client = new OkHttpClient();
    private List<Observer> observers = new ArrayList<>();
    private String result;
    private boolean changed;
    private final Object MUTEX = new Object();
    private SharedPreferences sharedPreferences;

    public boolean isServerAvailable() {
        boolean availability = false;
        OkHttpClient client = new OkHttpClient();
        try {
            Request request = new Request.Builder()
                    .url(StaticObjects.ParsinServerIp + SendOptionEnum.Check_server.url())
                    .build();

            Response response = client.newCall(request).execute();

            if (response.body().string().contains("ok"))
                availability = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return availability;
    }

    public void getProducts() {
        Request request = new Request.Builder()
                .url(StaticObjects.ParsinServerIp + SendOptionEnum.Get_Product_List.url())
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                postResult(response.body().string());
            }
        });
    }


    public void getBooths() {
//        String credential = Credentials.basic(PublicClass.username, PublicClass.password);
        Request request = new Request.Builder()
                .url(StaticObjects.ParsinServerIp + SendOptionEnum.Get_Booths_List.url())
//                .header("Authorization", credential)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                postResult(response.body().string());
                //Log.w(ParsinServer.class.getSimpleName(), response.body().string());
            }
        });
    }


    public void getProductByBoothId(int booth_id, Context contex) {
        sharedPreferences = contex.getSharedPreferences(Constants.PREFS_NAME, 0);
        Request request = new Request.Builder()
                .url(sharedPreferences.getString(StaticObjects.PARSIN_SERVER_NAME, StaticObjects.ParsinServerIp) + SendOptionEnum.Get_Booth_Item.url() + String.valueOf(booth_id) + "/")
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                postResult(response.body().string());
                //Log.w(ParsinServer.class.getSimpleName(), response.body().string());
            }
        });
    }


    @Override
    public void register(Observer obj) {
        if (obj == null) throw new NullPointerException("Null Observer");
        if (!observers.contains(obj)) observers.add(obj);
    }

    @Override
    public void unregister(Observer obj) {
        observers.remove(obj);
    }

    @Override
    public void notifyObservers() {
        List<Observer> observersLocal = null;
        //synchronization is used to make sure any observer registered after message is received is not notified
        synchronized (MUTEX) {
            if (!changed)
                return;
            observersLocal = new ArrayList<>(this.observers);
            this.changed = false;
        }
        for (Observer obj : observersLocal) {
            obj.update();
        }

    }

    @Override
    public Object getUpdate(Observer obj) {
        return this.result;
    }


    public void postResult(String msg) {
        //Log.e(MyAsyncTask.class.getSimpleName(), "Message Posted to Topic:" + msg);
        this.result = msg;
        this.changed = true;
        notifyObservers();
    }


}
