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

import poly.ph26873.coffeepoly.listData.ListData;
import poly.ph26873.coffeepoly.models.Product;

public class MyService extends Service {
    public MyService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference refPrd = database.getReference("coffee-poly").child("product");
        refPrd.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    ListData.listPrd.add(dataSnapshot.getValue(Product.class));
                }
                Log.d("zzz", "ListData.listPrd: = "+ListData.listPrd.size());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return START_NOT_STICKY;
    }


    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return null;
    }
}