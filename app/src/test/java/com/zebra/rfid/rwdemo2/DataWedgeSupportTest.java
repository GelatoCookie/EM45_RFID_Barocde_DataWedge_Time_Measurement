package com.zebra.rfid.rwdemo2;

import android.content.Intent;
import android.os.Bundle;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

@RunWith(RobolectricTestRunner.class)
public class DataWedgeSupportTest {

    @Test
    public void createSetConfigIntent_buildsExpectedActionAndExtras() {
        Bundle setConfig = new Bundle();
        setConfig.putString(RWDemoIntentParams.BUNDLE_EXTRA_PROFILE_NAME_KEY, RWDemoIntentParams.BUNDLE_EXTRA_PROFILE_NAME_VAL);

        Intent intent = DataWedgeSupport.createSetConfigIntent(setConfig);

        assertEquals(RWDemoIntentParams.ACTION, intent.getAction());
        Bundle extras = intent.getExtras();
        assertTrue(extras.containsKey(RWDemoIntentParams.ACTION_EXTRA_SET_CONFIG));
        assertEquals("true", extras.getString(RWDemoIntentParams.EXTRA_SEND_RESULT));
        assertEquals("RFID_CONFIG", extras.getString(RWDemoIntentParams.EXTRA_COMMAND_IDENTIFIER));
    }

    @Test
    public void createSoftTriggerIntent_buildsExpectedActionAndCommand() {
        Intent intent = DataWedgeSupport.createSoftTriggerIntent(
                RWDemoIntentParams.ACTION_EXTRA_SOFT_RFID_TRIGGER,
                "START_SCANNING");

        assertEquals(RWDemoIntentParams.ACTION, intent.getAction());
        assertEquals("START_SCANNING", intent.getStringExtra(RWDemoIntentParams.ACTION_EXTRA_SOFT_RFID_TRIGGER));
    }

    @Test
    public void decode_defaultsToScannerWhenSourceMissing() {
        Intent intent = new Intent();
        intent.putExtra(DataWedgeSupport.DATA_STRING_TAG, "ABC123");

        DataWedgeSupport.DecodedData decoded = DataWedgeSupport.decode(intent);

        assertEquals("ABC123", decoded.data);
        assertEquals(DataWedgeSupport.SOURCE_SCANNER, decoded.source);
    }

    @Test
    public void decode_usesEncryptedMsrFallbackWhenRawDataPresent() {
        Intent intent = new Intent();
        intent.putExtra(DataWedgeSupport.SOURCE_TAG, DataWedgeSupport.SOURCE_MSR);
        intent.putExtra(DataWedgeSupport.MSR_DATA_TAG, new byte[]{1, 2, 3});

        DataWedgeSupport.DecodedData decoded = DataWedgeSupport.decode(intent);

        assertEquals(DataWedgeSupport.DATA_MSR, decoded.data);
        assertEquals(DataWedgeSupport.SOURCE_MSR, decoded.source);
    }

    @Test
    public void resolveStatus_forScannerScanning_returnsGreenAndActive() {
        DataWedgeSupport.StatusUiState state = DataWedgeSupport.resolveStatus(
                DataWedgeSupport.SOURCE_SCANNER,
                RWDemoIntentParams.STATUS_SCANNING,
                "READING",
                "STOPPED");

        assertEquals("READING", state.displayStatus);
        assertEquals(DataWedgeSupport.UiTone.GREEN, state.tone);
        assertTrue(state.barcodeScanActive);
        assertFalse(state.rfidScanActive);
        assertFalse(state.dismissProgress);
    }

    @Test
    public void resolveStatus_forRfidWaiting_returnsStoppedAndDismissesProgress() {
        DataWedgeSupport.StatusUiState state = DataWedgeSupport.resolveStatus(
                "rfid",
                RWDemoIntentParams.STATUS_WAITING,
                "READING",
                "STOPPED");

        assertEquals("STOPPED", state.displayStatus);
        assertEquals(DataWedgeSupport.UiTone.GREEN, state.tone);
        assertFalse(state.rfidScanActive);
        assertFalse(state.barcodeScanActive);
        assertTrue(state.dismissProgress);
    }

    @Test
    public void resolveStatus_forActivated_returnsBlueTone() {
        DataWedgeSupport.StatusUiState state = DataWedgeSupport.resolveStatus(
                "rfid",
                RWDemoIntentParams.STATUS_ACTIVATED,
                "READING",
                "STOPPED");

        assertEquals(RWDemoIntentParams.STATUS_ACTIVATED, state.displayStatus);
        assertEquals(DataWedgeSupport.UiTone.BLUE, state.tone);
        assertFalse(state.dismissProgress);
    }
}
