package edu.cmu.ahncathlab;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.os.AsyncTask;
import android.os.Build;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

public class DeviceListAdapter extends BaseAdapter {
	
	private ArrayList<BluetoothDevice> mDevices;
	private ArrayList<byte[]> mRecords;
	private ArrayList<Integer> mRSSIs;
	private LayoutInflater mInflater;
	private long doorAdapterTime;
	private long labAdapterTime;
	private boolean in;
	private boolean out;
	
	public DeviceListAdapter(Activity par) {
		super();
		mDevices  = new ArrayList<BluetoothDevice>();
		mRecords = new ArrayList<byte[]>();
		mRSSIs = new ArrayList<Integer>();
		mInflater = par.getLayoutInflater();
	}
	
	public void addDevice(BluetoothDevice device, int rssi, byte[] scanRecord) {
		if(mDevices.contains(device) == false) {
			mDevices.add(device);
			mRSSIs.add(rssi);
			mRecords.add(scanRecord);
			in = TRUE;
			out = FALSE;
		}
		//Keep updating the rssi
		else{
			int pos = mDevices.indexOf(device);
			mRSSIs.set(pos,rssi);
		}

		//Check timings for in and out
		checkTime(device);
	}
	
	public BluetoothDevice getDevice(int index) {
		return mDevices.get(index);
	}
	
	public int getRssi(int index) {
		return mRSSIs.get(index);
	}
	
	public void clearList() {
		mDevices.clear();
		mRSSIs.clear();
		mRecords.clear();
	}
	
	@Override
	public int getCount() {
		return mDevices.size();
	}

	@Override
	public Object getItem(int position) {
		return getDevice(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// get already available view or create new if necessary
		FieldReferences fields;
        if (convertView == null) {
        	convertView = mInflater.inflate(R.layout.activity_scanning_item, null);
        	fields = new FieldReferences();
        	fields.deviceAddress = (TextView)convertView.findViewById(R.id.deviceAddress);
        	fields.deviceName    = (TextView)convertView.findViewById(R.id.deviceName);
        	fields.deviceRssi    = (TextView)convertView.findViewById(R.id.deviceRssi);
            convertView.setTag(fields);
        } else {
            fields = (FieldReferences) convertView.getTag();
        }			
		
        // set proper values into the view
        BluetoothDevice device = mDevices.get(position);
        int rssi = mRSSIs.get(position);
        String rssiString = (rssi == 0) ? "N/A" : rssi + " db";
        String name = device.getName();
        String address = device.getAddress();
        if(name == null || name.length() <= 0) name = "Unknown Device";
        
        fields.deviceName.setText(name);
        fields.deviceAddress.setText(address);
        fields.deviceRssi.setText(rssiString);

		return convertView;
	}
	
	private class FieldReferences {
		TextView deviceName;
		TextView deviceAddress;
		TextView deviceRssi;
	}

	//check timing to clock in and out
	public void checkTime(BluetoothDevice device){

		int pos = mDevices.indexOf(device);
		int rssi = mRSSIs.get(pos);

		String email = LoginActivity.logInEmail;
		DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
		DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
		Date date = new Date();
		String todayDate = dateFormat.format(date);
		String todayTime = timeFormat.format(date);

		//When moving into the lab
		if(device.getName().equalsIgnoreCase("0A0C21") && in){
			if(rssi>(-55)){
				System.out.println("Captured rssi" + rssi);
				doorAdapterTime = SystemClock.elapsedRealtime();
			}
		}

		if(device.getName().equalsIgnoreCase("100511") && in){
			//Check if time passed is within 15 seconds
			long intervalTime = SystemClock.elapsedRealtime() - doorAdapterTime;
			if(rssi>(-60) && intervalTime<15000){
				System.out.println("Moved In " + intervalTime);
				//TODO: Log move in

				//Add 'in' time in track_info table
				String csvString = "AddTrack" +","+ email +","+ todayDate +","+ todayTime +","+ "In";
				csvString = csvString +","+ LoginActivity.logInEmail +","+ Build.MODEL +","+ Build.MANUFACTURER +","+ Build.VERSION.RELEASE;
				new ExecuteTask().execute(csvString);

				System.out.println("Captured rssi for 222222222 " + rssi);
//				doorAdapterTime = SystemClock.elapsedRealtime();
				in = FALSE;
				out = TRUE;
			}
		}

		//When moving out
		if(device.getName().equalsIgnoreCase("100511") && out){
			if(rssi>(-55)){
				System.out.println("Captured for lab- out" + rssi);
				labAdapterTime = SystemClock.elapsedRealtime();
			}
		}

		if(device.getName().equalsIgnoreCase("0A0C21") && out){
			//Check if time passed is within 15 seconds
			long intervalTime = SystemClock.elapsedRealtime() - labAdapterTime;
			if(rssi>(-60) && intervalTime<15000){
				System.out.println("Moved Out " + intervalTime);
				//TODO: Log move out
				//Add 'out' time in track_info table
				String csvString = "AddTrack" +","+ email +","+ todayDate +","+ todayTime +","+ "Out";
				csvString = csvString +","+ LoginActivity.logInEmail +","+ Build.MODEL +","+ Build.MANUFACTURER +","+ Build.VERSION.RELEASE;
				new ExecuteTask().execute(csvString);

				System.out.println("Captured for door -out " + rssi);
//				doorAdapterTime = SystemClock.elapsedRealtime();
				in = TRUE;
				out = FALSE;
			}
		}

	}

	class ExecuteTask extends AsyncTask<String, Integer, String> {

		@Override
		protected String doInBackground(String... params) {
			System.out.println("In doInBackground");
			try {
				PostData(params);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(String s) {
//            mprogressBar1.setVisibility(View.GONE);
		}

	}

	public void PostData(String[] values) throws IOException {
		int status = 0;
		String output;

		try {
			System.out.println("In post");
			// Make call to a particular URL
			URL url = new URL("https://ahncathlabserver.herokuapp.com/MongoDBAdd/");
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();

			// set request method to POST and send name value pair
			conn.setRequestMethod("POST");
			conn.setDoOutput(true);
			// write to POST data area
			OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream());
			out.write(values[0]);
			out.close();

			// get HTTP response code sent by server
			status = conn.getResponseCode();

			//close the connection
			conn.disconnect();
		} // handle exceptions
		catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}
