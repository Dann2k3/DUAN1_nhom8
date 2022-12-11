package poly.ph26873.coffeepoly.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import poly.ph26873.coffeepoly.R;

public class MessagerActivity extends AppCompatActivity {
    private ImageView imv_back_layout_mess;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messager);
        initUi();
        back();
    }

    private void back() {
        imv_back_layout_mess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


    private void initUi() {
        imv_back_layout_mess = findViewById(R.id.imv_back_layout_mess);
    }
}