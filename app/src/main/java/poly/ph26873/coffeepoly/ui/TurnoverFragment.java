package poly.ph26873.coffeepoly.ui;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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
import poly.ph26873.coffeepoly.adapter.TurnoverRCVAdapter;
import poly.ph26873.coffeepoly.models.Turnover;

public class TurnoverFragment extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_turnover, container, false);
    }

    private RecyclerView tRecyclerView;
    private TurnoverRCVAdapter turnoverRCVAdapter;
    List<Turnover> list;
    private TextView tv_turn_all_total;
    private static final String TAG = "zzz";

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Đang tải dữ liệu...");
        progressDialog.show();
        tRecyclerView = view.findViewById(R.id.tRecyclerView);
        tv_turn_all_total = view.findViewById(R.id.tv_turn_all_total);
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        tRecyclerView.setLayoutManager(manager);
        tRecyclerView.setHasFixedSize(true);
        list = new ArrayList<>();
        turnoverRCVAdapter = new TurnoverRCVAdapter(getContext());
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference("coffee-poly/turnover");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    list.add(dataSnapshot.getValue(Turnover.class));
                }
                if(list.size()==0){
                    tv_turn_all_total.setVisibility(View.INVISIBLE);
                }else {
                    tv_turn_all_total.setVisibility(View.VISIBLE);
                    int tt = 0;
                    for (int i = 0; i < list.size(); i++) {
                        tt+=list.get(i).getTotal();
                    }
                    tv_turn_all_total.setText("Tổng doanh thu: "+tt+"K");
                }
                Log.d(TAG, "list: " + list.size());
                turnoverRCVAdapter.setData(list);
                tRecyclerView.setAdapter(turnoverRCVAdapter);
                RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
                tRecyclerView.addItemDecoration(itemDecoration);
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}