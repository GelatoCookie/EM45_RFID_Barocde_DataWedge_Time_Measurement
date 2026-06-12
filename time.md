# Time Measurement Documentation

This document describes how the RWDemo2 app calculates and displays activation and data received times for RFID and Barcode scanning.

## Overview

The app measures four distinct time intervals:

1. **RFID Activation Time** - Time from app start to DataWedge RFID profile activation
2. **Barcode Activation Time** - Time from app start to Scanner WAITING state
3. **RFID Data Received Time** - Time from RFID scan button click to first RFID tag received
4. **Barcode Data Received Time** - Time from barcode scan button click to first barcode scanned

All times are measured in seconds with millisecond precision (format: `seconds.milliseconds`).

---

## 1. RFID Activation Time

### Measurement Points
- **Start**: `onStart()` method is called
- **End**: `RESULT_GET_ACTIVE_PROFILE` intent received with profile = "RWDemo"
- **Label**: "DW Activated Profile"

### Implementation Details

#### Field Declarations
```java
// Member variables in RWDemoActivity class
private long activationTimerStartMs = -1L;
private boolean rfidActivationTimeReported = false;
private String rfidActivationElapsedLabel;
private boolean activationSummaryDialogShown = false;
```

#### Start Timer in onStart()
```java
@Override
protected void onStart() {
    Log.d(TAG, "onStart");
    super.onStart();
    // Initialize activation timer
    activationTimerStartMs = SystemClock.elapsedRealtime();
    rfidActivationTimeReported = false;
    barcodeWaitingTimeReported = false;
    activationSummaryDialogShown = false;
    rfidActivationElapsedLabel = null;
    barcodeWaitingElapsedLabel = null;
    rfidDataTimerStartMs = -1L;
    rfidDataReceivedElapsedLabel = null;
    barcodeDataTimerStartMs = -1L;
    barcodeDataReceivedElapsedLabel = null;
}
```

#### Capture Elapsed Time in BroadcastReceiver
```java
private BroadcastReceiver datawedgeBroadcastReceiver = new BroadcastReceiver() {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action == null) return;

        // Detect RFID profile activation
        if (intent.hasExtra(RESULT_GET_ACTIVE_PROFILE)) {
            String activeProfile = intent.getStringExtra(RESULT_GET_ACTIVE_PROFILE);
            if(BUNDLE_EXTRA_PROFILE_NAME_VAL.equals(activeProfile)) {
                if (DEBUG) Log.d(TAG, "ECRT: DW Activated Profile = " + activeProfile);
                
                if (!rwDemoProfileActivated) {
                    playSuccessBeep();
                }
                
                rwDemoProfileActivated = true;
                softScanTrigger.setEnabled(true);
                softScanTrigger.setAlpha(enabledButtonAlphaVlue);
                barcodeScanTrigger.setEnabled(true);
                barcodeScanTrigger.setAlpha(enabledButtonAlphaVlue);
                if (progressBar != null) {
                    progressBar.setVisibility(View.GONE);
                }
                rfidScanState = false;
                barcodeScanState = false;

                // TIMING: Calculate elapsed time from onStart to RFID profile activation
                String rfidElapsedText = null;
                if (!rfidActivationTimeReported && activationTimerStartMs > 0) {
                    long elapsedMs = SystemClock.elapsedRealtime() - activationTimerStartMs;
                    rfidElapsedText = formatElapsedLabel(elapsedMs);
                    rfidActivationElapsedLabel = rfidElapsedText;
                    rfidActivationTimeReported = true;  // Only capture once
                    maybeShowActivationSummaryDialog();
                }
                updateStatusUI(rfidStatusText, R.string.status_rfid, STATUS_ACTIVATED, rfidElapsedText);

                // Query status after profile activation
                queryStatus();
            }
        }
    }
};
```

**Calculation**:
```
RFID Activation Time (ms) = SystemClock.elapsedRealtime() - activationTimerStartMs
RFID Activation Time (s) = elapsedMs / 1000 with millisecond precision
Format: "seconds.milliseconds"  e.g., "2.345s"
```

### UI Display
- **Location**: RFID Status TextView
- **Format**: `RFID Activation Time -> X.XXXs`
- **Combined Dialog**: Shows both RFID and Barcode activation times when both are available

