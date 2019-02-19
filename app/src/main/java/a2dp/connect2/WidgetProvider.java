package a2dp.connect2;

import java.util.Set;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import android.util.Log;
import android.widget.RemoteViews;

public class WidgetProvider extends AppWidgetProvider {

	private static final String LOG_TAG = "Widget_Provider";
	private String PREFS = "bluetoothlauncher";

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.appwidget.AppWidgetProvider#onEnabled(android.content.Context)
	 */
	@Override
	public void onEnabled(Context context) {

		super.onEnabled(context);
	}

	@Override
	public void onReceive(Context context, Intent intent) {

		// Toast.makeText(context, "onRecieve", Toast.LENGTH_LONG).show();
		super.onReceive(context, intent);
	}

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {

		// Toast.makeText(context, "onUpdate", Toast.LENGTH_LONG).show();
		final int N = appWidgetIds.length;

		BluetoothAdapter bta = BluetoothAdapter.getDefaultAdapter();

		SharedPreferences preferences = context.getSharedPreferences(PREFS, 0);
		// Perform this loop procedure for each App Widget that belongs to this
		// provider
		for (int i = 0; i < N; i++) {
			int appWidgetId = appWidgetIds[i];

			// Create an Intent to launch
			Intent intent = new Intent(context, Connector.class);
			intent.putExtra("ID", appWidgetId);
			PendingIntent pendingIntent = PendingIntent.getService(context,
					appWidgetId, intent, PendingIntent.FLAG_UPDATE_CURRENT);

			Log.i(LOG_TAG,"Created widget ID = "+ appWidgetId);
			// Get the layout for the App Widget and attach an on-click listener
			// to the button

			RemoteViews views = new RemoteViews(context.getPackageName(),
					R.layout.widget_initial_layout);
			views.setOnClickPendingIntent(R.id.WidgetButton, pendingIntent);

			String WidgetId = String.valueOf(appWidgetId);
			//String bt_mac = preferences.getString(WidgetId, "Oops");
			String dname = preferences.getString(WidgetId + "_name", "Connect " + i);
			
			views.setTextViewText(R.id.WidgetButton, dname);
			// Tell the AppWidgetManager to perform an update on the current App
			// Widget
			appWidgetManager.updateAppWidget(appWidgetId, views);

		}
		super.onUpdate(context, appWidgetManager, appWidgetIds);
	}

}