package com.find.wifitool.View;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;

import com.find.wifitool.Controller.Adapters.BoothAdapter;
import com.find.wifitool.Controller.Adapters.ItemClickListener;
import com.find.wifitool.Controller.Observer;
import com.find.wifitool.Model.Booth;
import com.find.wifitool.R;
import com.find.wifitool.Utils.Server.ParsinServer;
import com.find.wifitool.Utils.StaticObjects;
import com.find.wifitool.View.BoothListActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;


public class BoothListActivity extends AppCompatActivity implements Observer {
    private static final String TAG = "BoothListActivity";
    RecyclerView mRecyclerView;
    BoothAdapter mAdapter;
    LinearLayoutManager mLayoutManager;
    List<Booth> arrayList;
    Handler handler;
    ParsinServer parsin;

    //TODO finish item and add some record

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booth_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());
        arrayList = new ArrayList<>();
        mRecyclerView = (RecyclerView) findViewById(R.id.booth_recycler);
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);
        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        // specify an sliderPagerAdapter (see also next example)
        mAdapter = new BoothAdapter(arrayList, BoothListActivity.this);
        mAdapter.setOnItemClickListener((view, position) -> {
            //Toast.makeText(BoothListActivity.this, "item" + position + " clicked", Toast.LENGTH_SHORT).show();
            StaticObjects.booth = arrayList.get(position);
            startActivity(new Intent(BoothListActivity.this, BoothItemActivity.class));
        });
        mRecyclerView.setAdapter(mAdapter);
        parsin = new ParsinServer();
        parsin.register(this);

        addItemToList();

        parsin.getBooths();

