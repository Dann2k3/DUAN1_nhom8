package poly.ph26873.coffeepoly.ui;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
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
import poly.ph26873.coffeepoly.adapter.BillDaGiaoRCVAdapter;
import poly.ph26873.coffeepoly.models.Bill;
import poly.ph26873.coffeepoly.models.User;


public class BillFaildFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_bill_faild, container, false);
    }

    private static final String TAG = "zzz";
    private RecyclerView recyclerView;
    private BillDaGiaoRCVAdapter adapter;
    private FirebaseDatabase database;
    private List<User> listUser = new ArrayList<>();
    private List<Bill> listBill = new ArrayList<>();

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Đang tải dữ liệu...");
        progressDialog.show();
        recyclerView = view.findViewById(R.id.billFRecyclerView);
        LinearLayoutManager manager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(manager);
        recyclerView.setHasFixedSize(true);
        adapter = new BillDaGiaoRCVAdapter(getContext());
        database = FirebaseDatabase.getInstance();
        layListUser();
        progressDialog.dismiss();
    }

    private void layListUser() {
        Log.d(TAG, "layListUser");
        DatabaseReference refuser = database.getReference("coffee-poly").child("user");
        refuser.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                User user = snapshot.getValue(User.class);
                if (user != null) {
                    listUser.add(user);
                }
                if (listUser.size() > 0) {
                    layListBill(listUser);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

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

    private void layListBill(List<User> listUser) {
        DatabaseReference reference = database.getReference("coffee-poly/bill");
        for (int i = 0; i < listUser.size(); i++) {
            reference.child(listUser.get(i).getId()).addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                    Bill bill = snapshot.getValue(Bill.class);
                    if (bill != null) {
                        if (bill.getStatus() == 2) {
                            listBill.add(bill);
                        }
                    }
                    Collections.reverse(listBill);
                    adapter.setData(listBill);
                    recyclerView.setAdapter(adapter);
                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                    Bill bill = snapshot.getValue(Bill.class);
                    if (bill != null) {
                        if (bill.getStatus() == 2) {
                            listBill.add(bill);
                            Collections.reverse(listBill);
                            adapter.setData(listBill);
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