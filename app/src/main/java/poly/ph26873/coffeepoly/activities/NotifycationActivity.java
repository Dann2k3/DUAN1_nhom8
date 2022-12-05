package poly.ph26873.coffeepoly.activities;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
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
import java.util.Comparator;
import java.util.List;

import poly.ph26873.coffeepoly.R;
import poly.ph26873.coffeepoly.adapter.NotifyRCVAdapter;
import poly.ph26873.coffeepoly.models.Notify;

public class NotifycationActivity extends AppCompatActivity {

    private ImageView imageView;
    private int count = 0;
    private RecyclerView recyclerView;
    private List<Notify> list;
    private static String em;
    private FirebaseDatabase database;
    private static boolean isFirts = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifycation);
        back();
        showListNotifyCation();
    }

    private void showListNotifyCation() {
        recyclerView = findViewById(R.id.notiRecyclerView);
        LinearLayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(manager);
        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(itemDecoration);
        NotifyRCVAdapter adapter = new NotifyRCVAdapter(this);
        list = new ArrayList<>();
        database = FirebaseDatabase.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        em = user.getEmail().replace("@gmail.com", "");
        DatabaseReference myRef1 = database.getReference("coffee-poly/notify/" + em);
        myRef1.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Notify notify = snapshot.getValue(Notify.class);
                if (notify != null) {
                    list.add(notify);
                    Collections.sort(list, new Comparator<Notify>() {
                        @Override
                        public int compare(Notify o1, Notify o2) {
                            return o1.getStatus()-o2.getStatus();
                        }
                    });
                    adapter.setData(list);
                    recyclerView.setAdapter(adapter);
                    if (isFirts == true) {
                        setAL();
                        isFirts = false;
                    }

                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                Notify notify = snapshot.getValue(Notify.class);
                if (notify != null && !list.isEmpty()) {
                    for (int i = 0; i < list.size(); i++) {
                        if (notify.getId() == list.get(i).getId()) {
                            list.remove(i);
                            adapter.setData(list);
                            recyclerView.setAdapter(adapter);
                            setAL();
                            break;
                        }
                    }
                }

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        adapter.setData(list);
        recyclerView.setAdapter(adapter);
        setAL();
    }

    private void back() {
        imageView = findViewById(R.id.imv_back_layout_notify);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                    overridePendingTransition(R.anim.prev_enter, R.anim.prev_exit);
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        count++;
        if (count < 2) {
            Toast.makeText(getApplicationContext(), "Nhấn 2 lần để thoát", Toast.LENGTH_SHORT).show();
            Handler handler = new Handler();
            handler.postDelayed(() -> count = 0, 500);
        } else {
            finishAffinity();
            System.exit(0);
            super.onBackPressed();
        }
    }

    private void setAL() {
        LayoutAnimationController layoutAnimationController = AnimationUtils.loadLayoutAnimation(this, R.anim.layout_animation);
        recyclerView.setLayoutAnimation(layoutAnimationController);
    }

    @Override
    protected void onStop() {
        super.onStop();
        for (int i = 0; i < list.size(); i++) {
            list.get(i).setStatus(1);
        }
        DatabaseReference myRef1 = database.getReference("coffee-poly/notify/" + em);
        myRef1.setValue(list);
    }
}