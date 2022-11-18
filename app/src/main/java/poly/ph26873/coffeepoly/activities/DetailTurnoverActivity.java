package poly.ph26873.coffeepoly.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import poly.ph26873.coffeepoly.R;

public class DetailTurnoverActivity extends AppCompatActivity {
    private ImageView imv_back_layout_detail_turnover;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_turnover);
        initUi();
        showInFomationBill();
        onClicktoBack();
    }

    private void showInFomationBill() {
    }

    private void onClicktoBack() {
        imv_back_layout_detail_turnover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initUi() {
        imv_back_layout_detail_turnover = findViewById(R.id.imv_back_layout_detail_turnover);
    }
}