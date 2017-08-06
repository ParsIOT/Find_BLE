package com.parsin.bletool.View;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;

import com.parsin.bletool.Controller.Adapters.ProductAdapter;
import com.parsin.bletool.Model.Product;
import com.parsin.bletool.R;
import com.parsin.bletool.Utils.DividerItemDecoration;
import com.parsin.bletool.Utils.StaticObjects;

import java.util.ArrayList;
import java.util.List;


public class ProductListActivity extends AppCompatActivity {

    RecyclerView mRecyclerView;
    ProductAdapter mAdapter;
    LinearLayoutManager mLayoutManager;
    List<Product> arrayList;

    //TODO finish item and add some record


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        arrayList = new ArrayList<>();
        mRecyclerView = (RecyclerView) findViewById(R.id.product_recycler);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this));
        // specify an sliderPagerAdapter (see also next example)
        mAdapter = new ProductAdapter(arrayList, ProductListActivity.this);
        mAdapter.setOnItemClickListener((view, position) -> {
            Toast.makeText(ProductListActivity.this, "item" + position + " clicked", Toast.LENGTH_SHORT).show();
            StaticObjects.product = arrayList.get(position);
            Intent intent = new Intent(ProductListActivity.this, ProductItemActivity.class);
            intent.putExtra("title", arrayList.get(position).getName());
            intent.putExtra("image", arrayList.get(position).getImageUrl());
            startActivity(intent);
        });

        mRecyclerView.setAdapter(mAdapter);

        addItemToList();

/*
        handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                getListFromServer();
            }
        }, 1000);
*/


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
        List<Product> localList = new ArrayList<>();
        localList.add(new Product(10,"راهنمای بازدیدکنندگان نمایشگاه", "",
                "ارائه راهکاری برای تبلیغات هوشمند، مدیریت محصولات و بررسی و تحلیل های آماری مشتریان برمبنای داده های حاصل از موقعیت یابی", 10000000, true,
                StaticObjects.ParsinServerIp + "/media/images/2017/07/20/Parsin1.png"));
        localList.add(new Product(20,"سامانه راهنمای موزه ها", "",
                "ارائه راهکار هایی برای ایجاد برنامه راهنمایی موزه ها و اماکن تاریخی و گردشکری بر مبنای تبلیغات مجاورتی و موقعیت یابی درون ساختمان.", 10000000, true,
                StaticObjects.ParsinServerIp + "/media/images/2017/07/20/parsin2.png"));
        localList.add(new Product(30,"راهنمای مشتریان مخصوص فروشگاه ها و اماکن تجاری", "",
                "ایجاد سامانه مکانیابی با هدف جذب و تحلیل مشتری در کنار کاربرد های رایج راهنمای مشتریان و پنل مدیریت فروشگاه", 10000000, true,
                StaticObjects.ParsinServerIp + "/media/images/2017/07/20/parsin3.png"));
        localList.add(new Product(40,"سامانه ردیابی کارکنان و تجهیزات", "",
                "سامانه ردیابی کارکنان و تجهیزات مبتنی بر پلتفرم موقعیت یابی", 10000000, true,
                StaticObjects.ParsinServerIp + "/media/images/2017/07/20/parsin4.png"));
        localList.add(new Product(50,"سیستم موقعیت یابی بیمارستان و مراکز درمانی", "",
                "سیستم موقعیت یابی بلادرنگ بیمارستان و مراکز درمانی", 10000000, true,
                StaticObjects.ParsinServerIp + "/media/images/2017/07/20/parsin5.png"));



/*
        DBHelper db = new DBHelper(ProductListActivity.this);
        for (Product product : localList){
            db.createProduct(product);
        }
*/

        arrayList.addAll(localList);

        mAdapter.notifyDataSetChanged();

    }


//    private void getListFromServer() {
//
//        try {
//            String res = new MyAsyncTask(SendOptionEnum.Get_Booths_List, null, -1).execute().get();
//
//            Log.e(BoothListActivity.class.getSimpleName(), res);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        } catch (ExecutionException e) {
//            e.printStackTrace();
//        }
//    }
}
