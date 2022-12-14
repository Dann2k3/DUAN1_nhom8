package poly.ph26873.coffeepoly.activities;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import poly.ph26873.coffeepoly.R;
import poly.ph26873.coffeepoly.adapter.DetailTurnoverBillRCVAdapter;
import poly.ph26873.coffeepoly.models.Bill;
import poly.ph26873.coffeepoly.models.Item_Bill;
import poly.ph26873.coffeepoly.models.Turnover;
import poly.ph26873.coffeepoly.service.MyReceiver;

public class DetailTurnoverActivity extends AppCompatActivity {
    private ImageView imv_back_layout_detail_turnover;
    private TextView tv_dt_turn_sta, tv_dt_turn_time, tv_dt_turn_name, tv_dt_turn_nbp, tv_dt_turn_email, tv_dt_turn_ad, tv_dt_turn_total;
    private RecyclerView dt_turn_RecyclerView;
    private DetailTurnoverBillRCVAdapter adapter;
    private FirebaseDatabase database;
    private static final String TAG = "zzz";
    private LinearLayout ln_internet_dt;
    private BroadcastReceiver receiver = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_turnover);
        initUi();
        database = FirebaseDatabase.getInstance();
        show();
        onClicktoBack();
        broadcast();
    }

    private void broadcast() {
        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (MyReceiver.isConnected == true) {
                    ln_internet_dt.setVisibility(View.INVISIBLE);
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, 0);
                    ln_internet_dt.setLayoutParams(lp);
                    show();
                } else {
                    ln_internet_dt.setVisibility(View.VISIBLE);
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    ln_internet_dt.setLayoutParams(lp);
                }
            }
        };

        registerReceiver(receiver, intentFilter);
    }


    private void show() {
        Turnover turnover = (Turnover) getIntent().getSerializableExtra("turnover");
        if (turnover != null) {
            Log.d(TAG, "onCreate: ");
            showInFomationBill(turnover);
            Log.d(TAG, "turnover !=null ");
        } else {
            String idb = getIntent().getStringExtra("id_bill");
            String idu = getIntent().getStringExtra("id_user");
            Log.d(TAG, "idb: " + idb);
            if (idb != null && idb.length() > 0) {
                showInFomationBill1(idb, idu);
            }
        }
    }

    private void showInFomationBill1(String idb, String idu) {
        ProgressDialog progressDialog = new ProgressDialog(DetailTurnoverActivity.this);
        progressDialog.setMessage("??ang t???i d??? li???u");
        progressDialog.show();
        if (idu != null && idu.length() > 0) {
            DatabaseReference reference = database.getReference("coffee-poly").child("bill").child(idu).child(idb);
            xuli(reference, progressDialog);
        } else {
            DatabaseReference reference = database.getReference("coffee-poly").child("bill").child(FirebaseAuth.getInstance().getCurrentUser().getEmail().replaceAll("@gmail.com", "")).child(idb);
            xuli(reference, progressDialog);
        }


    }

    private void xuli(DatabaseReference reference, ProgressDialog progressDialog) {
        reference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                progressDialog.dismiss();
                Bill bill = snapshot.getValue(Bill.class);
                if (bill != null) {
                    Dienthongtin(bill);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void showInFomationBill(Turnover turnover) {
        ProgressDialog progressDialog = new ProgressDialog(DetailTurnoverActivity.this);
        progressDialog.setMessage("??ang t???i d??? li???u");
        progressDialog.show();
        DatabaseReference reference = database.getReference("coffee-poly").child("bill").child(turnover.getPath()).child(turnover.getTime());
        reference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                progressDialog.dismiss();
                Bill bill = snapshot.getValue(Bill.class);
                if (bill != null) {
                    Dienthongtin(bill);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void Dienthongtin(Bill bill) {
        tv_dt_turn_time.setText("Th???i gian ?????t: " + bill.getId().replaceAll("_", "/"));
        tv_dt_turn_name.setText("T??n ng?????i ?????t: " + bill.getName());
        tv_dt_turn_nbp.setText("S??? ??i???n tho???i: " + bill.getNumberPhone());
        tv_dt_turn_email.setText("Email li??n h???: " + bill.getId_user() + "@gmail.com");
        tv_dt_turn_ad.setText("?????a ch???: " + bill.getAddress());
        if (bill.getStatus() == 4) {
            tv_dt_turn_sta.setText("Tr???ng th??i ????n h??ng: ???? giao");
            tv_dt_turn_total.setText("S??? ti???n ???? thanh to??n: " + bill.getTotal() + "K");
        } else if (bill.getStatus() == 5 || bill.getStatus() == 2) {
            tv_dt_turn_sta.setText("Tr???ng th??i ????n h??ng: ???? h???y");
            tv_dt_turn_total.setText("T???ng ti???n: " + bill.getTotal() + "K");
        } else {
            if (bill.getStatus() == 1) {
                tv_dt_turn_sta.setText("Tr???ng th??i ????n h??ng: Ch??? nh??n vi??n x??c nh???n");
            } else {
                tv_dt_turn_sta.setText("Tr???ng th??i ????n h??ng: ??ang giao h??ng");
            }
            tv_dt_turn_total.setText("S??? ti???n c???n thanh to??n: " + bill.getTotal() + "K");
        }

        hienThiListSanPham(bill);
    }

    private void hienThiListSanPham(Bill bill) {
        List<Item_Bill> list = bill.getList();
        adapter.setData(list);
        dt_turn_RecyclerView.setAdapter(adapter);
        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(DetailTurnoverActivity.this, DividerItemDecoration.VERTICAL);
        dt_turn_RecyclerView.addItemDecoration(itemDecoration);
    }

    private void onClicktoBack() {
        imv_back_layout_detail_turnover.setOnClickListener(v -> finish());
    }

    private void initUi() {
        imv_back_layout_detail_turnover = findViewById(R.id.imv_back_layout_detail_turnover);
        ln_internet_dt = findViewById(R.id.ln_internet_dt);
        tv_dt_turn_time = findViewById(R.id.tv_dt_turn_time);
        tv_dt_turn_sta = findViewById(R.id.tv_dt_turn_sta);
        tv_dt_turn_name = findViewById(R.id.tv_dt_turn_name);
        tv_dt_turn_nbp = findViewById(R.id.tv_dt_turn_nbp);
        tv_dt_turn_email = findViewById(R.id.tv_dt_turn_email);
        tv_dt_turn_ad = findViewById(R.id.tv_dt_turn_ad);
        tv_dt_turn_total = findViewById(R.id.tv_dt_turn_total);
        dt_turn_RecyclerView = findViewById(R.id.dt_turn_RecyclerView);
        LinearLayoutManager manager = new LinearLayoutManager(DetailTurnoverActivity.this, LinearLayoutManager.VERTICAL, false);
        dt_turn_RecyclerView.setLayoutManager(manager);
        dt_turn_RecyclerView.setHasFixedSize(true);
        adapter = new DetailTurnoverBillRCVAdapter(DetailTurnoverActivity.this);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (receiver != null) {
            unregisterReceiver(receiver);
            receiver = null;
        }
    }
}