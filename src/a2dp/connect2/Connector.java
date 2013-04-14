package a2dp.connect2;

import java.util.Set;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.IBluetoothA2dp;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

public class Connector extends Service {

	@Override
	public void onDestroy() {
		this.unregisterReceiver(receiver);
		super.onDestroy();
	}

	@Override
	protected void finalize() throws Throwable {
		// TODO Auto-generated method stub
		this.unregisterReceiver(receiver);
		super.finalize();
	}

	static final int ENABLE_BLUETOOTH = 1;
	private String PREFS = "bluetoothlauncher";
	private String LOG_TAG = "A2DP_Connect";
	private BluetoothDevice device = null;
	private String dname;
	private String bt_mac;
	// private static final String MY_UUID_STRING =
	// "af87c0d0-faac-11de-a839-0800200c9a67";
	Context application;
	int w_id;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Bundle extras = intent.getExtras();

		if (extras != null) {
			w_id = extras.getInt("ID", 0);

		} else {
			Toast.makeText(application, "Oops", Toast.LENGTH_LONG).show();
			done();
		}

		SharedPreferences preferences = getSharedPreferences(PREFS, 0);
		bt_mac = preferences.getString(String.valueOf(w_id), "");
		dname = preferences.getString(w_id + "_name", "oops");

		if (bt_mac != null)
			if (bt_mac.length() == 17) {

				BluetoothAdapter bta = BluetoothAdapter.getDefaultAdapter();

				if (!bta.isEnabled()) {
					Intent btIntent = new Intent(
							BluetoothAdapter.ACTION_REQUEST_ENABLE);
					btIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					application.startActivity(btIntent);

					return START_REDELIVER_INTENT;
				}

				Set<BluetoothDevice> pairedDevices = bta.getBondedDevices();
				// BluetoothDevice device = null;

				/*for (BluetoothDevice dev : pairedDevices) {
					if (dev.getAddress().equalsIgnoreCase(bt_mac)) {
						device = dev;
						// dname = a2dp.connect.Bt_iadl.getName(device);
						dname = preferences.getString(w_id
								+ "_name", dname);
					}
				}*/

				IBluetoothA2dp ibta = null;

				ibta = a2dp.connect2.Bt_iadl.getIBluetoothA2dp(this
						.getBaseContext());

			} else {
				Toast.makeText(application,
						getString(R.string.InvalidDevice) + " " + bt_mac,
						Toast.LENGTH_LONG).show();
				done();
			}

		else {
			Toast.makeText(application, getString(R.string.NullDevice),
					Toast.LENGTH_LONG).show();
			done();
		}
		return START_NOT_STICKY;
		// super.onStart(intent, startId);
	}

	private final BroadcastReceiver receiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context arg0, Intent arg1) {
			IBluetoothA2dp ibta = a2dp.connect2.Bt_iadl.ibta2;
			try {
				if (ibta != null && ibta.getConnectionState(device) == 0) {
					Toast.makeText(application,
							getString(R.string.Connecting) + "  " + dname,
							Toast.LENGTH_LONG).show();
				} else {
					Toast.makeText(application,
							getString(R.string.Disconnecting) + "  " + dname,
							Toast.LENGTH_LONG).show();
				}
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			connectBluetoothA2dp(bt_mac);
		}

	};

	/**
	 * @see android.app.Activity#onCreate(Bundle)
	 */

	public void onCreate() {
		// super.onCreate();
		application = getApplication();
		String filter_1_string = "a2dp.connect2.Connector.INTERFACE";
		IntentFilter filter1 = new IntentFilter(filter_1_string);
		this.registerReceiver(receiver, filter1);
	}

	private void connectBluetoothA2dp(String device) {
		new ConnectBt().execute(device);
	}

	private class ConnectBt extends AsyncTask<String, Void, Boolean> {

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
		 */
		@Override
		protected void onPostExecute(Boolean result) {

			super.onPostExecute(result);
			done();
		}

		BluetoothAdapter bta = BluetoothAdapter.getDefaultAdapter();

		protected void onPreExecute() {

		}

		@Override
		protected Boolean doInBackground(String... arg0) {

			BluetoothAdapter mBTA = BluetoothAdapter.getDefaultAdapter();
			if (mBTA == null || !mBTA.isEnabled())
				return false;

			Set<BluetoothDevice> pairedDevices = bta.getBondedDevices();
			BluetoothDevice device = null;
			for (BluetoothDevice dev : pairedDevices) {
				if (dev.getAddress().equalsIgnoreCase(arg0[0]))
					device = dev;
			}
			if (device == null)
				return false;
			/*
			 * mBTA.cancelDiscovery(); mBTA.startDiscovery();
			 */

			IBluetoothA2dp ibta = a2dp.connect2.Bt_iadl
					.getIBluetoothA2dp(a2dp.connect2.Connector.this);
			try {
				Log.d(LOG_TAG, "Here: " + ibta.getPriority(device));
				if (ibta != null && ibta.getConnectionState(device) == 0)
					ibta.connect(device);
				else
					ibta.disconnect(device);

			} catch (Exception e) {
				Log.e(LOG_TAG, "Error " + e.getMessage());
			}

			return true;
		}

	}

	private void done() {
		// this.finish();

		this.stopSelf();

	}

}
