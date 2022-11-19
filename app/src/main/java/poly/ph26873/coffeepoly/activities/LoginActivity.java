package poly.ph26873.coffeepoly.activities;

import android.app.ProgressDialog;
import android.content.Intent;
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

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "zzz";
    private ProgressDialog progressDialog;
    private FirebaseAuth mAuth;
    private EditText edtEmail, edtPass;
    private Button btnLogin;
    private TextView tv_reset_password;
    private TextInputLayout til_email, til_pass;
    //    public static final Pattern VALID_EMAIL_ADDRESS_REGEX =
//            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
    public static final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@gmail.com$", Pattern.CASE_INSENSITIVE);

    private int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Log.d(TAG, "---------LoginActivity------------- ");
        initUi();
        initAccount();
        checkUser();
        resetPassWord();
    }

    private void resetPassWord() {
        tv_reset_password.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, ResetPassWordActivity.class);
            String email_rs = edtEmail.getText().toString().trim();
            if (!email_rs.isEmpty()) {
                intent.putExtra("email_rs", email_rs);
            }
            startActivity(intent);
        });
    }

    private void initAccount() {
        Intent intent = getIntent();
        String email1 = intent.getStringExtra("email");
        String pass1 = intent.getStringExtra("pass");
        if (email1 != null) {
            edtEmail.setText(email1);
        }
        if (pass1 != null) {
            edtPass.setText(pass1);
        }
    }

    private void checkUser() {
        btnLogin.setOnClickListener(v -> {
            String email = edtEmail.getText().toString().trim().toLowerCase();
            String password = edtPass.getText().toString().trim();
            if (email.length() == 0) {
                til_email.setError("Email không được để trống");
                edtEmail.requestFocus();
                return;
            }
            if (!VALID_EMAIL_ADDRESS_REGEX.matcher(email).find()) {
                til_email.setError("Email không đúng định dạng");
                edtEmail.requestFocus();
                edtEmail.setSelection(email.length());
                return;
            }
            til_email.setError("");
            edtEmail.clearFocus();
            if (password.length() == 0) {
                til_pass.setError("Mật khẩu không được để trống");
                edtPass.requestFocus();
                return;
            }
            til_pass.setError("");
            edtPass.clearFocus();
            progressDialog.show();
            mAuth = FirebaseAuth.getInstance();
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(LoginActivity.this, task -> {
                        progressDialog.dismiss();
                        if (task.isSuccessful()) {
                            FirebaseDatabase database = FirebaseDatabase.getInstance();
                            DatabaseReference myRef1 = database.getReference("coffee-poly/bill_current/" + email.replaceAll("@gmail.com",""));
                            myRef1.removeValue(new DatabaseReference.CompletionListener() {
                                @Override
                                public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                                }
                            });
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            Toast.makeText(LoginActivity.this, "Đăng nhập thành công", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                            finishAffinity();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Tài khoản hoặc mật khẩu sai",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });

        });

    }


    private void initUi() {
        edtEmail = findViewById(R.id.edt_email);
        edtPass = findViewById(R.id.edt_pass);
        btnLogin = findViewById(R.id.btn_login);
        TextView tvSignUp = findViewById(R.id.tv_signup);
        til_email = findViewById(R.id.til_email);
        til_pass = findViewById(R.id.til_pass);
        tv_reset_password = findViewById(R.id.tv_reset_password);
        tvSignUp.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
            startActivity(intent);
        });
        progressDialog = new ProgressDialog(LoginActivity.this);
        progressDialog.setTitle("Đang đăng nhập");
        progressDialog.setMessage("Vui lòng đợi trong giây lát...");
    }

    @Override
    public void onBackPressed() {
        count++;
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
}