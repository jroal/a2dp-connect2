package a2dp.connect2;

import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

public class BtReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Context application = Connector.application;
        Intent intent2 = new Intent(application, WidgetProvider.class);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);

        int[] ids = AppWidgetManager.getInstance(application).getAppWidgetIds(new ComponentName(application, WidgetProvider.class));

        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
        application.sendBroadcast(intent2);
        // an Intent broadcast.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
