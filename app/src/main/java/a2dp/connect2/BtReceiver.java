package a2dp.connect2;

import android.appwidget.AppWidgetManager;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import java.util.Objects;

public class BtReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        Intent intent2 = new Intent(context, RunUpdate.class);

        context.startService(intent2);

/*        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            context.startService(intent2);
        } else {
            context.startForegroundService(intent2);
        }*/

        // an Intent broadcast.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
