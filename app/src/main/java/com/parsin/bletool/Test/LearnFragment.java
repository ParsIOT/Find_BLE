package com.parsin.bletool.Test;

/**
 * Created by hadi on 9/2/17.
 */

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.parsin.bletool.Controller.OnFragmentInteractionListener;
import com.parsin.bletool.R;
import com.parsin.bletool.Model.database.Event;
import com.parsin.bletool.Model.database.InternalDataBase;
import com.parsin.bletool.Utils.Timer;
import com.parsin.bletool.Utils.Utils;
import com.parsin.bletool.internal.Constants;
import com.parsin.bletool.internal.FindUtils;
import com.parsin.bletool.internal.wifi.WifiArrayAdapter;
import com.parsin.bletool.internal.wifi.WifiIntentReceiver;
import com.parsin.bletool.internal.wifi.WifiObject;


import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;




@SuppressLint("setJavaScriptEnabled")
public class LearnFragment extends Fragment {
    public static boolean isLearnTimerOn = false;
    private Timer timer;
    private int leanCounter = 0;
    private JSONObject wifiFingerprint;
    private String groupName;
    private String userName;
    private String locationName;
    private static final String TAG = LearnFragment.class.getSimpleName();

    String strLocationName = "";

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;
    private int msgSentCounter = 0;
    static ProgressDialog progress;
    //private variables
    private Context mContext = getActivity();
    private OnFragmentInteractionListener mListener;
    static Handler handler = new Handler();

    private SharedPreferences sharedPreferences;
    private String strUsername;
    private String strServer;
    private String strGroup;
    private int learnIntervalVal;
    private int learnPeriodVal;
    private static String dialogMsg = "Please wait while we are collecting the Wifi APs around you...\nmsgCounter is : ";

    private WebView mWebView;
    private Button mButton;
    EditText editText;
    private AlertDialog alertDialog;


    private ArrayList<WifiObject> arrayList;
    private WifiArrayAdapter wifiArrayAdapter;


    private static final int REQUEST_CODE_ASK_PERMISSIONS = 1;
    private int REQUEST_ENABLE_BT = 1;
    private HashMap<String, ArrayList<Integer>> hashMap;
    private boolean isLearningAllowed = false;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LearnFragment newInstance(String param1, String param2) {
        LearnFragment fragment = new LearnFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    // Required empty public constructor
    public LearnFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        progress = new ProgressDialog(getActivity());
        // Checking if the Location service is enabled in case of Android M or above users
        if (!FindUtils.isLocationAvailable(mContext)) {
            // notify user
            android.support.v7.app.AlertDialog.Builder dialog = new android.support.v7.app.AlertDialog.Builder(getActivity());
            dialog.setMessage("Location service is not On. Users running Android M and above have to turn on location services for FIND to work properly");
            dialog.setPositiveButton("Enable Locations service", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    getActivity().startActivity(myIntent);
                }
            });
            dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    logMeToast("Thank you!! Getting things in place.");
                }
            });
            dialog.show();
        }
        // Creating WiFi list adapter
        arrayList = new ArrayList<>();
        wifiArrayAdapter = new WifiArrayAdapter(getActivity(), R.layout.wifi_list_item, arrayList);
        // Getting values from Shared prefs for Learning
        sharedPreferences = getActivity().getSharedPreferences(Constants.PREFS_NAME, 0);
        strGroup = sharedPreferences.getString(Constants.GROUP_NAME, Constants.DEFAULT_GROUP);
        strUsername = sharedPreferences.getString(Constants.USER_NAME, Constants.DEFAULT_USERNAME);
        strServer = sharedPreferences.getString(Constants.SERVER_NAME, Constants.DEFAULT_SERVER);
        learnIntervalVal = sharedPreferences.getInt(Constants.LEARN_INTERVAL, Constants.DEFAULT_LEARNING_INTERVAL);
        learnPeriodVal = sharedPreferences.getInt(Constants.LEARN_PERIOD, Constants.DEFAULT_LEARNING_PERIOD);
        // Initialising internal DB n retriving values from it to fill our listView
        final InternalDataBase internalDataBase = new InternalDataBase(getActivity());
        List<Event> eventList = internalDataBase.getAllEvents();
        for (Event event : eventList) {
            WifiObject wifi = new WifiObject(event.getWifiName(), event.getWifiGroup(), event.getWifiUser());
            wifiArrayAdapter.add(wifi);
        }
