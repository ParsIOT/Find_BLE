package com.find.wifitool.internal.wifi;

import android.app.IntentService;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.find.wifitool.Utils.Utils;
import com.find.wifitool.Utils.httpCalls.FindWiFi;
import com.find.wifitool.Utils.httpCalls.FindWiFiImpl;
import com.find.wifitool.internal.Constants;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class WifiIntentReceiver extends IntentService {

    private BluetoothAdapter mBluetoothAdapter;
    private Handler mHandler;
    private static final long SCAN_PERIOD = 3000;
    private BluetoothLeScanner mLEScanner;
    private List<ScanFilter> filters;
    private ScanSettings settings;
    private BluetoothGatt mGatt;


    private static final String TAG = WifiIntentReceiver.class.getSimpleName();

    // private variables
    private WifiManager mWifiManager;
    private WifiData mWifiData;
    private FindWiFi client;
    private String eventName, userName, groupName, serverName, locationName;
    private JSONObject wifiFingerprint;
    private String currLocation;
    private Intent intent;

    private ArrayList<String> bleMacs;
    private ArrayList<Integer> bleRssi;
    private HashMap<String, ArrayList<Integer>> hashMap;


    private Handler handler;


    private static final Set<Character> AD_HOC_HEX_VALUES =
            new HashSet<Character>(Arrays.asList('2', '6', 'a', 'e', 'A', 'E'));

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     */
    public WifiIntentReceiver() {
        super(TAG);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        handler = new Handler();

    }

    @Override
    protected void onHandleIntent(Intent intent) {
        this.intent = intent;
/*        bleMacs = new ArrayList<>();
        bleRssi = new ArrayList<>();*/
        hashMap = new HashMap<>();
        handler.post(new Runnable() {
            @Override
            public void run() {
                doBle();
            }
        });
        /*client = new FindWiFiImpl(getApplicationContext());

        // Getting all the value passed from previous Fragment
        eventName = intent.getStringExtra("event");
        userName = intent.getStringExtra("userName");
        groupName = intent.getStringExtra("groupName");
        serverName = intent.getStringExtra("serverName");
        locationName = intent.getStringExtra("locationName");
        Long timeStamp = System.currentTimeMillis() / 1000;

        mWifiData = new WifiData();
        mWifiManager = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
        WifiLock wifilock = mWifiManager.createWifiLock(WifiManager.WIFI_MODE_FULL, "LockTag");

        // getting all wifi APs and forming data payload
        try {

            doBle();

            JSONObject wifiResults;
            JSONArray wifiResultsArray = new JSONArray();
            //List<ScanResult> mResults = mWifiManager.getScanResults();
            //FIXME what is different bet this and """for (ScanResult result : mWifiManager.getScanResults())"""

            wifilock.acquire();

            List<ScanResult> mResults = mWifiManager.getScanResults();
            mWifiManager.startScan();

            wifilock.release();

            for (ScanResult result : mResults) {
                wifiResults = new JSONObject();
//                if (shouldLog(result)){
                Log.d("learning :", "Name=" + result.SSID + "  Mac=" + result.BSSID + "  RSSI=" + result.level);
                wifiResults.put("mac", result.BSSID);
                wifiResults.put("rssi", result.level);
                wifiResultsArray.put(wifiResults);
                //}

            }


            wifiFingerprint = new JSONObject();
            wifiFingerprint.put("group", groupName);
            wifiFingerprint.put("username", userName);
            wifiFingerprint.put("location", locationName);
            wifiFingerprint.put("time", timeStamp);
            wifiFingerprint.put("wifi-fingerprint", wifiResultsArray);
            Log.d(TAG, String.valueOf(wifiFingerprint));

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        // Send the packet to server
//        int threadId = android.os.Process.getThreadPriority(android.os.Process.myTid());
//        Log.e("Thread ID" , String.valueOf(threadId));
//        Log.e("Thread ID" , String.valueOf(threadId));
//        Log.e("Thread ID" , String.valueOf(threadId));
        wifilock.acquire();
        sendPayload(eventName, serverName, wifiFingerprint);
        wifilock.release();*/
    }

    // Function to check to check the route(learn or track) and send data to server
    private void sendPayload(String event, String serverName, JSONObject json) throws InterruptedException {
        Log.e(TAG, "sendPayload: semaphore is acquired 1");
        Utils.semaphore.acquire();
        if (event.equalsIgnoreCase("track")) {
            Callback postTrackEvent = new Callback() {
                @Override
                public void onFailure(Request request, IOException e) {
                    Log.e(TAG, "Failed request: " + request, e);
                    Log.e(TAG, "sendPayload: semaphore is released 2");
                    Utils.semaphore.release();

                }

                @Override
                public void onResponse(Response response) throws IOException {
                    String body = response.body().string();
                    if (response.isSuccessful()) {
                        Log.d(TAG, body);
                        try {
                            JSONObject json = new JSONObject(body);
                            currLocation = json.getString("location");
                        } catch (JSONException e) {
                            Log.e(TAG, "Failed to extract location from response: " + body, e);
                        }
                        sendCurrentLocation(currLocation);
                    } else {
                        Log.e(TAG, "Unsuccessful request: " + body);
                    }
                    Log.e(TAG, "sendPayload: semaphore is released 3");
                    Utils.semaphore.release();

                }
            };
            client.findTrack(postTrackEvent, serverName, json);

        } else {
            Callback postLearnEvent = new Callback() {
                @Override
                public void onFailure(Request request, IOException e) {
                    Log.e(TAG, "Failed request: " + request, e);
                    Toast.makeText(getApplicationContext(), "Can't connect to server.",
                            Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "sendPayload: semaphore is released 4");
                    Utils.semaphore.release();

                }

                @Override
                public void onResponse(Response response) throws IOException {
                    String body = response.body().string();
                    Log.d(TAG, "Learning step was successful:\n" + body);
                    Log.e(TAG, "sendPayload: semaphore is released 5");
                    Utils.semaphore.release();

                }
            };
            client.findLearn(postLearnEvent, serverName, json);

        }
    }

    // Broadcasting current location extracted from Response
    private void sendCurrentLocation(String location) {
        if (location != null && !location.isEmpty()) {
            Intent intent = new Intent(Constants.TRACK_BCAST);
            intent.putExtra("location", location);
            LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
        }
    }

    /**
     * Returns true if the given scan should be logged, or false if it is an
     * ad-hoc AP or if it is an AP that has opted out of Google's collection
     * practices.
     */
    private static boolean shouldLog(final ScanResult sr) {
        // Only apply this test if we have exactly 17 character long BSSID which should
        // be the case.
        final char secondNybble = sr.BSSID.length() == 17 ? sr.BSSID.charAt(1) : ' ';

        if (AD_HOC_HEX_VALUES.contains(secondNybble)) {
            return false;
        } else {
            return true;
        }
    }

    private void doBle() {
        mHandler = new Handler();
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, "BLE Not Supported",
                    Toast.LENGTH_SHORT).show();
        }
        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        /*if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);*/

        if (Build.VERSION.SDK_INT >= 21) {
            mLEScanner = mBluetoothAdapter.getBluetoothLeScanner();
            settings = new ScanSettings.Builder()
                    .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                    .setReportDelay(0)
                    .build();
            filters = new ArrayList<>();
        }
        scanLeDevice(true);


    }

    private void scanLeDevice(final boolean enable) {
        if (enable) {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (Build.VERSION.SDK_INT < 21) {
                        mBluetoothAdapter.stopLeScan(mLeScanCallback);
                    } else {
                        mLEScanner.stopScan(mScanCallback);
                    }

                    scanLeDevice(false);
                    sendBleToServer();
                    if (mGatt != null) {
                        mGatt.close();
                        mGatt = null;
                    }

                }
            }, SCAN_PERIOD);
            try {
                Log.e(TAG, "sendPayload: semaphore is acquired 6");
                Utils.semaphore.acquire();
                if (Build.VERSION.SDK_INT < 21) {
                    mBluetoothAdapter.startLeScan(mLeScanCallback);
                } else {
                    mLEScanner.startScan(filters, settings, mScanCallback);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } else {
            if (Build.VERSION.SDK_INT < 21) {
                mBluetoothAdapter.stopLeScan(mLeScanCallback);
            } else {
                mLEScanner.stopScan(mScanCallback);
            }
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private ScanCallback mScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, android.bluetooth.le.ScanResult result) {
            Log.e(TAG, "sendPayload: semaphore is released 7");
            Utils.semaphore.release();
            Log.i("callbackType", String.valueOf(callbackType));
            Log.i("result", result.toString());
            BluetoothDevice btDevice = result.getDevice();
            Log.e(TAG, "onScanResult: " + "name: " + btDevice.getName() + " rssi: " + String.valueOf(result.getRssi()) + " mac: " + btDevice.getAddress());
/*            if (!bleMacs.contains(btDevice.getAddress())) {
                bleMacs.add(btDevice.getAddress());
                bleRssi.add(result.getRssi());
            } else {
                int idx = bleMacs.indexOf(btDevice.getAddress());
                bleRssi.set(idx, (bleRssi.get(idx) + result.getRssi()) / 2);
            }*/
            if (hashMap.containsKey(btDevice.getAddress())) {
                hashMap.get(btDevice.getAddress()).add(result.getRssi());
            } else {
                ArrayList<Integer> newArr = new ArrayList<>();
                newArr.add(result.getRssi());
                hashMap.put(btDevice.getAddress(), newArr);
            }
//            connectToDevice(btDevice);
        }

        @Override
        public void onBatchScanResults(List<android.bluetooth.le.ScanResult> results) {
            for (android.bluetooth.le.ScanResult sr : results) {
                Log.i("ScanResult - Results", sr.toString());
            }
        }

        @Override
        public void onScanFailed(int errorCode) {
            Log.e(TAG, "sendPayload: semaphore is released 8");
            Utils.semaphore.release();
            Log.e("Scan Failed", "Error Code: " + errorCode);
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private BluetoothAdapter.LeScanCallback mLeScanCallback =
            new BluetoothAdapter.LeScanCallback() {
                @Override
                public void onLeScan(final BluetoothDevice device, int rssi,
                                     byte[] scanRecord) {

                    // runOnUiThread
                    Log.e(TAG, "sendPayload: semaphore is released 9");
                    Utils.semaphore.release();


                    Log.i("onLeScan", device.toString());

//                    connectToDevice(device);
                }


            };

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void connectToDevice(BluetoothDevice device) {
        if (mGatt == null) {
            mGatt = device.connectGatt(this, false, gattCallback);
            mGatt.requestConnectionPriority(BluetoothGatt.CONNECTION_PRIORITY_HIGH);
            //scanLeDevice(false);// will stop after first device detection
            scanLeDevice(true);
        }
    }

    private final BluetoothGattCallback gattCallback = new BluetoothGattCallback() {
        @Override
        public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
            Log.e("onReadRemoteRssi", gatt.getDevice().getName() + " " + rssi);
        }

        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            Log.i("onConnectionStateChange", "Status: " + status);
            /*switch (newState) {
                case BluetoothProfile.STATE_CONNECTED:
                    Log.i("gattCallback", "STATE_CONNECTED");
                    gatt.discoverServices();
                    break;
                case BluetoothProfile.STATE_DISCONNECTED:
                    Log.e("gattCallback", "STATE_DISCONNECTED");
                    break;
                default:
                    Log.e("gattCallback", "STATE_OTHER");
            }*/

        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            /*Log.e("BluetoothLeService", "onServicesDiscovered()");
            if (status == BluetoothGatt.GATT_SUCCESS) {


                List<BluetoothGattService> gattServices = gatt.getServices();
                Log.e("onServicesDiscovered", "Services count: "+gattServices.size());

                for (BluetoothGattService gattService : gattServices) {
                    String serviceUUID = gattService.getUuid().toString();
                    Log.w("onServicesDiscovered", "Service uuid "+serviceUUID + " service characteristic " + gattService.getCharacteristic(gattService.getUuid()));
                }

            } else {
                Log.w(BleActivity.class.getSimpleName(), "onServicesDiscovered received: " + status);
            }
            try {
                List<BluetoothGattService> services = gatt.getServices();
                Log.i("onServicesDiscovered", services.toString());
                gatt.readCharacteristic(services.get(1).getCharacteristics().get(0));
            }catch (IndexOutOfBoundsException e){
                e.printStackTrace();
            }*/
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            Log.i("onCharacteristicRead", characteristic.toString());
            gatt.disconnect();
        }
    };

    public void sendBleToServer() {

        client = new FindWiFiImpl(getApplicationContext());
        // Getting all the value passed from previous Fragment
        eventName = intent.getStringExtra("event");
        userName = intent.getStringExtra("userName");
        groupName = intent.getStringExtra("groupName");
        serverName = intent.getStringExtra("serverName");
        locationName = intent.getStringExtra("locationName");
        Long timeStamp = System.currentTimeMillis() / 1000;

        mWifiData = new WifiData();
        // getting all wifi APs and forming data payload
        try {
            JSONObject wifiResults;
            JSONArray wifiResultsArray = new JSONArray();
            //List<ScanResult> mResults = mWifiManager.getScanResults();
            //FIXME what is different bet this and """for (ScanResult result : mWifiManager.getScanResults())"""


/*            for (int i = 0; i < bleMacs.size(); i++) {
                wifiResults = new JSONObject();
//                if (shouldLog(result)){
//                Log.d("learning :", "Name=" + result.SSID + "  Mac=" + result.BSSID + "  RSSI=" + result.level);
                wifiResults.put("mac", bleMacs.get(i));
                wifiResults.put("rssi", bleRssi.get(i));
                wifiResultsArray.put(wifiResults);
                //}
            }*/


            Log.e(TAG, "///////////////////////////////////////// Logging HashMap ///////////////////////////////////////////////");

            for (String s : hashMap.keySet()) {
                String log = s + " :" + "\n";
                ArrayList array = hashMap.get(s);
                log = log + Arrays.toString(array.toArray());
                wifiResults = new JSONObject();
                int sum = 0;
                for (int i = 0; i < array.size(); i++)
                    sum = sum + (int)array.get(i);
                int avg = sum / array.size();
//                if (shouldLog(result)){
//                Log.d("learning :", "Name=" + result.SSID + "  Mac=" + result.BSSID + "  RSSI=" + result.level);
                wifiResults.put("mac", s);
                wifiResults.put("rssi", avg);
                wifiResultsArray.put(wifiResults);
                Log.e(TAG, log);
                //}
            }

            wifiFingerprint = new JSONObject();
            wifiFingerprint.put("group", groupName);
            wifiFingerprint.put("username", userName);
            wifiFingerprint.put("location", locationName);
            wifiFingerprint.put("time", timeStamp);
            wifiFingerprint.put("wifi-fingerprint", wifiResultsArray);
            Log.d(TAG, String.valueOf(wifiFingerprint));

            sendPayload(eventName, serverName, wifiFingerprint);

        } catch (Exception ex) {
            ex.printStackTrace();
        }


    }


}
