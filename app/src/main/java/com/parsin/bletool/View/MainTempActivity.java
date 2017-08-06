package com.parsin.bletool.View;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.parsin.bletool.Controller.Adapters.HorizontalListAdapter;
import com.parsin.bletool.Controller.Adapters.ItemClickListener;
import com.parsin.bletool.Controller.Adapters.SliderPagerAdapter;
import com.parsin.bletool.Controller.Adapters.TodayNewsAdapter;
import com.parsin.bletool.Model.EmkanatRefahi;
import com.parsin.bletool.Model.TodayNews;
import com.parsin.bletool.NewsActivity;
import com.parsin.bletool.R;
import com.parsin.bletool.Utils.DividerItemDecoration;
import com.parsin.bletool.Utils.Utils;
import com.parsin.bletool.Utils.ViewPagerIndicator;
import com.squareup.picasso.Picasso;


import java.util.ArrayList;

public class MainTempActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "MainTempActivity";
    private static final int REQUEST_CODE_ASK_PERMISSIONS = 1;
    boolean isCollapsed;

    ArrayList<View> sliderList;
    LayoutInflater inflater;

    NavigationView navigationView;
    ViewPagerIndicator mIndicator;
    LinearLayout mLinearLayout;

    TodayNewsAdapter newsAdapter;
    HorizontalListAdapter emkanatAdapter;
    ArrayList<EmkanatRefahi> emkanatList;
    ArrayList<TodayNews> todayNewsList;
    SliderPagerAdapter sliderPagerAdapter;
    ViewPager viewPager;
    int currentPage = 0;
    Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_temp);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.getMenu().getItem(0).setChecked(true);

        if(Build.VERSION.SDK_INT >= 15) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {

                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_FINE_LOCATION)) {
                } else {
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            REQUEST_CODE_ASK_PERMISSIONS);
                }
            }
        }


        isCollapsed = true;
        inflater = LayoutInflater.from(this);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainTempActivity.this, MapActivity.class));
            }
        });

        sliderPagerAdapter = new SliderPagerAdapter(MainTempActivity.this);
        viewPager = (ViewPager) findViewById(R.id.slider_main_pager);
        viewPager.setAdapter(sliderPagerAdapter);
        mLinearLayout = (LinearLayout) findViewById(R.id.slider_indicator);


        news();
        emkanat();

        RelativeLayout news_container = (RelativeLayout) findViewById(R.id.news_container);
        ImageButton btnExpandNews = (ImageButton) findViewById(R.id.news_expand_btn);
        btnExpandNews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isCollapsed) {
                    Utils.expand(news_container);
                    isCollapsed = false;
                } else {
                    Utils.collapse(news_container, todayNewsList.size());
                    isCollapsed = true;
                }
            }
        });

        findViewById(R.id.img_btn_search).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainTempActivity.this, ProductListActivity.class));
            }
        });
        findViewById(R.id.img_btn_product).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainTempActivity.this, ProductListActivity.class));
            }
        });
        findViewById(R.id.img_btn_booth).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainTempActivity.this, BoothListActivity.class));
            }
        });


        if (Build.VERSION.SDK_INT >= 23) {
            if (!checkPermission()) {
                requestPermission(); // Code for permission
                // Code for above or equal 23 API Oriented Device
                // Your Permission granted already .Do next code
                //MyApp.logging();
            }
        }

        addToSlider("http://pishgaman.net/wp-content/uploads/2016/12/elecomp.jpg", "نمایشگاه الکترونیک، کامپیوتر و تجارت الکترونیک");
        addToSlider("http://parsiotco.ir/wp-content/uploads/2017/05/PARSiotFinalFinalFinal-2.png", "پارسیوت ارایه دهنده خدمات هوشمند مکان مبنا در درون ساختمان");

        mIndicator = new ViewPagerIndicator(this, mLinearLayout, viewPager, R.drawable.indicator_circle);
        mIndicator.setPageCount(sliderPagerAdapter.getSize());
        mIndicator.show();

    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.w(TAG, "onRequestPermissionsResult: " + "Permission granted, Loading Mission Control!");
                    Toast.makeText(MainTempActivity.this, "Permission granted, Loading Mission Control!",
                            Toast.LENGTH_SHORT)
                            .show();
                } else {
                    // Permission Denied
                    Toast.makeText(MainTempActivity.this, "App need FINE LOCATION ACCESS to discover nearby Wifi APs",
                            Toast.LENGTH_SHORT)
                            .show();
                }
                return;
            }
        }
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {

            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        //item.setChecked(true);
        //Fragment fragment = null;
        if (id == R.id.nav_map) {
            //fragment = new SettingsFragment();
            /*if (this.getClass().getSimpleName() != MainActivity.class.getSimpleName()){
                startActivity(new Intent(getClass(), MainActivity.class));
            }*/
//            startActivity(new Intent(getApplicationContext() , MainActivity.class));
//            finish();
        } else if (id == R.id.nav_product) {
            startActivity(new Intent(getApplicationContext() , ProductListActivity.class));
        } else if (id == R.id.nav_booth) {
            startActivity(new Intent(getApplicationContext() , BoothListActivity.class));
        }else if (id == R.id.nav_news) {
            startActivity(new Intent(getApplicationContext() , NewsActivity.class));
        } else if (id == R.id.nav_aboutUs){
            startActivity(new Intent(getApplicationContext() , AboutUsActivity.class));
        }else if (id == R.id.nav_setting){
            startActivity(new Intent(MainTempActivity.this, MainActivity.class));
        }

/*
        // Insert the fragment by replacing any existing fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.content, fragment)
                .commit();
*/

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    Runnable scroll_slider = new Runnable() {
        public void run() {
            if (currentPage == sliderPagerAdapter.getSize() -1) {
                currentPage = 0;
            }
            viewPager.setCurrentItem(currentPage, true);
            currentPage++;
            handler.postDelayed(scroll_slider, 2000);
        }
    };

    private void addToSlider(String imageUrl, String text) {
        Log.e(TAG, "addToSlider: " + imageUrl + "  " + text);
        View v = inflater.inflate(R.layout.slider_item_layout, null, false);
        ImageView img1 = (ImageView) v.findViewById(R.id.slider_image);
        TextView tv1 = (TextView) v.findViewById(R.id.slider_text);
        img1.setContentDescription(imageUrl);
        tv1.setText(text);
        Picasso.with(MainTempActivity.this).load(imageUrl).error(R.mipmap.no_image).into(img1);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            tv1.setTextColor(getResources().getColor(R.color.white, null));
        } else
            tv1.setTextColor(getResources().getColor(R.color.white));
        //sliderList.add(v);
        sliderPagerAdapter.addView(v);

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

        sliderPagerAdapter.notifyDataSetChanged();
    }

    private void news() {
        todayNewsList = new ArrayList<>();
        newsAdapter = new TodayNewsAdapter(todayNewsList, this);

//        newsAdapter.setOnItemClickListener(new ItemClickListener() {
//            @Override
//            public void onClick(View view, int position) {
//                long id = todayNewsList.get(position).getId();
//                int maxId = new NewsDao(MainActivity.this).getMaxId();
//                Log.e(TAG, "onClick: " + id + " " + maxId);
//                if (maxId >= id) {
//                    Log.e(TAG, "onClick: " + "in the if");
//                    Intent intent = new Intent(MainActivity.this, NewsDetailsActivity.class);
//                    intent.putExtra("news_id", id);
//                    startActivity(intent);
//                }
//            }
//        });

        RecyclerView recycler = (RecyclerView) findViewById(R.id.recycler_today_news);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recycler.setLayoutManager(mLayoutManager);
        recycler.setAdapter(newsAdapter);
        recycler.addItemDecoration(new DividerItemDecoration(this));
        recycler.setNestedScrollingEnabled(false);
        addNewsSample();
    }

    private void addNewsSample() {
        todayNewsList.add(new TodayNews(1, "توسط دانشکده مهندسی راه آهن دانشگاه برگزار می شود: پنجمین کنفرانس بین المللی پیشرفت های اخیر در مهندسی راه آهن",
                "ششمین جشنواره حرکت دانشگاه علم و صنعت ایران، صبح دوشنبه هجدهم اردیبهشت ماه 1396 با حضور نماینده وزارت علوم، تحقیقات و فناوری، رییس دانشگاه و جمعی از مسئولان دانشگاه، در محل آمفی تئاتر روز باز گشایش یافت.",
                "http://jobstr.com/user_images/international-flower-buyer-4426.jpg"));

        todayNewsList.add(new TodayNews(2, "اطلاعیه مرکز بهداشت ودرمان دانشگاه",
                "به اطلاع اساتید، دانشجویان و کارکنان محترم دانشگاه علم وصنعت ایران می رساند پایگاه سلامت علم و صنعت که در خیابان شهید ملک لو دایر شده، هیچ ارتباطی با مرکز بهداشت و درمان دانشگاه ندارد و وابسته به مرکز بهداشت شمال شرق تهران می باشد.",
                "http://www.flowermeaning.com/flower-pics/Chrysanthemum-Meaning.jpg"));

        todayNewsList.add(new TodayNews(3, "انتخاب دانشگاه علم و صنعت ایران برای همکاری با دانشگاه آخن آلمان توسط وزارت علوم",
                "در دیدار با مسئولان دانشگاه آخن مقرر شد دانشگاه های علم و صنعت ایران، صنعتی شریف و دانشگاه صنعتی امیر کبیر محوریت همکاری ایران با این دانشگاه را برعهده بگیرند.",
                "http://jobstr.com/user_images/international-flower-buyer-4426.jpg"));

        todayNewsList.add(new TodayNews(4, "برگزاری سخنرانی علمی در دانشکده ی مهندسی مکانیک دانشگاه علم و صنعت ایران",
                "در راستای همکاری متخصصان و دانشمندان غیر مقیم گروه مهندسی ساخت و تولید دانشکده مهندسی مکانیک سخنرانی علمی با عنوان\" Variable stiffness Composite Plates\" را روز شنبه بیست و سوم اردیبهشت ماه 96 برگزار خواهد نمود.",
                "http://www.flowermeaning.com/flower-pics/Chrysanthemum-Meaning.jpg"));

        todayNewsList.add(new TodayNews(5, "برگزاری سخنرانی علمی در دانشکده ی مهندسی مکانیک دانشگاه علم و صنعت ایران",
                "در راستای همکاری متخصصان و دانشمندان غیر مقیم گروه مهندسی ساخت و تولید دانشکده مهندسی مکانیک سخنرانی علمی با عنوان\" Variable stiffness Composite Plates\" را روز شنبه بیست و سوم اردیبهشت ماه 96 برگزار خواهد نمود.",
                "http://www.flowermeaning.com/flower-pics/Chrysanthemum-Meaning.jpg"));

        newsAdapter.notifyDataSetChanged();

    }

    private void emkanat(){
        RecyclerView recycler = (RecyclerView) findViewById(R.id.recycler_emkanat_refahi);
        emkanatList = new ArrayList<>();
        emkanatAdapter = new HorizontalListAdapter(emkanatList, this);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, true);

        emkanatAdapter.setOnItemClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int position) {
                Intent intent = new Intent(MainTempActivity.this, MapActivity.class);
                intent.putExtra("emkanat", emkanatList.get(position).getXy());
                startActivity(intent);
            }
        });

        recycler.setLayoutManager(mLayoutManager);
        recycler.setAdapter(emkanatAdapter);
        recycler.addItemDecoration(new DividerItemDecoration(this));

        emkanatList.add(new EmkanatRefahi(0, R.mipmap.question, "اطلاعات", "50,50"));
        emkanatList.add(new EmkanatRefahi(1, R.mipmap.praying_room, "نمازخانه", "100,50"));
        emkanatList.add(new EmkanatRefahi(2, R.mipmap.wc, "سرویس بهداشتی", "200,50"));
        emkanatList.add(new EmkanatRefahi(3, R.mipmap.parking, "پارگینگ", "150,50"));
        emkanatList.add(new EmkanatRefahi(4, R.mipmap.door, "درب ورود و خروج", "10,50"));
        emkanatList.add(new EmkanatRefahi(5, R.mipmap.restaurant, "رستوران", "100,100"));
        emkanatAdapter.notifyDataSetChanged();
    }

    private boolean checkPermission() {
        int result1 = ContextCompat.checkSelfPermission(MainTempActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int result2 = ContextCompat.checkSelfPermission(MainTempActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE);
        int result3 = ContextCompat.checkSelfPermission(MainTempActivity.this, Manifest.permission.READ_LOGS);
        if (result1 == PackageManager.PERMISSION_GRANTED && result2 == PackageManager.PERMISSION_GRANTED && result3 == PackageManager.PERMISSION_GRANTED ) {
            return true;
        } else {
            return false;
        }
    }

    private void requestPermission() {

        if (ActivityCompat.shouldShowRequestPermissionRationale(MainTempActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) &&
                ActivityCompat.shouldShowRequestPermissionRationale(MainTempActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) &&
                ActivityCompat.shouldShowRequestPermissionRationale(MainTempActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            Toast.makeText(MainTempActivity.this, "Write External Storage permission allows us to do store images. Please allow this permission in App Settings.", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(MainTempActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.READ_LOGS}, 1);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        //handler.removeCallbacks(scroll_slider);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //handler.postDelayed(scroll_slider, 2000);
    }
}
