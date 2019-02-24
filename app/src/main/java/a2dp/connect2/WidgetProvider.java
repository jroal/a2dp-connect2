package a2dp.connect2;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
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

        Log.i(LOG_TAG, "Widget enabled");

        super.onEnabled(context);
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        Log.i(LOG_TAG, "Widget receive");
        // Toast.makeText(context, "onRecieve", Toast.LENGTH_LONG).show();
        //String address = intent.getStringExtra()
        //isDeviceConnected()
        super.onReceive(context, intent);
    }


    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager,
                         int[] appWidgetIds) {

        BluetoothAdapter bta = BluetoothAdapter.getDefaultAdapter();

        SharedPreferences preferences = context.getSharedPreferences(PREFS, 0);
        // Perform this loop procedure for each App Widget that belongs to this
        // provider

        for (int appWidgetId : appWidgetIds) {
            // Create an Intent to launch
            Intent intent = new Intent(context, Connector.class);
            intent.putExtra("ID", appWidgetId);
            PendingIntent pendingIntent;
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                pendingIntent = PendingIntent.getService(context,
                        appWidgetId, intent, PendingIntent.FLAG_CANCEL_CURRENT);
            } else {
                pendingIntent = PendingIntent.getForegroundService(context,
                        appWidgetId, intent, PendingIntent.FLAG_CANCEL_CURRENT);
            }


            Log.i(LOG_TAG, "Widget ID = " + appWidgetId);
            // Get the layout for the App Widget and attach an on-click listener
            // to the button

            RemoteViews views = new RemoteViews(context.getPackageName(),
                    R.layout.widget_initial_layout);
            views.setOnClickPendingIntent(R.id.WidgetButton, pendingIntent);
            String WidgetId = String.valueOf(appWidgetId);
            String bt_mac = preferences.getString(WidgetId, "O");
            String dname = preferences.getString(WidgetId + "_name", "Connect " + appWidgetId);
            views.setTextViewText(R.id.WidgetButton, dname);

            // Tell the AppWidgetManager to perform an update on the current App
            // Widget
            appWidgetManager.updateAppWidget(appWidgetId, views);

        }

        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }



}