---

## 2. Barcode Activation Time

### Measurement Points
- **Start**: `onStart()` method is called
- **End**: `RESULT_SCANNER_STATUS` intent received with status = "WAITING"
- **Label**: "Scanner WAITING"

### Implementation Details

#### Field Declarations
```java
// Member variables in RWDemoActivity class
private long activationTimerStartMs = -1L;
private boolean barcodeWaitingTimeReported = false;
private String barcodeWaitingElapsedLabel;
```

#### Start Timer in onStart()
```java
@Override
protected void onStart() {
    Log.d(TAG, "onStart");
    super.onStart();
    // Initialize activation timer (shared with RFID)
    activationTimerStartMs = SystemClock.elapsedRealtime();
    barcodeWaitingTimeReported = false;
    // ... (also reset other timing fields)
}
```

#### Capture Elapsed Time in BroadcastReceiver
```java
private BroadcastReceiver datawedgeBroadcastReceiver = new BroadcastReceiver() {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action == null) return;

        // Detect scanner status query result
        if (intent.hasExtra(RESULT_SCANNER_STATUS)) {
            String status = intent.getStringExtra(RESULT_SCANNER_STATUS);
            if (DEBUG) Log.d(TAG, "ECRT: Scanner Status (Query Result) = " + status);
            
            // TIMING: Calculate elapsed time from onStart to Scanner WAITING
            String barcodeElapsedText = null;
            if (STATUS_WAITING.equalsIgnoreCase(status)
                    && !barcodeWaitingTimeReported
                    && activationTimerStartMs > 0) {
                long elapsedMs = SystemClock.elapsedRealtime() - activationTimerStartMs;
                barcodeElapsedText = formatElapsedLabel(elapsedMs);
                barcodeWaitingElapsedLabel = barcodeElapsedText;
                barcodeWaitingTimeReported = true;  // Only capture once
                maybeShowActivationSummaryDialog();
            }
            updateStatusUI(scannerStatusText, R.string.status_scanner, status, barcodeElapsedText);
            if ("WAITING".equalsIgnoreCase(status)) dismissProgressDialog();
        }
    }
};
```

**Calculation**:
```
Barcode Activation Time (ms) = SystemClock.elapsedRealtime() - activationTimerStartMs
Barcode Activation Time (s) = elapsedMs / 1000 with millisecond precision
Format: "seconds.milliseconds"  e.g., "1.567s"
```

### UI Display
- **Location**: Scanner Status TextView
- **Format**: `Barcode Activation Time -> X.XXXs`
- **Combined Dialog**: Shows both RFID and Barcode activation times when both are available

---

## 3. RFID Data Received Time

### Measurement Points
- **Start**: User clicks RFID scan button (soft trigger in `softScanTrigger.setOnClickListener()`)
- **End**: First RFID tag data arrives in `handleDecodeData(Intent i)` method
- **Label**: "RFID Data Received"

### Implementation Details

#### Field Declarations
```java
// Member variables in RWDemoActivity class
private long rfidDataTimerStartMs = -1L;
private String rfidDataReceivedElapsedLabel;
```

#### Start Timer on RFID Button Click
```java
softScanTrigger = (ImageButton) findViewById(R.id.softscanbutton);
softScanTrigger.setOnClickListener(v -> {
    Log.d(TAG, "onClick softScanTrigger pressed");
    if ((SystemClock.elapsedRealtime() - clickTime) < 500) {
        return;  // Debounce double-clicks
    }
    clickTime = SystemClock.elapsedRealtime();
    
    // Clear UI and counts before inventory
    if (!rfidScanState) {
        clearData();
        // TIMING: Start RFID data timer
        rfidDataTimerStartMs = SystemClock.elapsedRealtime();
        rfidDataReceivedElapsedLabel = null;
    }
    
    toggleSoftRfidTrigger();
});
```

