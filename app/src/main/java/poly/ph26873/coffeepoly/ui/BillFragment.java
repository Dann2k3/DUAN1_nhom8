package poly.ph26873.coffeepoly.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;

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
import java.util.Collections;
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


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d("zzz", "-----------BillFragment-------------- ");
        billRecyclerView = view.findViewById(R.id.billRecyclerView);
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        billRecyclerView.setLayoutManager(manager);
        billRecyclerView.setHasFixedSize(true);
        billRCVAdapter = new BillRCVAdapter(getContext());
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String email = user.getEmail().replaceAll("@gmail.com", "");
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference("coffee-poly/bill/" + email);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Bill> bills = new ArrayList<>();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    bills.add(dataSnapshot.getValue(Bill.class));
                }
                if (bills.size() > 0) {
                    List<Bill> bills1 = new ArrayList<>();
                    for (int i = 0; i < bills.size(); i++) {
                        if (bills.get(i).getStatus() == 1 || bills.get(i).getStatus() == 0) {
                            bills1.add(bills.get(i));
                            Log.d("zzz", "bills1.add(bills.get(i)): " + bills.get(i).getId());
                        }
                    }
                    Collections.reverse(bills1);
                    billRCVAdapter.setData(bills1);
                    setAL();
                    billRecyclerView.setAdapter(billRCVAdapter);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void setAL() {
        LayoutAnimationController layoutAnimationController = AnimationUtils.loadLayoutAnimation(getContext(), R.anim.layout_animation);
        billRecyclerView.setLayoutAnimation(layoutAnimationController);
    }
}