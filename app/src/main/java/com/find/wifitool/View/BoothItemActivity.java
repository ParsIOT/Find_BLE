package com.find.wifitool.View;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.find.wifitool.Controller.Adapters.ItemClickListener;
import com.find.wifitool.Controller.Adapters.ProductAdapter;
import com.find.wifitool.Controller.Observer;
import com.find.wifitool.Utils.DividerItemDecoration;
import com.find.wifitool.Utils.Server.ParsinServer;
import com.find.wifitool.Model.Booth;
import com.find.wifitool.Model.Product;
import com.find.wifitool.R;
import com.find.wifitool.Utils.StaticObjects;
import com.squareup.picasso.Picasso;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class BoothItemActivity extends AppCompatActivity implements Observer {
    private ImageView image;
    private TextView tv_title;
    private TextView tv_owner;
    private TextView tv_description;
    private List<Product> products;
    private Booth booth;

    private RecyclerView mRecyclerView;
    private ProductAdapter mAdapter;
    private LinearLayoutManager mLayoutManager;

    ParsinServer server = new ParsinServer();

    private BottomSheetBehavior mBottomSheetBehavior;
    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booth_item);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            toolbar.setTitleTextColor(getResources().getColor(R.color.colorYellow, null));
        } else toolbar.setTitleTextColor(getResources().getColor(R.color.colorYellow));


        products = new ArrayList<>();

        booth = StaticObjects.booth;

        server.register(this);
        server.getProductByBoothId(1, this);

        prepareMainView();

        prepareBottomSheet();

        prepareRecycler();

        addItemToList();



        /*parsinServer = new MyAsyncTask(SendOptionEnum.Get_Booth_Item, null, booth.getId());
        parsinServer.register(this);
        parsinServer.execute();*/
    }

    private void prepareMainView() {

        //Log.e(BoothItemActivity.class.getSimpleName(), booth.toString());
        //toolbar.setTitle(booth.getName());
        image = (ImageView) findViewById(R.id.coordinator_image);

        tv_title = (TextView) findViewById(R.id.item_title);
        tv_owner = (TextView) findViewById(R.id.item_owner);
        tv_description = (TextView) findViewById(R.id.item_description);
        getSupportActionBar().setTitle(booth.getName());
        tv_title.setText(booth.getName());
        tv_owner.setText(booth.getOwner());
        tv_description.setText(booth.getDescription() != null ? booth.getDescription() : "ندارد");


        Picasso.with(BoothItemActivity.this).load(booth.getImage_url())
                .error(R.drawable.no_image)
                .into(image);
    }

    private void prepareBottomSheet() {
        fab = (FloatingActionButton) findViewById(R.id.fab);
        View llBottomSheet = findViewById(R.id.bottom_sheet);
        mBottomSheetBehavior = BottomSheetBehavior.from(llBottomSheet);
        mBottomSheetBehavior.setPeekHeight(100);
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        mBottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                fab.animate().scaleX(1 - slideOffset).scaleY(1 - slideOffset).setDuration(0).start();
            }
        });
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        });
    }

    private void prepareRecycler() {
        mRecyclerView = (RecyclerView) findViewById(R.id.booth_product_bottom_sheet_recycler);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this));
        // specify an adapter (see also next example)
        mAdapter = new ProductAdapter(products, BoothItemActivity.this);
        mAdapter.setOnItemClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int position) {
                Toast.makeText(BoothItemActivity.this, "item" + position + " clicked", Toast.LENGTH_SHORT).show();
                StaticObjects.product = products.get(position);
                startActivity(new Intent(BoothItemActivity.this, ProductItemActivity.class));
            }
        });
        mRecyclerView.setAdapter(mAdapter);
    }


    @Override
    public void update() {
        String msg = (String) server.getUpdate(this);
        Log.e("update", msg);
        server.unregister(this);
        try {
            JSONObject jsonObject = new JSONObject(msg);
            JSONArray jsonArray = jsonObject.getJSONArray("booth_Products");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject json = jsonArray.getJSONObject(i);
                Product product = new Product(json.getInt("id"), json.getString("name"), json.getString("model")
                        , json.getString("description"), json.getInt("price"), json.getBoolean("status"), null);

                products.add(product);

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private void addItemToList() {

        products.add(new Product(10, "پژو 597", "تیپ 12", null, 25000000, true,
                "http://www.topgear.com/sites/default/files/styles/16x9_1280w/public/images/news-article/2016/05/cc421a04d7766b7df8f517bfbff1905d/1192121_peugeot_3008_1605styp_001_b.jpg"));
        products.add(new Product(22, "سمند 682", "تیپ 37", null, 29530000, false,
                "https://www.ikco.ir/UploadedFiles/Permanent/201604/sr7lnvkin7.jpg"));
        products.add(new Product(10, "پژو 597", "تیپ 12", null, 25000000, true,
                "http://www.topgear.com/sites/default/files/styles/16x9_1280w/public/images/news-article/2016/05/cc421a04d7766b7df8f517bfbff1905d/1192121_peugeot_3008_1605styp_001_b.jpg"));
        products.add(new Product(22, "سمند 682", "تیپ 37", null, 29530000, false,
                "https://www.ikco.ir/UploadedFiles/Permanent/201604/sr7lnvkin7.jpg"));
        products.add(new Product(10, "پژو 597", "تیپ 12", null, 25000000, true,
                "http://www.topgear.com/sites/default/files/styles/16x9_1280w/public/images/news-article/2016/05/cc421a04d7766b7df8f517bfbff1905d/1192121_peugeot_3008_1605styp_001_b.jpg"));
        products.add(new Product(22, "سمند 682", "تیپ 37", null, 29530000, false,
                "https://www.ikco.ir/UploadedFiles/Permanent/201604/sr7lnvkin7.jpg"));
        products.add(new Product(10, "پژو 597", "تیپ 12", null, 25000000, true,
                "http://www.topgear.com/sites/default/files/styles/16x9_1280w/public/images/news-article/2016/05/cc421a04d7766b7df8f517bfbff1905d/1192121_peugeot_3008_1605styp_001_b.jpg"));
        products.add(new Product(22, "سمند 682", "تیپ 37", null, 29530000, false,
                "https://www.ikco.ir/UploadedFiles/Permanent/201604/sr7lnvkin7.jpg"));
        products.add(new Product(10, "پژو 597", "تیپ 12", null, 25000000, true,
                "http://www.topgear.com/sites/default/files/styles/16x9_1280w/public/images/news-article/2016/05/cc421a04d7766b7df8f517bfbff1905d/1192121_peugeot_3008_1605styp_001_b.jpg"));
        products.add(new Product(22, "سمند 682", "تیپ 37", null, 29530000, false,
                "https://www.ikco.ir/UploadedFiles/Permanent/201604/sr7lnvkin7.jpg"));
        products.add(new Product(10, "پژو 597", "تیپ 12", null, 25000000, true,
                "http://www.topgear.com/sites/default/files/styles/16x9_1280w/public/images/news-article/2016/05/cc421a04d7766b7df8f517bfbff1905d/1192121_peugeot_3008_1605styp_001_b.jpg"));
        products.add(new Product(22, "سمند 682", "تیپ 37", null, 29530000, false,
                "https://www.ikco.ir/UploadedFiles/Permanent/201604/sr7lnvkin7.jpg"));
        products.add(new Product(10, "پژو 597", "تیپ 12", null, 25000000, true,
                "http://www.topgear.com/sites/default/files/styles/16x9_1280w/public/images/news-article/2016/05/cc421a04d7766b7df8f517bfbff1905d/1192121_peugeot_3008_1605styp_001_b.jpg"));
        products.add(new Product(22, "سمند 682", "تیپ 37", null, 29530000, false,
                "https://www.ikco.ir/UploadedFiles/Permanent/201604/sr7lnvkin7.jpg"));
        products.add(new Product(10, "پژو 597", "تیپ 12", null, 25000000, true,
                "http://www.topgear.com/sites/default/files/styles/16x9_1280w/public/images/news-article/2016/05/cc421a04d7766b7df8f517bfbff1905d/1192121_peugeot_3008_1605styp_001_b.jpg"));
        products.add(new Product(22, "سمند 682", "تیپ 37", null, 29530000, false,
                "https://www.ikco.ir/UploadedFiles/Permanent/201604/sr7lnvkin7.jpg"));
        products.add(new Product(10, "پژو 597", "تیپ 12", null, 25000000, true,
                "http://www.topgear.com/sites/default/files/styles/16x9_1280w/public/images/news-article/2016/05/cc421a04d7766b7df8f517bfbff1905d/1192121_peugeot_3008_1605styp_001_b.jpg"));
        products.add(new Product(22, "سمند 682", "تیپ 37", null, 29530000, false,
                "https://www.ikco.ir/UploadedFiles/Permanent/201604/sr7lnvkin7.jpg"));
        products.add(new Product(10, "پژو 597", "تیپ 12", null, 25000000, true,
                "http://www.topgear.com/sites/default/files/styles/16x9_1280w/public/images/news-article/2016/05/cc421a04d7766b7df8f517bfbff1905d/1192121_peugeot_3008_1605styp_001_b.jpg"));
        products.add(new Product(22, "سمند 682", "تیپ 37", null, 29530000, false,
                "https://www.ikco.ir/UploadedFiles/Permanent/201604/sr7lnvkin7.jpg"));

        /*DBHelper db = new DBHelper(ProductListActivity.this);
        for (Product product : products){
            db.createProduct(product);
        }*/


        mAdapter.notifyDataSetChanged();

    }


}
