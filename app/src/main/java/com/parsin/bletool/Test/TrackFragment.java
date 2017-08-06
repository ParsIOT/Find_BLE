package com.parsin.bletool.Test;

/**
 * Created by root on 4/12/17.
 */

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.parsin.bletool.Controller.OnFragmentInteractionListener;
import com.parsin.bletool.R;
import com.parsin.bletool.View.Fragment.SettingsFragment;
import com.parsin.bletool.internal.Constants;
import com.parsin.bletool.internal.FindUtils;
import com.parsin.bletool.internal.wifi.WifiIntentReceiver;

import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Response;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;

import org.json.JSONObject;
import org.json.JSONArray;

import java.io.IOException;

public class TrackFragment extends Fragment {

    private static final String TAG = TrackFragment.class.getSimpleName();

    //   TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    String urlAdvServer = "http://192.168.1.120:8000/fa/booth/advertisementJson";
    private Handler advrJSONreq;
    private String resJSONreq;
    private String advrName;
    private String advrText;
    private float sectionDimensions[][];

    private String mParam1;
    private String mParam2;

    //private variables
    private OnFragmentInteractionListener mListener;
    private Context mContext;
    private SharedPreferences sharedPreferences;
    private String strUsername;
    private String strServer;
    private String strGroup;
    private String strPreLocation = "";  // We don't need any location value fr Tracking
    private int trackVal;

    private TextView currLocView;
    private WebView mWebView;
    private Button mButton;

    private String lastLoc = "0";
    private Boolean getNotifFromServer = true;
    Handler handler = new Handler();

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SettingsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SettingsFragment newInstance(String param1, String param2) {
        SettingsFragment fragment = new SettingsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    // Required empty public constructor
    public TrackFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        // Checking if the Location service is enabled in case of Android M or above users
        if (!FindUtils.isLocationAvailable(mContext)) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
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

        // Getting values from Shared prefs for Tracking
        sharedPreferences = getActivity().getSharedPreferences(Constants.PREFS_NAME, 0);
        strGroup = sharedPreferences.getString(Constants.GROUP_NAME, Constants.DEFAULT_GROUP);
        strUsername = sharedPreferences.getString(Constants.USER_NAME, Constants.DEFAULT_USERNAME);
        strServer = sharedPreferences.getString(Constants.SERVER_NAME, Constants.DEFAULT_SERVER);
        trackVal = sharedPreferences.getInt(Constants.TRACK_INTERVAL, Constants.DEFAULT_TRACKING_INTERVAL);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_track, container, false);
        currLocView = (TextView) rootView.findViewById(R.id.labelLocationName);


        mWebView = (WebView) rootView.findViewById(R.id.web_view);
        WebSettings mWebSettings = mWebView.getSettings();
        mWebSettings.setJavaScriptEnabled(true);
        mWebView.addJavascriptInterface(new WebAppInterface(getActivity()), "Android");
        mWebView.loadUrl("file:///android_asset/leaflet/test-map3.html");
        mButton = (Button) rootView.findViewById(R.id.button);
        mButton.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                //mWebView.loadUrl("javascript:update_map()");
                Toast.makeText(getActivity(), "Long Click to Toggle\nWebView & TextView", Toast.LENGTH_SHORT).show();
            }
        });
        mButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (mWebView.getVisibility() == View.GONE) {
                    mWebView.setVisibility(View.VISIBLE);
                } else mWebView.setVisibility(View.GONE);

                return false;
            }
        });


//        handler.post(runnableCode);

        // Listener to the broadcast message from WifiIntent
//        LocalBroadcastManager.getInstance(mContext).registerReceiver(mMessageReceiver,
//                new IntentFilter(Constants.TRACK_BCAST));


        // Inflate the layout for this fragment
        return rootView;
    }
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (!isVisibleToUser) {
            super.onDetach();
            try {
                handler.removeCallbacks(runnableCode);
                LocalBroadcastManager.getInstance(mContext).unregisterReceiver(mMessageReceiver);
                getNotifFromServer = true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            handler.post(runnableCode);
            LocalBroadcastManager.getInstance(mContext).registerReceiver(mMessageReceiver,
                    new IntentFilter(Constants.TRACK_BCAST));

        }
    }

    // Getting the CurrentLocation from the received braodcast
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("tracking:", "New location.");
            String currLocation = intent.getStringExtra("location");

            currLocView.setTextColor(getResources().getColor(R.color.currentLocationColor));
            currLocView.setText(currLocation);

            Log.d("your location:", currLocation);


            if (getNotifFromServer) {
                getAdvJSON(currLocation);

//                advr.name  ---> advrName
//                advr.text  ---> advrText
//                advr.locations --> sectionDimensions  //each location has 4 numbers that show 2 corners of the square
//                x_y = getIntXY(currLocation);
//                if (inSquare(x_Y, advr.locations)) {
//                    notifyThis(advr.name, advr.text);
//                }
                getNotifFromServer = false;
            }
