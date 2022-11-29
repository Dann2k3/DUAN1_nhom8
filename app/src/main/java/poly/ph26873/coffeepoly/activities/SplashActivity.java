package poly.ph26873.coffeepoly.activities;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import poly.ph26873.coffeepoly.R;
import poly.ph26873.coffeepoly.listData.ListData;
import poly.ph26873.coffeepoly.service.MyService;

public class SplashActivity extends AppCompatActivity {

    private static final String TAG = "zzz";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
//        xoaFav();
        TextView tv_coofee_poly = findViewById(R.id.tv_coofee_poly);
        ObjectAnimator ob1 = ObjectAnimator.ofFloat(tv_coofee_poly, "rotation", 0f, 360f);
        ob1.setDuration(2000);
        ObjectAnimator ob2 = ObjectAnimator.ofFloat(tv_coofee_poly, "alpha", 0.3f, 1.1f);
        ob2.setDuration(2000);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.play(ob1).with(ob2);
        animatorSet.start();
        Log.d(TAG, "--------------SplashActivity--------------");

        if (kiemTraInternet() == true) {
            serviceConnection();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (ListData.type_user_current == -1) {
                        Toast.makeText(SplashActivity.this, "Đã xảy ra lỗi",Toast.LENGTH_SHORT).show();
                        System.exit(0);
                        return;
                    }else {
                        nextActivity();
                    }

                }
            }, 3000);
        } else {
            Toast.makeText(SplashActivity.this, "Hãy kiểm tra kết nối mạng", Toast.LENGTH_SHORT).show();
            AlertDialog.Builder builder = new AlertDialog.Builder(SplashActivity.this);
            builder.setTitle("Không có kết nối mạng");
            builder.setCancelable(false);
            builder.setPositiveButton("Thoát", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    System.exit(0);
                }
            });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
            return;
        }
    }

    private void serviceConnection() {
        Intent intent = new Intent(SplashActivity.this, MyService.class);
        startService(intent);
    }


    private boolean kiemTraInternet() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifi = connectivityManager.getNetworkInfo(connectivityManager.TYPE_WIFI);
        NetworkInfo mobile = connectivityManager.getNetworkInfo(connectivityManager.TYPE_MOBILE);
        if (wifi != null && wifi.isConnected() || (mobile != null && mobile.isConnected())) {
            return true;
        } else {
            return false;
        }
    }

    private void xoaFav() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference("coffee-poly/cart");
        reference.removeValue(new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                Toast.makeText(SplashActivity.this, "delete", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void nextActivity() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
            startActivity(intent);
        } else {
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference myRef1 = database.getReference("coffee-poly/bill_current/" + user.getEmail().replaceAll("@gmail.com", ""));
            myRef1.removeValue(new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                }
            });
            Toast.makeText(this, "Đăng nhập thành công", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "user: " + user.getEmail());
            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
            startActivity(intent);
        }
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        kiemTraInternet();
    }
    //hhfkrhghfkhghghg
}