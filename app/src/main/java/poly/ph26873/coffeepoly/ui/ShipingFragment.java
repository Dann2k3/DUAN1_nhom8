package poly.ph26873.coffeepoly.ui;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import poly.ph26873.coffeepoly.R;
import poly.ph26873.coffeepoly.adapter.ShipingRCVAdapter;
import poly.ph26873.coffeepoly.models.Bill;
import poly.ph26873.coffeepoly.models.User;


public class ShipingFragment extends Fragment {



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_shiping, container, false);
    }


    private RecyclerView recyclerView;
    private ShipingRCVAdapter adapter;
    private FirebaseDatabase database;
    private static final String TAG = "zzz";
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Đang tải dữ liệu...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        recyclerView = view.findViewById(R.id.shipRecyclerView);
        adapter = new ShipingRCVAdapter(getContext());
        LinearLayoutManager manager = new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(manager);
        recyclerView.setHasFixedSize(true);
        database = FirebaseDatabase.getInstance();
        layListUser();
        progressDialog.dismiss();
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
                    listBill.clear();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        listBill.add(dataSnapshot.getValue(Bill.class));
                    }
                    if (listBill.size() > 0) {
                        List<Bill> list = new ArrayList<>();
                        for (int j = 0; j < listBill.size(); j++) {
                            if (listBill.get(j).getStatus() == 0) {
                                list.add(listBill.get(j));
                            }
                        }
                        Collections.reverse(list);
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



    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.menu_search, menu);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setMaxWidth(Integer.MAX_VALUE);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                adapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return false;
            }

        });
    }

}