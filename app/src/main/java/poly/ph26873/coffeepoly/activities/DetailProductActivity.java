package poly.ph26873.coffeepoly.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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
import poly.ph26873.coffeepoly.models.Favorite;
import poly.ph26873.coffeepoly.models.Product;
import poly.ph26873.coffeepoly.models.TypeProduct;

public class DetailProductActivity extends AppCompatActivity {
    private static final String TAG = "zzz";
    private static final String TABLE_NAME = "coffee-poly";
    private static final String COL_TYPE_PRODUCT = "type_product";
    private ImageView imv_detail_product_favorite, imv_detail_product_avatar, imv_back_layout_detail_product, imv_detai_product_remove, imv_detai_product_add;
    private TextView tv_detai_product_total, tv_detai_product_name, tv_detai_product_content, tv_detai_product_quantitySold, tv_detai_product_status, tv_detai_product_type, tv_detai_product_price, tv_detai_product_quantity;
    private ScrollView scrV_content;
    private int a = 1;
    private Button btn_detai_product_add_to_cart;
    private static final String COL_FAVORITE = "favorite";
    private List<Integer> favoriteList = new ArrayList<>();
    private Product product;
    private int indexFavorite;
    private int b = -1;
    private String idu;
    private int vitriSanPhamtrongList;
    private DatabaseReference reference;
    private int aa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_product);
        initUi();
        Intent intent = getIntent();
        product = (Product) intent.getSerializableExtra("product");
        aa = intent.getIntExtra("a", 1);
        backActivity();
        showInformationProduct();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        assert user != null;
        idu = user.getEmail().replaceAll("@gmail.com", "");
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        reference = database.getReference(TABLE_NAME).child(COL_FAVORITE).child(idu);
        kiemtraSanPhamTrongFavorite();
        onClickImagefavorite();
    }


    private void onClickImagefavorite() {
        imv_detail_product_favorite.setOnClickListener(v -> {
            if (indexFavorite == 1) {
                themVaoFavorite();
            } else if (indexFavorite == 2) {
                xoaFavorite();
            }
        });

    }

    private void xoaFavorite() {
        reference.child("list_id_product").child(vitriSanPhamtrongList + "").removeValue((error, ref) -> {
            imv_detail_product_favorite.setImageResource(R.drawable.heart);
            Log.d(TAG, "delete favorite: successfully");
            Log.d(TAG, "da xoa san pham " + product.getName());
            restart();
        });

    }

    private void themVaoFavorite() {
        Favorite favorite = new Favorite();
        favorite.setId_user(idu);
        favoriteList.add(product.getId());
        favorite.setList_id_product(favoriteList);
        reference.setValue(favorite, (error, ref) -> {
            imv_detail_product_favorite.setImageResource(R.drawable.heart1);
            Log.d(TAG, "add favorite: successfully");
            Log.d(TAG, "cap nhat lai list f: ");
            restart();
        });

    }

    private void restart() {
        Intent intent = new Intent(DetailProductActivity.this, DetailProductActivity.class);
        intent.putExtra("product", product);
        int dfg = Integer.parseInt(tv_detai_product_quantity.getText().toString().trim());
        intent.putExtra("a", dfg);
        startActivity(intent);
        intent.clone();
    }


    private void kiemtraSanPhamTrongFavorite() {
        Log.d(TAG, "idu: " + idu);
        reference.child("list_id_product").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    favoriteList.add(dataSnapshot.getValue(Integer.class));
                }
                Log.d(TAG, "favoriteList: " + favoriteList.size());
                if (favoriteList == null || favoriteList.size() == 0) {
                    indexFavorite = 1;
                    Log.d(TAG, "list f null hoac f.list null ");
                    imv_detail_product_favorite.setImageResource(R.drawable.heart);
                } else {
                    Log.d(TAG, "list f khong null: ");
                    Integer idp = product.getId();
                    for (int i = 0; i < favoriteList.size(); i++) {
                        if (idp == favoriteList.get(i)) {
                            Log.d(TAG, "id co trong list: ");
                            vitriSanPhamtrongList = i;
                            Log.d(TAG, "vitriSanPhamtrongList: " + vitriSanPhamtrongList);
                            b++;
                        }
                    }
                    if (b == -1) {
                        indexFavorite = 1;
                        Log.d(TAG, "id khong co trong list: ");
                        imv_detail_product_favorite.setImageResource(R.drawable.heart);
                    } else {
                        Log.d(TAG, "id co trong list: ");
                        indexFavorite = 2;
                        imv_detail_product_favorite.setImageResource(R.drawable.heart1);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

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
        btn_detai_product_add_to_cart.setOnClickListener(v -> {
            Log.d(TAG, "quatity: " + a);
            Toast.makeText(DetailProductActivity.this, "Chức năng này chưa code", Toast.LENGTH_SHORT).show();
        });
    }

    @SuppressLint("SetTextI18n")
    private void showInformationProduct() {
        if (product != null) {
            Log.d(TAG, "id product: " + product.getId());
            imv_detail_product_avatar.setImageResource(product.getImage());
            tv_detai_product_name.setText(product.getName());
            tv_detai_product_content.setText(product.getContent());
            if (product.getContent().length() <= 150) {
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                scrV_content.setLayoutParams(lp);
            }
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
            if (aa > 0) {
                tv_detai_product_total.setText("Thành tiền: " + aa * product.getPrice() + "K");
                tv_detai_product_quantity.setText(aa + "");
            } else {
                tv_detai_product_total.setText("Thành tiền: " + product.getPrice() + "K");
                tv_detai_product_quantity.setText(a + "");
            }
            changeQuantityProduct(product.getPrice());
        }
    }

    private void backActivity() {
        imv_back_layout_detail_product.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DetailProductActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    private void initUi() {
        imv_detail_product_avatar = findViewById(R.id.imv_detail_product_avatar);
        imv_back_layout_detail_product = findViewById(R.id.imv_back_layout_detail_product);
        imv_detai_product_remove = findViewById(R.id.imv_detai_product_remove);
        imv_detai_product_add = findViewById(R.id.imv_detai_product_add);
        imv_detail_product_favorite = findViewById(R.id.imv_detail_product_favorite);
        tv_detai_product_name = findViewById(R.id.tv_detai_product_name);
        tv_detai_product_content = findViewById(R.id.tv_detai_product_content);
        tv_detai_product_quantitySold = findViewById(R.id.tv_detai_product_quantitySold);
        tv_detai_product_status = findViewById(R.id.tv_detai_product_status);
        tv_detai_product_type = findViewById(R.id.tv_detai_product_type);
        tv_detai_product_price = findViewById(R.id.tv_detai_product_price);
        tv_detai_product_quantity = findViewById(R.id.tv_detai_product_quantity);
        tv_detai_product_total = findViewById(R.id.tv_detai_product_total);
        btn_detai_product_add_to_cart = findViewById(R.id.btn_detai_product_add_to_cart);
        scrV_content = findViewById(R.id.scrV_content);
    }
}