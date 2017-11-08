package edu.cmu.ahncathlab;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.Intent;
import android.os.ParcelUuid;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sbked on 11/5/2017.
 */

public class BLETag {
    final static String ANT_TAG_SERVICE_UUID = "000018ab-0000-0000-0000-000000000000";
    final static String DISP_TAG_SERVICE_UUID = "000018aa-0000-0000-0000-000000000000";
    final static String TAG_SERVICE_UUID_MASK = "0000FFFF-0000-0000-0000-000000000000";


    static BluetoothAdapter.LeScanCallback leScanCallback;
    private static BluetoothAdapter btAdapter;

    private static BluetoothLeScanner btScanner;
    private final static int REQUEST_ENABLE_BT = 1;

    public static void Init(final Activity activity) {
        // Start the BLE manager and adapter
        BluetoothManager btManager = (BluetoothManager) activity.getSystemService (Context.BLUETOOTH_SERVICE);
        btAdapter = btManager.getAdapter ();
        if (btAdapter != null && btAdapter.isEnabled ()) {
            Intent enableIntent = new Intent (BluetoothAdapter.ACTION_REQUEST_ENABLE);
            activity.startActivityForResult (enableIntent, REQUEST_ENABLE_BT);
        }
    }

    private final static ScanCallback scanCallback = new ScanCallback () {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            BluetoothDevice device = result.getDevice ();
            if (device != null) {
                System.out.println(result.describeContents());
                //com.sigenix.hhcompliance.TagManager.TagFound (result);
            }
        }
    };

    public static boolean StartScanning() {
        ScanSettings settings = new ScanSettings.Builder ()
                .setScanMode (ScanSettings.SCAN_MODE_LOW_LATENCY)
                .setReportDelay (0).build ();

        // add scan filters for the service UUID of our tags
        List<ScanFilter> scanFilters = new ArrayList<>();

        // attempt to scan for just our tags based on service UUID
        ScanFilter.Builder b1 = new ScanFilter.Builder();
        b1.setServiceUuid(ParcelUuid.fromString(ANT_TAG_SERVICE_UUID),
                ParcelUuid.fromString(TAG_SERVICE_UUID_MASK));
        scanFilters.add(b1.build());

        ScanFilter.Builder b2 = new ScanFilter.Builder();
        b2.setServiceUuid(ParcelUuid.fromString(DISP_TAG_SERVICE_UUID),
                ParcelUuid.fromString(TAG_SERVICE_UUID_MASK));
        scanFilters.add(b2.build());

        btScanner = btAdapter.getBluetoothLeScanner ();
        if (btScanner == null) {
            return false;
        } else {
            // start the scan, but don't use the scan filter...it doesn't work for some reason...no tags are found
            btScanner.startScan (null, settings, scanCallback);
            return true;
        }
    }


    public static void StopScan() {
        btScanner.stopScan (scanCallback);
    }


}
