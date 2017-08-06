package com.parsin.bletool.View;

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
import com.parsin.bletool.Controller.Adapters.ItemClickListener;
import com.parsin.bletool.Controller.Adapters.ProductAdapter;
import com.parsin.bletool.Controller.Observer;
import com.parsin.bletool.Utils.DividerItemDecoration;
import com.parsin.bletool.Utils.Server.ParsinServer;
import com.parsin.bletool.Model.Booth;
import com.parsin.bletool.Model.Product;
import com.parsin.bletool.R;
import com.parsin.bletool.Utils.StaticObjects;
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
        // specify an sliderPagerAdapter (see also next example)
        mAdapter = new ProductAdapter(products, BoothItemActivity.this);
        mAdapter.setOnItemClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int position) {
                Toast.makeText(BoothItemActivity.this, "item" + position + " clicked", Toast.LENGTH_SHORT).show();
                StaticObjects.product = products.get(position);
                Intent intent = new Intent(BoothItemActivity.this, ProductItemActivity.class);
                intent.putExtra("title", products.get(position).getName());
                intent.putExtra("image", products.get(position).getImageUrl());
                startActivity(intent);
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

        products.add(new Product(10,"راهنمای بازدیدکنندگان نمایشگاه", "",
                "ارائه راهکاری برای تبلیغات هوشمند، مدیریت محصولات و بررسی و تحلیل های آماری مشتریان برمبنای داده های حاصل از موقعیت یابی", 10000000, true,
                StaticObjects.ParsinServerIp + "/media/images/2017/07/20/Parsin1.png"));
        products.add(new Product(20,"سامانه راهنمای موزه ها", "",
                "ارائه راهکار هایی برای ایجاد برنامه راهنمایی موزه ها و اماکن تاریخی و گردشکری بر مبنای تبلیغات مجاورتی و موقعیت یابی درون ساختمان.", 10000000, true,
                StaticObjects.ParsinServerIp + "/media/images/2017/07/20/parsin2.png"));
        products.add(new Product(30,"راهنمای مشتریان مخصوص فروشگاه ها و اماکن تجاری", "",
                "ایجاد سامانه مکانیابی با هدف جذب و تحلیل مشتری در کنار کاربرد های رایج راهنمای مشتریان و پنل مدیریت فروشگاه", 10000000, true,
                StaticObjects.ParsinServerIp + "/media/images/2017/07/20/parsin3.png"));
        products.add(new Product(40,"سامانه ردیابی کارکنان و تجهیزات", "",
                "سامانه ردیابی کارکنان و تجهیزات مبتنی بر پلتفرم موقعیت یابی", 10000000, true,
                StaticObjects.ParsinServerIp + "/media/images/2017/07/20/parsin4.png"));
        products.add(new Product(50,"سیستم موقعیت یابی بیمارستان و مراکز درمانی", "",
                "سیستم موقعیت یابی بلادرنگ بیمارستان و مراکز درمانی", 10000000, true,
                StaticObjects.ParsinServerIp + "/media/images/2017/07/20/parsin5.png"));
        /*DBHelper db = new DBHelper(ProductListActivity.this);
        for (Product product : products){
            db.createProduct(product);
        }*/


        mAdapter.notifyDataSetChanged();

    }


}