#### Capture Elapsed Time in handleDecodeData
```java
private void handleDecodeData(Intent i) {
    if (i == null)
        return;

    DataWedgeSupport.DecodedData decodedData = DataWedgeSupport.decode(i);
    String data = decodedData.data;
    String source = decodedData.source;
    String labelType = decodedData.labelType;

    if (DEBUG) Log.d(TAG, "ECRT: handleDecodeData data=" + data + " source=" + source + " type=" + labelType);

    if (DataWedgeSupport.SOURCE_SCANNER.equalsIgnoreCase(source)){
        playSuccessBeep();
        dismissProgressDialog();
    }

    if (data != null && !data.isEmpty()) {
        boolean isBarcodeSource = DataWedgeSupport.SOURCE_SCANNER.equalsIgnoreCase(source);

        // TIMING: Calculate elapsed time from RFID click to first RFID tag received
        if (!isBarcodeSource && rfidDataTimerStartMs > 0 && rfidDataReceivedElapsedLabel == null) {
            long elapsedMs = SystemClock.elapsedRealtime() - rfidDataTimerStartMs;
            rfidDataReceivedElapsedLabel = formatElapsedLabel(elapsedMs);
        }

        // Update total and per-tag counters
        totalTags++;
        uniqueTags.add(data);
        int tagCount = tagCounts.containsKey(data) ? tagCounts.get(data) + 1 : 1;
        tagCounts.put(data, tagCount);

        // Determine display type (Barcode type or "EPC" for RFID)
        String displayType = "EPC";
        if (isBarcodeSource) {
            displayType = (labelType != null && !labelType.isEmpty()) ? labelType : "Barcode";
        }
        tagTypes.put(data, displayType);

        updateCountUI();
        renderTagRows();

        // Update RFID status UI to show data received time
        if (!isBarcodeSource) {
            updateStatusUI(rfidStatusText, R.string.status_rfid, STATUS_SCANNING);
        } else {
            updateStatusUI(scannerStatusText, R.string.status_scanner, STATUS_SCANNING);
        }

        // Dismiss progress and stop timer when data is received
        if (isBarcodeSource) {
            stopBarcodeScan();
        }
        // For RFID we keep progress until timeout or manual stop
    }
    setIntent(null);
    mDataIntent = null;
}
```

**Calculation**:
```
RFID Data Received Time (ms) = SystemClock.elapsedRealtime() - rfidDataTimerStartMs
RFID Data Received Time (s) = elapsedMs / 1000 with millisecond precision
Format: "seconds.milliseconds"  e.g., "3.456s"
```

### UI Display
- **Location**: RFID Status TextView
- **Format**: `RFID Data Received Time -> X.XXXs`
- **Multi-line RFID Status**: Shows both activation and data received times:
  ```
  Status: Reading (X.XXXs)
  RFID Activation Time -> Y.YYYs
  RFID Data Received Time -> Z.ZZZs
  ```

---

## 4. Barcode Data Received Time

### Measurement Points
- **Start**: User clicks barcode scan button (soft trigger in `barcodeScanTrigger.setOnClickListener()`)
- **End**: First barcode data arrives in `handleDecodeData(Intent i)` method
- **Label**: "Barcode Data Received"

### Implementation Details

#### Field Declarations
```java
// Member variables in RWDemoActivity class
private long barcodeDataTimerStartMs = -1L;
private String barcodeDataReceivedElapsedLabel;
```

#### Start Timer on Barcode Button Click
```java
barcodeScanTrigger = (ImageButton) findViewById(R.id.barcodeScanButton);
barcodeScanTrigger.setOnClickListener(v -> {
    Log.d(TAG, "onClick barcodeScanTrigger pressed");
    if ((SystemClock.elapsedRealtime() - clickTime) < 500) {
        return;  // Debounce double-clicks
    }
    clickTime = SystemClock.elapsedRealtime();
    
    // Clear UI and counts before scan
    if (!barcodeScanState) {
        clearData();
        // TIMING: Start barcode data timer
        barcodeDataTimerStartMs = SystemClock.elapsedRealtime();
        barcodeDataReceivedElapsedLabel = null;
    }

    toggleSoftBarcodeTrigger();
});
```

