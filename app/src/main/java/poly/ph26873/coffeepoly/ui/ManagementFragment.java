package poly.ph26873.coffeepoly.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import poly.ph26873.coffeepoly.R;
import poly.ph26873.coffeepoly.adapter.ManagementRCVAdapter;
import poly.ph26873.coffeepoly.models.Bill;
import poly.ph26873.coffeepoly.models.User;


public class ManagementFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_management, container, false);
    }

    private RecyclerView maRecyclerView;
    private ManagementRCVAdapter managementRCVAdapter;
    private FirebaseDatabase database;
    private static final String TAG = "zzz";

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG, "-------------ManagementFragment-----------------");
        database = FirebaseDatabase.getInstance();
        maRecyclerView = view.findViewById(R.id.maRecyclerView);
        LinearLayoutManager manager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        maRecyclerView.setLayoutManager(manager);
        maRecyclerView.setHasFixedSize(true);
        managementRCVAdapter = new ManagementRCVAdapter(getContext());
        layListUser();
    }


    private void layListUser() {
        Log.d(TAG, "layListUser");
        DatabaseReference refuser = database.getReference("coffee-poly").child("user");
        refuser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<User> listUser = new ArrayList<>();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    listUser.add(dataSnapshot.getValue(User.class));
                }
                if (listUser.size() > 0) {
                    Log.d(TAG, "listUser: " + listUser.size());
                    layListBill(listUser);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d(TAG, "onCancelled: layListUser");
            }
        });
    }

    private void layListBill(List<User> listUser) {
        DatabaseReference reference = database.getReference("coffee-poly/bill");
        List<Bill> listBill = new ArrayList<>();
        for (int i = 0; i < listUser.size(); i++) {
            Log.d(TAG, listUser.get(i).getId().toUpperCase() + "");
            reference.child(listUser.get(i).getId()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        listBill.add(dataSnapshot.getValue(Bill.class));
                        Log.d(TAG, "listBill: " + listBill.size());
                        if (listBill.size() > 0) {
                            List<Bill> list = new ArrayList<>();
                            for (int j = 0; j < listBill.size(); j++) {
                                if (listBill.get(j).getStatus() == 1) {
                                    list.add(listBill.get(j));
                                    Log.d(TAG, "list can tim: " + list.size());
                                }
                            }
                            Collections.reverse(list);
                            Log.d(TAG, "list: " + list.size());
                            managementRCVAdapter.setData(list);
                            maRecyclerView.setAdapter(managementRCVAdapter);
                        }
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

}