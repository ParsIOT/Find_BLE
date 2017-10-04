package com.parsin.bletool.View;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.parsin.bletool.NewsActivity;
import com.parsin.bletool.Controller.OnFragmentInteractionListener;
import com.parsin.bletool.Controller.Adapters.PagerAdapter;
import com.parsin.bletool.R;
import com.parsin.bletool.Utils.StaticObjects;
import com.parsin.bletool.Utils.Utils;
import com.parsin.bletool.internal.Constants;


import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;
import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnFragmentInteractionListener, BeaconConsumer {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int BLUETOOTH_ENABLE_REQUEST_ID = 10;
    private static final int WEIGHTED_AVERAGE_LIST_SIZE = 3;
    private int how_many_scan;
    private int one_scan_period;

    NavigationView navigationView;

    final private int REQUEST_CODE_ASK_PERMISSIONS = 1;
    private SharedPreferences sharedPreferences;
    private BeaconManager beaconManager;
    private int altBeaconCounter = 0;
    private HashMap<String, ArrayList<Integer>> hashMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.getMenu().getItem(0).setChecked(true);

        if (Build.VERSION.SDK_INT >= 15) {
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
        // Calling function to set some default values if its our first run
        sharedPreferences = getSharedPreferences(Constants.PREFS_NAME, 0);
        StaticObjects.ParsinServerIp = sharedPreferences.getString(StaticObjects.PARSIN_SERVER_NAME, StaticObjects.ParsinServerIp);
        how_many_scan = sharedPreferences.getInt(Constants.HOW_MANY_SCAN_NAME, Constants.HOW_MANY_SCAN);
        one_scan_period = sharedPreferences.getInt(Constants.ONE_SCAN_PERIOD_NAME, Constants.ONE_SCAN_PERIOD);
        setDefaultPrefs();
/*
        // Set the Learn Fragment as default
        Fragment fragment = new LearnFragment(); //It's better to be changed to TrackFragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.content, fragment)
                .commit();
*/
        ///////////////////////////////////////////////////////////////////////////////////////////////
        navigationView.getMenu().getItem(0).setChecked(true);
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, BLUETOOTH_ENABLE_REQUEST_ID);
        } else setupViewPagerView();

        hashMap = new HashMap<>();
        setupAltBeacon();

    }

    private void setupAltBeacon() {
        beaconManager = BeaconManager.getInstanceForApplication(getApplicationContext());
        beaconManager.getBeaconParsers().clear();
        // BeaconManager setup
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("m:0-3=4c000215,i:4-19,i:20-21,i:22-23,p:24-24"));
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24"));
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("m:2-3=beac,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25"));
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24"));
        // Detect the main identifier (UID) frame:
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("s:0-1=feaa,m:2-2=00,p:3-3:-41,i:4-13,i:14-19"));
        // Detect the telemetry (TLM) frame:
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("x,s:0-1=feaa,m:2-2=20,d:3-3,d:4-5,d:6-7,d:8-11,d:12-15"));
        // Detect the URL frame:
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("s:0-1=feaa,m:2-2=10,p:3-3:-41,i:4-21v"));
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("s:0-1=feaa,m:2-2=00,p:3-3:-41,i:4-13,i:14-19"));
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("s:0-1=feaa,m:2-2=10,p:3-3:-41,i:4-20v"));

        beaconManager.setForegroundScanPeriod(one_scan_period);
        beaconManager.setBackgroundScanPeriod(one_scan_period);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == BLUETOOTH_ENABLE_REQUEST_ID) {
            if (resultCode == RESULT_OK) {
                // Request granted - bluetooth is turning on...
                Handler mHandler = new Handler();
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                setupViewPagerView();
                            }
                        });
                    }
                }, 300);
            }
            if (resultCode == RESULT_CANCELED) {
                // Request denied by user, or an error was encountered while
                // attempting to enable bluetooth
            }
        }
    }

    private void setupViewPagerView() {
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText("یادگیری"));
        tabLayout.addTab(tabLayout.newTab().setText("پیمایش"));
        tabLayout.addTab(tabLayout.newTab().setText("تنظیمات"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        PagerAdapter adapter = new PagerAdapter
                (getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        viewPager.setCurrentItem(1);

    }

    @Override
    protected void onRestart() {
        navigationView.getMenu().getItem(0).setChecked(true);
        super.onRestart();
    }

    @Override
    protected void onPause() {
        navigationView.getMenu().getItem(0).setChecked(true);
        super.onPause();
        if (beaconManager.isBound(this)) {
                    /*Only do this in onDestroy() not onPause()
                    Can't be bind() & unbind() several times*/
            beaconManager.unbind(this);
        }
    }

    @Override
    protected void onResume() {
        navigationView.getMenu().getItem(0).setChecked(true);
        super.onResume();
        if (!beaconManager.isBound(this)) {
            beaconManager.bind(this);
            Log.d(TAG, "onCreate: service bind");
            //beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout(URI_BEACON_LAYOUT));
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Toast.makeText(MainActivity.this, "Permission granted, Loading Mission Control!",
                            Toast.LENGTH_SHORT)
                            .show();
                } else {
                    // Permission Denied
                    Toast.makeText(MainActivity.this, "App need FINE LOCATION ACCESS to discover nearby Wifi APs",
                            Toast.LENGTH_SHORT)
                            .show();
                }
                return;
            }
        }
    }

    // Setting default values in case fo 1st run
    private void setDefaultPrefs() {
        SharedPreferences.Editor editor = sharedPreferences.edit();

        boolean isFirstRun = sharedPreferences.contains(Constants.IS_FIRST_RUN);

        if (!isFirstRun) {
            editor.putString(Constants.USER_NAME, Constants.DEFAULT_USERNAME);
            editor.putString(Constants.SERVER_NAME, Constants.DEFAULT_SERVER);
            editor.putString(StaticObjects.PARSIN_SERVER_NAME, StaticObjects.ParsinServerIp);
            editor.putString(Constants.GROUP_NAME, Constants.DEFAULT_GROUP);
            editor.putInt(Constants.TRACK_INTERVAL, Constants.DEFAULT_TRACKING_INTERVAL);
            editor.putInt(Constants.LEARN_PERIOD, Constants.DEFAULT_LEARNING_PERIOD);
            editor.putInt(Constants.LEARN_INTERVAL, Constants.DEFAULT_LEARNING_INTERVAL);
            editor.putBoolean(Constants.IS_FIRST_RUN, false);
            editor.apply();
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
    public void onBeaconServiceConnect() {
        beaconManager.addRangeNotifier(new RangeNotifier() {
            @Override
            public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
                if (beacons.size() > 0) {
                    altBeaconCounter++;
                    for (Beacon beacon : beacons) {
                        if (!hashMap.containsKey(beacon.getBluetoothAddress())) {
                            ArrayList<Integer> arrayList = new ArrayList<>();
                            arrayList.add(beacon.getRssi());
                            hashMap.put(beacon.getBluetoothAddress(), arrayList);
                        } else {
                            ArrayList<Integer> arrayList = hashMap.get(beacon.getBluetoothAddress());
                            arrayList.add(beacon.getRssi());
                            hashMap.put(beacon.getBluetoothAddress(), arrayList);
                        }
                    }
                    if (altBeaconCounter >= how_many_scan) {
//                        handler.post(runnableCode);
                      /*  int weightedRes = 0;
                        int weight_sum = Utils.getSum(weight_arr);
                        for (String s: hashMap.keySet()){
                            if (weightedHashMap.containsKey(s)){
                                ArrayList<Integer> arrayList = hashMap.get(s);
                                arrayList.add(Utils.getMedian(hashMap.get(s)));
                                if (arrayList.size() <= WEIGHTED_AVERAGE_LIST_SIZE){
                                    for (int i = 2; i < WEIGHTED_AVERAGE_LIST_SIZE; i++) {
                                        if (arrayList.size() == i) {
                                            weightedRes = 5;
                                        }
                                    }
                                }
                            }else{
                                int median = Utils.getMedian(hashMap.get(s));
                                ArrayList<Integer> arrayList = new ArrayList<>();
                                arrayList.add(median);
                                weightedHashMap.put(s, arrayList);
                                weightedRes = median;
                            }
                        }*/
                        Utils.hashMap = new HashMap<>(hashMap);
                        EventBus.getDefault().post(hashMap);
                        altBeaconCounter = 0;
                        hashMap.clear();
                    }
                }
                Log.d(TAG, "beacon: size -> ");
            }

        });

        try {
            beaconManager.startRangingBeaconsInRegion(new Region("beaconscanner", null, null, null));
        } catch (RemoteException e) {
            e.printStackTrace();
        }
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
            startActivity(new Intent(getApplicationContext(), ProductListActivity.class));
        } else if (id == R.id.nav_booth) {
            startActivity(new Intent(getApplicationContext(), BoothListActivity.class));
        } else if (id == R.id.nav_news) {
            startActivity(new Intent(getApplicationContext(), NewsActivity.class));
        } else {
            ;
            startActivity(new Intent(getApplicationContext(), AboutUsActivity.class));

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

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


}
