package poly.ph26873.coffeepoly.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.core.ThreadInitializer;

import java.util.List;

import poly.ph26873.coffeepoly.R;
import poly.ph26873.coffeepoly.models.Product;

public class AllProductRCVAdapter extends RecyclerView.Adapter<AllProductRCVAdapter.ProductHolder> {

    private Context context;
    private List<Product> list;

    public AllProductRCVAdapter(Context context) {
        this.context = context;
    }

    public void setData(List<Product> list){
        this.list = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public AllProductRCVAdapter.ProductHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_item_product,parent,false);
        return new ProductHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AllProductRCVAdapter.ProductHolder holder, int position) {
        Product product = list.get(position);
        if (product != null) {
            holder.tv_product_name_rcm.setText(product.getName());
            holder.imv_product_avatar_rcm.setImageResource(R.drawable.prd_cps);
        }
    }

    @Override
    public int getItemCount() {
        if(list != null){
            return list.size();
        }
        return 0;
    }

    public class ProductHolder extends RecyclerView.ViewHolder {
        private ImageView imv_product_avatar_rcm;
        private TextView tv_product_name_rcm;
        public ProductHolder(@NonNull View itemView) {
            super(itemView);
            imv_product_avatar_rcm = itemView.findViewById(R.id.imv_product_avatar_rcm);
            tv_product_name_rcm = itemView.findViewById(R.id.tv_product_name_rcm);
        }
    }
}