#### Capture Elapsed Time in handleDecodeData
```java
private void handleDecodeData(Intent i) {
    if (i == null)
        return;

    DataWedgeSupport.DecodedData decodedData = DataWedgeSupport.decode(i);
    String data = decodedData.data;
    String source = decodedData.source;
    String labelType = decodedData.labelType;

    if (DEBUG) Log.d(TAG, "ECRT: handleDecodeData data=" + data + " source=" + source + " type=" + labelType);

    if (DataWedgeSupport.SOURCE_SCANNER.equalsIgnoreCase(source)){
        playSuccessBeep();
        dismissProgressDialog();
    }

    if (data != null && !data.isEmpty()) {
        boolean isBarcodeSource = DataWedgeSupport.SOURCE_SCANNER.equalsIgnoreCase(source);

        // TIMING: Calculate elapsed time from barcode click to first barcode scanned
        if (isBarcodeSource && barcodeDataTimerStartMs > 0 && barcodeDataReceivedElapsedLabel == null) {
            long elapsedMs = SystemClock.elapsedRealtime() - barcodeDataTimerStartMs;
            barcodeDataReceivedElapsedLabel = formatElapsedLabel(elapsedMs);
        }

        // Update total and per-tag counters
        totalTags++;
        uniqueTags.add(data);
        int tagCount = tagCounts.containsKey(data) ? tagCounts.get(data) + 1 : 1;
        tagCounts.put(data, tagCount);

        // Determine display type (Barcode type or "EPC" for RFID)
        String displayType = "EPC";
        if (isBarcodeSource) {
            displayType = (labelType != null && !labelType.isEmpty()) ? labelType : "Barcode";
        }
        tagTypes.put(data, displayType);

        updateCountUI();
        renderTagRows();

        // Update status UI to show data received time
        if (!isBarcodeSource) {
            updateStatusUI(rfidStatusText, R.string.status_rfid, STATUS_SCANNING);
        } else {
            updateStatusUI(scannerStatusText, R.string.status_scanner, STATUS_SCANNING);
        }

        // Dismiss progress and stop timer when data is received
        if (isBarcodeSource) {
            stopBarcodeScan();
        }
        // For RFID we keep progress until timeout or manual stop
    }
    setIntent(null);
    mDataIntent = null;
}
```

**Calculation**:
```
Barcode Data Received Time (ms) = SystemClock.elapsedRealtime() - barcodeDataTimerStartMs
Barcode Data Received Time (s) = elapsedMs / 1000 with millisecond precision
Format: "seconds.milliseconds"  e.g., "0.512s"
```

### UI Display
- **Location**: Scanner Status TextView
- **Format**: `Barcode Data Received Time -> X.XXXs`
- **Multi-line Scanner Status**: Shows both activation and data received times:
  ```
  Status: Stopped (X.XXXs)
  Barcode Activation Time -> Y.YYYs
  Barcode Data Received Time -> Z.ZZZs
  ```

---

## Helper Methods and UI Updates

### Time Format Conversion Helper

```java
private String formatElapsedLabel(long elapsedMs) {
    long seconds = elapsedMs / 1000;
    long millis = elapsedMs % 1000;
    return seconds + "." + String.format("%03d", millis) + "s";
}
```

### Combined Summary Dialog

```java
private void maybeShowActivationSummaryDialog() {
    // Only show once
    if (activationSummaryDialogShown) {
        return;
    }
    
    // Wait for both RFID and Barcode activation times to be measured
    if (rfidActivationElapsedLabel == null || barcodeWaitingElapsedLabel == null) {
        return;
    }
    
    activationSummaryDialogShown = true;
    showActivationTimeDialog(
            "Activation Time Summary",
            "RFID (DW Activated Profile): " + rfidActivationElapsedLabel
                    + "\nBarcode (Scanner WAITING): " + barcodeWaitingElapsedLabel);
}

private void showActivationTimeDialog(String title, String message) {
    runOnUiThread(() -> new AlertDialog.Builder(RWDemoActivity.this)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(android.R.string.ok, null)
            .show());
}
```

### Status UI Update with Timing Display

The status UI method has been overloaded to support elapsed time labels:

