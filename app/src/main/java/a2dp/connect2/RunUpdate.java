package a2dp.connect2;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.bluetooth.BluetoothA2dp;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.IBluetoothA2dp;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;


public class RunUpdate extends Service {
    @Override
    public void onDestroy() {
        doUnbindService(this);
        super.onDestroy();
    }

    public static boolean mIsBound = false;
    public static IBluetoothA2dp ibta2 = null;
    static String LOG_TAG = "RunUpdate";
    private String PREFS = "bluetoothlauncher";
    static  SharedPreferences preferences;
    static Context application;
    static RemoteViews views;
    private NotificationManager mNotificationManager = null;
    private NotificationManagerCompat notificationManagerCompat = null;
    private static final String A2DP_FOREGROUND = "a2dp_foreground";
    NotificationChannel channel_f;

    public RunUpdate() {

        final BluetoothAdapter mBTA = BluetoothAdapter.getDefaultAdapter();

        new CountDownTimer(3000, 1000){

            @Override
            public void onTick(long l) {

            }

            @Override
            public void onFinish() {
                Intent intent = new Intent(application, WidgetProvider.class);
                intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);

                AppWidgetManager awm = AppWidgetManager.getInstance(getApplication());

                int[] ids = awm.getAppWidgetIds(new ComponentName(getApplication(), WidgetProvider.class));

                for(int id: ids){
                    String bt_mac = preferences.getString(Integer.toString(id), "O");
                    BluetoothDevice btd = mBTA.getRemoteDevice(bt_mac);
                    if (bt_mac.length() == 17 && isDeviceConnected(bt_mac)) {
                        views.setInt(R.id.WidgetButton, "setBackgroundResource", R.drawable.icon);

                    } else {
                        views.setInt(R.id.WidgetButton, "setBackgroundResource", R.drawable.icon2);

                    }
                    awm.updateAppWidget(id,views);
                }

                intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
                sendBroadcast(intent);

                stopSelf();
            }
        }.start();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        application = getApplicationContext();
        preferences = getApplicationContext().getSharedPreferences(PREFS, 0);
        views = new RemoteViews(this.getPackageName(),
                R.layout.widget_initial_layout);
        if(!mIsBound) getIBluetoothA2dp(this);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        application = getApplicationContext();
        preferences = getApplicationContext().getSharedPreferences(PREFS, 0);
        views = new RemoteViews(this.getPackageName(),
                R.layout.widget_initial_layout);
        if(!mIsBound) getIBluetoothA2dp(this);
        notificationManagerCompat = NotificationManagerCompat.from(application);
        super.onCreate();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    static void getIBluetoothA2dp(Context context) {

        Intent i = new Intent(IBluetoothA2dp.class.getName());

        String filter;
        filter = context.getPackageManager().resolveService(i, PackageManager.GET_RESOLVED_FILTER).serviceInfo.packageName;
        i.setPackage(filter);

        if (context.bindService(i, mConnection, Context.BIND_AUTO_CREATE)) {
            //Log.i(LOG_TAG, "mConnection service bound");
            //Toast.makeText(context, "started service connection", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Bluetooth start service connection failed", Toast.LENGTH_SHORT).show();
            //Log.e(LOG_TAG, "Could not bind to Bluetooth A2DP Service");
        }

    }


    public static ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {

            mIsBound = true;
            ibta2 = IBluetoothA2dp.Stub.asInterface(service);
            //sendIntent();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mIsBound = false;

        }

    };

    public static boolean isDeviceConnected(String btd)  {
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(btd);


        int sinkState = 0;
        if (device != null) {
            try {
                sinkState = ibta2.getConnectionState(device);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        Boolean connected = sinkState == BluetoothA2dp.STATE_CONNECTED || sinkState == BluetoothA2dp.STATE_CONNECTING;

        Boolean result = mBluetoothAdapter != null && mBluetoothAdapter.isEnabled()
                && mBluetoothAdapter.getProfileConnectionState(BluetoothA2dp.A2DP) == BluetoothA2dp.STATE_CONNECTED && connected ;
        Log.i(LOG_TAG, "Mac connected " + btd + " - " + result);
        return result;
    }

    public static void doUnbindService(Context context) {
        if (mIsBound) {
            // Detach our existing connection.
            try {
                context.unbindService(mConnection);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }


    }

// This will only be needed if I ever start this as a foreground service.

    private void updatenot() {
        if (channel_f == null) createNotificationChannel();
        if (mNotificationManager != null) {
            mNotificationManager.cancelAll();
        } else {
            createNotificationChannel();
        }
        if (notificationManagerCompat != null) {
            notificationManagerCompat.cancelAll();
        } else {
            createNotificationChannel();
        }

        String temp = "";
        Notification not = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            not = new NotificationCompat.Builder(application, A2DP_FOREGROUND)
                    .setContentTitle(
                            getResources().getString(R.string.app_name))

                    .setSmallIcon(R.drawable.icon)
                    .setCategory(Notification.CATEGORY_SERVICE)
                    .setContentText(temp)
                    .setChannelId(A2DP_FOREGROUND).build();
            notificationManagerCompat.notify(1, not);
            //Toast.makeText(application, "Test on " + car + " " +not.getChannelId(), Toast.LENGTH_LONG).show();
        } else {
            not = new NotificationCompat.Builder(application, LAUNCHER_APPS_SERVICE)
                    .setContentTitle(
                            getResources().getString(R.string.app_name))
                    //.setContentIntent(contentIntent)
                    .setSmallIcon(R.drawable.icon)
                    .setContentText(temp)
                    .setPriority(Notification.PRIORITY_LOW)
                    .build();
            notificationManagerCompat.notify(1, not);
        }
        this.startForeground(1, not);
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void createNotificationChannel() {
        mNotificationManager = getSystemService(NotificationManager.class);
        //notificationManagerCompat = NotificationManagerCompat.from(application);

        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            // set up foreground notification channel
            CharSequence name2 = getString(R.string.foreground_channel_name);
            String description2 = getString(R.string.foreground_channel_description);
            int importance2 = NotificationManager.IMPORTANCE_LOW;
            channel_f = new NotificationChannel(A2DP_FOREGROUND, name2, importance2);
            channel_f.setDescription(description2);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            mNotificationManager.createNotificationChannel(channel_f);

        }
    }
}