//        timer = new Timer(sendPayloadTask, "LearnThread", 0, false);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_learn, container, false);
        ListView listView = (ListView) rootView.findViewById(R.id.listView);
        listView.setAdapter(wifiArrayAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                final CharSequence[] items = {"Delete", "Append", "Cancel"};
                android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getActivity());
                builder.setTitle("Choose an item ...!!!");
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                        if (items[item].equals("Delete")) {
                            InternalDataBase db = new InternalDataBase(getActivity());
                            WifiObject obj = arrayList.get(position);
                            Event event = new Event(obj.wifiName, obj.grpName, obj.userName);
                            db.deleteRecord(event);
                            wifiArrayAdapter.remove(obj);

                        } else if (items[item].equals("Append")) {
                            strLocationName = arrayList.get(position).wifiName;
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString(Constants.LOCATION_NAME, strLocationName);
                            editor.apply();
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    progress.setTitle("Learning");
                                    progress.setMessage(dialogMsg + String.valueOf(msgSentCounter));
                                    progress.setCanceledOnTouchOutside(false);
                                    progress.setIndeterminate(true);
                                    progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                                    progress.show();
                                }
                            });

                            msgSentCounter = 0;

                            Runnable progressRunnable = new Runnable() {
                                @Override
                                public void run() {
                                    progress.dismiss();
                                    handler.removeCallbacks(runnableCode);
                                }
                            };
                            Handler pdCanceller = new Handler();
                            pdCanceller.postDelayed(progressRunnable, learnPeriodVal * 60 * 1000);

                            handler.post(runnableCode);
                            //Toast.makeText(mContext, text, Toast.LENGTH_SHORT).show();

                        } else if (items[item].equals("Cancel")) {
                            dialog.dismiss();
                        }
                    }
                });
                builder.show();
            }
        });
        mWebView = (WebView) rootView.findViewById(R.id.web_view);
        WebSettings mWebSettings = mWebView.getSettings();
        mWebSettings.setJavaScriptEnabled(true);
        mWebView.addJavascriptInterface(new WebAppInterface(getActivity()), "Android");
        mWebView.loadUrl("file:///android_asset/leaflet/test-map.html");
        mButton = (Button) rootView.findViewById(R.id.button);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mWebView.getVisibility() == View.GONE) {
                    mWebView.setVisibility(View.VISIBLE);
                } else mWebView.setVisibility(View.GONE);

            }
        });
        FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
                final EditText edittext = new EditText(getActivity());
                alert.setTitle("Enter Location");

                alert.setView(edittext);

                alert.setPositiveButton("add", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        if (edittext.getText().toString() == "") {
                            Toast.makeText(getActivity(), "editText was empty", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        } else {
                            WifiObject wifiObject = new WifiObject(edittext.getText().toString(),
                                    sharedPreferences.getString(Constants.GROUP_NAME, Constants.DEFAULT_GROUP),
                                    sharedPreferences.getString(Constants.USER_NAME, Constants.DEFAULT_USERNAME));
                            wifiArrayAdapter.add(wifiObject);
                            InternalDataBase dataBase = new InternalDataBase(getActivity());
                            dataBase.addEvent(new Event(wifiObject.wifiName, wifiObject.grpName, wifiObject.userName));

                        }

                    }
                });

                alert.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                    }
                });

                alert.show();
            }
        });
        return rootView;
    }

    // Insert new location into listView
    public void insertIntoList(WifiObject wifi) {
        final WifiObject wifiObject = wifi;
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                wifiArrayAdapter.add(wifiObject);
            }
        });
        msgSentCounter = 0;

/*        Runnable progressRunnable = new Runnable() {
            @Override
            public void run() {
                progress.dismiss();
                handler.removeCallbacks(runnableCode);
            }
        };
        Handler pdCanceller = new Handler();
        pdCanceller.postDelayed(progressRunnable, learnPeriodVal * 60 * 1000);*/
    }

    // Timers to keep track of our Learning period
    private Runnable runnableCode = new Runnable() {
        @Override
        public void run() {
            if (hashMap.size() > 0) {
                // Passing values to WifiIntent for further processing
                if (Build.VERSION.SDK_INT >= 23) {
//                if(FindUtils.isWiFiAvailable(mContext) && FindUtils.hasAnyLocationPermission(mContext)) {
                    sharedPreferences = getActivity().getSharedPreferences(Constants.PREFS_NAME, 0);
                    strGroup = sharedPreferences.getString(Constants.GROUP_NAME, Constants.DEFAULT_GROUP);
                    strUsername = sharedPreferences.getString(Constants.USER_NAME, Constants.DEFAULT_USERNAME);
                    strServer = sharedPreferences.getString(Constants.SERVER_NAME, Constants.DEFAULT_SERVER);
                    learnIntervalVal = sharedPreferences.getInt(Constants.LEARN_INTERVAL, Constants.DEFAULT_LEARNING_INTERVAL);
                    learnPeriodVal = sharedPreferences.getInt(Constants.LEARN_PERIOD, Constants.DEFAULT_LEARNING_PERIOD);
                    Intent intent = new Intent(mContext, WifiIntentReceiver.class);
                    intent.putExtra("event", Constants.LEARN_TAG);
                    intent.putExtra("groupName", strGroup);
                    intent.putExtra("userName", strUsername);
                    intent.putExtra("serverName", strServer);
                    intent.putExtra("locationName", sharedPreferences.getString(Constants.LOCATION_NAME, ""));
                    intent.putExtra("hashMap", hashMap);
                    mContext.startService(intent);

                } else if (Build.VERSION.SDK_INT < 23) {
                    if (FindUtils.isWiFiAvailable(mContext)) {
                        Intent intent = new Intent(mContext, WifiIntentReceiver.class);
                        intent.putExtra("event", Constants.LEARN_TAG);
                        intent.putExtra("groupName", strGroup);
                        intent.putExtra("userName", strUsername);
                        intent.putExtra("serverName", strServer);
                        intent.putExtra("locationName", sharedPreferences.getString(Constants.LOCATION_NAME, ""));
                        intent.putExtra("hashMap", hashMap);
                        mContext.startService(intent);
                    }
                } else {
                    return;
                }
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        // Update your UI or do any Post job after the time consuming task
                        updateUI(789);
                        // remember to dismiss the progress dialog on UI thread
                    }
                });
            }

