package poly.ph26873.coffeepoly.ui;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import poly.ph26873.coffeepoly.R;
import poly.ph26873.coffeepoly.adapter.BillDaGiaoRCVAdapter;
import poly.ph26873.coffeepoly.adapter.BillRCVAdapter;
import poly.ph26873.coffeepoly.models.Bill;
import poly.ph26873.coffeepoly.models.User;


public class BillDaGiaoFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_bill_da_giao, container, false);
    }

    private RecyclerView recyclerView;
    private BillDaGiaoRCVAdapter adapter;
    private List<Bill> listBill;
    private List<User> listUser;
    private FirebaseDatabase database;
    private static final String TAG = "zzz";

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Đang tải dữ liệu...");
        progressDialog.show();
        recyclerView = view.findViewById(R.id.bill1RecyclerView);
        LinearLayoutManager manager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(manager);
        recyclerView.setHasFixedSize(true);
        adapter = new BillDaGiaoRCVAdapter(getContext());
        listBill = new ArrayList<>();
        listUser = new ArrayList<>();
        database = FirebaseDatabase.getInstance();
        layListUser();
        progressDialog.dismiss();

    }

    private void layListUser() {
        listUser.clear();
        Log.d(TAG, "layListUser");
        DatabaseReference refuser = database.getReference("coffee-poly").child("user");
        refuser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listBill.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    listUser.add(dataSnapshot.getValue(User.class));
                }
                if (listUser.size() > 0) {
                    Log.d(TAG, "listUser: " + listUser.size());
                    layListBill();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d(TAG, "onCancelled: layListUser");
            }
        });
    }

    private void layListBill() {
        listBill.clear();
        final int[] yy = {-1};
        DatabaseReference reference = database.getReference("coffee-poly/bill");
        for (int i = 0; i < listUser.size(); i++) {
            yy[0] = i;
            Log.d(TAG, listUser.get(i).getId().toUpperCase() + "");
            reference.child(listUser.get(i).getId()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        listBill.add(dataSnapshot.getValue(Bill.class));
                    }
                    if (yy[0] == listUser.size() - 1) {
                        List<Bill> list = new ArrayList<>();
                        list.clear();
                        for (int j = 0; j < listBill.size(); j++) {
                            if (listBill.get(j).getStatus() == 0) {
                                list.add(listBill.get(j));
                            }
                        }
                        Log.d(TAG, "list can tim: " + list.size());
                        adapter.setData(list);
                        recyclerView.setAdapter(adapter);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }


}