# DataWedge Time Measurement Methodology

This document describes how Activation Time and Data Received Time are measured for both RFID and barcode workflows in the application.

## Quick Validation

1. **Monotonic Timing**: Using `SystemClock.elapsedRealtime()` is the correct choice. It keeps measurements immune to system clock jumps (e.g., NTP synchronization events) and captures true elapsed duration, including deep sleep time.
2. **Activation Logic**: Measuring from `onStart()` is robust because it captures elapsed time from initial UI visibility to DataWedge profile and hardware readiness.
3. **State Mapping**:
    - **RFID**: Uses `RESULT_GET_ACTIVE_PROFILE` and checks for the `"RWDemo"` profile.
    - **Barcode**: Uses the `WAITING` scanner status, which is the most accurate indicator that scanner hardware is powered and ready.
4. **Trigger-to-Data Latency**: Resetting the timer on the button `onClick` event and stopping it at the first `handleDecodeData` event provides a clear user-perceived response metric.

## 1. Activation Time (App Startup to Hardware Ready)

Activation Time measures the delay between the application entering the foreground and the DataWedge hardware (RFID sled and barcode scanner) reaching a ready state.

### Measurement Flow
1. **Baseline (T0)**: The measurement starts in the `onStart()` lifecycle method of `RWDemoActivity` using `SystemClock.elapsedRealtime()`.
    - *Code:* `activationTimerStartMs = SystemClock.elapsedRealtime();`
2. **RFID Activation**: The time is recorded when the application receives a DataWedge broadcast confirming that the `"RWDemo"` profile is now active.
    - *Condition:* `RESULT_GET_ACTIVE_PROFILE` extra matches `"RWDemo"`.
    - *Calculation:* `Elapsed = CurrentTime - activationTimerStartMs`
3. **Barcode Activation**: The time is recorded when the application receives a DataWedge status notification indicating the scanner is in the `"WAITING"` state.
    - *Condition:* `RESULT_SCANNER_STATUS` extra equals `"WAITING"`.
    - *Calculation:* `Elapsed = CurrentTime - activationTimerStartMs`

### Summary Dialog
Once both hardware components report a ready state for the first time after startup, an Activation Time Summary dialog is shown with precise millisecond values.

---

## 2. Data Received Time (Trigger to First Data)

Data Received Time measures latency from user interaction (tapping a scan button) to the first captured data returned via intent.

### Measurement Flow
1. **Trigger Point**: The measurement starts immediately when the user clicks the "Soft Scan" (RFID) or "Barcode Scan" button.
    - *RFID Code:* `rfidDataTimerStartMs = SystemClock.elapsedRealtime();`
    - *Barcode Code:* `barcodeDataTimerStartMs = SystemClock.elapsedRealtime();`
2. **Data Capture**: The time is recorded in `handleDecodeData` when the first valid payload arrives for that specific source.
    - *RFID Condition:* First occurrence where `source` is RFID and payload is non-empty.
    - *Barcode Condition:* First occurrence where `source` is `scanner` and payload is non-empty.
3. **Calculation**:
    - `Data Latency = CurrentTime - TriggerPoint`

---

## 3. Implementation Details

- **Precision**: All measurements use `SystemClock.elapsedRealtime()`, which provides a monotonic clock that includes sleep time, making it ideal for interval timing.
- **Display Format**: Times are formatted as `S.mmm` seconds (e.g., `1.234s`).
- **Reset Logic**:
    - Activation timers reset on every `onStart()`.
    - Data latency timers reset every time a new scan is initiated via the UI buttons.

## 4. Why These Metrics Matter
- **Activation Time** helps identify delays in DataWedge profile switching or hardware initialization (e.g., Bluetooth handshake for RFID sleds).
- **Data Received Time** quantifies scanning responsiveness from a user perspective.
