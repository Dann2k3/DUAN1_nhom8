package poly.ph26873.coffeepoly.activities;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ms.square.android.expandabletextview.ExpandableTextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

import poly.ph26873.coffeepoly.R;
import poly.ph26873.coffeepoly.models.Item_Bill;
import poly.ph26873.coffeepoly.models.Product;
import poly.ph26873.coffeepoly.models.TypeProduct;
import poly.ph26873.coffeepoly.service.MyReceiver;

public class DetailProductActivity extends AppCompatActivity {
    private static final String TAG = "zzz";
    private static final String TABLE_NAME = "coffee-poly";
    private static final String COL_TYPE_PRODUCT = "type_product";
    private static final String COL_CART = "cart";
    private ImageView imv_detail_product_favorite, imv_detail_product_avatar, imv_back_layout_detail_product, imv_detai_product_remove, imv_detai_product_add;
    private TextView tv_detai_product_total, tv_detai_product_name, tv_detai_product_content, tv_detai_product_quantitySold, tv_detai_product_status, tv_detai_product_type, tv_detai_product_price, tv_detai_product_quantity;
    private int a = 1;
    private Button btn_detai_product_add_to_cart;
    private static final String COL_FAVORITE = "favorite";
    private final List<Integer> favoriteList = new ArrayList<>();
    private Product product;
    private String idu;
    private DatabaseReference reference;
    private int PrInList = 1;
    private ProgressDialog progressDialog;
    private int ViTri;
    private FirebaseDatabase database;
    private LinearLayout ln_out;
    private ExpandableTextView expandableTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_product);
        initUi();
        Intent intent = getIntent();
        product = (Product) intent.getSerializableExtra("product");
        backActivity();
        showInformationProduct();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        assert user != null;
        idu = Objects.requireNonNull(user.getEmail()).replaceAll("@gmail.com", "");
        database = FirebaseDatabase.getInstance();
        kiemTraLoaiTaiKhoan();
        layDanhSachYeuThich();
        onClickImagefavorite();
        addToCart();
    }

    private void kiemTraLoaiTaiKhoan() {
        DatabaseReference reference = database.getReference("coffee-poly").child("type_user").child(idu);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int type = Objects.requireNonNull(snapshot).getValue(Integer.class);
                if (type != 2) {
                    imv_detail_product_favorite.setVisibility(View.INVISIBLE);
                    ln_out.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void addToCart() {
        btn_detai_product_add_to_cart.setOnClickListener(v -> {
            if (!MyReceiver.isConnected) {
                Toast.makeText(this, "Không có kết nối mạng", Toast.LENGTH_SHORT).show();
                return;
            }
            progressDialog.setMessage("Đang thêm vào giỏ hàng");
            progressDialog.setCancelable(false);
            progressDialog.show();
            Item_Bill item_bill = new Item_Bill();
            item_bill.setId_product(product.getId());
            item_bill.setQuantity(a);
            Calendar calendar = Calendar.getInstance();
            @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd_MM_yyyy HH:mm:ss");
            String time = simpleDateFormat.format(calendar.getTime());
            item_bill.setTime(time);
            reference = database.getReference(TABLE_NAME).child(COL_CART).child(idu).child(time);
            reference.setValue(item_bill, (error, ref) -> {
                progressDialog.dismiss();
                Log.d(TAG, "Đã thêm vào giỏ hàng ");
                Toast.makeText(DetailProductActivity.this, "Đã thêm vào giỏ hàng", Toast.LENGTH_SHORT).show();
                AlertDialog.Builder builder = new AlertDialog.Builder(DetailProductActivity.this);
                builder.setTitle("Sản phẩm đã được chuyển đến giở hàng");
                builder.setMessage("Bạn có muốn chuyển đến giỏ hàng?");
                builder.setPositiveButton("Tiếp tục", (dialog, which) -> {
                    Intent intent = new Intent(DetailProductActivity.this, MainActivity.class);
                    intent.putExtra("goto", "cart");
                    startActivity(intent);
                    finish();
                });
                builder.setNegativeButton("Quay lại", (dialog, which) -> {
                });
                builder.setCancelable(true);
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            });
        });
    }

    private void layDanhSachYeuThich() {
        favoriteList.clear();
        reference = database.getReference(TABLE_NAME).child(COL_FAVORITE).child(idu).child("list_id_product");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot data : snapshot.getChildren()) {
                    favoriteList.add(data.getValue(Integer.class));
                }
                Log.d(TAG, "size của danh sách yêu thích: " + favoriteList.size());
                Log.d(TAG, "id trong danh sách yêu thích: " + favoriteList);
                kiemTraIdProductTrongList(favoriteList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d(TAG, "loi khi lay danh sach yeu thich !!!");
            }
        });
    }

    private void kiemTraIdProductTrongList(List<Integer> favoriteList1) {
        PrInList = -1;
        if (favoriteList1.size() == 0) {
            imv_detail_product_favorite.setImageResource(R.drawable.heart);
            Log.d(TAG, " listfavorite trống");
            Log.d(TAG, " PrInList :" + PrInList);
            return;
        }
        for (int i = 0; i < favoriteList1.size(); i++) {
            if (product.getId() == favoriteList1.get(i)) {
                PrInList = 1;
                ViTri = i;
                Log.d(TAG, "ViTri = " + ViTri);
            }
        }
        if (PrInList == 1) {
            imv_detail_product_favorite.setImageResource(R.drawable.heart1);
            Log.d(TAG, "id product đã có trong listfavorite ");
            Log.d(TAG, "PrInList " + PrInList);
        } else {
            imv_detail_product_favorite.setImageResource(R.drawable.heart);
            Log.d(TAG, "id product chưa có trong listfavorite ");
            Log.d(TAG, "PrInList " + PrInList);
        }

    }


    private void onClickImagefavorite() {
        imv_detail_product_favorite.setOnClickListener(v -> {
            if (!MyReceiver.isConnected) {
                Toast.makeText(this, "Không có kết nối mạng", Toast.LENGTH_SHORT).show();
                return;
            }
            if (PrInList == -1) {
                progressDialog.setMessage("Đang thêm vào danh sách yêu thích");
                progressDialog.setCancelable(false);
                progressDialog.show();
                favoriteList.add(product.getId());
                reference.setValue(favoriteList, (error, ref) -> {
                    layDanhSachYeuThich();
                    progressDialog.dismiss();
                });
            } else {
                progressDialog.setMessage("Đang xoá khỏi danh sách yêu thích");
                progressDialog.show();
                favoriteList.remove(ViTri);
                reference.setValue(favoriteList, (error, ref) -> {
                    layDanhSachYeuThich();
                    progressDialog.dismiss();
                });
            }
        });

    }


    @SuppressLint("SetTextI18n")
    private void changeQuantityProduct(long price) {
        imv_detai_product_add.setOnClickListener(v -> {
            a++;
            tv_detai_product_quantity.setText(a + "");
            tv_detai_product_total.setText("Thành tiền: " + price * a + "K");
        });
        imv_detai_product_remove.setOnClickListener(v -> {
            if (a == 1) {
                Toast.makeText(DetailProductActivity.this, "Số lượng ít nhất bằng 1", Toast.LENGTH_SHORT).show();
                return;
            }
            a--;
            tv_detai_product_quantity.setText(a + "");
            tv_detai_product_total.setText("Thành tiền: " + price * a + "K");
        });

        //------------------------------------------------------------------------------------------------------
    }

    @SuppressLint("SetTextI18n")
    private void showInformationProduct() {
        if (product != null) {
            Log.d(TAG, "id product: " + product.getId());
            Glide.with(DetailProductActivity.this).load(product.getImage()).error(R.color.red).into(imv_detail_product_avatar);
            tv_detai_product_name.setText(product.getName());
            expandableTextView.setText(product.getContent()+"");
            tv_detai_product_quantitySold.setText("Số lượng đã bán: " + product.getQuantitySold());
            tv_detai_product_status.setText("Trạng thái sản phẩm: " + product.getStatus());
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference reference = database.getReference(TABLE_NAME).child(COL_TYPE_PRODUCT).child(String.valueOf(product.getType()));
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    TypeProduct typeProduct = snapshot.getValue(TypeProduct.class);
                    assert typeProduct != null;
                    tv_detai_product_type.setText("Nguồn gốc: " + typeProduct.getCountry());
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    tv_detai_product_type.setText("Nguồn gốc: Không có dữ liệu");
                }
            });
            tv_detai_product_price.setText("Đơn giá: " + product.getPrice() + "K");
            tv_detai_product_total.setText("Thành tiền: " + product.getPrice() + "K");
            tv_detai_product_quantity.setText(a + "");
            changeQuantityProduct(product.getPrice());
        }
    }

    private void backActivity() {
        imv_back_layout_detail_product.setOnClickListener(v -> finish());
    }

    private void initUi() {
        imv_detail_product_avatar = findViewById(R.id.imv_detail_product_avatar);
        imv_back_layout_detail_product = findViewById(R.id.imv_back_layout_detail_product);
        imv_detai_product_remove = findViewById(R.id.imv_detai_product_remove);
        imv_detai_product_add = findViewById(R.id.imv_detai_product_add);
        imv_detail_product_favorite = findViewById(R.id.imv_detail_product_favorite);
        tv_detai_product_name = findViewById(R.id.tv_detai_product_name);
        tv_detai_product_quantitySold = findViewById(R.id.tv_detai_product_quantitySold);
        tv_detai_product_status = findViewById(R.id.tv_detai_product_status);
        tv_detai_product_type = findViewById(R.id.tv_detai_product_type);
        tv_detai_product_price = findViewById(R.id.tv_detai_product_price);
        tv_detai_product_quantity = findViewById(R.id.tv_detai_product_quantity);
        btn_detai_product_add_to_cart = findViewById(R.id.btn_detai_product_add_to_cart);
        ln_out = findViewById(R.id.ln_out);
        expandableTextView = findViewById(R.id.expand_text_view);
        tv_detai_product_total = findViewById(R.id.tv_detai_product_total);
        progressDialog = new ProgressDialog(DetailProductActivity.this);
    }
}