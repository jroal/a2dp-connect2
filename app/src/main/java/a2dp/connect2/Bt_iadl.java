package a2dp.connect2;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Set;

import android.bluetooth.BluetoothA2dp;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.IBluetooth;
import android.bluetooth.IBluetoothA2dp;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

public class Bt_iadl {

	public static final String filter_1_string = "a2dp.connect2.Connector.INTERFACE";
	public static String NameFilter = "a2dp.connect2.Connector.NAME";
	public static IBluetoothA2dp ibta2 = null;
	static IBluetooth ibt2 = null;
	static String address;
	static Context c1;
	public static boolean mIsBound = false;
	public static boolean m2IsBound = false;
	static String LOG_TAG = "Bt-iadl";



	static void getNames(Context c1) {

		int i = 0;
		BluetoothAdapter mBTA = BluetoothAdapter.getDefaultAdapter();

		if (mBTA != null) {
			Set<BluetoothDevice> pairedDevices = mBTA.getBondedDevices();
			// If there are paired devices
			if (pairedDevices.size() > 0) {
				// Loop through paired devices
				for (BluetoothDevice device : pairedDevices) {
					String dname = device.getName();

					try {
						Method m = device.getClass().getMethod("getAliasName");
						Object res = m.invoke(device);
						if (res != null)
							dname = res.toString();
					} catch (NoSuchMethodException e) {
						e.printStackTrace();
					} catch (InvocationTargetException e) {
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					}

					if (dname == null)
						dname = device.getName();
					MainActivity.temp[i][0] = dname;
					MainActivity.temp[i][1] = device.getAddress();
					if (i > 48)
						break;
					i++;
				}
			}
		}

		Intent intent = new Intent();
		intent.setAction(NameFilter);
		intent.putExtra("slength", i);
		c1.sendBroadcast(intent);
	}



	private static void sendIntent() {
		Intent intent = new Intent();
		intent.setAction(filter_1_string);
		c1.sendBroadcast(intent);
	};





    public static boolean isDeviceConnected(String btd)  {
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(btd);


        int sinkState = 0;
        try {
            sinkState = ibta2.getConnectionState(device);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        Boolean connected = sinkState == BluetoothA2dp.STATE_CONNECTED || sinkState == BluetoothA2dp.STATE_CONNECTING;

        Boolean result = mBluetoothAdapter != null && mBluetoothAdapter.isEnabled()
                && mBluetoothAdapter.getProfileConnectionState(BluetoothA2dp.A2DP) == BluetoothA2dp.STATE_CONNECTED && connected ;
        Log.i(LOG_TAG, "Mac connected " + btd + " - " + result);
        return result;
    }

}