/*        handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                getListFromServer();
            }
        }, 1000);*/


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.booth_list, menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.list_search).getActionView();

        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                mAdapter.getFilter().filter(newText);
                return false;
            }
        });
        return true;
    }


    private void addItemToList() {
        arrayList.add(new Booth("پارسیوت", "", "پارس اینترنت اشیا ارایه دهنده راهکار های مبتی بر مکان یابی درون ساختمان", "http://parsiotco.ir/wp-content/uploads/2017/05/PARSiotFinalFinalFinal-2.png"));
        arrayList.add(new Booth("آرمان رایان شریف", "", null, "http://armansoft.ir/wp-content/uploads/2015/08/logoar.png"));
        arrayList.add(new Booth("ایرانسل", "", null, StaticObjects.ParsinServerIp + "/static/img/irancell.jpg"));
        arrayList.add(new Booth("همراه اول", "", null, StaticObjects.ParsinServerIp + "/static/img/hamrah.png"));
//        arrayList.add(new Booth("پارسیوت", "کمیل شاه حسینی", null, "http://parsiotco.ir/wp-content/uploads/2016/10/logo.jpg"));
//        arrayList.add(new Booth("انتشارات گاج", "آقای جوکار", null, "http://gaj.ir/img/common/logo-h.png"));
//        arrayList.add(new Booth("پارسیوت", "کمیل شاه حسینی", null, "http://parsiotco.ir/wp-content/uploads/2016/10/logo.jpg"));
//        arrayList.add(new Booth("انتشارات گاج", "آقای جوکار", null, "http://gaj.ir/img/common/logo-h.png"));
//        arrayList.add(new Booth("پارسیوت", "کمیل شاه حسینی", null, "http://parsiotco.ir/wp-content/uploads/2016/10/logo.jpg"));
//        arrayList.add(new Booth("انتشارات گاج", "آقای جوکار", null, "http://gaj.ir/img/common/logo-h.png"));
//        arrayList.add(new Booth("پارسیوت", "کمیل شاه حسینی", null, "http://parsiotco.ir/wp-content/uploads/2016/10/logo.jpg"));
//        arrayList.add(new Booth("انتشارات گاج", "آقای جوکار", null, "http://gaj.ir/img/common/logo-h.png"));
//        arrayList.add(new Booth("پارسیوت", "کمیل شاه حسینی", null, "http://parsiotco.ir/wp-content/uploads/2016/10/logo.jpg"));
//        arrayList.add(new Booth("انتشارات گاج", "آقای جوکار", null, "http://gaj.ir/img/common/logo-h.png"));
//        arrayList.add(new Booth("پارسیوت", "کمیل شاه حسینی", null, "http://parsiotco.ir/wp-content/uploads/2016/10/logo.jpg"));
//        arrayList.add(new Booth("انتشارات گاج", "آقای جوکار", null, "http://gaj.ir/img/common/logo-h.png"));
//        arrayList.add(new Booth("پارسیوت", "کمیل شاه حسینی", null, "http://parsiotco.ir/wp-content/uploads/2016/10/logo.jpg"));
//        arrayList.add(new Booth("انتشارات گاج", "آقای جوکار", null, "http://gaj.ir/img/common/logo-h.png"));
//        arrayList.add(new Booth("پارسیوت", "کمیل شاه حسینی", null, "http://parsiotco.ir/wp-content/uploads/2016/10/logo.jpg"));
//        arrayList.add(new Booth("انتشارات گاج", "آقای جوکار", null, "http://gaj.ir/img/common/logo-h.png"));
//        arrayList.add(new Booth("پارسیوت", "کمیل شاه حسینی", null, "http://parsiotco.ir/wp-content/uploads/2016/10/logo.jpg"));
//        arrayList.add(new Booth("انتشارات گاج", "آقای جوکار", null, "http://gaj.ir/img/common/logo-h.png"));
//        arrayList.add(new Booth("پارسیوت", "کمیل شاه حسینی", null, "http://parsiotco.ir/wp-content/uploads/2016/10/logo.jpg"));
//        arrayList.add(new Booth("انتشارات گاج", "آقای جوکار", null, "http://gaj.ir/img/common/logo-h.png"));
//        arrayList.add(new Booth("پارسیوت", "کمیل شاه حسینی", null, "http://parsiotco.ir/wp-content/uploads/2016/10/logo.jpg"));
//        arrayList.add(new Booth("انتشارات گاج", "آقای جوکار", null, "http://gaj.ir/img/common/logo-h.png"));
//        arrayList.add(new Booth("پارسیوت", "کمیل شاه حسینی", null, "http://parsiotco.ir/wp-content/uploads/2016/10/logo.jpg"));
//        arrayList.add(new Booth("انتشارات گاج", "آقای جوکار", null, "http://gaj.ir/img/common/logo-h.png"));
//        arrayList.add(new Booth("پارسیوت", "کمیل شاه حسینی", null, "http://parsiotco.ir/wp-content/uploads/2016/10/logo.jpg"));
//        arrayList.add(new Booth("انتشارات گاج", "آقای جوکار", null, "http://gaj.ir/img/common/logo-h.png"));
//        arrayList.add(new Booth("پارسیوت", "کمیل شاه حسینی", null, "http://parsiotco.ir/wp-content/uploads/2016/10/logo.jpg"));
//        arrayList.add(new Booth("انتشارات گاج", "آقای جوکار", null, "http://gaj.ir/img/common/logo-h.png"));
//        arrayList.add(new Booth("پارسیوت", "کمیل شاه حسینی", null, "http://parsiotco.ir/wp-content/uploads/2016/10/logo.jpg"));
//        arrayList.add(new Booth("انتشارات گاج", "آقای جوکار", null, "http://gaj.ir/img/common/logo-h.png"));
//        arrayList.add(new Booth("پارسیوت", "کمیل شاه حسینی", null, "http://parsiotco.ir/wp-content/uploads/2016/10/logo.jpg"));
//        arrayList.add(new Booth("انتشارات گاج", "آقای جوکار", null, "http://gaj.ir/img/common/logo-h.png"));
//        arrayList.add(new Booth("پارسیوت", "کمیل شاه حسینی", null, "http://parsiotco.ir/wp-content/uploads/2016/10/logo.jpg"));
//        arrayList.add(new Booth("انتشارات گاج", "آقای جوکار", null, "http://gaj.ir/img/common/logo-h.png"));
//        arrayList.add(new Booth("پارسیوت", "کمیل شاه حسینی", null, "http://parsiotco.ir/wp-content/uploads/2016/10/logo.jpg"));
//        arrayList.add(new Booth("انتشارات گاج", "آقای جوکار", null, "http://gaj.ir/img/common/logo-h.png"));
//        arrayList.add(new Booth("پارسیوت", "کمیل شاه حسینی", null, "http://parsiotco.ir/wp-content/uploads/2016/10/logo.jpg"));
//        arrayList.add(new Booth("انتشارات گاج", "آقای جوکار", null, "http://gaj.ir/img/common/logo-h.png"));
//        arrayList.add(new Booth("پارسیوت", "کمیل شاه حسینی", null, "http://parsiotco.ir/wp-content/uploads/2016/10/logo.jpg"));
//        arrayList.add(new Booth("انتشارات گاج", "آقای جوکار", null, "http://gaj.ir/img/common/logo-h.png"));
/*
        arrayList.add(new Booth("پارسیوت", "کمیل شاه حسینی", null, "http://parsiotco.ir/wp-content/uploads/2016/10/logo.jpg"));
        arrayList.add(new Booth("انتشارات گاج", "آقای جوکار", null, "http://gaj.ir/img/common/logo-h.png"));
        arrayList.add(new Booth("پارسیوت", "کمیل شاه حسینی", null, "http://parsiotco.ir/wp-content/uploads/2016/10/logo.jpg"));
        arrayList.add(new Booth("انتشارات گاج", "آقای جوکار", null, "http://gaj.ir/img/common/logo-h.png"));
        arrayList.add(new Booth("پارسیوت", "کمیل شاه حسینی", null, "http://parsiotco.ir/wp-content/uploads/2016/10/logo.jpg"));
        arrayList.add(new Booth("انتشارات گاج", "آقای جوکار", null, "http://gaj.ir/img/common/logo-h.png"));
        arrayList.add(new Booth("پارسیوت", "کمیل شاه حسینی", null, "http://parsiotco.ir/wp-content/uploads/2016/10/logo.jpg"));
        arrayList.add(new Booth("انتشارات گاج", "آقای جوکار", null, "http://gaj.ir/img/common/logo-h.png"));
*/

        mAdapter.notifyDataSetChanged();

    }

    private void serverResultToArr(String result){
        try {
            JSONObject json = new JSONObject(result);
            JSONArray resArr = json.getJSONArray("booths");
            for (int idx = 0 ; idx < resArr.length(); idx++){
                JSONObject boothJson = resArr.getJSONObject(idx);
                arrayList.add(new Booth(boothJson.getInt("id"), boothJson.getString("name"),
                        boothJson.getString("owner"), boothJson.getString("description"),
                        "http://www.snut.fr/wp-content/uploads/2015/07/image-de-la-nature-5.jpg"));
            }
            runOnUiThread(() -> mAdapter.notifyDataSetChanged());

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update() {
        serverResultToArr((String) parsin.getUpdate(this));
//        Log.e(TAG, "update: " + parsin.getUpdate(this));
        parsin.unregister(this);


    }
}
