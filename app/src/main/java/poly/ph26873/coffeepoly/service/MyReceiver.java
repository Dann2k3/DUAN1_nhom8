package poly.ph26873.coffeepoly.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;
import android.widget.Toast;

public class MyReceiver extends BroadcastReceiver {
    private int a = 0;
    public static boolean isConnected = false;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())) {
            if (isNetWorkAvailabe(context)) {
                isConnected = true;
                if (a != 0) {
                    Toast.makeText(context, "Internet connected", Toast.LENGTH_SHORT).show();
                    a = 0;
                }
            } else {
                isConnected = false;
                a++;
                Toast.makeText(context, "Internet Disconnected", Toast.LENGTH_LONG).show();
            }
        }
    }

    private boolean isNetWorkAvailabe(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager == null) {
            return false;
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                Network network = connectivityManager.getActiveNetwork();
                if (network == null) {
                    return false;
                } else {
                    NetworkCapabilities networkCapabilities = connectivityManager.getNetworkCapabilities(network);
                    return networkCapabilities != null && networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI);
                }
            } else {
                NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
                return networkInfo != null && networkInfo.isConnected();
            }
        }

    }
}