package poly.ph26873.coffeepoly.activities;

import android.annotation.SuppressLint;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.target.ImageViewTarget;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.net.URL;

import poly.ph26873.coffeepoly.R;
import poly.ph26873.coffeepoly.service.MyService;
import poly.ph26873.coffeepoly.ui.BillDaGiaoFragment;
import poly.ph26873.coffeepoly.ui.BillFaildFragment;
import poly.ph26873.coffeepoly.ui.BillFragment;
import poly.ph26873.coffeepoly.ui.CartFragment;
import poly.ph26873.coffeepoly.ui.FavouriteFragment;
import poly.ph26873.coffeepoly.ui.HistoryFragment;
import poly.ph26873.coffeepoly.ui.HomeFragment;
import poly.ph26873.coffeepoly.ui.ManagementFragment;
import poly.ph26873.coffeepoly.ui.PassWordFragment;
import poly.ph26873.coffeepoly.ui.SettingFragment;
import poly.ph26873.coffeepoly.ui.TopProductFragment;
import poly.ph26873.coffeepoly.ui.TurnoverFragment;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private static final String TABLE_NAME = "coffee-poly";
    private Toolbar toolbar;
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private int count = 0;
    private ImageView img_avatar, imv_back_layout_header;
    private TextView tv_name, tv_email;
    private static final String TAG = "zzz";
    private final SettingFragment settingFragment = new SettingFragment();
    public static final int MY_REQUESTCODE = 511;
    private int IDmenu = -1;
    private Intent intent;


    final private ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if (result.getResultCode() == RESULT_OK) {
            Intent intent = result.getData();
            if (intent == null) {
                return;
            }
            Uri uri = intent.getData();
            settingFragment.setmUri(uri);
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                settingFragment.setBitmapImageview(bitmap);
            } catch (IOException e) {
                Log.d(TAG, "set bitmap: error");
                e.printStackTrace();
            }
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "---------------MainActivity---------------");
        intent = getIntent();
        initUi();
        setSupportActionBar(toolbar);
        showToolBar("Trang chủ");
        toolbarAddNav();
        showInfomationUser();
        serviceConnection();
    }

    private void serviceConnection() {
        Intent intent = new Intent(MainActivity.this, MyService.class);
        startService(intent);
    }


    private void checkAccountType(FirebaseUser user) {
        assert user.getEmail() != null;
        String chilgPath = user.getEmail().replaceAll("@gmail.com", "");
        Log.d(TAG, "chilgPath: " + chilgPath);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference readUser = database.getReference(TABLE_NAME).child("type_user").child(chilgPath);
        readUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int type = snapshot.getValue(Integer.class);
                if (type == 2) {
                    navigationView.getMenu().findItem(R.id.nav_all_setting_1).setVisible(false);
                    navigationView.getMenu().findItem(R.id.nav_all_setting_2).setVisible(false);
                }
                if (type == 1) {
                    navigationView.getMenu().findItem(R.id.nav_all_setting_1).setVisible(false);
                    navigationView.getMenu().findItem(R.id.nav_all_setting_0).setVisible(false);
                    replaceFragmemt(new ManagementFragment());
                    hieuUngChecked(R.id.nav_order_management);
                    showToolBar("Quản lí hóa đơn");
                    IDmenu = R.id.nav_order_management;

                } else {
                    navigationView.getMenu().findItem(R.id.nav_all_setting_2).setVisible(false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void showInfomationUser() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            return;
        }
        String name = user.getDisplayName();
        String email = user.getEmail();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference("coffee-poly").child("user").child(email.replaceAll("@gmail.com", "")).child("image");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String image = snapshot.getValue(String.class);
                Uri uri = Uri.parse(image);
                Glide.with(MainActivity.this).asGif().load(uri).error("https://firebasestorage.googleapis.com/v0/b/coffepoly-f7e3b.appspot.com/o/gif_avatar.gif?alt=media&token=5755ac07-e204-491e-8f0d-8eb4df811505").into(img_avatar);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        if (name != null && !name.trim().isEmpty()) {
            tv_name.setText(name);
        } else {
            assert email != null;
            name = email.replaceAll("@gmail.com", "");
            tv_name.setText(name);
        }
        tv_email.setText(email);
        Log.d(TAG, "name user: " + name);
        Log.d(TAG, "email user: " + email);
        checkAccountType(user);
    }


    private void showToolBar(String title) {
        int max = title.length();
        SpannableString string = new SpannableString(title);
        string.setSpan(new RelativeSizeSpan(1.5f), 0, max, 0);
        string.setSpan(new StyleSpan(Typeface.BOLD_ITALIC), 0, max, 0);
        string.setSpan(new UnderlineSpan(), 0, max, 0);
        toolbar.setTitle(string);
    }

    private void initUi() {
        toolbar = findViewById(R.id.toolbar);
        navigationView = findViewById(R.id.navgation_view);
        drawerLayout = findViewById(R.id.drawer_layout);
        img_avatar = navigationView.getHeaderView(0).findViewById(R.id.img_avatar);
        tv_name = navigationView.getHeaderView(0).findViewById(R.id.tv_name);
        tv_email = navigationView.getHeaderView(0).findViewById(R.id.tv_email);
        imv_back_layout_header = navigationView.getHeaderView(0).findViewById(R.id.imv_back_layout_header);

    }

    private void toolbarAddNav() {
        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(MainActivity.this, drawerLayout, toolbar, R.string.nav_open, R.string.nav_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        String gotoFrg = intent.getStringExtra("goto");
        if (gotoFrg == null) {
            navigationView.setCheckedItem(R.id.nav_home);
            IDmenu = R.id.nav_home;
            replaceFragmemt(new HomeFragment());
            navigationView.getMenu().findItem(R.id.nav_home).setChecked(true);
        } else {
            if (gotoFrg.equals("cart")) {
                showToolBar("Đặt hàng");
                navigationView.setCheckedItem(R.id.nav_cart);
                IDmenu = R.id.nav_cart;
                replaceFragmemt(new CartFragment());
                navigationView.getMenu().findItem(R.id.nav_cart).setChecked(true);
//            } else if (gotoFrg.equals("home")) {
//                navigationView.setCheckedItem(R.id.nav_home);
//                IDmenu = R.id.nav_home;
//                replaceFragmemt(new HomeFragment());
//                navigationView.getMenu().findItem(R.id.nav_home).setChecked(true);
            } else if (gotoFrg.equals("setting")) {
                navigationView.setCheckedItem(R.id.nav_setting);
                IDmenu = R.id.nav_setting;
                replaceFragmemt(new SettingFragment());
                navigationView.getMenu().findItem(R.id.nav_setting).setChecked(true);
            } else {
                navigationView.setCheckedItem(R.id.nav_order_management);
                IDmenu = R.id.nav_order_management;
                replaceFragmemt(new ManagementFragment());
                navigationView.getMenu().findItem(R.id.nav_order_management).setChecked(true);
            }
        }

        imv_back_layout_header.setOnClickListener(v -> closeNavigation());
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.nav_home:
                replaceFragmemt(new HomeFragment());
                hieuUngChecked(id);
                showToolBar("Trang chủ");
                closeNavigation();
                IDmenu = id;
                break;
            case R.id.nav_cart:
                replaceFragmemt(new CartFragment());
                hieuUngChecked(id);
                showToolBar("Đặt hàng");
                closeNavigation();
                IDmenu = id;
                break;

            case R.id.nav_bill:
                replaceFragmemt(new BillFragment());
                hieuUngChecked(id);
                showToolBar("Đơn hàng của bạn");
                closeNavigation();
                IDmenu = id;
                break;
            case R.id.nav_favourite:
                replaceFragmemt(new FavouriteFragment());
                hieuUngChecked(id);
                closeNavigation();
                showToolBar("Yêu thích");
                IDmenu = id;
                break;
            case R.id.nav_history:
                replaceFragmemt(new HistoryFragment());
                hieuUngChecked(id);
                closeNavigation();
                showToolBar("Lịch sử");
                IDmenu = id;
                break;


            case R.id.nav_order_management:
                replaceFragmemt(new ManagementFragment());
                hieuUngChecked(id);
                closeNavigation();
                showToolBar("Quản lí hóa đơn");
                IDmenu = id;
                break;

            case R.id.nav_history_bill:
                replaceFragmemt(new BillDaGiaoFragment());
                hieuUngChecked(id);
                closeNavigation();
                showToolBar("Đơn hàng thành công");
                IDmenu = id;
                break;

            case R.id.nav_bill_faild:
                replaceFragmemt(new BillFaildFragment());
                hieuUngChecked(id);
                closeNavigation();
                showToolBar("Đơn hàng thất bại");
                IDmenu = id;
                break;


            case R.id.nav_turnover:
                replaceFragmemt(new TurnoverFragment());
                hieuUngChecked(id);
                closeNavigation();
                showToolBar("Doanh thu");
                IDmenu = id;
                break;

            case R.id.nav_top_product:
                replaceFragmemt(new TopProductFragment());
                hieuUngChecked(id);
                closeNavigation();
                showToolBar("Bảng xếp hạng sản phẩm");
                IDmenu = id;
                break;
            case R.id.nav_setting:
                replaceFragmemt(settingFragment);
                hieuUngChecked(id);
                closeNavigation();
                showToolBar("Thiết lập tài khoản");
                IDmenu = id;
                break;
            case R.id.nav_password:
                replaceFragmemt(new PassWordFragment());
                hieuUngChecked(id);
                closeNavigation();
                showToolBar("Thay đổi mật khẩu");
                IDmenu = id;
                break;
            case R.id.nav_logout:
                hieuUngChecked(id);
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Bạn muốn đăng xuất tài khoản?");
                builder.setMessage("Hãy nhấn đắng xuất.");
                builder.setPositiveButton("Đăng xuất", (dialog, which) -> {
                    Toast.makeText(MainActivity.this, "Logout account", Toast.LENGTH_SHORT).show();
                    FirebaseAuth.getInstance().signOut();
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                });
                builder.setNegativeButton("Hủy", (dialog, which) -> {
                    closeNavigation();
                    hieuUngChecked(IDmenu);
                });
                builder.setCancelable(false);
                AlertDialog alertDialog = builder.create();
                alertDialog.show();

                break;

        }
        return false;
    }

    private void hieuUngChecked(int id) {
        int[] mang = {R.id.nav_home, R.id.nav_cart, R.id.nav_favourite, R.id.nav_history, R.id.nav_setting, R.id.nav_logout, R.id.nav_bill, R.id.nav_turnover, R.id.nav_top_product, R.id.nav_order_management, R.id.nav_history_bill, R.id.nav_bill_faild};
        for (int j : mang) {
            navigationView.getMenu().findItem(j).setChecked(id == j);
        }
    }

    private void replaceFragmemt(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.content_frame, fragment).addToBackStack("back");
        transaction.commitAllowingStateLoss();
    }


    @Override
    public void onBackPressed() {
        count++;
        closeNavigation();
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

    private void closeNavigation() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
    }

    public void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        activityResultLauncher.launch(Intent.createChooser(intent, "Select picture"));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_REQUESTCODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openGallery();
            } else {
                Toast.makeText(this, "Hãy cấp quyền truy cập bộ nhớ", Toast.LENGTH_SHORT).show();
            }
        }
    }



}