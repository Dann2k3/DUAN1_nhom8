package poly.ph26873.coffeepoly.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import poly.ph26873.coffeepoly.R;

public class SplashActivity extends AppCompatActivity {

    private static final String TAG = "zzz";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        TextView tv_coofee_poly = findViewById(R.id.tv_coofee_poly);
        ObjectAnimator ob1 = ObjectAnimator.ofFloat(tv_coofee_poly, "rotation", 0f,360f);
        ob1.setDuration(2000);
        ObjectAnimator ob2 = ObjectAnimator.ofFloat(tv_coofee_poly, "alpha", 0.3f, 1.1f);
        ob2.setDuration(2000);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.play(ob1).with(ob2);
        animatorSet.start();
        Log.d(TAG, "--------------SplashActivity--------------");
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                nextActivity();
            }
        }, 3000);
    }

    private void nextActivity() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user == null){
            Intent intent = new Intent(SplashActivity.this,LoginActivity.class);
            startActivity(intent);
        }else {
            Toast.makeText(this, "Đăng nhập thành công", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "user: " +user.getEmail());
            Intent intent = new Intent(SplashActivity.this,MainActivity.class);
            startActivity(intent);
        }
        finish();
    }
}