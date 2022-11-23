package poly.ph26873.coffeepoly.activities;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import poly.ph26873.coffeepoly.R;
import poly.ph26873.coffeepoly.adapter.DetailTurnoverBillRCVAdapter;
import poly.ph26873.coffeepoly.adapter.DetailTurnoverRCVAdapter;
import poly.ph26873.coffeepoly.models.Bill;
import poly.ph26873.coffeepoly.models.Item_Bill;
import poly.ph26873.coffeepoly.models.Turnover;

public class DetailTurnoverActivity extends AppCompatActivity {
    private ImageView imv_back_layout_detail_turnover;
    private TextView tv_dt_turn_time, tv_dt_turn_name, tv_dt_turn_nbp, tv_dt_turn_email, tv_dt_turn_ad, tv_dt_turn_total;
    private RecyclerView dt_turn_RecyclerView;
    private DetailTurnoverBillRCVAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_turnover);
        initUi();
        Turnover turnover = (Turnover) getIntent().getSerializableExtra("turnover");
        if (turnover != null) {
            showInFomationBill(turnover);
        }
        onClicktoBack();
    }

    private void showInFomationBill(Turnover turnover) {
        ProgressDialog progressDialog = new ProgressDialog(DetailTurnoverActivity.this);
        progressDialog.setMessage("Đang tải dữ liệu");
        progressDialog.show();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference("coffee-poly").child("bill").child(turnover.getPath()).child(turnover.getTime());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                progressDialog.dismiss();
                Bill bill = snapshot.getValue(Bill.class);
                if (bill != null) {
                    tv_dt_turn_time.setText("Thời gian đặt: " + bill.getId());
                    tv_dt_turn_name.setText("Tên người đặt: " + bill.getName());
                    tv_dt_turn_nbp.setText("Số điện thoại: " + bill.getNumberPhone());
                    tv_dt_turn_email.setText("Email liên hệ: " + bill.getId_user() + "@gmail.com");
                    tv_dt_turn_ad.setText("Địa chỉ: " + bill.getAddress());
                    tv_dt_turn_total.setText("Số tiền đã thanh toán: " + bill.getTotal() + "K");
                    hienThiListSanPham(bill);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void hienThiListSanPham(Bill bill) {
        List<Item_Bill> list = bill.getList();
        adapter.setData(list);
        dt_turn_RecyclerView.setAdapter(adapter);
        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(DetailTurnoverActivity.this, DividerItemDecoration.VERTICAL);
        dt_turn_RecyclerView.addItemDecoration(itemDecoration);
    }

    private void onClicktoBack() {
        imv_back_layout_detail_turnover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initUi() {
        imv_back_layout_detail_turnover = findViewById(R.id.imv_back_layout_detail_turnover);
        tv_dt_turn_time = findViewById(R.id.tv_dt_turn_time);
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
}