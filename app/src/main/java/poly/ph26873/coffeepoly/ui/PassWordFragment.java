package poly.ph26873.coffeepoly.ui;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import poly.ph26873.coffeepoly.R;
import poly.ph26873.coffeepoly.service.MyReceiver;


public class PassWordFragment extends Fragment {


    private static final String TAG = "zzz";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_pass_word, container, false);
    }

    private TextInputLayout til_pass_old, til_pass_new, til_pass_new1;
    private EditText edt_pass_old, edt_pass_new, edt_pass_new1;
    private Button btn_change_pass;
    private ProgressDialog progressDialog;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG, "-------------PassWordFragment--------------------- ");
        initUi(view);
        onClickChangerPassWord();
    }

    private void onClickChangerPassWord() {
        btn_change_pass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String passOld = edt_pass_old.getText().toString().trim();
                String passNew = edt_pass_new.getText().toString().trim();
                String passNew1 = edt_pass_new1.getText().toString().trim();
                if (passOld.isEmpty()) {
                    til_pass_old.setError("Kh??ng ???????c ????? tr???ng tr?????ng n??y!");
                    edt_pass_old.requestFocus();
                    return;
                }
                til_pass_old.setError("");
                if (passNew.isEmpty()) {
                    til_pass_new.setError("Kh??ng ???????c ????? tr???ng tr?????ng n??y!");
                    edt_pass_new.requestFocus();
                    return;
                }
                if (passNew.length() <= 5) {
                    til_pass_new.setError("M???t kh???u m???i ph???i nhi???u h??n 5 k?? t???!");
                    edt_pass_new.requestFocus();
                    return;
                }
                til_pass_new.setError("");
                if (passNew1.isEmpty()) {
                    til_pass_new1.setError("Kh??ng ???????c ????? tr???ng tr?????ng n??y!");
                    edt_pass_new1.requestFocus();
                    return;
                }
                til_pass_new1.setError("");
                if (!passNew.equals(passNew1)) {
                    til_pass_new1.setError("M???t kh???u nh???p l???i kh??ng kh???p!");
                    edt_pass_new1.requestFocus();
                    return;
                }
                til_pass_new1.setError("");
                if (MyReceiver.isConnected == false) {
                    Toast.makeText(getContext(), "Kh??ng c?? k???t n???i m???ng", Toast.LENGTH_LONG).show();
                    return;
                }
                progressDialog.show();
                UserAuthentication(passOld, passNew1);
            }
        });
    }

    private void UserAuthentication(String pass, String passNew1) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        AuthCredential credential = EmailAuthProvider
                .getCredential(user.getEmail(), pass);
        user.reauthenticate(credential)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        progressDialog.dismiss();
                        if (task.isSuccessful()) {
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            user.updatePassword(passNew1)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Log.d(TAG, "User password updated: " + passNew1);
//                                                Toast.makeText(getActivity(), "Thay ?????i m???t kh???u th??nh c??ng", Toast.LENGTH_SHORT).show();
                                                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                                                transaction.replace(R.id.content_frame, new Password_update_notification_Fragment()).commitAllowingStateLoss();
                                                putPW(user.getEmail().replaceAll("@gmail.com", ""), passNew1);
                                            }
                                        }
                                    });
                        } else {
                            Toast.makeText(getActivity(), "M???t kh???u c?? kh??ng ch??nh x??c", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void putPW(String id, String passNew1) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference("coffee-poly").child("pw_user").child(id);
        reference.setValue(passNew1);
    }

    private void initUi(View view) {
        til_pass_old = view.findViewById(R.id.til_pass_old);
        til_pass_new = view.findViewById(R.id.til_pass_new);
        til_pass_new1 = view.findViewById(R.id.til_pass_new1);
        edt_pass_old = view.findViewById(R.id.edt_pass_old);
        edt_pass_new = view.findViewById(R.id.edt_pass_new);
        edt_pass_new1 = view.findViewById(R.id.edt_pass_new1);
        btn_change_pass = view.findViewById(R.id.btn_change_pass);
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setTitle("??ang thay ?????i m???t kh???u...");
    }
}