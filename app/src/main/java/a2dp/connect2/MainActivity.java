package a2dp.connect2;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.RemoteViews;
import android.widget.Toast;

public class MainActivity extends Activity {
	@Override
	protected void onDestroy() {
		try {
			if (receiver_registered) {
				application.unregisterReceiver(receiver1);
				receiver_registered = false;
			}
			//a2dp.connect2.Bt_iadl.doUnbindService2(application);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		super.onDestroy();
	}

	@Override
	protected void onStart() {
		if (!receiver_registered) {
			IntentFilter f1 = new IntentFilter(Bt_iadl.NameFilter);
			application.registerReceiver(receiver1, f1);
			receiver_registered = true;
		}
		super.onStart();
	}

	@Override
	protected void onStop() {

		super.onStop();
	}


	private String PREFS = "bluetoothlauncher";
	static boolean receiver_registered = false;

	// int w_id = 0;
	int mAppWidgetId;
	public Context application;
	String dname;
	public final static String temp[][] = new String[50][2];

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		application = getApplication();
		Intent intent = getIntent();
		Bundle extras = intent.getExtras();
		if (extras != null) {
			mAppWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID,
					AppWidgetManager.INVALID_APPWIDGET_ID);
		}

		
		config(mAppWidgetId);

	}

	public void config(final int id) {

		BluetoothAdapter mBTA = BluetoothAdapter.getDefaultAdapter();
		if (mBTA == null) {
			Toast.makeText(this, R.string.NoBluetooth, Toast.LENGTH_LONG)
					.show();
			return;
		}
		// If Bluetooth is not yet enabled, enable it
		if (!mBTA.isEnabled()) {
			Toast.makeText(application, R.string.NeedEnable, Toast.LENGTH_LONG)
					.show();
			this.finish();
			/*
			 * Intent enableBluetooth = new Intent(
			 * BluetoothAdapter.ACTION_REQUEST_ENABLE); try {
			 * startActivity(enableBluetooth); } catch (Exception e) {
			 * e.printStackTrace(); } // Now implement the onActivityResult()
			 * and wait for it to // be invoked with ENABLE_BLUETOOTH //
			 * onActivityResult(ENABLE_BLUETOOTH, result, enableBluetooth);
			 */return;
		}
		// Toast.makeText(this, "Bluetooth", Toast.LENGTH_LONG).show();
		if (!receiver_registered) {
			IntentFilter f1 = new IntentFilter(Bt_iadl.NameFilter);
			application.registerReceiver(receiver1, f1);
			receiver_registered = true;
		}
		a2dp.connect2.Bt_iadl.getNames(application);
	}

	void createList(int length) {
		String[] lstring = new String[length];
		for (int j = 0; j < length; j++) {
			lstring[j] = temp[j][0] + " - " + temp[j][1];
		}

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.BuilderTitle);
		builder.setItems(lstring, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int item) {
				// Use MODE_WORLD_READABLE and/or MODE_WORLD_WRITEABLE to grant
				// access to other applications
				SharedPreferences preferences = getSharedPreferences(PREFS, 0);
				SharedPreferences.Editor editor = preferences.edit();
				String ws = String.valueOf(mAppWidgetId);
				editor.putString(ws, temp[item][1]);
				dname = temp[item][0];
				editor.putString(ws + "_name", dname);
				editor.commit();
				done();
			}
		});
		builder.show();
	}

	BroadcastReceiver receiver1 = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			int slength = intent.getIntExtra("slength", 0);
			createList(slength);
		}

	};

	void done() {
		AppWidgetManager appWidgetManager = AppWidgetManager
				.getInstance(application);

		Intent intent = new Intent(application, Connector.class);
		intent.putExtra("ID", mAppWidgetId);
		PendingIntent pendingIntent = PendingIntent.getService(application,
				mAppWidgetId, intent, PendingIntent.FLAG_UPDATE_CURRENT);

		// Get the layout for the App Widget and attach an on-click listener
		// to the button
		RemoteViews views = new RemoteViews(application.getPackageName(),
				R.layout.widget_initial_layout);
		views.setOnClickPendingIntent(R.id.WidgetButton, pendingIntent);

		views.setTextViewText(R.id.WidgetButton, dname);
		appWidgetManager.updateAppWidget(mAppWidgetId, views);
		Intent resultValue = new Intent();
		resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
		// resultValue.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
		setResult(RESULT_OK, resultValue);

		finish();
	}

}