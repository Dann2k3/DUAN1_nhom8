package poly.ph26873.coffeepoly.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.regex.Pattern;

import poly.ph26873.coffeepoly.R;
import poly.ph26873.coffeepoly.models.User;
import poly.ph26873.coffeepoly.service.MyReceiver;

public class SignUpActivity extends AppCompatActivity {
    private static final String TAG = "zzz";
    private EditText edtEmail, edtPass;
    private Button btnSignUp;
    private TextView tvSignIn;
    private TextInputLayout til_email1, til_pass1;
    private FirebaseAuth mAuth;
    //    public static final Pattern VALID_EMAIL_ADDRESS_REGEX =
//            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
    public static final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@gmail.com$", Pattern.CASE_INSENSITIVE);
    private ProgressDialog progressDialog;
    private static final String TABLE_NAME = "coffee-poly";
    private static final String COL_USER = "user";
    private MyReceiver myReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        myReceiver = new MyReceiver();
        Log.d(TAG, "---------SignUpActivity------------- ");
        initUi();
        signInAccount();
        changeToSignUp();
    }

    private void changeToSignUp() {
        tvSignIn.setOnClickListener(v -> {
            Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void signInAccount() {
        btnSignUp.setOnClickListener(v -> {
            String email = edtEmail.getText().toString().trim().toLowerCase();
            String password = edtPass.getText().toString().trim();
            if (email.length() == 0) {
                til_email1.setError("Không được để trống trường này!");
                edtEmail.requestFocus();
                return;
            }
            if (!VALID_EMAIL_ADDRESS_REGEX.matcher(email).find()) {
                til_email1.setError("Email sai định dạng!");
                edtEmail.requestFocus();
                edtEmail.setSelection(email.length());
                return;
            }
            til_email1.setError("");
            edtEmail.clearFocus();
            if (password.length() < 6) {
                til_pass1.setError("Mật khẩu phải nhiều hơn 5 kí tự!");
                edtPass.requestFocus();
                return;
            }
            til_pass1.setError("");
            edtPass.clearFocus();
            if (MyReceiver.isConnected == false) {
                Toast.makeText(this, "Không có kết nối mạng", Toast.LENGTH_LONG).show();
                return;
            }
            progressDialog.setTitle("Đang tiến hành tạo tài khoản...");
            progressDialog.show();
            mAuth = FirebaseAuth.getInstance();
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(SignUpActivity.this, task -> {
                        progressDialog.dismiss();
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            String email1 = email.replaceAll("@gmail.com", "");
                            User user = new User(email1, email1, 18, email, "Nam", "null", "null", "https://firebasestorage.googleapis.com/v0/b/coffepoly-f7e3b.appspot.com/o/avatar.jpg?alt=media&token=131ad1fb-e9c5-49e6-a2b8-429955b12588");
                            CreateFrofileUser(user, email1);
                            Toast.makeText(SignUpActivity.this, "Tạo tài khoản thành công", Toast.LENGTH_SHORT).show();
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                                    intent.putExtra("email", email);
                                    intent.putExtra("pass", password);
                                    startActivity(intent);
                                    finishAffinity();
                                }
                            }, 1000);
                        } else {
                            Log.d(TAG, "tao tai khoan that bai");
                            Toast.makeText(SignUpActivity.this, "Tài khoản đã tồn tại!", Toast.LENGTH_SHORT).show();
                        }
                    });
        });
    }

    public void CreateFrofileUser(User user, String email1) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference newUser = database.getReference(TABLE_NAME).child(COL_USER).child(email1);
        newUser.setValue(user, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                Log.d(TAG, "tạo user trên firebase thành công... ");
                DatabaseReference keyRef = database.getReference(TABLE_NAME).child("type_user").child(email1);
                keyRef.setValue(2);
            }

        });
    }


    private void initUi() {
        edtEmail = findViewById(R.id.edt_email1);
        edtPass = findViewById(R.id.edt_pass1);
        btnSignUp = findViewById(R.id.btn_signup);
        tvSignIn = findViewById(R.id.tv_signin);
        til_email1 = findViewById(R.id.til_email1);
        til_pass1 = findViewById(R.id.til_pass1);
        progressDialog = new ProgressDialog(SignUpActivity.this);
        progressDialog.setTitle("Đang tạo tài khoản...");
    }

    @Override
    protected void onStart() {
        super.onStart();
        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(myReceiver, intentFilter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(myReceiver);
    }
}