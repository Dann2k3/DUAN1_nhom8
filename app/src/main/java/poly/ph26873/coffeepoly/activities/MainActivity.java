package poly.ph26873.coffeepoly.activities;

import android.annotation.SuppressLint;
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
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.IOException;

import poly.ph26873.coffeepoly.R;
import poly.ph26873.coffeepoly.ui.CartFragment;
import poly.ph26873.coffeepoly.ui.FavouriteFragment;
import poly.ph26873.coffeepoly.ui.HistoryFragment;
import poly.ph26873.coffeepoly.ui.HomeFragment;
import poly.ph26873.coffeepoly.ui.PassWordFragment;
import poly.ph26873.coffeepoly.ui.SettingFragment;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private Toolbar toolbar;
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private int count = 0;
    private ImageView img_avatar,imv_back_layout_header;
    private TextView tv_name, tv_email;
    private static final String TAG = "zzz";
    private final SettingFragment settingFragment = new SettingFragment();
    public static final int MY_REQUESTCODE = 511;


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
        initUi();
        setSupportActionBar(toolbar);
        showToolBar("Trang chủ");
        toolbarAddNav();
        showInfomationUser();

    }

    public void showInfomationUser() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            return;
        }
        String name = user.getDisplayName();
        String email = user.getEmail();
        Uri photoUrl = user.getPhotoUrl();
        if (name != null && !name.trim().isEmpty()) {
            tv_name.setText(name);
        } else {
            int c = 0;
            if (email != null) {
                for (int i = 0; i < email.length(); i++) {
                    if (String.valueOf(email.charAt(i)).equals("@")) {
                        c = i;
                        Log.d(TAG, "index name : " + c);
                        continue;
                    }
                    Log.d(TAG, "i : " + i);
                }
            }
            assert email != null;
            name = email.substring(0, c);
            tv_name.setText(name);
        }
        tv_email.setText(email);
        Glide.with(this).load(photoUrl).error(R.drawable.anime_naruto).into(img_avatar);
        Log.d(TAG, "name user: " + name);
        Log.d(TAG, "email user: " + email);
        Log.d(TAG, "avatar user: " + photoUrl);
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
        navigationView.setCheckedItem(R.id.nav_home);
        replaceFragmemt(new HomeFragment());
        navigationView.getMenu().findItem(R.id.nav_home).setChecked(true);

        imv_back_layout_header.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeNavigation();
            }
        });
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
                break;
            case R.id.nav_cart:
                replaceFragmemt(new CartFragment());
                hieuUngChecked(id);
                showToolBar("Đặt hàng");
                closeNavigation();
                break;
            case R.id.nav_favourite:
                replaceFragmemt(new FavouriteFragment());
                hieuUngChecked(id);
                closeNavigation();
                showToolBar("Yêu thích");
                break;
            case R.id.nav_history:
                replaceFragmemt(new HistoryFragment());
                hieuUngChecked(id);
                closeNavigation();
                showToolBar("Lịch sử");
                break;
            case R.id.nav_setting:
                replaceFragmemt(settingFragment);
                hieuUngChecked(id);
                closeNavigation();
                showToolBar("Thiết lập tài khoản");
                break;
            case R.id.nav_password:
                replaceFragmemt(new PassWordFragment());
                hieuUngChecked(id);
                closeNavigation();
                showToolBar("Thay đổi mật khẩu");
                break;
            case R.id.nav_logout:
                Toast.makeText(this, "Logout account", Toast.LENGTH_SHORT).show();
                hieuUngChecked(id);
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
                break;

        }
        return false;
    }

    private void hieuUngChecked(int id) {
        int[] mang = {R.id.nav_home, R.id.nav_cart, R.id.nav_favourite, R.id.nav_history, R.id.nav_setting, R.id.nav_logout};
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