```java
// Legacy method - calls overloaded version with null elapsed label
private void updateStatusUI(TextView textView, int stringResId, String status) {
    updateStatusUI(textView, stringResId, status, null);
}

// Main method - updates status and includes elapsed timing labels
private void updateStatusUI(TextView textView, int stringResId, String status, String elapsedLabel) {
    runOnUiThread(() -> {
        if (status == null || textView == null) return;

        String target = textView == scannerStatusText ? DataWedgeSupport.SOURCE_SCANNER : "rfid";
        DataWedgeSupport.StatusUiState state = DataWedgeSupport.resolveStatus(
                target,
                status,
                getString(R.string.status_reading),
                getString(R.string.status_stopped));

        if (textView == rfidStatusText) {
            rfidScanState = state.rfidScanActive;
            if (!state.rfidScanActive && rfidTimeoutRunnable != null) {
                timeoutHandler.removeCallbacks(rfidTimeoutRunnable);
                rfidTimeoutRunnable = null;
            }
        } else if (textView == scannerStatusText) {
            barcodeScanState = state.barcodeScanActive;
            if (!state.barcodeScanActive && barcodeTimeoutRunnable != null) {
                timeoutHandler.removeCallbacks(barcodeTimeoutRunnable);
                barcodeTimeoutRunnable = null;
            }
        }

        // Build status text with optional elapsed label
        String statusText = getString(stringResId, state.displayStatus);
        if (elapsedLabel != null && !elapsedLabel.isEmpty()) {
            statusText = statusText + " (" + elapsedLabel + ")";
        }
        
        // Add activation and data received timing information
        String activationTimeUiText = buildActivationTimeUiTextFor(textView);
        if (!activationTimeUiText.isEmpty()) {
            statusText = statusText + "\n" + activationTimeUiText;
        }
        
        textView.setText(statusText);

        // Update text color based on state
        if (state.tone == DataWedgeSupport.UiTone.GREEN) {
            textView.setTextColor(getResources().getColor(R.color.status_green));
        } else if (state.tone == DataWedgeSupport.UiTone.BLUE) {
            textView.setTextColor(getResources().getColor(R.color.status_blue));
        } else {
            textView.setTextColor(getResources().getColor(R.color.status_red));
        }

        if (state.dismissProgress) {
            dismissProgressDialog();
        }
    });
}
```

### Per-Status Timing UI Builder

This method builds the timing display text specific to each status TextView:

```java
private String buildActivationTimeUiTextFor(TextView textView) {
    if (textView == scannerStatusText) {
        // For Scanner/Barcode status, show barcode-specific timings
        List<String> parts = new ArrayList<>();
        if (barcodeWaitingElapsedLabel != null) {
            parts.add("Barcode Activation Time -> " + barcodeWaitingElapsedLabel);
        }
        if (barcodeDataReceivedElapsedLabel != null) {
            parts.add("Barcode Data Received Time -> " + barcodeDataReceivedElapsedLabel);
        }
        if (!parts.isEmpty()) {
            return android.text.TextUtils.join("\n", parts);
        }
    }
    
    if (textView == rfidStatusText) {
        // For RFID status, show RFID-specific timings
        List<String> parts = new ArrayList<>();
        if (rfidActivationElapsedLabel != null) {
            parts.add("RFID Activation Time -> " + rfidActivationElapsedLabel);
        }
        if (rfidDataReceivedElapsedLabel != null) {
            parts.add("RFID Data Received Time -> " + rfidDataReceivedElapsedLabel);
        }
        if (!parts.isEmpty()) {
            return android.text.TextUtils.join("\n", parts);
        }
    }
    
    return "";
}
```

---

## Time Format Conversion

### Format Helper Method

```java
private String formatElapsedLabel(long elapsedMs) {
    long seconds = elapsedMs / 1000;
    long millis = elapsedMs % 1000;
    return seconds + "." + String.format("%03d", millis) + "s";
}
```

This method converts milliseconds to a human-readable format:
- Extracts seconds by dividing elapsed milliseconds by 1000
- Extracts remaining milliseconds using modulo operator
- Formats milliseconds as 3-digit zero-padded string
- Returns combined format: "seconds.milliseconds" (e.g., "2.345s")

### Examples
| Milliseconds | Formatted Output | Explanation |
|-------------|-----------------|-------------|
| 1234 | 1.234s | 1 second, 234 milliseconds |
| 567 | 0.567s | 0 seconds, 567 milliseconds |
| 5000 | 5.000s | 5 seconds, 0 milliseconds |
| 45678 | 45.678s | 45 seconds, 678 milliseconds |
| 100 | 0.100s | 0 seconds, 100 milliseconds |
| 12000 | 12.000s | 12 seconds, 0 milliseconds |

### SystemClock.elapsedRealtime()

