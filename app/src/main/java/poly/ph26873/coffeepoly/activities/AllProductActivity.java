package poly.ph26873.coffeepoly.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import poly.ph26873.coffeepoly.R;
import poly.ph26873.coffeepoly.adapter.HorizontalRCVAdapter;
import poly.ph26873.coffeepoly.models.Product;

public class AllProductActivity extends AppCompatActivity {

    private ImageView imv_back_layout_all_product;
    private RecyclerView recyclerView;
    private HorizontalRCVAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_product);
        initUi();
        back();
        showAllProduct();
    }

    private void showAllProduct() {
        List<Product> list = new ArrayList<>();
        Intent intent = getIntent();
        list = (List<Product>) intent.getSerializableExtra("list");
        if(list.size()>0){
            adapter.setData(list);
            recyclerView.setAdapter(adapter);
        }
    }

    private void back() {
        imv_back_layout_all_product.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initUi() {
        imv_back_layout_all_product = findViewById(R.id.imv_back_layout_all_product);
        recyclerView = findViewById(R.id.allRecyclerView);
        GridLayoutManager manager= new GridLayoutManager(AllProductActivity.this,3);
        recyclerView.setLayoutManager(manager);
        recyclerView.setHasFixedSize(true);
        adapter = new HorizontalRCVAdapter(AllProductActivity.this);
    }
}