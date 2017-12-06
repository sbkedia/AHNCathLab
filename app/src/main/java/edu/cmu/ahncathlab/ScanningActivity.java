package edu.cmu.ahncathlab;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.content.ContentValues.TAG;

public class ScanningActivity extends ListActivity {
	
	private static final long SCANNING_TIMEOUT = 4 * 1000; /* 4 seconds */
	private static final int ENABLE_BT_REQUEST_ID = 1;
	
	private boolean mScanning = false;
	private Handler mHandler = new Handler();
	private DeviceListAdapter mDevicesListAdapter = null;
	private BleWrapper mBleWrapper = null;
    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_scanning_item);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Android M Permission check 
            if (this.checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("This app needs location access");
                builder.setMessage("Please grant location access so this app can detect beacons.");
                builder.setPositiveButton(android.R.string.ok, null);
                builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        requestPermissions(new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_COARSE_LOCATION);
                    }
                });
                builder.show();
            }
        }

        // create BleWrapper with empty callback object except uiDeviceFound function (we need only that here)
        mBleWrapper = new BleWrapper(this, new BleWrapperUiCallbacks.Null() {
        	@Override
        	public void uiDeviceFound(final BluetoothDevice device, final int rssi, final byte[] record) {
        		handleFoundDevice(device, rssi, record);
        	}
        });
        
        // check if we have BT and BLE on board
        if(mBleWrapper.checkBleHardwareAvailable() == false) {
        	bleMissing();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[],
                                           int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_COARSE_LOCATION: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "coarse location permission granted");
                } else {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Functionality limited");
                    builder.setMessage("Since location access has not been granted, this app will not be able to discover beacons when in the background.");
                    builder.setPositiveButton(android.R.string.ok, null);
                    builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                        }
                    });
                    builder.show();
                }
                return;
            }
        }
    }

    @Override
    protected void onResume() {
    	super.onResume();
    	
    	// on every Resume check if BT is enabled (user could turn it off while app was in background etc.)
    	if(mBleWrapper.isBtEnabled() == false) {
			// BT is not turned on - ask user to make it enabled
			Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
		    startActivityForResult(enableBtIntent, ENABLE_BT_REQUEST_ID);
		    // see onActivityResult to check what is the status of our request
		}
    	
    	// initialize BleWrapper object
        mBleWrapper.initialize();
    	
    	mDevicesListAdapter = new DeviceListAdapter(this);
        setListAdapter(mDevicesListAdapter);
    	
        // Automatically start scanning for devices
    	mScanning = true;
        mBleWrapper.startScanning();
		// remember to add timeout for scanning to not run it forever and drain the battery
//		addScanningTimeout();

//        invalidateOptionsMenu();

    };

    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.scanning, menu);

        if (mScanning) {
            menu.findItem(R.id.scanning_start).setVisible(false);
            menu.findItem(R.id.scanning_stop).setVisible(true);
            menu.findItem(R.id.scanning_indicator)
                .setActionView(R.layout.progress_indicator);

        } else {
            menu.findItem(R.id.scanning_start).setVisible(true);
            menu.findItem(R.id.scanning_stop).setVisible(false);
            menu.findItem(R.id.scanning_indicator).setActionView(null);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.scanning_start:
            	mScanning = true;
            	mBleWrapper.startScanning();
                break;
            case R.id.scanning_stop:
            	mScanning = false;
            	mBleWrapper.stopScanning();
                break;
        }
        
        invalidateOptionsMenu();
        return true;
    }
    
    /* check if user agreed to enable BT */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // user didn't want to turn on BT
        if (requestCode == ENABLE_BT_REQUEST_ID) {
        	if(resultCode == Activity.RESULT_CANCELED) {
		    	btDisabled();
		        return;
		    }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

	/* make sure that potential scanning will take no longer
	 * than <SCANNING_TIMEOUT> seconds from now on */
	private void addScanningTimeout() {
		Runnable timeout = new Runnable() {
            @Override
            public void run() {
            	if(mBleWrapper == null) return;
                mScanning = false;
                mBleWrapper.stopScanning();
                invalidateOptionsMenu();
            }
        };
        mHandler.postDelayed(timeout, SCANNING_TIMEOUT);
	}    

	/* add device to the current list of devices */
    private void handleFoundDevice(final BluetoothDevice device,
            final int rssi,
            final byte[] scanRecord)
	{
	    //Specific address of the required beacons
        if(device.getAddress().equalsIgnoreCase("00:AA:55:A4:A8:4C") || device.getAddress().equalsIgnoreCase("00:AA:55:89:04:45"))
//	    if(device.getAddress().equalsIgnoreCase("00:AA:55:A4:A8:09") || device.getAddress().equalsIgnoreCase("00:AA:55:A4:A8:91"))
		// adding to the UI have to happen in UI thread
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
//                try {
//                    Thread.sleep(200);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
				mDevicesListAdapter.addDevice(device, rssi, scanRecord);
				mDevicesListAdapter.notifyDataSetChanged();
			}
		});

	}	

    private void btDisabled() {
    	Toast.makeText(this, "Sorry, BT has to be turned ON for us to work!", Toast.LENGTH_LONG).show();
        finish();    	
    }
    
    private void bleMissing() {
    	Toast.makeText(this, "BLE Hardware is required but not available!", Toast.LENGTH_LONG).show();
        finish();    	
    }
}