Why we use `SystemClock.elapsedRealtime()`:
- **High precision**: Millisecond accuracy
- **System-clock independent**: Not affected by system clock adjustments
- **Monotonic**: Always increases, never decreases
- **Suitable for measuring intervals**: Perfect for elapsed time calculations
- **Android best practice**: Recommended for measuring time intervals in Android

---

## Timing Measurement Data Flow

### Activity Lifecycle and Timing Initialization

```
Activity Created (onCreate)
    ↓
Activity Started (onStart)
    ├─→ activationTimerStartMs = SystemClock.elapsedRealtime()  [START]
    ├─→ rfidActivationTimeReported = false
    ├─→ barcodeWaitingTimeReported = false
    ├─→ activationSummaryDialogShown = false
    ├─→ rfidDataTimerStartMs = -1L
    ├─→ rfidDataReceivedElapsedLabel = null
    ├─→ barcodeDataTimerStartMs = -1L
    ├─→ barcodeDataReceivedElapsedLabel = null
    ├─→ (All timing fields reset for fresh measurement)
    ↓
Activity Resumed (onResume)
    ├─→ Profile activation query sent to DataWedge
    ├─→ Scanner status query sent to DataWedge
    ↓
```

### RFID Activation Measurement Flow

```
onStart() called
    ↓
activationTimerStartMs = SystemClock.elapsedRealtime()  [START TIME]
    ↓
(App waiting for DataWedge to activate RFID profile)
    ↓
datawedgeBroadcastReceiver receives RESULT_GET_ACTIVE_PROFILE
    ├─→ Check: !rfidActivationTimeReported && activationTimerStartMs > 0
    ├─→ YES: Calculate elapsed time
    │   ├─→ long elapsedMs = SystemClock.elapsedRealtime() - activationTimerStartMs
    │   ├─→ String rfidElapsedText = formatElapsedLabel(elapsedMs)
    │   ├─→ rfidActivationElapsedLabel = rfidElapsedText
    │   ├─→ rfidActivationTimeReported = true
    │   ├─→ maybeShowActivationSummaryDialog()
    │   └─→ updateStatusUI(rfidStatusText, ...)  [UI UPDATED]
    └─→ NO: Skip (already measured)
```

### Barcode Activation Measurement Flow

```
onStart() called
    ↓
activationTimerStartMs = SystemClock.elapsedRealtime()  [START TIME]
    ↓
(App waiting for Scanner to reach WAITING state)
    ↓
datawedgeBroadcastReceiver receives RESULT_SCANNER_STATUS
    ├─→ Check: STATUS_WAITING && !barcodeWaitingTimeReported && activationTimerStartMs > 0
    ├─→ YES: Calculate elapsed time
    │   ├─→ long elapsedMs = SystemClock.elapsedRealtime() - activationTimerStartMs
    │   ├─→ String barcodeElapsedText = formatElapsedLabel(elapsedMs)
    │   ├─→ barcodeWaitingElapsedLabel = barcodeElapsedText
    │   ├─→ barcodeWaitingTimeReported = true
    │   ├─→ maybeShowActivationSummaryDialog()
    │   └─→ updateStatusUI(scannerStatusText, ...)  [UI UPDATED]
    └─→ NO: Skip (already measured or not WAITING status)
```

### RFID Data Measurement Flow

```
User clicks RFID Scan Button
    ↓
softScanTrigger.setOnClickListener() triggered
    ├─→ Check: !rfidScanState
    ├─→ YES:
    │   ├─→ clearData()
    │   ├─→ rfidDataTimerStartMs = SystemClock.elapsedRealtime()  [START TIME]
    │   ├─→ rfidDataReceivedElapsedLabel = null
    │   └─→ toggleSoftRfidTrigger()
    └─→ NO: Skip (already scanning)
    ↓
(App waiting for first RFID tag)
    ↓
RFID tag received (Intent callback)
    ↓
handleDecodeData(Intent i) called
    ├─→ Decode intent to get data
    ├─→ boolean isBarcodeSource = check source
    ├─→ Check: !isBarcodeSource && rfidDataTimerStartMs > 0 && rfidDataReceivedElapsedLabel == null
    ├─→ YES: Calculate elapsed time
    │   ├─→ long elapsedMs = SystemClock.elapsedRealtime() - rfidDataTimerStartMs
    │   ├─→ rfidDataReceivedElapsedLabel = formatElapsedLabel(elapsedMs)
    │   ├─→ updateStatusUI(rfidStatusText, ...)  [UI UPDATED]
    │   └─→ Update tag counters and display
    └─→ NO: Skip (already measured)
```

