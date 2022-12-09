package poly.ph26873.coffeepoly.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import poly.ph26873.coffeepoly.R;
import poly.ph26873.coffeepoly.models.Bill;
import poly.ph26873.coffeepoly.models.User;
import poly.ph26873.coffeepoly.service.MyReceiver;

public class DetailUserActivity extends AppCompatActivity {

    private ImageView imv_back_layout_detail_user, imv_avatar_detail_user;
    private TextView tv_name_detail_user, tv_email_detail_user, tv_id_detail_user, tv_age_detail_user,
            tv_gender_detail_user, tv_nb_detail_user, tv_email1_detail_user, tv_ad_detail_user,
            tv_enable_detail_user, tv_pw_detail_user, tv_bill_tc, tv_bill_tb, tv_bill_tb1;
    private LinearLayout ln_internet, ln_detail_user;
    private Button btn_ycmk, btn_dis, btn_ena;
    private FirebaseDatabase database;
    private String pa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_user);
        database = FirebaseDatabase.getInstance();
        initUI();
        back();
        Intent intent = getIntent();
        User user = (User) intent.getSerializableExtra("user");
        show(user);
        onclick(user);
    }

    private void onclick(User user) {
        btn_ena.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MyReceiver.isConnected == false) {
                    Toast.makeText(DetailUserActivity.this, "Không có kết nối mạng", Toast.LENGTH_SHORT).show();
                    return;
                }
                DatabaseReference reference1 = database.getReference("coffee-poly").child("user").child(user.getId()).child("enable");
                reference1.setValue(0);
            }
        });
        btn_dis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MyReceiver.isConnected == false) {
                    Toast.makeText(DetailUserActivity.this, "Không có kết nối mạng", Toast.LENGTH_SHORT).show();
                    return;
                }
                DatabaseReference reference1 = database.getReference("coffee-poly").child("user").child(user.getId()).child("enable");
                reference1.setValue(1);
            }
        });
        btn_ycmk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MyReceiver.isConnected == false) {
                    Toast.makeText(DetailUserActivity.this, "Không có kết nối mạng", Toast.LENGTH_SHORT).show();
                    return;
                }
                FirebaseAuth auth = FirebaseAuth.getInstance();
                auth.sendPasswordResetEmail(user.getEmail())
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(DetailUserActivity.this, "Chúng tôi đã gửi yêu cầu thiết lập lại mật khẩu qua email", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(DetailUserActivity.this, "Đã có lỗi xảy ra khi gửi mã thiết lập lại mật khẩu", Toast.LENGTH_SHORT).show();
                            }
                        });

            }
        });
    }


    private void show(User user) {
        if (user != null) {
            if (MyReceiver.isConnected == false) {
                if (user.getEnable() == 1) {
                    tv_enable_detail_user.setText("Vô hiệu hóa");
                    ln_detail_user.setBackgroundResource(R.color.black1);
                    anChucNang(btn_dis, btn_ena);
                } else {
                    ln_detail_user.setBackgroundResource(R.color.white);
                    anChucNang(btn_ena, btn_dis);
                    tv_enable_detail_user.setText("Đã kích hoạt");
                }
                ln_internet.setVisibility(View.VISIBLE);
                Glide.with(this).load(user.getImage()).error(R.drawable.image_guest).into(imv_avatar_detail_user);
                tv_name_detail_user.setText(user.getName());
                tv_email_detail_user.setText(user.getEmail());
                tv_id_detail_user.setText(user.getId());
                tv_age_detail_user.setText(user.getAge() + "");
                tv_gender_detail_user.setText(user.getGender());
                tv_nb_detail_user.setText(user.getNumberPhone());
                tv_email1_detail_user.setText(user.getEmail());
                tv_ad_detail_user.setText(user.getAddress());
                tv_bill_tc.setText("Lấy dữ liệu thất bại");
                tv_bill_tb.setText("Lấy dữ liệu thất bại");
                tv_bill_tb1.setText("Lấy dữ liệu thất bại");
            } else {
                ln_internet.setVisibility(View.INVISIBLE);
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference reference = database.getReference("coffee-poly").child("user").child(user.getId());
                reference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        User user1 = snapshot.getValue(User.class);
                        if (user1 != null) {
                            if (user1.getEnable() == 1) {
                                ln_detail_user.setBackgroundResource(R.color.black1);
                                anChucNang(btn_dis, btn_ena);
                            } else {
                                ln_detail_user.setBackgroundResource(R.color.white);
                                anChucNang(btn_ena, btn_dis);
                            }
                            Glide.with(getApplicationContext()).load(user1.getImage()).error(R.drawable.image_guest).into(imv_avatar_detail_user);
                            tv_name_detail_user.setText(user1.getName());
                            tv_email_detail_user.setText(user1.getEmail());
                            tv_id_detail_user.setText(user1.getId());
                            tv_age_detail_user.setText(user1.getAge() + "");
                            tv_gender_detail_user.setText(user1.getGender());
                            tv_nb_detail_user.setText(user1.getNumberPhone());
                            tv_email1_detail_user.setText(user1.getEmail());
                            tv_ad_detail_user.setText(user1.getAddress());
                            if (user1.getEnable() == 0) {
                                tv_enable_detail_user.setText("Đã kích hoạt");
                            } else {
                                tv_enable_detail_user.setText("Vô hiệu hóa");
                            }
                            DatabaseReference reference1 = database.getReference("coffee-poly").child("pw_user").child(user.getId());
                            reference1.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    pa = snapshot.getValue(String.class);
                                    tv_pw_detail_user.setText(pa);
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                List<Bill> list4 = new ArrayList<>();
                List<Bill> list2 = new ArrayList<>();
                List<Bill> list5 = new ArrayList<>();

                DatabaseReference reference1 = database.getReference("coffee-poly").child("bill").child(user.getId());
                reference1.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            Bill bill = dataSnapshot.getValue(Bill.class);
                            if (bill != null) {
                                if (bill.getStatus() == 2) {
                                    list2.add(bill);
                                } else if (bill.getStatus() == 4) {
                                    list4.add(bill);
                                } else {
                                    list5.add(bill);
                                }
                            }
                        }
                        tv_bill_tc.setText(String.valueOf(list4.size()));
                        tv_bill_tb.setText(String.valueOf(list2.size()));
                        tv_bill_tb1.setText(String.valueOf(list5.size()));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        }
    }

    private void anChucNang(Button button, Button button1) {
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                0, 0);
        button.setLayoutParams(lp);
        LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        button1.setLayoutParams(lp1);
    }

    private void back() {
        imv_back_layout_detail_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initUI() {
        imv_back_layout_detail_user = findViewById(R.id.imv_back_layout_detail_user);
        imv_avatar_detail_user = findViewById(R.id.imv_avatar_detail_user);
        tv_name_detail_user = findViewById(R.id.tv_name_detail_user);
        tv_email_detail_user = findViewById(R.id.tv_email_detail_user);
        tv_id_detail_user = findViewById(R.id.tv_id_detail_user);
        tv_age_detail_user = findViewById(R.id.tv_age_detail_user);
        tv_gender_detail_user = findViewById(R.id.tv_gender_detail_user);
        tv_nb_detail_user = findViewById(R.id.tv_nb_detail_user);
        tv_email1_detail_user = findViewById(R.id.tv_email1_detail_user);
        tv_ad_detail_user = findViewById(R.id.tv_ad_detail_user);
        tv_enable_detail_user = findViewById(R.id.tv_enable_detail_user);
        tv_pw_detail_user = findViewById(R.id.tv_pw_detail_user);
        ln_internet = findViewById(R.id.ln_internet);
        ln_detail_user = findViewById(R.id.ln_detail_user);
        btn_dis = findViewById(R.id.btn_dis);
        btn_ycmk = findViewById(R.id.btn_ycmk);
        btn_ena = findViewById(R.id.btn_ena);
        tv_bill_tc = findViewById(R.id.tv_bill_tc);
        tv_bill_tb = findViewById(R.id.tv_bill_tb);
        tv_bill_tb1 = findViewById(R.id.tv_bill_tb1);
    }
}