//            getIntXY(currLocation);
            getIntXY(currLocation);
            if (resJSONreq != "") {
                Log.d("resJSONreqFull", "resJSONreqx");
                float x_y[] = getIntXY(currLocation);
                if (inSquare(x_y, sectionDimensions)) {
                    notifyThis(advrName, advrText);
                }
            }


            //Log.e("TRACK", currLocation);
            if (!strPreLocation.equals(currLocation)) {
                String js_location = String.format("javascript:update_map(\'%s\')", currLocation);
                mWebView.loadUrl(js_location);
            }

            strPreLocation = currLocation;


        }
    };

    // Timers to keep track of our Tracking period
    private Runnable runnableCode = new Runnable() {
        @Override
        public void run() {
            if (Build.VERSION.SDK_INT >= 23) {
                if (FindUtils.isWiFiAvailable(mContext) && FindUtils.hasAnyLocationPermission(mContext)) {
                    Intent intent = new Intent(mContext, WifiIntentReceiver.class);
                    intent.putExtra("event", Constants.TRACK_TAG);
                    intent.putExtra("groupName", strGroup);
                    intent.putExtra("userName", strUsername);
                    intent.putExtra("serverName", strServer);
                    intent.putExtra("locationName", sharedPreferences.getString(Constants.LOCATION_NAME, ""));
                    mContext.startService(intent);
                }
            } else if (Build.VERSION.SDK_INT < 23) {
                if (FindUtils.isWiFiAvailable(mContext)) {
                    Intent intent = new Intent(mContext, WifiIntentReceiver.class);
                    intent.putExtra("event", Constants.TRACK_TAG);
                    intent.putExtra("groupName", strGroup);
                    intent.putExtra("userName", strUsername);
                    intent.putExtra("serverName", strServer);
                    intent.putExtra("locationName", sharedPreferences.getString(Constants.LOCATION_NAME, ""));
                    mContext.startService(intent);
                }
            } else {
                return;
            }
            handler.postDelayed(runnableCode, trackVal * 1000);
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
        super.onDetach();
        handler.removeCallbacks(runnableCode);
        LocalBroadcastManager.getInstance(mContext).unregisterReceiver(mMessageReceiver);
        getNotifFromServer = true;
        mListener = null;
    }
    // Logging message in form of Toasts
    private void logMeToast(String message) {
        Log.d(TAG, message);
        toast(message);
    }
    private void toast(String s) {
        Toast.makeText(getActivity(), s, Toast.LENGTH_LONG).show();
    }
    public class WebAppInterface {
        Context mContext;

        WebAppInterface(Context c) {
            mContext = c;
        }

        @JavascriptInterface
        public void sendToAndroid(String text) {
            //TODO  save text
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
    public void notifyThis(String title, String message) {
        NotificationCompat.Builder b = new NotificationCompat.Builder(this.getContext());
        b.setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.ic_action_map)
                .setTicker("{your tiny message}")
                .setContentTitle(title)
                .setContentText(message)
                .setContentInfo("INFO");

        NotificationManager nm = (NotificationManager) this.getContext().getSystemService(Context.NOTIFICATION_SERVICE);
        nm.notify(1, b.build());
    }


    private float[] getIntXY(String xyStr) {
        float x_y[] = new float[2];
        String x, y;
        x_y[0] = Float.parseFloat(xyStr.substring(xyStr.indexOf(",") + 1, xyStr.length()));
        x_y[1] = Float.parseFloat(xyStr.substring(0, xyStr.indexOf(",")));
        Log.d("XXXX:", xyStr.substring(xyStr.indexOf(",") + 1, xyStr.length()));
        Log.d("YYYY:", xyStr.substring(0, xyStr.indexOf(",")));
        return x_y;
    }
    private boolean inSquare(float[] x_y, float[][] sectionDimensions) {
        float dotX = x_y[0];
        float dotY = x_y[1];
        for (int i = 0; i < sectionDimensions.length; i++) {
            float cornerXY[] = sectionDimensions[i];
            if (cornerXY[0] <= dotX && dotX <= cornerXY[2] && cornerXY[1] <= dotY && dotY <= cornerXY[3]) {
                return true;
            }
        }
        return false;
    }
    public void getAdvJSON(String currLocation) {
        resJSONreq = "";
        OkHttpClient client = new OkHttpClient();
        advrJSONreq = new Handler(Looper.getMainLooper());
        // GET request
        Request request = new Request.Builder()
                .url(urlAdvServer)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                final String mMessage = request.toString();
                Log.e("resJSONError1", mMessage);

            }

            @Override
            public void onResponse(Response response) throws IOException {
                final String mMessage = response.body().string();
//                Log.e("resJSONError2", mMessage);
                advrJSONreq.post(new Runnable() {
                    @Override
                    public void run() {
                        resJSONreq = mMessage; // must be inside run()
                        Log.d("resJSON", mMessage);
                        Log.d("resJSONParser:", resJSONreq);
                        try {
                            JSONObject reader = new JSONObject(resJSONreq);
                            advrName = reader.getString("name");
                            advrText = reader.getString("text");

                            Log.d("Advertisment Name:", advrName);
                            Log.d("Advertisment Text:", advrText);

                            JSONArray advrSections = reader.getJSONArray("sections");

                            float sectionDimensions[][] = new float[advrSections.length()][4];

                            for (int i = 0; i < advrSections.length(); i++) {
                                JSONObject section = advrSections.getJSONObject(i);
                                String sectionName = section.getString("section_name");

                                float x1 = Float.parseFloat(section.getString("b_x"));
                                float y1 = Float.parseFloat(section.getString("b_y"));
                                float x2 = Float.parseFloat(section.getString("t_x"));
                                float y2 = Float.parseFloat(section.getString("t_y"));
                                sectionDimensions[i][0] = x1;
                                sectionDimensions[i][1] = y1;
                                sectionDimensions[i][2] = x2;
                                sectionDimensions[i][3] = y2;
                            }
//                            resJSONreq="";
                        } catch (Exception e) {
                            Log.e("JSONParsing", e.getMessage());
                        }
                    }
                });
            }
        });

    }
}