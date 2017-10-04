package com.parsin.bletool.internal;


public class Constants {

    public static int DEFAULT_BAR = 60;
    private Constants() {
    }
//    public static double[] weightArr = {0.1995, 0.1760, 0.1210, 0.0648, 0.027, 0.005};
    public static double[] weightArr = {0.1995, 0.1760, 0.1210};
    private static final String PACKAGE_NAME = "om.find.wifitool";

    // Shared prefs
    public static final String PREFS_NAME = PACKAGE_NAME + "com.find.wifitool.Prefs";

    public static final String USER_NAME = PACKAGE_NAME + "user";
    public static final String GROUP_NAME = PACKAGE_NAME + "group";
    public static final String SERVER_NAME = PACKAGE_NAME + "server";
    public static final String LOCATION_NAME = PACKAGE_NAME + "location";
    public static final String TRACK_INTERVAL = PACKAGE_NAME + "trackInterval";
    public static final String LEARN_INTERVAL = PACKAGE_NAME + "learnInterval";
    public static final String LEARN_PERIOD = PACKAGE_NAME + "learnPeriod";
    public static final String IS_FIRST_RUN = PACKAGE_NAME + "isFirstRun";
    public static final String TrackCounterName = PACKAGE_NAME + "trackCounter";
    public static final String ONE_SCAN_PERIOD_NAME = PACKAGE_NAME + "oneScanPeriod";
    public static final String HOW_MANY_SCAN_NAME = PACKAGE_NAME + "howManyScan";
    public static final String HOW_MANY_LEARNING_NAME = PACKAGE_NAME + "howManyLearning";
    public static final String SEND_PAYLOAD_PERIOD_NAME = PACKAGE_NAME + "SendPayloadPeriod";
    //Default values
    public static final String DEFAULT_USERNAME = "hadi";

    public static String DEFAULT_GROUP =  "arman3";
    public static String DEFAULT_SERVER = "http://104.237.255.199:18003/";
    public static String DEFAULT_LOCATION_NAME = "location";
    public static int DEFAULT_TRACKING_INTERVAL = 3;
    public static int DEFAULT_LEARNING_INTERVAL = 3;
    public static int DEFAULT_TRACKING_COUNTER = 30;
    public static int DEFAULT_LEARNING_PERIOD =5;
    public static int ALT_BEACON_TRACK_INTERVAL_AMOUNT = 10;


    public static int ONE_SCAN_PERIOD = 300;
    public static int HOW_MANY_SCAN = 10;
    public static int HOW_MANY_LEARNING = 5;
    public static int SEND_PAYLOAD_PERIOD = 3000;

    // BRaodcast message tag
    public static final String TRACK_BCAST = "com.find.wifitool.track";

    public static final String TRACK_TAG = "track";
    public static final String LEARN_TAG = "learn";

    // Web URLs
    public static final String FIND_GITHUB_URL = "https://github.com/schollz/find";
    public static final String FIND_APP_URL = " https://github.com/uncleashi/find-client-android";
    public static final String FIND_WEB_URL = "https://www.internalpositioning.com/";
    public static final String FIND_ISSUES_URL = "https://github.com/schollz/find/issues";

}
