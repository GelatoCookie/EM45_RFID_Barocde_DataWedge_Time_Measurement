## DataWedge Profile Requirements

This app depends on a DataWedge profile named **RWDemo**.

At startup, the app sends `SET_CONFIG` with `CREATE_IF_NOT_EXIST` and attempts to configure the profile automatically. 
If auto-configuration fails (for example, due to device policy, missing plugin support, or DataWedge state), configure the profile manually using the required values below.

### Required Profile Values

- **Profile name**: `RWDemo`
- **Config mode**: `CREATE_IF_NOT_EXIST`
- **Profile enabled**: `true`
- **Associated app**: package `com.zebra.rfid.rwdemo2`, activity list `*`

### Required Plugin Configuration

- **RFID plugin (`RFID`)**: `rfid_input_enabled=true`
- **RFID formatting plugin (`RFID_F`)**: output plugin name `INTENT`
- **Barcode plugin (`BARCODE`)**: `scanner_input_enabled=true`
- **Intent output plugin (`INTENT`)**:
    - `intent_output_enabled=true`
    - `intent_action=com.zebra.rfid.rwdemo.RWDEMO2`
    - `intent_category=android.intent.category.DEFAULT`
    - `intent_delivery=0`
- **Keystroke output plugin (`KEYSTROKE`)**: `keystroke_output_enabled=false`

### Manual Setup Steps (DataWedge UI)

1. Open **DataWedge** on the device.
2. Create or edit profile **RWDemo**.
3. Associate the profile with app package **com.zebra.rfid.rwdemo2** and activity `*`.
4. Enable plugins and set values exactly as listed in **Required Plugin Configuration**.
5. Save the profile and relaunch the app.

### Quick Verification Checklist

- DataWedge is installed and enabled on the target device.
- Profile **RWDemo** exists and is active for this app.
- Triggering RFID/Barcode updates status indicators in-app.
- Decoded data is delivered to action `com.zebra.rfid.rwdemo.RWDEMO2`.

If profile creation or activation still fails, check device support for RFID and scanner plugins and confirm DataWedge is not restricted by device management policy.
# DataWedge RFID/Barcode Demo App (ECRT)

## Tested Platforms

- EM45
- TC53e-RFID
- TC27-RFD40P
  TC27-RFD40P | 15.0.77 / 11R01 | AT_FULL_UPDATE_14-35-10.00-UG-U127-STD-ATH-04 | May 2026 |

## Version rel0.2 (1.0.3)

### What's New
- **Conflict Resolution**: Startup detection for `com.zebra.rfid.demo.sdksample` - automatically backgrounds conflicting app to safely start `RWDemo2`.
- **Background Alert & Safety**: Plays Alarm Tone x2 and globally deactivates DataWedge on background to prevent resource conflicts.
- **Auto-Activation**: Re-enables DataWedge globally on resume.
- **Icon Refresh**: Modernized launcher and app icons with barcode theme.

## Version 1.0.2

This release includes suspend/resume behavior hardening and version/branding updates:

### What's New
- **Suspend UX Improvement**: On `ACTION_SCREEN_OFF`, the app now calls `moveTaskToBack(true)` after stopping active scans.
- **Foreground Recovery Logging**: Added screen-off logging to simplify suspend/resume diagnostics.
- **Version/Branding Refresh**: App label text updated to `Rfid DW Demo v1.0.2`.
- **About/Legal Refresh**: Copyright text updated to 2026.

### Key Features
- **Real-time Hardware Status**: Uses DataWedge Notification API to monitor and display the state of the Barcode Scanner and RFID Sled.
- **Modern UI Indicators**: Color-coded status bar for immediate hardware feedback.
    - **Green**: Ready (Waiting/Connected/Scanning).
    - **Blue**: Activated (RWDemo Profile is active and configured).
    - **Red**: Error/Disconnected (Disconnected/No Plug-in/Unknown).
- **Consolidated Configuration**: Automatically creates and configures the "RWDemo" DataWedge profile.
- **Robust Hardware Detection**: Validates RFID plug-in availability via `SET_CONFIG` results.

### Integration Details
- **Notification API**: Registered for `SCANNER_STATUS` and `RFID_STATUS`.
- **Intent API**: Uses `com.symbol.datawedge.api.ACTION` for all hardware triggers and configuration.
- **Package**: `com.zebra.rfid.rwdemo2`

© 2026 Zebra Technologies Corporation and/or its affiliates.
