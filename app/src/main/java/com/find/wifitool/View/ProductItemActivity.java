package com.find.wifitool.View;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.find.wifitool.Controller.Adapters.SliderPagerAdapter;
import com.find.wifitool.R;
import com.find.wifitool.Utils.Utils;
import com.github.aakira.expandablelayout.ExpandableRelativeLayout;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class ProductItemActivity extends AppCompatActivity {

    private static final String TAG = ProductItemActivity.class.getSimpleName();
    ArrayList<View> sliderList = new ArrayList<>();
    SliderPagerAdapter adapter = new SliderPagerAdapter(this);
    ViewPager viewPager;
    LayoutInflater inflater;
    SwitchCompat availability_switch;
    TextView cost;
    TextView availabiblity_tv;
    TextView product_description;
    TextView product_weight;
    TextView product_manufacturer;
    TextView product_size;
    int cost_int = 2500000;
    boolean isCollapsed;

    String image_url = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_item);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });




        isCollapsed = true;
        availability_switch = (SwitchCompat) findViewById(R.id.product_availability_switch);
        cost = (TextView) findViewById(R.id.product_cost);
        availabiblity_tv = (TextView) findViewById(R.id.product_availability_text_view);
        product_description = (TextView) findViewById(R.id.product_description);
        product_weight = (TextView) findViewById(R.id.product_weight);
        product_manufacturer = (TextView) findViewById(R.id.product_manufacturer);
        product_size = (TextView) findViewById(R.id.product_size);

        ////////////////////// slider ///////////////////////

        sliderList = new ArrayList<>();
        adapter = new SliderPagerAdapter(this);
        viewPager = (ViewPager) findViewById(R.id.slider_important_event);
        viewPager.setAdapter(adapter);
        inflater = LayoutInflater.from(this);
/*
        viewPager.setOffscreenPageLimit(2); //your choice
        viewPager.setCurrentItem(Integer.MAX_VALUE/2, false);
*/

        /////////////////////////////////////////////////////


        ExpandableRelativeLayout news_container = (ExpandableRelativeLayout) findViewById(R.id.product_description_container);
        ImageButton btnExpandNews = (ImageButton) findViewById(R.id.description_expand_btn);
        btnExpandNews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isCollapsed) {
                    Log.w(TAG, "expand");
                    news_container.expand();
                    btnExpandNews.setImageResource(R.drawable.ic_expand_less);
                    isCollapsed = false;
                } else {
                    Log.w(TAG, "Collapsed");
                    news_container.collapse();
                    btnExpandNews.setImageResource(R.drawable.ic_expand_more);
                    isCollapsed = true;
                }
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/
                startActivity(new Intent(ProductItemActivity.this, MapActivity.class));
            }
        });

        String s = String.format("%s", makeCostTextView(cost_int));
        cost.setText(s);

        availability_switch.setClickable(false);
        availability_switch.setChecked(true);
        changeAvailabilityTv();


        ////////////////////// description text /////////////////////
        product_description.setText(R.string.product_description);
        s = String.format("%s", Utils.toPersianNum("325.5 x 109 x 111.3 ", false));
        product_size.setText(s);
        s = String.format("%s کیلوگرم", Utils.toPersianNum(String.valueOf(1260), false));
        product_weight.setText(s);
        product_manufacturer.setText("پارسیوت");

        Intent intent = getIntent();
        ActionBar actionBar = getSupportActionBar();
        if (intent.hasExtra("title") && actionBar != null) {
            actionBar.setTitle(intent.getStringExtra("title"));
        }
        if (intent.hasExtra("image"))
            image_url = intent.getStringExtra("image");



        addToSlider(image_url,
                "");
        addToSlider(image_url,
                "");


    }

    private void changeAvailabilityTv(){
        if (availability_switch.isChecked())
            availabiblity_tv.setText("موجود");
        else
            availabiblity_tv.setText("ناموجود");
    }

    private void addToSlider(String imageUrl, String text) {
        Log.e(TAG, "addToSlider: " + imageUrl + "  " + text);
        View v = inflater.inflate(R.layout.slider_item_layout, null, false);
        ImageView img1 = (ImageView) v.findViewById(R.id.slider_image);

        ImageView imageView = (ImageView) v.findViewById(R.id.shadow);
        imageView.setVisibility(View.GONE);

        TextView tv1 = (TextView) v.findViewById(R.id.slider_text);
        img1.setContentDescription(imageUrl);
//        img1.setImageResource(R.mipmap.mountain);
//        tv1.setText("ایران در روز نخست به 8 مدال رسید/ 2 طلا، 2 نقره و 4 برنز سهم کاروان ایران");
        tv1.setText(text);
        Picasso.with(ProductItemActivity.this).load(imageUrl).error(R.mipmap.no_image).into(img1);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            tv1.setTextColor(getResources().getColor(R.color.white, null));
        } else
            tv1.setTextColor(getResources().getColor(R.color.white));
        //sliderList.add(v);
        adapter.addView(v);
/*
        v = inflater.inflate(R.layout.slider_item_main_activity, null, false);
        img1 = (ImageView) v.findViewById(R.id.slider_image);
        tv1 = (TextView) v.findViewById(R.id.slider_text);
        img1.setImageResource(R.mipmap.maxresdefault);
        tv1.setText("یارانه ۴۵۵۰۰ تومانی اردیبهشت سه شنبه واریز می شود");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            tv1.setTextColor(getResources().getColor(R.color.white, null));
        } else
            tv1.setTextColor(getResources().getColor(R.color.white));
        sliderPagerAdapter.addView(v);
*/

        adapter.notifyDataSetChanged();
    }
    public String makeCostTextView(int cost){
        String s = String.valueOf(cost);
        s = Utils.toPersianNum(s, false);
        StringBuilder sb = new StringBuilder();
        int size = s.length();
        for (int i = 0; i < size ; i++) {
            sb.insert(0, s.charAt(size - 1 - i));
            if (i % 3 == 2 && i != size-1){
                sb.insert(0, ",");
            }
            Log.e(TAG, String.valueOf(i) + " " + s.charAt(size - 1 - i));
        }
        return sb.toString();
    }
}
