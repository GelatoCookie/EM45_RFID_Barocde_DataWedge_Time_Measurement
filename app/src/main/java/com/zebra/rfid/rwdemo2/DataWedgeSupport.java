package com.zebra.rfid.rwdemo2;

import android.content.Intent;
import android.os.Bundle;

final class DataWedgeSupport {
    static final String SOURCE_SCANNER = "scanner";
    static final String SOURCE_MSR = "msr";
    static final String DATA_STRING_TAG = "com.symbol.datawedge.data_string";
    static final String SOURCE_TAG = "com.symbol.datawedge.source";
    static final String MSR_DATA_TAG = "com.symbol.datawedge.msr_data";
    static final String DATA_MSR = "[encrypted MSR data]";

    enum UiTone {
        GREEN,
        BLUE,
        RED
    }

    static final class StatusUiState {
        final String displayStatus;
        final UiTone tone;
        final boolean rfidScanActive;
        final boolean barcodeScanActive;
        final boolean dismissProgress;

        StatusUiState(String displayStatus, UiTone tone, boolean rfidScanActive, boolean barcodeScanActive, boolean dismissProgress) {
            this.displayStatus = displayStatus;
            this.tone = tone;
            this.rfidScanActive = rfidScanActive;
            this.barcodeScanActive = barcodeScanActive;
            this.dismissProgress = dismissProgress;
        }
    }

    static final class DecodedData {
        final String data;
        final String source;
        final String labelType;

        DecodedData(String data, String source, String labelType) {
            this.data = data;
            this.source = source;
            this.labelType = labelType;
        }
    }

    private DataWedgeSupport() {
    }

    static Intent createDataWedgeIntent(String action, Bundle extras) {
        Intent intent = new Intent();
        intent.setAction(action);
        if (extras != null) {
            intent.putExtras(extras);
        }
        return intent;
    }

    static Intent createSetConfigIntent(Bundle setConfigBundle) {
        Bundle extras = new Bundle();
        extras.putBundle(RWDemoIntentParams.ACTION_EXTRA_SET_CONFIG, setConfigBundle);
        extras.putString(RWDemoIntentParams.EXTRA_SEND_RESULT, "true");
        extras.putString(RWDemoIntentParams.EXTRA_COMMAND_IDENTIFIER, "RFID_CONFIG");
        return createDataWedgeIntent(RWDemoIntentParams.ACTION, extras);
    }

    static Intent createSoftTriggerIntent(String triggerExtra, String command) {
        Bundle extras = new Bundle();
        extras.putString(triggerExtra, command);
        return createDataWedgeIntent(RWDemoIntentParams.ACTION, extras);
    }

    static DecodedData decode(Intent intent) {
        if (intent == null) {
            return new DecodedData(null, SOURCE_SCANNER, null);
        }

        String data = intent.getStringExtra(DATA_STRING_TAG);
        String source = intent.getStringExtra(SOURCE_TAG);
        String labelType = intent.getStringExtra("com.symbol.datawedge.label_type");
        if (source == null) {
            source = SOURCE_SCANNER;
        }

        if ((data == null || data.isEmpty()) && SOURCE_MSR.equalsIgnoreCase(source)) {
            byte[] rawData = intent.getByteArrayExtra(MSR_DATA_TAG);
            if (rawData != null) {
                data = DATA_MSR;
            }
        }

        return new DecodedData(data, source, labelType);
    }

    static StatusUiState resolveStatus(String target, String status, String readingText, String stoppedText) {
        if (target == null || status == null) {
            return new StatusUiState(null, UiTone.RED, false, false, false);
        }

        if (RWDemoIntentParams.STATUS_ACTIVATED.equalsIgnoreCase(status)) {
            return new StatusUiState(status, UiTone.BLUE, false, false, false);
        }

        boolean isScannerTarget = target.equalsIgnoreCase(SOURCE_SCANNER);
        boolean isScanning = RWDemoIntentParams.STATUS_SCANNING.equalsIgnoreCase(status);
        boolean isWaiting = RWDemoIntentParams.STATUS_WAITING.equalsIgnoreCase(status);
        boolean isConnected = RWDemoIntentParams.STATUS_CONNECTED.equalsIgnoreCase(status);

        String displayStatus = status;
        if (isScanning) {
            displayStatus = readingText;
        } else if (isWaiting) {
            displayStatus = stoppedText;
        }

        UiTone tone = (isWaiting || isConnected || isScanning || stoppedText.equalsIgnoreCase(displayStatus)) ? UiTone.GREEN : UiTone.RED;
        boolean isActive = isScanning;
        boolean dismissProgress = isWaiting || "IDLE".equalsIgnoreCase(status);

        if (isScannerTarget) {
            return new StatusUiState(displayStatus, tone, false, isActive, dismissProgress);
        }

        return new StatusUiState(displayStatus, tone, isActive, false, dismissProgress);
    }
}