//            handler.postDelayed(runnableCode, learnIntervalVal * 1000);
//            handler.postDelayed(runnableCode, Constants.HOW_MANY_SCAN * 1000);
            //ATTENTION: this commented for altbeacon usage.
        }
    };

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
        try {
            mListener = (OnFragmentInteractionListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        /*if (mGatt == null) {
            return;
        }
        mGatt.close();
        mGatt = null;*/
        super.onDetach();
        if (progress.isShowing())
            progress.dismiss();
        handler.removeCallbacks(runnableCode);
        mListener = null;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        log("learn visibility: " + String.valueOf(isVisibleToUser));
        if (isVisibleToUser) {
            /*if (!EventBus.getDefault().isRegistered(this)) {
                EventBus.getDefault().register(this);
                log("event bus has been registered");
            }*/
            if (getActivity() != null) {
                /*if (!isLearnTimerOn)
                    changeTimerState();*/
            }
        } else {
            /*if (EventBus.getDefault().isRegistered(this))
                EventBus.getDefault().unregister(this);*/
            /*if (getActivity() != null) {
                if (isLearnTimerOn)
                    changeTimerState();
            }*/
        }
    }

    private void logMeToast(String message) {
        Log.e(TAG, message);
        Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
    }

    private void toast(String s) {
        Toast.makeText(getActivity(), s, Toast.LENGTH_LONG).show();
    }

    /**
     * @param leanCounter
     * if learnCounter is greater than 0 it will update amount of msgSentCounter in Learning Progress
     * else it will finish progress
     */
    public static void updateUI(int leanCounter) {
        handler.post(new Runnable() {
        @Override
            public void run() {
            if (leanCounter > 0)
                progress.setMessage(dialogMsg + String.valueOf(leanCounter));
            else
                if (progress.isShowing())
                    progress.dismiss();
            }
        });
    }

    private void log(String message) {
        Log.d(TAG, message);
    }


    public class WebAppInterface {
        Context mContext;

        WebAppInterface(Context c) {
            mContext = c;
        }

        @JavascriptInterface
        public void sendToAndroid(String text) {
            //TODO  save text
//            if (serverIsReachable()){
            if (true) {
                strLocationName = text;
                InternalDataBase internalDataBase = new InternalDataBase(getActivity());
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(Constants.LOCATION_NAME, strLocationName);
                editor.apply();
                WifiObject wifi = new WifiObject(strLocationName, strGroup, strUsername);

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        progress.setTitle("Learning");
                        progress.setMessage(dialogMsg + String.valueOf(msgSentCounter));
                        progress.setCanceledOnTouchOutside(false);
                        progress.setIndeterminate(true);
                        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                        progress.show();
                    }
                });

                insertIntoList(wifi);
                internalDataBase.addEvent(new Event(strLocationName, strGroup, strUsername));
//                handler.post(runnableCode);
                Intent intent = new Intent(getActivity(), LearnIntentService.class);
                intent.putExtra("location", strLocationName);
                mContext.startService(intent);
                isLearningAllowed = true;
                Toast.makeText(mContext, text, Toast.LENGTH_SHORT).show();
            } else {
                alertDialog = new AlertDialog.Builder(getActivity()).create();
                alertDialog.setTitle("OooPs");
                alertDialog.setMessage("server is not reachable");
                alertDialog.show();
            }
        }

        @JavascriptInterface
        public String getFromAndroid() {
            return "This is from android.";
        }

        @JavascriptInterface
        public void startMap() {
            Intent mIntent = new Intent();
            ComponentName component = new ComponentName(
                    "com.google.android.apps.maps",
                    "com.google.android.maps.MapsActivity");
            mIntent.setComponent(component);
            startActivity(mIntent);
        }
    }


}