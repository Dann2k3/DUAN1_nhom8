package poly.ph26873.coffeepoly.ui;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import me.relex.circleindicator.CircleIndicator;
import poly.ph26873.coffeepoly.R;
import poly.ph26873.coffeepoly.activities.MainActivity;
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
    private BannerViewPagerAdapter pagerAdapter;
    private CircleIndicator circleIndicator;
    private List<Banner> listBanner;
    private Handler handler = new Handler();
    private Runnable runnable = new Runnable() {
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

    private RecyclerView recyclerView_rcm_product;
    private HorizontalRCVAdapter horizontalRCVAdapter;
    private List<Product> list_rcm_product;
    private FirebaseDatabase database;




    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initUi(view);
        showBanner();
        database = FirebaseDatabase.getInstance();
        showRecommentProduct();

    }

    private void showRecommentProduct() {
        list_rcm_product = new ArrayList<>();
        Log.d(TAG, "showRecommentProduct: 0------0");
        LinearLayoutManager manager  = new LinearLayoutManager(getContext(),RecyclerView.HORIZONTAL,true);
        recyclerView_rcm_product.setLayoutManager(manager);
        recyclerView_rcm_product.setHasFixedSize(true);
        horizontalRCVAdapter = new HorizontalRCVAdapter(getContext());
        DatabaseReference  myProduct = database.getReference(TABLE_NAME).child(COL_PRODUCT);
        myProduct.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Product product  = dataSnapshot.getValue(Product.class);
                    list_rcm_product.add(product);
                }
//                List<Product> list = new ArrayList<>();
//                for (int i = 0; i < 10; i++) {
//                    list.add(list_rcm_product.get(0));
//                }
                Log.d(TAG, "list: "+list_rcm_product.size());
                horizontalRCVAdapter.setData(list_rcm_product);
                recyclerView_rcm_product.setAdapter(horizontalRCVAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d(TAG, "faild product: ");
            }
        });

    }

    private void showBanner() {
        listBanner = getListBanner();
        pagerAdapter = new BannerViewPagerAdapter(listBanner);
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