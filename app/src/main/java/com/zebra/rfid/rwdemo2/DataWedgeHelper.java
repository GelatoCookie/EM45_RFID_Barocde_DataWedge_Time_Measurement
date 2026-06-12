package com.zebra.rfid.rwdemo2;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

public class DataWedgeHelper {
    private final Context context;

    public DataWedgeHelper(Context context) {
        this.context = context;
    }

    public void sendDataWedgeIntent(String action, Bundle extras) {
        Intent intent = DataWedgeSupport.createDataWedgeIntent(action, extras);
        try {
            context.sendBroadcast(intent);
        } catch (Exception e) {
            Toast.makeText(context, "Failed to send DataWedge intent: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    public void sendProfileConfig(Bundle setConfigBundle) {
        sendDataWedgeIntent(RWDemoIntentParams.ACTION, DataWedgeSupport.createSetConfigIntent(setConfigBundle).getExtras());
    }

    public void sendSoftRfidTrigger(boolean start) {
        sendDataWedgeIntent(RWDemoIntentParams.ACTION, DataWedgeSupport.createSoftTriggerIntent(RWDemoIntentParams.ACTION_EXTRA_SOFT_RFID_TRIGGER, start ? "START_SCANNING" : "STOP_SCANNING").getExtras());
    }

    public void sendSoftBarcodeTrigger(boolean start) {
        sendDataWedgeIntent(RWDemoIntentParams.ACTION, DataWedgeSupport.createSoftTriggerIntent(RWDemoIntentParams.ACTION_EXTRA_SOFT_SCAN_TRIGGER, start ? "START_SCANNING" : "STOP_SCANNING").getExtras());
    }

    public void getAvailableProfiles() {
        Bundle extras = new Bundle();
        extras.putString(RWDemoIntentParams.GET_AVAILABLE_PROFILE_LIST, "");
        sendDataWedgeIntent(RWDemoIntentParams.ACTION, extras);
    }

    public void showError(String message) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }
}
