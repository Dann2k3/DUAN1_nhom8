package poly.ph26873.coffeepoly.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import poly.ph26873.coffeepoly.R;
import poly.ph26873.coffeepoly.models.Product;
import poly.ph26873.coffeepoly.models.TypeProduct;

public class DetailProductActivity extends AppCompatActivity {
    private static final String TAG = "zzz";
    private static final String TABLE_NAME = "coffee-poly";
    private static final String COL_TYPE_PRODUCT = "type_product";
    private ImageView imv_back_layout_detail_product, imv_detai_product_remove, imv_detai_product_add;
    private TextView tv_detai_product_total, tv_detai_product_name, tv_detai_product_content, tv_detai_product_quantitySold, tv_detai_product_status, tv_detai_product_type, tv_detai_product_price, tv_detai_product_quantity;
    private ScrollView scrV_content;
    private int a = 1;
    private Button btn_detai_product_add_to_cart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_product);
        initUi();
        backActivity();
        showInformationProduct();
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
            Log.d(TAG, "quatity: "+a);
            Toast.makeText(DetailProductActivity.this, "Chức năng này chưa code", Toast.LENGTH_SHORT).show();
        });
    }

    @SuppressLint("SetTextI18n")
    private void showInformationProduct() {
        Intent intent = getIntent();
        Product product = (Product) intent.getSerializableExtra("product");
        if (product != null) {
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
            tv_detai_product_price.setText("Đơn giá: " + product.getPrice()+"K");
            tv_detai_product_total.setText("Thành tiền: " + product.getPrice()+"K");
            tv_detai_product_quantity.setText(a + "");
            changeQuantityProduct(product.getPrice());
        }
    }

    private void backActivity() {
        imv_back_layout_detail_product.setOnClickListener(v -> finish());
    }

    private void initUi() {
        imv_back_layout_detail_product = findViewById(R.id.imv_back_layout_detail_product);
        imv_detai_product_remove = findViewById(R.id.imv_detai_product_remove);
        imv_detai_product_add = findViewById(R.id.imv_detai_product_add);
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