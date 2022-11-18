package poly.ph26873.coffeepoly.ui;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import poly.ph26873.coffeepoly.R;
import poly.ph26873.coffeepoly.activities.MainActivity;
import poly.ph26873.coffeepoly.adapter.CartRCVAdapter;
import poly.ph26873.coffeepoly.models.Bill;
import poly.ph26873.coffeepoly.models.Item_Bill;
import poly.ph26873.coffeepoly.models.Product;


public class CartFragment extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_cart, container, false);
    }

    private RecyclerView cartRecyclerView;
    private CartRCVAdapter cartRCVAdapter;
    private List<Item_Bill> list;
    private TextView tv_cart_thong_ke, tv_cart_tong_tien;
    private Button btn_cart_order;
    private String email;
    private FirebaseDatabase database;
    private String thong_ke = "";
    private int tong_tien = 0;
    private List<Item_Bill> list1;
    private static final String TAG = "zzz";
    private List<String> listSPdel;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        cartRecyclerView = view.findViewById(R.id.cartRecyclerView);
        tv_cart_thong_ke = view.findViewById(R.id.tv_cart_thong_ke);
        tv_cart_tong_tien = view.findViewById(R.id.tv_cart_tong_tien);
        btn_cart_order = view.findViewById(R.id.btn_cart_order);
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        cartRecyclerView.setLayoutManager(manager);
        cartRecyclerView.setHasFixedSize(true);
        cartRCVAdapter = new CartRCVAdapter(getContext());
        list = new ArrayList<>();
        ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Đang tải dữ liệu của giỏ hàng");
        progressDialog.show();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        email = user.getEmail().replaceAll("@gmail.com", "");
        database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference("coffee-poly/cart/" + email);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                progressDialog.dismiss();
                list.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    list.add(dataSnapshot.getValue(Item_Bill.class));
                }
                cartRCVAdapter.setData(list);
                cartRecyclerView.setAdapter(cartRCVAdapter);
                layDanhSachTinhTien();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        btn_cart_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.setMessage("Đang tiến hàng đặt hàng....");
                progressDialog.show();
                DatabaseReference AddressRef = database.getReference("coffee-poly/user/" + email + "/address");
                AddressRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        progressDialog.dismiss();
                        String ad = snapshot.getValue(String.class);
                        if (ad.equalsIgnoreCase("null")) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                            builder.setTitle("Tài khoản chưa cập nhật địa chỉ");
                            builder.setMessage("Hãy cập nhật địa chỉ trước khi đặt hàng");
                            builder.setCancelable(true);
                            builder.setPositiveButton("Cập nhật ngay", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent(getContext(), MainActivity.class);
                                    intent.putExtra("goto", "setting");
                                    startActivity(intent);
                                }
                            });
                            builder.setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            });
                            AlertDialog alertDialog = builder.create();
                            alertDialog.show();
                            return;
                        }
                        String ret = String.valueOf(requireActivity().getIntent().hasExtra("return"));
                        if (ret != null && ret.equals("return")) {
                            getActivity().getIntent().getExtras().remove("return");
                            return;
                        }
                        if (tong_tien > 0) {
                            Calendar calendar = Calendar.getInstance();
                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd_MM_yyyy kk:mm:ss");
                            String time = simpleDateFormat.format(calendar.getTime());
                            Bill bill = new Bill();
                            bill.setId(time);
                            bill.setList(list1);
                            bill.setTotal(tong_tien);
                            bill.setAddress(ad);
                            bill.setNote(thong_ke);
                            bill.setStatus(1);
                            DatabaseReference myBill = database.getReference("coffee-poly/bill/" + email + "/" + time);
                            myBill.setValue(bill, new DatabaseReference.CompletionListener() {
                                @Override
                                public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                                    Log.d(TAG, "Đã cập nhật đơn hàng lên bill");
                                    Toast.makeText(getContext(), "Đặt hàng thành công", Toast.LENGTH_SHORT).show();
                                    DatabaseReference delBillcurrent = database.getReference("coffee-poly/bill_current/" + email);
                                    delBillcurrent.removeValue(new DatabaseReference.CompletionListener() {
                                        @Override
                                        public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                                            Log.d(TAG, "Đã xóa bill hiện thời");
                                            xoaSanPhamTrongGioHang();
                                        }
                                    });
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });
    }

    private void xoaSanPhamTrongGioHang() {
        for (int i = 0; i < listSPdel.size(); i++) {
            int pp = i;
            DatabaseReference delBillcurrent = database.getReference("coffee-poly/cart/" + email + "/" + listSPdel.get(i));
            delBillcurrent.removeValue(new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                    Log.d(TAG, "Đã xóa sản phẩm trong giỏ hàng " + listSPdel.get(pp));
                }
            });
        }

    }


    public void layDanhSachTinhTien() {
        list1 = new ArrayList<>();
        DatabaseReference reference1 = database.getReference("coffee-poly/bill_current/" + email);
        reference1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list1.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    list1.add(dataSnapshot.getValue(Item_Bill.class));
                }
                if (list1.size() == 0) {
                    tv_cart_thong_ke.setText("");
                    tv_cart_tong_tien.setText("");
                    return;
                }
                layDanhSachSanPham(list1);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void layDanhSachSanPham(List<Item_Bill> list1) {
        List<Product> list2 = new ArrayList<>();
        DatabaseReference reference = database.getReference("coffee-poly/product");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    list2.add(dataSnapshot.getValue(Product.class));
                }
                soSanh(list1, list2);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void soSanh(List<Item_Bill> list1, List<Product> list2) {
        listSPdel = new ArrayList<>();
        listSPdel.clear();
        List<Product> list3 = new ArrayList<>();
        for (int i = 0; i < list1.size(); i++) {
            for (int j = 0; j < list2.size(); j++) {
                if (list1.get(i).getId_product() == list2.get(j).getId()) {
                    list3.add(list2.get(j));
                    listSPdel.add(list1.get(i).getTime());
                }
            }
        }
        tong_tien = 0;
        thong_ke = " ";
        for (int i = 0; i < list3.size(); i++) {
            tong_tien += list1.get(i).getQuantity() * list3.get(i).getPrice();
            thong_ke +=list3.get(i).getName() + "  x" + list1.get(i).getQuantity() + "  *" + list3.get(i).getPrice() + "K  = " + list1.get(i).getQuantity() * list3.get(i).getPrice() + "K" + "\n ";
        }
        tv_cart_thong_ke.setText(thong_ke);
        tv_cart_tong_tien.setText("Thanh toán: " + tong_tien + "K");
    }
}