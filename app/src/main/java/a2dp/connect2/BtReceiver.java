package a2dp.connect2;

import android.appwidget.AppWidgetManager;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

import java.util.Objects;

public class BtReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Context application = Connector.application;
        Intent intent2 = new Intent(application, WidgetProvider.class);
        intent2.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);

        int[] ids = AppWidgetManager.getInstance(application).getAppWidgetIds(new ComponentName(application, WidgetProvider.class));
        BluetoothDevice bt;
        try {
            bt = (BluetoothDevice) Objects.requireNonNull(intent.getExtras()).get(
                    BluetoothDevice.EXTRA_DEVICE);
        } catch (Exception e1) {
            bt = null;
            e1.printStackTrace();
        }
        intent2.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
        intent2.putExtra("BT", bt.getAddress());
        application.sendBroadcast(intent2);
        // an Intent broadcast.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
