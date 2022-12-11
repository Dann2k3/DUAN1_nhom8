package poly.ph26873.coffeepoly.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import poly.ph26873.coffeepoly.R;
import poly.ph26873.coffeepoly.activities.LoginActivity;
import poly.ph26873.coffeepoly.activities.SplashActivity;
import poly.ph26873.coffeepoly.listData.ListData;
import poly.ph26873.coffeepoly.models.Notify;

public class MyReceiver extends BroadcastReceiver {
    private int a = 0;
    public static boolean isConnected = true;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())) {
            if (isNetWorkAvailabe(context)==true) {
                isConnected = true;
                if (a != 0) {
                    Toast.makeText(context, "Internet connected", Toast.LENGTH_SHORT).show();
                    a = 0;
                }
//                kiemTraThongBao(context);
            } else {
                isConnected = false;
                a++;
                Toast.makeText(context, "Internet Disconnected", Toast.LENGTH_LONG).show();
            }
        }
        kiemTraEnable(context);

    }

    private void kiemTraEnable(Context context) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            DatabaseReference readUserE = database.getReference("coffee-poly").child("user").child(FirebaseAuth.getInstance().getCurrentUser().getEmail().replaceAll("@gmail.com", "")).child("enable");
            readUserE.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    ListData.enable_user_current = snapshot.getValue(Integer.class);
                    if (ListData.enable_user_current == 1) {
                        FirebaseAuth.getInstance().signOut();
                        Intent intent1 = new Intent(context, SplashActivity.class);
                        context.startActivity(intent1);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    private void kiemTraThongBao(Context context) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String em = user.getEmail().replaceAll("@gmail.com", "");
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference reference = database.getReference("coffee-poly").child("type_user").child(em);
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    int typeU = snapshot.getValue(Integer.class);
                    if (typeU == 2) {
                        layThongBaoMoi(database, em, context);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    private void layThongBaoMoi(FirebaseDatabase database, String em, Context context) {
        List<Notify> list = new ArrayList<>();
        DatabaseReference reference = database.getReference("coffee-poly").child("notify");
        if (ListData.type_user_current == 2) {
            reference.child(em);
        } else {
            reference.child("Staff_Ox3325");
        }
        reference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Notify notify = snapshot.getValue(Notify.class);
                if (notify != null && notify.getStatus() == 0) {
                    list.add(notify);
                    Log.d("mmm", "+1");
                }
                if (list.size() > 0) {
                    hienThongBao(context);
                    list.clear();
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Notify notify = snapshot.getValue(Notify.class);
                if (notify != null && notify.getStatus() == 1 && !list.isEmpty()) {
                    for (int i = 0; i < list.size(); i++) {
                        if (list.get(i).getTime() == notify.getTime()) {
                            list.remove(i);
                            break;
                        }
                    }
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                Notify notify = snapshot.getValue(Notify.class);
                if (notify != null && !list.isEmpty()) {
                    for (int i = 0; i < list.size(); i++) {
                        if (list.get(i).getTime() == notify.getTime()) {
                            list.remove(i);
                            break;
                        }
                    }
                }
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void hienThongBao(Context context) {
        View view1 = LayoutInflater.from(context).inflate(R.layout.layout_toast, null);
        Toast toast = new Toast(context);
        toast.setGravity(Gravity.CENTER_HORIZONTAL, 0, -850);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(view1);
        toast.show();
    }



    private boolean isNetWorkAvailabe(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager == null) {
            return false;
        } else {
                NetworkInfo wifi = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
                NetworkInfo mobile = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
                return wifi != null && wifi.isConnected() || (mobile != null && mobile.isConnected());
        }
    }
}