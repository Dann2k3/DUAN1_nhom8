package poly.ph26873.coffeepoly.ui;

import android.app.ProgressDialog;
import android.os.Bundle;
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

import java.util.ArrayList;
import java.util.List;

import poly.ph26873.coffeepoly.R;
import poly.ph26873.coffeepoly.adapter.CartRCVAdapter;
import poly.ph26873.coffeepoly.models.Item_Bill;
import poly.ph26873.coffeepoly.models.Product;


public class CartFragment extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
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
                Toast.makeText(getContext(), "Chưa code chức năng này", Toast.LENGTH_SHORT).show();
            }
        });
    }


    public void layDanhSachTinhTien() {
        List<Item_Bill> list1 = new ArrayList<>();
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
        List<Product> list3 = new ArrayList<>();
        for (int i = 0; i < list1.size(); i++) {
            for (int j = 0; j < list2.size(); j++) {
                if (list1.get(i).getId_product() == list2.get(j).getId()) {
                    list3.add(list2.get(j));
                }
            }
        }
        String thong_ke = "";
        int tong_tien = 0;
        for (int i = 0; i < list3.size(); i++) {
            tong_tien += list1.get(i).getQuantity() * list3.get(i).getPrice();
            thong_ke +=list3.get(i).getName() + "  x" + list1.get(i).getQuantity() + "  *" + list3.get(i).getPrice() + "K  = " + tong_tien + "K" + "\n ";
        }
        tv_cart_thong_ke.setText(thong_ke);
        tv_cart_tong_tien.setText("Thanh toán: " + tong_tien + "K");
    }
}