package com.zebra.rfid.rwdemo2;

import android.content.Intent;
import android.widget.TextView;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.Rule;

import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class RWDemoActivityInstrumentationTest {

    @Rule
    public ActivityTestRule<RWDemoActivity> activityRule = new ActivityTestRule<>(RWDemoActivity.class);

    @Test
    public void rfidResultBroadcast_updatesRfidStatusText() {
        RWDemoActivity activity = activityRule.getActivity();

        Intent intent = new Intent();
        intent.setAction(RWDemoIntentParams.RESULT_ACTION);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.putExtra(RWDemoIntentParams.RESULT_RFID_STATUS, RWDemoIntentParams.STATUS_WAITING);
        ApplicationProvider.getApplicationContext().sendBroadcast(intent);

        InstrumentationRegistry.getInstrumentation().waitForIdleSync();

        TextView rfidStatusText = activity.findViewById(R.id.rfidStatusText);
        assertEquals("RFID: STOPPED", rfidStatusText.getText().toString());
    }
}
