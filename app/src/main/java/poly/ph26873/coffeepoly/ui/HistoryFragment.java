package poly.ph26873.coffeepoly.ui;

import android.app.ProgressDialog;
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
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import poly.ph26873.coffeepoly.R;
import poly.ph26873.coffeepoly.adapter.HistoryRCVAdapter;
import poly.ph26873.coffeepoly.models.History;


public class HistoryFragment extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_history, container, false);
    }

    private RecyclerView hisRecyclerView;
    private HistoryRCVAdapter historyRCVAdapter;
    private List<History> list;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        hisRecyclerView = view.findViewById(R.id.hisRecyclerView);
        list = new ArrayList<>();
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        hisRecyclerView.setLayoutManager(manager);
        hisRecyclerView.setHasFixedSize(true);
        historyRCVAdapter = new HistoryRCVAdapter(getContext());
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String email = user.getEmail().replaceAll("@gmail.com", "");
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference("coffee-poly/history/" + email);
        reference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                History history = snapshot.getValue(History.class);
                if (history != null && history.getStatus() == 0) {
                    list.add(history);
                    Collections.reverse(list);
                    historyRCVAdapter.setData(list);
                    hisRecyclerView.setAdapter(historyRCVAdapter);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                History history = snapshot.getValue(History.class);
                if (list == null || history == null) {
                    return;
                }
                    for (int i = 0; i < list.size(); i++) {
                        if (list.get(i).getId() == history.getId()) {
                            list.remove(list.get(i));
                            Collections.reverse(list);
                            historyRCVAdapter.setData(list);
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
//        reference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                list.clear();
//                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
//                    list.add(dataSnapshot.getValue(History.class));
//                }
//                Collections.reverse(list);
//                historyRCVAdapter.setData(list);
//                hisRecyclerView.setAdapter(historyRCVAdapter);
//                progressDialog.dismiss();
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
    }
}