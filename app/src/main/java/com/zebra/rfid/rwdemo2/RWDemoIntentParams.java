/**
 * Copyright (C) 2019 Zebra Technologies Corporation and/or its affiliates.
 * All rights reserved.
 */
package com.zebra.rfid.rwdemo2;

/**
 * RWDemoIntentParams is class made to hold the
 * intent API parameter values of the DataWedge application
 */
public class RWDemoIntentParams {

    public static final String ACTION_EXTRA_SOFT_RFID_TRIGGER = "com.symbol.datawedge.api.SOFT_RFID_TRIGGER";
    public static final String ACTION_EXTRA_SOFT_SCAN_TRIGGER = "com.symbol.datawedge.api.SOFT_SCAN_TRIGGER";

    public static final String ACTION  = "com.symbol.datawedge.api.ACTION";
    public static final String RESULT_ACTION  = "com.symbol.datawedge.api.RESULT_ACTION";
    public static final String ACTION_EXTRA_SET_CONFIG  = "com.symbol.datawedge.api.SET_CONFIG";
    public static final String ACTION_EXTRA_GET_CONFIG  = "com.symbol.datawedge.api.GET_CONFIG";

    // Profile name
    public static final String BUNDLE_EXTRA_PROFILE_NAME_KEY = "PROFILE_NAME";
    public static final String BUNDLE_EXTRA_PROFILE_NAME_VAL = "RWDemo";

    // Config mode
    public static final String BUNDLE_EXTRA_CONFIG_MODE_KEY = "CONFIG_MODE";
    public static final String BUNDLE_EXTRA_CONFIG_MODE_VAL = "CREATE_IF_NOT_EXIST";

    // Profile enable
    public static final String BUNDLE_EXTRA_PROFILE_ENABLED_KEY = "PROFILE_ENABLED";
    public static final String BUNDLE_EXTRA_PROFILE_ENABLED_VAL = "true";

    // Reset config
    public static final String BUNDLE_EXTRA_RESET_CONFIG_KEY = "RESET_CONFIG";
    public static final String BUNDLE_EXTRA_RESET_CONFIG_VAL = "false";

    // Package name
    public static final String BUNDLE_EXTRA_PACKAGE_NAME_KEY = "PACKAGE_NAME";

    // Activity List
    public static final String BUNDLE_EXTRA_ACTIVITY_LIST_KEY = "ACTIVITY_LIST";
    public static final String[] BUNDLE_EXTRA_ACTIVITY_LIST_VAL_ARR = new String[]{"*"};

    // App List
    public static final String BUNDLE_EXTRA_APP_LIST_KEY = "APP_LIST";

    // Param list
    public static final String BUNDLE_EXTRA_PARAM_LIST = "PARAM_LIST";

    // Plugin config
    public static final String BUNDLE_EXTRA_PLUGIN_CONFIG = "PLUGIN_CONFIG";

    // Plugin enable/disable
    public static final String PLUGIN_ENABLE_PARAM_KEY = "rfid_input_enabled";
    public static final String PLUGIN_ENABLE_PARAM_VAL = "true";

    // Plugin names
    public static final String BUNDLE_EXTRA_PLUGIN_NAME = "PLUGIN_NAME";

    // RFID Plugin params
    public final static String PLUGIN_NAME_RFID = "RFID";
    public final static String PLUGIN_NAME_RFID_F = "RFID_F";
    public final static String PLUGIN_RFID_F_ENABLE_PARAM = "rfidf_enabled";
    public static final String RFID_TAG_READ_DURATION_PARAM = "rfidf_tag_read_duration";

    // Barcode Plugin params
    public final static String PLUGIN_NAME_BARCODE = "BARCODE";
    public final static String BARCODE_ENABLE_PARAM_KEY = "scanner_input_enabled";

    // Output plugin params
    public final static String PLUGIN_NAME_INTENT = "INTENT";
    public static final String BUNDLE_EXTRA_OUTPUT_PLUGIN_NAME = "OUTPUT_PLUGIN_NAME";

    // Intent output enabled
    // Intent Props bundle
    public final static String INTENT_OUTPUT_ENABLED_KEY = "intent_output_enabled";
    public final static String INTENT_OUTPUT_ENABLED_VALUE = "true";

    // Keystroke plugin params
    public final static String PLUGIN_NAME_KEYSTROKE = "KEYSTROKE";

