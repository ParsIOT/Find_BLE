package com.find.wifitool.internal;


public class Constants {

    public static int DEFAULT_BAR = 60;

    private Constants() {
    }

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

    //Default values
    public static final String DEFAULT_USERNAME = "hadi";
    public static final  String DEFAULT_GROUP =  "arman3";
    public static String DEFAULT_SERVER = "http://192.168.88.254:8003/";
    public static final String DEFAULT_LOCATION_NAME = "location";
    public static final int DEFAULT_TRACKING_INTERVAL = 3;
    public static final int DEFAULT_LEARNING_INTERVAL = 3;
    public static final int DEFAULT_LEARNING_PERIOD =5;

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
