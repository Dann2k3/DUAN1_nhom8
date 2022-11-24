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

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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
    private List<User> listUser = new ArrayList<>();

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
        listUser.clear();
        Log.d(TAG, "layListUser");
        DatabaseReference refuser = database.getReference("coffee-poly").child("user");
        refuser.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                User user = snapshot.getValue(User.class);
                if (user != null) {
                    listUser.add(user);
                    layListBill();
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                User user = snapshot.getValue(User.class);
                if (user == null || listUser.isEmpty()) {
                    return;
                }
                for (int i = 0; i < listUser.size(); i++) {
                    if (listUser.get(i).getId() == user.getId()) {
                        listUser.set(i, user);
                        layListBill();
                        break;
                    }
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void layListBill() {
        Log.d(TAG, "layListBill");
        List<Bill> listBill = new ArrayList<>();
        DatabaseReference reference = database.getReference("coffee-poly/bill");
        for (int i = 0; i < listUser.size(); i++) {
            final int index = i;
            reference.child(listUser.get(i).getId()).addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                    if (index == 0) {
                        Log.d(TAG, "index[0]: "+index);
                        listBill.clear();
                    }
                    Bill bill = snapshot.getValue(Bill.class);
                    if (bill != null && bill.getStatus() == 1 && !listBill.contains(bill)) {
                        listBill.add(bill);
                        Collections.reverse(listBill);
                        managementRCVAdapter.setData(listBill);
                        maRecyclerView.setAdapter(managementRCVAdapter);
                    }
                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                    Bill bill = snapshot.getValue(Bill.class);
                    if (bill == null || listBill.isEmpty()) {
                        return;
                    }
                    for (int j = 0; j < listBill.size(); j++) {
                        if (listBill.get(j).getId() == bill.getId()) {
                            listBill.remove(listBill.get(j));
                            Collections.reverse(listBill);
                            managementRCVAdapter.setData(listBill);
                            break;
                        }
                    }
                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot snapshot) {

                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

}