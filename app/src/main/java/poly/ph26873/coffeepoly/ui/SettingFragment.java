package poly.ph26873.coffeepoly.ui;

import static poly.ph26873.coffeepoly.activities.MainActivity.MY_REQUESTCODE;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import poly.ph26873.coffeepoly.R;
import poly.ph26873.coffeepoly.activities.MainActivity;
import poly.ph26873.coffeepoly.activities.SignUpActivity;
import poly.ph26873.coffeepoly.models.User;


public class SettingFragment extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_setting, container, false);
    }

    private ImageView imv_avatar_frgst;
    private EditText edt_user_name_frgst, edt_age_frgst, edt_address_frgst;
    private Button btn_change_info_frgst;
    private TextInputLayout til_age_frgst, til_name_frgst, til_address_frgst;
    private static final String TAG = "zzz";
    private Uri mUri;
    private MainActivity mainActivity;
    private ProgressDialog progressDialog;
    private Spinner sp_gender_frgst;
    private SignUpActivity signUpActivity;
    private static final String TABLE_NAME = "coffee-poly";
    private static final String COL_USER = "user";


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG, "----------------SettingFragment-------------------");
        initUi(view);
        showInfomationAccount();
        initClickListener();
    }


    private void initClickListener() {
        imv_avatar_frgst.setOnClickListener(v -> checkPermission());
        btn_change_info_frgst.setOnClickListener(v -> onClickUpDateProfile());
    }


    private void onClickUpDateProfile() {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            return;
        }
        String name = edt_user_name_frgst.getText().toString().trim();

        if (name.isEmpty()) {
            til_name_frgst.setError("Không được để trống trường này!");
            edt_user_name_frgst.requestFocus();
            return;
        }
        til_name_frgst.setError("");
        if (edt_age_frgst.getText().toString().isEmpty()) {
            til_age_frgst.setError("Không được để trống trường này!");
            edt_age_frgst.requestFocus();
            return;
        }
        til_age_frgst.setError("");
        if (edt_address_frgst.getText().toString().trim().isEmpty()) {
            til_address_frgst.setError("Không được để trống trường này!");
            edt_address_frgst.requestFocus();
            return;
        }
        til_address_frgst.setError("");
        edt_address_frgst.clearFocus();
        edt_user_name_frgst.clearFocus();
        edt_age_frgst.clearFocus();
        progressDialog.show();
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(name)
                .setPhotoUri(mUri)
                .build();

        user.updateProfile(profileUpdates)
                .addOnCompleteListener(task -> {
                    progressDialog.dismiss();
                    if (task.isSuccessful()) {
                        Log.d(TAG, "User profile updated.");
                        Toast.makeText(getActivity(), "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                        mainActivity.showInfomationUser();
                        String EM = user.getEmail().replaceAll("@gmail.com", "");
                        User user1 = new User(EM, user.getDisplayName(), Integer.parseInt(edt_age_frgst.getText().toString().trim()), EM, sp_gender_frgst.getSelectedItem().toString(), edt_address_frgst.getText().toString().trim());
                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                        DatabaseReference newUser = database.getReference(TABLE_NAME).child(COL_USER).child(EM).child(EM);
                        newUser.setValue(user1, new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                                Log.d(TAG, "Cập nhật dữ liệu user");
                                Intent intent = getActivity().getIntent();
                                if(intent.getStringExtra("goto")!=null){
                                    String set = getActivity().getIntent().getExtras().getString("goto");
                                    if (set != null) {
                                        if (set.equalsIgnoreCase("setting")) {
                                            Intent intent1 = new Intent(getContext(), MainActivity.class);
                                            intent1.putExtra("goto", "cart");
                                            intent1.putExtra("return","return");
                                            startActivity(intent1);
                                            getActivity().finish();
                                        }
                                    }
                                }
                            }
                        });
                    }
                });
    }

    private void checkPermission() {
        if (mainActivity == null) {
            return;
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            mainActivity.openGallery();
            return;
        }
        if (getActivity().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            mainActivity.openGallery();
        } else
            getActivity().requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_REQUESTCODE);
    }


    private void showInfomationAccount() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            return;
        }
        ReadFrofileUser(user.getEmail().replaceAll("@gmail.com", ""));

        Uri avatar = user.getPhotoUrl();

        Glide.with(getActivity()).load(avatar).error(R.drawable.anime_naruto).into(imv_avatar_frgst);
        if (avatar != null) {
            Log.d(TAG, "uri img: " + avatar);
        }

    }

    public void ReadFrofileUser(String email1) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference readUser = database.getReference(TABLE_NAME).child(COL_USER).child(email1).child(email1);
        readUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                if (user != null) {
                    edt_user_name_frgst.setText(user.getName());
                    edt_age_frgst.setText(user.getAge() + "");
                    edt_address_frgst.setText(user.getAddress());
                    if (user.getGender().equalsIgnoreCase("Nam")) {
                        sp_gender_frgst.setSelection(0);
                    } else {
                        sp_gender_frgst.setSelection(1);
                    }
                    progressDialog.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d(TAG, "lỗi đọc thông tin user ");
            }
        });
    }

    private void initUi(View view) {
        til_name_frgst = view.findViewById(R.id.til_name_frgst);
        til_age_frgst = view.findViewById(R.id.til_age_frgst);
        til_address_frgst = view.findViewById(R.id.til_address_frgst);
        imv_avatar_frgst = view.findViewById(R.id.imv_avatar_frgst);
        edt_user_name_frgst = view.findViewById(R.id.edt_user_name_frgst);
        edt_age_frgst = view.findViewById(R.id.edt_age_frgst);
        edt_address_frgst = view.findViewById(R.id.edt_address_frgst);
        btn_change_info_frgst = view.findViewById(R.id.btn_change_info_frgst);
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setTitle("Đang cập nhật...");
        mainActivity = (MainActivity) getActivity();
        signUpActivity = new SignUpActivity();
        sp_gender_frgst = view.findViewById(R.id.sp_genger_frgst);
        ArrayList<String> listGender = new ArrayList<>();
        listGender.add("Nam");
        listGender.add("Nữ");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, listGender);
        sp_gender_frgst.setAdapter(adapter);
        progressDialog.show();
    }

    public void setBitmapImageview(Bitmap bitmap) {
        imv_avatar_frgst.setImageBitmap(bitmap);
    }

    public void setmUri(Uri mUri) {
        this.mUri = mUri;
    }
}