### Barcode Data Measurement Flow

```
User clicks Barcode Scan Button
    ↓
barcodeScanTrigger.setOnClickListener() triggered
    ├─→ Check: !barcodeScanState
    ├─→ YES:
    │   ├─→ clearData()
    │   ├─→ barcodeDataTimerStartMs = SystemClock.elapsedRealtime()  [START TIME]
    │   ├─→ barcodeDataReceivedElapsedLabel = null
    │   └─→ toggleSoftBarcodeTrigger()
    └─→ NO: Skip (already scanning)
    ↓
(App waiting for first barcode)
    ↓
Barcode scanned (Intent callback)
    ↓
handleDecodeData(Intent i) called
    ├─→ Decode intent to get data
    ├─→ boolean isBarcodeSource = check source
    ├─→ Check: isBarcodeSource && barcodeDataTimerStartMs > 0 && barcodeDataReceivedElapsedLabel == null
    ├─→ YES: Calculate elapsed time
    │   ├─→ long elapsedMs = SystemClock.elapsedRealtime() - barcodeDataTimerStartMs
    │   ├─→ barcodeDataReceivedElapsedLabel = formatElapsedLabel(elapsedMs)
    │   ├─→ updateStatusUI(scannerStatusText, ...)  [UI UPDATED]
    │   ├─→ stopBarcodeScan()
    │   └─→ Update tag counters and display
    └─→ NO: Skip (already measured)
```

---

## Timing Measurement Data Flow

### Reset on Activity Start
All timing measurements are reset in `onStart()`:
```java
activationTimerStartMs = -1L;
rfidActivationTimeReported = false;
barcodeWaitingTimeReported = false;
rfidActivationElapsedLabel = null;
barcodeWaitingElapsedLabel = null;
rfidDataTimerStartMs = -1L;
rfidDataReceivedElapsedLabel = null;
barcodeDataTimerStartMs = -1L;
barcodeDataReceivedElapsedLabel = null;
```

### One-Time Capture
Each measurement is captured only once:
- RFID activation: `rfidActivationTimeReported` flag prevents re-capture
- Barcode activation: `barcodeWaitingTimeReported` flag prevents re-capture
- RFID data: `rfidDataReceivedElapsedLabel == null` check prevents re-capture
- Barcode data: `barcodeDataReceivedElapsedLabel == null` check prevents re-capture

---

## Combined Summary Dialog

### Behavior
When both RFID and Barcode activation times are captured, a single summary dialog is displayed once:

```
Title: "Activation Time Summary"
Message:
  RFID (DW Activated Profile): X.XXXs
  Barcode (Scanner WAITING): Y.YYYs
```

### Implementation
```java
private void maybeShowActivationSummaryDialog() {
    if (activationSummaryDialogShown) return;  // Only once
    
    if (rfidActivationElapsedLabel == null || barcodeWaitingElapsedLabel == null) {
        return;  // Wait for both measurements
    }
    
    activationSummaryDialogShown = true;
    showActivationTimeDialog(
        "Activation Time Summary",
        "RFID (DW Activated Profile): " + rfidActivationElapsedLabel
            + "\nBarcode (Scanner WAITING): " + barcodeWaitingElapsedLabel);
}
```

---

## Summary Table

| Time Measurement | Start Event | End Event | UI Location | Format |
|-----------------|-------------|-----------|-------------|--------|
| RFID Activation | `onStart()` | RFID profile active | RFID Status | "RFID Activation Time -> X.XXXs" |
| Barcode Activation | `onStart()` | Scanner WAITING | Scanner Status | "Barcode Activation Time -> X.XXXs" |
| RFID Data Received | RFID button click | RFID tag received | RFID Status | "RFID Data Received Time -> X.XXXs" |
| Barcode Data Received | Barcode button click | Barcode scanned | Scanner Status | "Barcode Data Received Time -> X.XXXs" |

All measurements use `SystemClock.elapsedRealtime()` for high-precision, system-clock-agnostic timing.
