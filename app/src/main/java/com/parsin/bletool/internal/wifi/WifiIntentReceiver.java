package com.parsin.bletool.internal.wifi;

import android.annotation.TargetApi;
import android.app.IntentService;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.parsin.bletool.Utils.httpCalls.FindWiFi;
import com.parsin.bletool.Utils.httpCalls.FindWiFiImpl;
import com.parsin.bletool.internal.Constants;
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

    private BluetoothLeScanner mLEScanner;
    private List<ScanFilter> filters;
    private ScanSettings settings;
    private BluetoothGatt mGatt;

    private static final long SCAN_PERIOD = 1500;

    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothLeScanner mBluetoothLeScanner;
    private ScanCallback mScanCallback;
    private Handler mHandler;

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
        mHandler = new Handler();
        hashMap = new HashMap<>();


    }

    @Override
    protected void onHandleIntent(Intent intent) {
        this.intent = intent;
        bleMacs = new ArrayList<>();
        bleRssi = new ArrayList<>();
        mHandler.post(new Runnable() {
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
    private void sendPayload(String event, String serverName, JSONObject json) {
        if (event.equalsIgnoreCase("track")) {
            Callback postTrackEvent = new Callback() {
                @Override
                public void onFailure(Request request, IOException e) {
                    Log.e(TAG, "Failed request: " + request, e);
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
                }

                @Override
                public void onResponse(Response response) throws IOException {
                    String body = response.body().string();
                    Log.d(TAG, "Learning step was successful:\n" + body);
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

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void doBle() {
        mBluetoothAdapter = ((BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE))
                .getAdapter();
        mBluetoothLeScanner = mBluetoothAdapter.getBluetoothLeScanner();
        if (mScanCallback == null) {
            Log.d(TAG, "Starting Scanning");

            // Will stop the scanning after a set time.
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Log.d(TAG, "run: wanna stop callback");
                    stopScanning();
                }
            }, SCAN_PERIOD);

            // Kick off a new scan.
            mScanCallback = new SampleScanCallback();
            mBluetoothLeScanner.startScan(buildScanFilters(), buildScanSettings(), mScanCallback);
            //mBluetoothLeScanner.startScan(mScanCallback); //TODO: set setting and filters.

        } else {
            //Toast.makeText(getActivity(), R.string.already_scanning, Toast.LENGTH_SHORT);
        }

    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void stopScanning() {
        Log.d(TAG, "Stopping Scanning");

        // Stop the scan, wipe the callback.
        mBluetoothLeScanner.stopScan(mScanCallback);
        sendBleToServer();
        mScanCallback = null;

        // Even if no new results, update 'last seen' times.
        //mAdapter.notifyDataSetChanged();
    }

    /**
     * Return a List of {@link ScanFilter} objects to filter by Service UUID.
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private List<ScanFilter> buildScanFilters() {
        List<ScanFilter> scanFilters = new ArrayList<>();

        ScanFilter.Builder builder = new ScanFilter.Builder();
        // Comment out the below line to see all BLE devices around you
        //builder.setServiceUuid(Constants.Service_UUID);
        scanFilters.add(builder.build());

        return scanFilters;
    }

    /**
     * Return a {@link ScanSettings} object set to use low power (to preserve battery life).
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private ScanSettings buildScanSettings() {
        ScanSettings.Builder builder = new ScanSettings.Builder();
        builder.setScanMode(ScanSettings.SCAN_MODE_BALANCED)
                .setReportDelay(0);
        return builder.build();
    }



//    private void scanLeDevice(final boolean enable) {
//        if (enable) {
//            mHandler.postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    if (Build.VERSION.SDK_INT < 21) {
//                        mBluetoothAdapter.stopLeScan(mLeScanCallback);
//                    } else {
//                        mLEScanner.stopScan(mScanCallback);
//                    }
//
//                    sendBleToServer();
//                    scanLeDevice(false);
//                    if (mGatt != null) {
//                        mGatt.close();
//                        mGatt = null;
//                    }
//
//
//                }
//            }, SCAN_PERIOD);
//            if (Build.VERSION.SDK_INT < 21) {
//                mBluetoothAdapter.startLeScan(mLeScanCallback);
//            } else {
//                mLEScanner.startScan(filters, settings, mScanCallback);
//            }
//        } else {
//            if (Build.VERSION.SDK_INT < 21) {
//                mBluetoothAdapter.stopLeScan(mLeScanCallback);
//            } else {
//                mLEScanner.stopScan(mScanCallback);
//            }
//        }
//    }

//    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
//    private ScanCallback mScanCallback = new ScanCallback() {
//        @Override
//        public void onScanResult(int callbackType, android.bluetooth.le.ScanResult result) {
//            Log.i("callbackType", String.valueOf(callbackType));
//            Log.i("result", result.toString());
//            BluetoothDevice btDevice = result.getDevice();
//            ScanRecord scanRecord = result.getScanRecord();
//
//            Log.e(TAG, "onScanResult: " + "name: " + btDevice.getName() + " rssi: " + String.valueOf(result.getRssi()) + " mac: " + btDevice.getAddress());
//            if (!bleMacs.contains(btDevice.getAddress())) {
//                bleMacs.add(btDevice.getAddress());
//                bleRssi.add(result.getRssi());
//            } else {
//                int idx = bleMacs.indexOf(btDevice.getAddress());
//                bleRssi.set(idx, (bleRssi.get(idx) + result.getRssi()) / 2);
//            }
//            connectToDevice(btDevice);
//        }
//
//        @Override
//        public void onBatchScanResults(List<android.bluetooth.le.ScanResult> results) {
//            for (android.bluetooth.le.ScanResult sr : results) {
//                Log.i("ScanResult - Results", sr.toString());
//            }
//        }
//
//        @Override
//        public void onScanFailed(int errorCode) {
//            Log.e("Scan Failed", "Error Code: " + errorCode);
//        }
//    };

//    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
//    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
//                @Override
//                public void onLeScan(final BluetoothDevice device, int rssi,
//                                     byte[] scanRecord) {
//
//                    // runOnUiThread
//
//                    Log.i("onLeScan", device.toString());
//
//                    connectToDevice(device);
//                }
//
//
//            };

//    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
//    public void connectToDevice(BluetoothDevice device) {
//        if (mGatt == null) {
//            mGatt = device.connectGatt(this, false, gattCallback);
//            mGatt.requestConnectionPriority(BluetoothGatt.CONNECTION_PRIORITY_HIGH);
//            //scanLeDevice(false);// will stop after first device detection
//            scanLeDevice(true);
//        }
//    }

//    private final BluetoothGattCallback gattCallback = new BluetoothGattCallback() {
//        @Override
//        public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
//            Log.e("onReadRemoteRssi", gatt.getDevice().getName() + " " + rssi);
//        }
//
//        @Override
//        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
//            Log.i("onConnectionStateChange", "Status: " + status);
//            /*switch (newState) {
//                case BluetoothProfile.STATE_CONNECTED:
//                    Log.i("gattCallback", "STATE_CONNECTED");
//                    gatt.discoverServices();
//                    break;
//                case BluetoothProfile.STATE_DISCONNECTED:
//                    Log.e("gattCallback", "STATE_DISCONNECTED");
//                    break;
//                default:
//                    Log.e("gattCallback", "STATE_OTHER");
//            }*/
//
//        }
//
//        @Override
//        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
//            /*Log.e("BluetoothLeService", "onServicesDiscovered()");
//            if (status == BluetoothGatt.GATT_SUCCESS) {
//
//
//                List<BluetoothGattService> gattServices = gatt.getServices();
//                Log.e("onServicesDiscovered", "Services count: "+gattServices.size());
//
//                for (BluetoothGattService gattService : gattServices) {
//                    String serviceUUID = gattService.getUuid().toString();
//                    Log.w("onServicesDiscovered", "Service uuid "+serviceUUID + " service characteristic " + gattService.getCharacteristic(gattService.getUuid()));
//                }
//
//            } else {
//                Log.w(BleActivity.class.getSimpleName(), "onServicesDiscovered received: " + status);
//            }
//            try {
//                List<BluetoothGattService> services = gatt.getServices();
//                Log.i("onServicesDiscovered", services.toString());
//                gatt.readCharacteristic(services.get(1).getCharacteristics().get(0));
//            }catch (IndexOutOfBoundsException e){
//                e.printStackTrace();
//            }*/
//        }
//
//        @Override
//        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
//            Log.i("onCharacteristicRead", characteristic.toString());
//            gatt.disconnect();
//        }
//    };


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



            for (String s: hashMap.keySet()){
                Log.d(TAG, "sendBleToServer() called " + s + " : " + Arrays.toString(hashMap.get(s).toArray()));

                String mac = s.substring(s.indexOf(" "));

                int rssi = 0;
                for (int r : hashMap.get(s)) rssi = rssi + r;
                rssi = rssi / hashMap.get(s).size();

                wifiResults = new JSONObject();
                wifiResults.put("mac", mac);
                wifiResults.put("rssi", rssi);

                wifiResultsArray.put(wifiResults);
            }
            Log.d(TAG, "sendBleToServer() called //////****  ****/////\n");

/*            for (int i = 0; i < bleMacs.size(); i++) {
                wifiResults = new JSONObject();
//                if (shouldLog(result)){
//                Log.d("learning :", "Name=" + result.SSID + "  Mac=" + result.BSSID + "  RSSI=" + result.level);

                wifiResults.put("mac", bleMacs.get(i));
                wifiResults.put("rssi", bleRssi.get(i));
                wifiResultsArray.put(wifiResults);
                //}

            }*/


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

        sendPayload(eventName, serverName, wifiFingerprint);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private class SampleScanCallback extends ScanCallback {

        @Override
        public void onBatchScanResults(List<android.bluetooth.le.ScanResult> results) {
            super.onBatchScanResults(results);

            for (android.bluetooth.le.ScanResult result : results) {
                //mAdapter.add(result);
                //Log.d(TAG, "onBatchScanResults() called with: results = [" + result.getDevice().getName() + " : " + result.getRssi() + "]");

            }
            //mAdapter.notifyDataSetChanged();
        }

        @Override
        public void onScanResult(int callbackType, android.bluetooth.le.ScanResult result) {
            super.onScanResult(callbackType, result);
            //Log.d(TAG, "onScanResult() called with: result = [" + result.getDevice().getName() + " : " + result.getRssi() + "]");
            String key = result.getDevice().getName() + " " + result.getDevice().getAddress();
            if (!hashMap.containsKey(key)){
                ArrayList<Integer> value = new ArrayList<>();
                value.add(result.getRssi());
                hashMap.put(key, value);
            }else {
                ArrayList<Integer> value = new ArrayList<>();
                value.addAll(hashMap.get(key));
                value.add(result.getRssi());
                hashMap.put(key, value);

            }
            //mAdapter.add(result);
            //mAdapter.notifyDataSetChanged();
        }

        @Override
        public void onScanFailed(int errorCode) {
            super.onScanFailed(errorCode);
            Toast.makeText(WifiIntentReceiver.this, "Scan failed with error: " + errorCode, Toast.LENGTH_LONG)
                    .show();
        }
    }


}