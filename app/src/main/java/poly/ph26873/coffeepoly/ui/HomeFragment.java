package poly.ph26873.coffeepoly.ui;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import me.relex.circleindicator.CircleIndicator;
import poly.ph26873.coffeepoly.R;
import poly.ph26873.coffeepoly.adapter.BannerViewPagerAdapter;
import poly.ph26873.coffeepoly.adapter.HorizontalRCVAdapter;
import poly.ph26873.coffeepoly.models.Banner;
import poly.ph26873.coffeepoly.models.Product;


public class HomeFragment extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    private ViewPager viewPager;
    private CircleIndicator circleIndicator;
    private List<Banner> listBanner;
    private final Handler handler = new Handler();
    private final Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (viewPager.getCurrentItem() == listBanner.size() - 1) {
                viewPager.setCurrentItem(0);
            } else {
                viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
            }
        }
    };
    private static final String TAG = "zzz";
    private static final String TABLE_NAME = "coffee-poly";
    private static final String COL_PRODUCT = "product";

    private RecyclerView recyclerView_rcm_product, mRecycerView_all_product;
    private List<Product> list_rcm_product;
    private FirebaseDatabase database;
    private ProgressDialog progressDialog;


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initUi(view);
        showBanner();
        database = FirebaseDatabase.getInstance();
        getList_rcm_product();
    }

    private void getList_rcm_product() {
        DatabaseReference myProduct = database.getReference(TABLE_NAME).child(COL_PRODUCT);
        myProduct.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                progressDialog.dismiss();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Product product = dataSnapshot.getValue(Product.class);
                    list_rcm_product.add(product);
                }
                Log.d(TAG, "list_rcm_product: " + list_rcm_product.size());
                Log.d(TAG, "price: "+list_rcm_product.get(0).getPrice());
                showRecommentProduct();
                showAllProduct();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d(TAG, "faild product: ");
            }
        });
    }

    private void showAllProduct() {
        Log.d(TAG, "showAllProduct: --------");
        GridLayoutManager manager = new GridLayoutManager(getContext(), 3);
        mRecycerView_all_product.setLayoutManager(manager);
        mRecycerView_all_product.setHasFixedSize(true);
        HorizontalRCVAdapter allProductAdapter = new HorizontalRCVAdapter(getContext());
        allProductAdapter.setData(list_rcm_product);
        mRecycerView_all_product.setAdapter(allProductAdapter);
    }

    private void showRecommentProduct() {
        Log.d(TAG, "showRecommentProduct:");
        LinearLayoutManager manager = new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, true);
        recyclerView_rcm_product.setLayoutManager(manager);
        recyclerView_rcm_product.setHasFixedSize(true);
        HorizontalRCVAdapter horizontalRCVAdapter = new HorizontalRCVAdapter(getContext());
        List<Product> listProductRecoomment  = new ArrayList<>();
        for (int i = 0; i < list_rcm_product.size(); i++) {
            Log.d(TAG, "sp: "+i+" "+list_rcm_product.get(i).getId()+" "+list_rcm_product.get(i).getPrice()+" "+list_rcm_product.get(i).getQuantitySold());
            if(list_rcm_product.get(i).getQuantitySold() >= 2000){
                listProductRecoomment.add(list_rcm_product.get(i));
            }
        }
        horizontalRCVAdapter.setData(listProductRecoomment);
        recyclerView_rcm_product.setAdapter(horizontalRCVAdapter);
    }

    private void showBanner() {
        listBanner = getListBanner();
        BannerViewPagerAdapter pagerAdapter = new BannerViewPagerAdapter(listBanner);
        viewPager.setAdapter(pagerAdapter);
        circleIndicator.setViewPager(viewPager);
        handler.postDelayed(runnable, 2000);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                handler.removeCallbacks(runnable);
                handler.postDelayed(runnable, 3000);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }


    private void initUi(View view) {
        viewPager = view.findViewById(R.id.viewPager);
        circleIndicator = view.findViewById(R.id.circleIndicator);
        recyclerView_rcm_product = view.findViewById(R.id.mRecyclerView_rcm);
        mRecycerView_all_product = view.findViewById(R.id.mRecycerView_all_product);
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Đang tải dữ liệu...");
        progressDialog.show();
        list_rcm_product = new ArrayList<>();
    }

    private List<Banner> getListBanner() {
        List<Banner> list = new ArrayList<>();
        list.add(new Banner(R.drawable.bn_1));
        list.add(new Banner(R.drawable.bn2));
        list.add(new Banner(R.drawable.bn_3));
        list.add(new Banner(R.drawable.bn4));
        list.add(new Banner(R.drawable.bn5));
        return list;
    }

    @Override
    public void onPause() {
        super.onPause();
        handler.removeCallbacks(runnable);
    }


    @Override
    public void onResume() {
        super.onResume();
        handler.postDelayed(runnable, 3000);
    }
}