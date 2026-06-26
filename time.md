# DataWedge Time Measurement Methodology

This document explains how the "Activation Time" and "Data Received Time" are measured for both RFID and Barcode within the application.

## 1. Activation Time (App Startup to Hardware Ready)

Activation time measures the delay between the application entering the foreground and the DataWedge hardware (RFID Sled and Barcode Scanner) reaching a "Ready" state.

### Measurement Flow:
1.  **Baseline (T0)**: The measurement starts in the `onStart()` lifecycle method of `RWDemoActivity` using `SystemClock.elapsedRealtime()`.
    - *Code:* `activationTimerStartMs = SystemClock.elapsedRealtime();`
2.  **RFID Activation**: The time is recorded when the application receives a DataWedge broadcast confirming that the **"RWDemo"** profile is now active.
    - *Condition:* `RESULT_GET_ACTIVE_PROFILE` extra matches `"RWDemo"`.
    - *Calculation:* `Elapsed = CurrentTime - activationTimerStartMs`
3.  **Barcode Activation**: The time is recorded when the application receives a DataWedge status notification indicating the scanner is in the **"WAITING"** state.
    - *Condition:* `RESULT_SCANNER_STATUS` extra equals `"WAITING"`.
    - *Calculation:* `Elapsed = CurrentTime - activationTimerStartMs`

### Summary Dialog:
Once both hardware components report their ready state for the first time after a start, an "Activation Time Summary" dialog is shown to the user with the precise millisecond values.

---

## 2. Data Received Time (Trigger to First Data)

Data Received time measures the "latency" from when the user physically interacts with the application (by tapping a scan button) until the first piece of captured data is returned via intent.

### Measurement Flow:
1.  **Trigger Point**: The measurement starts immediately when the user clicks the "Soft Scan" (RFID) or "Barcode Scan" button.
    - *RFID Code:* `rfidDataTimerStartMs = SystemClock.elapsedRealtime();`
    - *Barcode Code:* `barcodeDataTimerStartMs = SystemClock.elapsedRealtime();`
2.  **Data Capture**: The time is recorded in `handleDecodeData` when the first valid payload arrives for that specific source.
    - *RFID Condition:* First occurrence where `source` is RFID and payload is non-empty.
    - *Barcode Condition:* First occurrence where `source` is `scanner` and payload is non-empty.
3.  **Calculation**:
    - `Data Latency = CurrentTime - TriggerPoint`

---

## 3. Implementation Details

- **Precision**: All measurements use `SystemClock.elapsedRealtime()`, which provides a monotonic clock including sleep time, making it ideal for interval timing.
- **Display Format**: Times are formatted as `S.mmm` seconds (e.g., `1.234s`).
- **Reset Logic**: 
    - Activation timers reset on every `onStart()`.
    - Data latency timers reset every time a new scan is initiated via the UI buttons.

## 4. Why these metrics matter
- **Activation Time** helps identify delays in DataWedge profile switching or hardware initialization (e.g., Bluetooth handshake for RFID sleds).
- **Data Received Time** quantifies the actual responsiveness of the scanning system from a user's perspective.