    // Keystroke output enabled
    public final static String KEYSTROKE_ENABLED_KEY = "keystroke_output_enabled";
    public final static String KEYSTROKE_ENABLED_VALUE = "false";


    // Intent action
    public final static String INTENT_ACTION_KEY = "intent_action";
    public final static String INTENT_ACTION_VALUE = "com.zebra.rfid.rwdemo.RWDEMO2";

    // Intent category
    public final static String INTENT_CATEGORY_KEY = "intent_category";
    public final static String INTENT_CATEGORY_VALUE = "android.intent.category.DEFAULT";

    // Intent delivery
    public final static String INTENT_DELIVERY_KEY = "intent_delivery";
    public final static String INTENT_DELIVERY_VALUE = "0";

    // Get available profile list keys
    public final static String GET_AVAILABLE_PROFILE_LIST = "com.symbol.datawedge.api.GET_PROFILES_LIST";
    public static final String GET_AVAILABLE_PROFILE_LIST_RESULT = "com.symbol.datawedge.api.RESULT_GET_PROFILES_LIST";
    public static final String GET_ACTIVE_PROFILE = "com.symbol.datawedge.api.GET_ACTIVE_PROFILE";
    public static final String RESULT_GET_ACTIVE_PROFILE = "com.symbol.datawedge.api.RESULT_GET_ACTIVE_PROFILE";

    // Notification API
    public static final String ACTION_EXTRA_REGISTER_FOR_NOTIFICATION = "com.symbol.datawedge.api.REGISTER_FOR_NOTIFICATION";
    public static final String ACTION_EXTRA_UNREGISTER_FOR_NOTIFICATION = "com.symbol.datawedge.api.UNREGISTER_FOR_NOTIFICATION";
    public static final String NOTIFICATION_ACTION = "com.symbol.datawedge.api.NOTIFICATION_ACTION";
    public static final String NOTIFICATION_BUNDLE = "com.symbol.datawedge.api.NOTIFICATION_BUNDLE";

    // Registration Bundle Keys
    public static final String BUNDLE_EXTRA_APPLICATION_NAME = "com.symbol.datawedge.api.APPLICATION_NAME";
    public static final String BUNDLE_EXTRA_NOTIFICATION_TYPE = "com.symbol.datawedge.api.NOTIFICATION_TYPE";

    // Notification Result Keys (inside the bundle)
    public static final String KEY_NOTIFICATION_TYPE = "NOTIFICATION_TYPE";
    public static final String KEY_NOTIFICATION_STATUS = "STATUS";

    public static final String NOTIFICATION_TYPE_SCANNER_STATUS = "SCANNER_STATUS";
    public static final String NOTIFICATION_TYPE_RFID_STATUS = "RFID_STATUS";

    // Query Actions
    public static final String ACTION_EXTRA_GET_SCANNER_STATUS = "com.symbol.datawedge.api.GET_SCANNER_STATUS";
    public static final String ACTION_EXTRA_GET_RFID_STATUS = "com.symbol.datawedge.api.GET_RFID_STATUS";

    // Result Actions Extras
    public static final String EXTRA_SEND_RESULT = "SEND_RESULT";
    public static final String EXTRA_COMMAND_IDENTIFIER = "COMMAND_IDENTIFIER";
    public static final String EXTRA_RESULT = "RESULT";
    public static final String EXTRA_RESULT_INFO = "RESULT_INFO";

    // Query Result Extras
    public static final String RESULT_SCANNER_STATUS = "com.symbol.datawedge.api.RESULT_SCANNER_STATUS";
    public static final String RESULT_RFID_STATUS = "com.symbol.datawedge.api.RESULT_RFID_STATUS";

    // Status Values
    public static final String STATUS_WAITING = "WAITING";
    public static final String STATUS_SCANNING = "SCANNING";
    public static final String STATUS_DISABLED = "DISABLED";
    public static final String STATUS_CONNECTED = "CONNECTED";
    public static final String STATUS_DISCONNECTED = "DISCONNECTED";
    public static final String STATUS_ACTIVATED = "Activated";

    // RFID configuration
    public final static String INTENT_KEY_SESSION = "rfid_session";
    public final static String INTENT_KEY_POWER_LEVEL = "rfid_antenna_transmit_power";
    public final static String INTENT_KEY_RFID_LINK_PROFILE_PARAM = "rfid_link_profile";
    public final static String INTENT_KEY_RFID_DYNAMIC_POWER_MODE_ENABLED_PARAM = "rfid_dynamic_power_mode";


}
