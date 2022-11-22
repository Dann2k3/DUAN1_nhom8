package poly.ph26873.coffeepoly.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import poly.ph26873.coffeepoly.listData.ListData;
import poly.ph26873.coffeepoly.models.Product;
import poly.ph26873.coffeepoly.models.QuantitySoldInMonth;

public class MyService extends Service {
    public MyService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM_yyyy");
        String month = simpleDateFormat.format(calendar.getTime());
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        capNhatListProduct(database);
        capNhatListQuanProduct(database,month);
        return START_NOT_STICKY;
    }



    private void capNhatListQuanProduct(FirebaseDatabase database, String month) {
        DatabaseReference refQuanPrd  = database.getReference("coffee-poly").child("turnover_product").child(month);
        refQuanPrd.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ListData.listQuanPrd.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    ListData.listQuanPrd.add(dataSnapshot.getValue(QuantitySoldInMonth.class));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void capNhatListProduct(FirebaseDatabase database) {
        DatabaseReference refPrd = database.getReference("coffee-poly").child("product");
        refPrd.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ListData.listPrd.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    ListData.listPrd.add(dataSnapshot.getValue(Product.class));
                }
                Log.d("zzz", "ListData.listPrd: = " + ListData.listPrd.size());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return null;
    }
}