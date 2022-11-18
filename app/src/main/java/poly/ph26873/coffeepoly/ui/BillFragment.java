package poly.ph26873.coffeepoly.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import poly.ph26873.coffeepoly.R;
import poly.ph26873.coffeepoly.adapter.BillRCVAdapter;
import poly.ph26873.coffeepoly.models.Bill;


public class BillFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_bill, container, false);
    }

    private RecyclerView billRecyclerView;
    private BillRCVAdapter billRCVAdapter;
    private List<Bill> list;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        billRecyclerView = view.findViewById(R.id.billRecyclerView);
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        billRecyclerView.setLayoutManager(manager);
        billRecyclerView.setHasFixedSize(true);
        billRCVAdapter = new BillRCVAdapter(getContext());
        list = new ArrayList<>();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String email = user.getEmail().replaceAll("@gmail.com", "");
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference("coffee-poly/bill/" + email);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Bill> bills = new ArrayList<>();
                bills.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    bills.add(dataSnapshot.getValue(Bill.class));
                }
                list.clear();
                for (int i = 0; i < bills.size(); i++) {
                    if (bills.get(i).getStatus() == 1) {
                        list.add(bills.get(i));
                    }
                }
                List<Bill> billList = new ArrayList<>();
                billList.clear();
                for (int i = list.size()-1; i >=0 ; i--) {
                    if(list.get(i).getStatus()==1){
                        billList.add(list.get(i));
                    }
                }
                billRCVAdapter.setData(billList);
                billRecyclerView.setAdapter(billRCVAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}