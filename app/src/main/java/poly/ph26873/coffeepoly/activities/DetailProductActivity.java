package poly.ph26873.coffeepoly.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import poly.ph26873.coffeepoly.listData.ListData;
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
    private TextView tv_detai_product_total, tv_detai_product_name, tv_detai_product_quantitySold, tv_detai_product_status, tv_detai_product_type, tv_detai_product_price, tv_detai_product_quantity;
    private int a = 1;
    private Button btn_detai_product_add_to_cart, btn_het_hang;
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
    private RelativeLayout re_het;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_product);
        initUi();
        database = FirebaseDatabase.getInstance();
        layIdUser();
        kiemTraLoaiTaiKhoan();
        Intent intent = getIntent();
        if (MyReceiver.isConnected == false) {
            product = (Product) intent.getSerializableExtra("product");
            kiemTraHetHang();
            layDanhSachYeuThich();
        } else {
            Product product1 = (Product) intent.getSerializableExtra("product");
            DatabaseReference reference = database.getReference("coffee-poly").child("product").child(String.valueOf(product1.getId()));
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    product = snapshot.getValue(Product.class);
                    if (product != null) {
                        kiemTraHetHang();
                        layDanhSachYeuThich();
                        showInformationProduct();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

        backActivity();
        showInformationProduct();
        addToCart();
    }

    private void layIdUser() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        assert user != null;
        idu = Objects.requireNonNull(user.getEmail()).replaceAll("@gmail.com", "");
    }

    private void kiemTraHetHang() {
        if (product.getStatus() == 1) {
            re_het.setVisibility(View.VISIBLE);
            ln_out.setVisibility(View.INVISIBLE);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, 0);
            ln_out.setLayoutParams(lp);
            btn_het_hang.setText("X??c nh???n c??n h??ng");
            btn_het_hang.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ChangeStatus(0);
                }
            });
        } else {
            re_het.setVisibility(View.INVISIBLE);
            if (ListData.type_user_current == 2) {
                ln_out.setVisibility(View.VISIBLE);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                ln_out.setLayoutParams(lp);
            } else {
                btn_het_hang.setText("X??c nh???n h???t h??ng h??ng");
                btn_het_hang.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ChangeStatus(1);
                    }
                });
            }
        }
    }

    private void ChangeStatus(int i) {
        if (MyReceiver.isConnected == false) {
            Toast.makeText(DetailProductActivity.this, "Kh??ng c?? k???t n???i m???ng", Toast.LENGTH_SHORT).show();
            return;
        }
        progressDialog.show();
        DatabaseReference reference = database.getReference("coffee-poly").child("product").child(String.valueOf(product.getId())).child("status");
        reference.setValue(i, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                progressDialog.dismiss();
            }
        });
    }

    private void kiemTraLoaiTaiKhoan() {
        Log.d(TAG, "kiemTraLoaiTaiKhoan: " + ListData.type_user_current);
        if (ListData.type_user_current != 2) {
            imv_detail_product_favorite.setVisibility(View.INVISIBLE);
            ln_out.setVisibility(View.INVISIBLE);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, 0);
            ln_out.setLayoutParams(lp);
            btn_het_hang.setVisibility(View.VISIBLE);
        } else {
            imv_detail_product_favorite.setVisibility(View.VISIBLE);
            btn_het_hang.setVisibility(View.INVISIBLE);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, 0);
            btn_het_hang.setLayoutParams(lp);
            onClickImagefavorite();
        }
    }


    private void addToCart() {
        btn_detai_product_add_to_cart.setOnClickListener(v -> {
            if (!MyReceiver.isConnected) {
                Toast.makeText(this, "Kh??ng c?? k???t n???i m???ng", Toast.LENGTH_SHORT).show();
                return;
            }
            if (product.getStatus() == 1) {
                Toast.makeText(this, "S???n ph???m hi???n ??ang h???t h??ng", Toast.LENGTH_SHORT).show();
                return;
            }
            progressDialog.setMessage("??ang th??m v??o gi??? h??ng");
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
                Log.d(TAG, "???? th??m v??o gi??? h??ng ");
                Toast.makeText(DetailProductActivity.this, "???? th??m v??o gi??? h??ng", Toast.LENGTH_SHORT).show();
                AlertDialog.Builder builder = new AlertDialog.Builder(DetailProductActivity.this);
                builder.setTitle("S???n ph???m ???? ???????c chuy???n ?????n gi??? h??ng");
                builder.setMessage("B???n c?? mu???n chuy???n ?????n gi??? h??ng?");
                builder.setPositiveButton("Ti???p t???c", (dialog, which) -> {
                    Intent intent = new Intent(DetailProductActivity.this, MainActivity.class);
                    intent.putExtra("goto", "cart");
                    startActivity(intent);
                    finish();
                });
                builder.setNegativeButton("Quay l???i", (dialog, which) -> {
                });
                builder.setCancelable(true);
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            });
        });
    }

    private void layDanhSachYeuThich() {
        if (ListData.type_user_current != 2) {
            return;
        }
        favoriteList.clear();
        reference = database.getReference(TABLE_NAME).child(COL_FAVORITE).child(idu).child("list_id_product");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot data : snapshot.getChildren()) {
                    favoriteList.add(data.getValue(Integer.class));
                }
                Log.d(TAG, "size c???a danh s??ch y??u th??ch: " + favoriteList.size());
                Log.d(TAG, "id trong danh s??ch y??u th??ch: " + favoriteList);
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
            Log.d(TAG, " listfavorite tr???ng");
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
            Log.d(TAG, "id product ???? c?? trong listfavorite ");
            Log.d(TAG, "PrInList " + PrInList);
        } else {
            imv_detail_product_favorite.setImageResource(R.drawable.heart);
            Log.d(TAG, "id product ch??a c?? trong listfavorite ");
            Log.d(TAG, "PrInList " + PrInList);
        }

    }


    private void onClickImagefavorite() {
        imv_detail_product_favorite.setOnClickListener(v -> {
            if (!MyReceiver.isConnected) {
                Toast.makeText(this, "Kh??ng c?? k???t n???i m???ng", Toast.LENGTH_SHORT).show();
                return;
            }
            if (PrInList == -1) {
                progressDialog.setMessage("??ang th??m v??o danh s??ch y??u th??ch");
                progressDialog.setCancelable(false);
                progressDialog.show();
                favoriteList.add(product.getId());
                reference.setValue(favoriteList, (error, ref) -> {
                    layDanhSachYeuThich();
                    progressDialog.dismiss();
                });
            } else {
                progressDialog.setMessage("??ang xo?? kh???i danh s??ch y??u th??ch");
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
            tv_detai_product_total.setText("Th??nh ti???n: " + price * a + "K");
        });
        imv_detai_product_remove.setOnClickListener(v -> {
            if (a == 1) {
                Toast.makeText(DetailProductActivity.this, "S??? l?????ng ??t nh???t b???ng 1", Toast.LENGTH_SHORT).show();
                return;
            }
            a--;
            tv_detai_product_quantity.setText(a + "");
            tv_detai_product_total.setText("Th??nh ti???n: " + price * a + "K");
        });

        //------------------------------------------------------------------------------------------------------
    }

    @SuppressLint("SetTextI18n")
    private void showInformationProduct() {
        if (product != null) {
            Log.d(TAG, "id product: " + product.getId());
            if (isValidContextForGlide(DetailProductActivity.this) == true) {
                Glide.with(DetailProductActivity.this).load(product.getImage()).error(R.color.red).into(imv_detail_product_avatar);
            }
            tv_detai_product_name.setText(product.getName());
            expandableTextView.setText(product.getContent() + "");
            tv_detai_product_quantitySold.setText("S??? l?????ng ???? b??n: " + product.getQuantitySold());
            if (product.getStatus() == 0) {
                tv_detai_product_status.setText("Tr???ng th??i s???n ph???m: C??n h??ng");
            } else {
                tv_detai_product_status.setText("Tr???ng th??i s???n ph???m: T???m th???i h???t h??ng");
            }

            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference reference = database.getReference(TABLE_NAME).child(COL_TYPE_PRODUCT).child(String.valueOf(product.getType()));
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    TypeProduct typeProduct = snapshot.getValue(TypeProduct.class);
                    assert typeProduct != null;
                    tv_detai_product_type.setText("Ngu???n g???c: " + typeProduct.getCountry());
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    tv_detai_product_type.setText("Ngu???n g???c: Kh??ng c?? d??? li???u");
                }
            });
            tv_detai_product_price.setText("????n gi??: " + product.getPrice() + "K");
            tv_detai_product_total.setText("Th??nh ti???n: " + product.getPrice() + "K");
            tv_detai_product_quantity.setText(a + "");
            changeQuantityProduct(product.getPrice());
        }
    }

    public static boolean isValidContextForGlide(final Context context) {
        if (context == null) {
            return false;
        }
        if (context instanceof Activity) {
            final Activity activity = (Activity) context;
            if (activity.isDestroyed() || activity.isFinishing()) {
                return false;
            }
        }
        return true;
    }

    private void backActivity() {
        imv_back_layout_detail_product.setOnClickListener(v -> finish());
    }

    private void initUi() {
        re_het = findViewById(R.id.re_het);
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
        btn_het_hang = findViewById(R.id.btn_het_hang);
        ln_out = findViewById(R.id.ln_out);
        expandableTextView = findViewById(R.id.expand_text_view);
        tv_detai_product_total = findViewById(R.id.tv_detai_product_total);
        progressDialog = new ProgressDialog(DetailProductActivity.this);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("sl", tv_detai_product_quantity.getText().toString());
        outState.putString("tt", tv_detai_product_total.getText().toString());
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        a = Integer.parseInt(savedInstanceState.getString("sl"));
        tv_detai_product_quantity.setText(savedInstanceState.getString("sl"));
        tv_detai_product_total.setText(savedInstanceState.getString("tt"));
    }
}