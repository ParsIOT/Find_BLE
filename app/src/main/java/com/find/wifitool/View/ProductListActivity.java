package com.find.wifitool.View;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;

import com.find.wifitool.Controller.Adapters.ItemClickListener;
import com.find.wifitool.Controller.Adapters.ProductAdapter;
import com.find.wifitool.Model.Product;
import com.find.wifitool.R;
import com.find.wifitool.Utils.DividerItemDecoration;
import com.find.wifitool.Utils.Server.SendOptionEnum;
import com.find.wifitool.Utils.StaticObjects;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;


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
        // specify an adapter (see also next example)
        mAdapter = new ProductAdapter(arrayList, ProductListActivity.this);
        mAdapter.setOnItemClickListener((view, position) -> {
            Toast.makeText(ProductListActivity.this, "item" + position + " clicked", Toast.LENGTH_SHORT).show();
            StaticObjects.product = arrayList.get(position);
            startActivity(new Intent(ProductListActivity.this, ProductItemActivity.class));
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
        localList.add(new Product(10,"پژو 597", "تیپ 12", null, 25000000, true,
                "http://www.topgear.com/sites/default/files/styles/16x9_1280w/public/images/news-article/2016/05/cc421a04d7766b7df8f517bfbff1905d/1192121_peugeot_3008_1605styp_001_b.jpg"));
        localList.add(new Product(22,"سمند 682", "تیپ 37", null, 29530000, false,
                "https://www.ikco.ir/UploadedFiles/Permanent/201604/sr7lnvkin7.jpg"));
        localList.add(new Product(10,"پژو 597", "تیپ 12", null, 25000000, true,
                "http://www.topgear.com/sites/default/files/styles/16x9_1280w/public/images/news-article/2016/05/cc421a04d7766b7df8f517bfbff1905d/1192121_peugeot_3008_1605styp_001_b.jpg"));
        localList.add(new Product(22,"سمند 682", "تیپ 37", null, 29530000, false,
                "https://www.ikco.ir/UploadedFiles/Permanent/201604/sr7lnvkin7.jpg"));
        localList.add(new Product(10,"پژو 597", "تیپ 12", null, 25000000, true,
                "http://www.topgear.com/sites/default/files/styles/16x9_1280w/public/images/news-article/2016/05/cc421a04d7766b7df8f517bfbff1905d/1192121_peugeot_3008_1605styp_001_b.jpg"));
        localList.add(new Product(22,"سمند 682", "تیپ 37", null, 29530000, false,
                "https://www.ikco.ir/UploadedFiles/Permanent/201604/sr7lnvkin7.jpg"));
        localList.add(new Product(10,"پژو 597", "تیپ 12", null, 25000000, true,
                "http://www.topgear.com/sites/default/files/styles/16x9_1280w/public/images/news-article/2016/05/cc421a04d7766b7df8f517bfbff1905d/1192121_peugeot_3008_1605styp_001_b.jpg"));
        localList.add(new Product(22,"سمند 682", "تیپ 37", null, 29530000, false,
                "https://www.ikco.ir/UploadedFiles/Permanent/201604/sr7lnvkin7.jpg"));
        localList.add(new Product(10,"پژو 597", "تیپ 12", null, 25000000, true,
                "http://www.topgear.com/sites/default/files/styles/16x9_1280w/public/images/news-article/2016/05/cc421a04d7766b7df8f517bfbff1905d/1192121_peugeot_3008_1605styp_001_b.jpg"));
        localList.add(new Product(22,"سمند 682", "تیپ 37", null, 29530000, false,
                "https://www.ikco.ir/UploadedFiles/Permanent/201604/sr7lnvkin7.jpg"));
        localList.add(new Product(10,"پژو 597", "تیپ 12", null, 25000000, true,
                "http://www.topgear.com/sites/default/files/styles/16x9_1280w/public/images/news-article/2016/05/cc421a04d7766b7df8f517bfbff1905d/1192121_peugeot_3008_1605styp_001_b.jpg"));
        localList.add(new Product(22,"سمند 682", "تیپ 37", null, 29530000, false,
                "https://www.ikco.ir/UploadedFiles/Permanent/201604/sr7lnvkin7.jpg"));
        localList.add(new Product(10,"پژو 597", "تیپ 12", null, 25000000, true,
                "http://www.topgear.com/sites/default/files/styles/16x9_1280w/public/images/news-article/2016/05/cc421a04d7766b7df8f517bfbff1905d/1192121_peugeot_3008_1605styp_001_b.jpg"));
        localList.add(new Product(22,"سمند 682", "تیپ 37", null, 29530000, false,
                "https://www.ikco.ir/UploadedFiles/Permanent/201604/sr7lnvkin7.jpg"));
        localList.add(new Product(10,"پژو 597", "تیپ 12", null, 25000000, true,
                "http://www.topgear.com/sites/default/files/styles/16x9_1280w/public/images/news-article/2016/05/cc421a04d7766b7df8f517bfbff1905d/1192121_peugeot_3008_1605styp_001_b.jpg"));
        localList.add(new Product(22,"سمند 682", "تیپ 37", null, 29530000, false,
                "https://www.ikco.ir/UploadedFiles/Permanent/201604/sr7lnvkin7.jpg"));
        localList.add(new Product(10,"پژو 597", "تیپ 12", null, 25000000, true,
                "http://www.topgear.com/sites/default/files/styles/16x9_1280w/public/images/news-article/2016/05/cc421a04d7766b7df8f517bfbff1905d/1192121_peugeot_3008_1605styp_001_b.jpg"));
        localList.add(new Product(22,"سمند 682", "تیپ 37", null, 29530000, false,
                "https://www.ikco.ir/UploadedFiles/Permanent/201604/sr7lnvkin7.jpg"));
        localList.add(new Product(10,"پژو 597", "تیپ 12", null, 25000000, true,
                "http://www.topgear.com/sites/default/files/styles/16x9_1280w/public/images/news-article/2016/05/cc421a04d7766b7df8f517bfbff1905d/1192121_peugeot_3008_1605styp_001_b.jpg"));
        localList.add(new Product(22,"سمند 682", "تیپ 37", null, 29530000, false,
                "https://www.ikco.ir/UploadedFiles/Permanent/201604/sr7lnvkin7.jpg"));
        localList.add(new Product(10,"پژو 597", "تیپ 12", null, 25000000, true,
                "http://www.topgear.com/sites/default/files/styles/16x9_1280w/public/images/news-article/2016/05/cc421a04d7766b7df8f517bfbff1905d/1192121_peugeot_3008_1605styp_001_b.jpg"));
        localList.add(new Product(22,"سمند 682", "تیپ 37", null, 29530000, false,
                "https://www.ikco.ir/UploadedFiles/Permanent/201604/sr7lnvkin7.jpg"));
        localList.add(new Product(10,"پژو 597", "تیپ 12", null, 25000000, true,
                "http://www.topgear.com/sites/default/files/styles/16x9_1280w/public/images/news-article/2016/05/cc421a04d7766b7df8f517bfbff1905d/1192121_peugeot_3008_1605styp_001_b.jpg"));
        localList.add(new Product(22,"سمند 682", "تیپ 37", null, 29530000, false,
                "https://www.ikco.ir/UploadedFiles/Permanent/201604/sr7lnvkin7.jpg"));
        localList.add(new Product(10,"پژو 597", "تیپ 12", null, 25000000, true,
                "http://www.topgear.com/sites/default/files/styles/16x9_1280w/public/images/news-article/2016/05/cc421a04d7766b7df8f517bfbff1905d/1192121_peugeot_3008_1605styp_001_b.jpg"));
        localList.add(new Product(22,"سمند 682", "تیپ 37", null, 29530000, false,
                "https://www.ikco.ir/UploadedFiles/Permanent/201604/sr7lnvkin7.jpg"));

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
