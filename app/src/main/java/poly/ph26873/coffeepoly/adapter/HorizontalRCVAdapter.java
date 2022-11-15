package poly.ph26873.coffeepoly.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.Serializable;
import java.util.List;

import poly.ph26873.coffeepoly.R;
import poly.ph26873.coffeepoly.activities.DetailProductActivity;
import poly.ph26873.coffeepoly.models.Product;

public class HorizontalRCVAdapter extends RecyclerView.Adapter<HorizontalRCVAdapter.ProductsHolder> {
    private Context context;
    private List<Product> list;

    public HorizontalRCVAdapter(Context context) {
        this.context = context;
    }

    public void setData(List<Product> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public HorizontalRCVAdapter.ProductsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_item_product, parent, false);
        return new ProductsHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HorizontalRCVAdapter.ProductsHolder holder, int position) {
        Product product = list.get(position);
        if (product != null) {
            holder.tv_product_name_rcm.setText(product.getName());
            holder.imv_product_avatar_rcm.setImageResource(R.drawable.prd_cps);
            holder.onclick_item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, DetailProductActivity.class);
                    intent.putExtra("product", (Serializable) product);
                    context.startActivity(intent);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        if (list != null) {
            return list.size();
        }
        return 0;
    }

    public class ProductsHolder extends RecyclerView.ViewHolder {
        private ImageView imv_product_avatar_rcm;
        private TextView tv_product_name_rcm;
        private LinearLayout onclick_item;

        public ProductsHolder(@NonNull View itemView) {
            super(itemView);
            imv_product_avatar_rcm = itemView.findViewById(R.id.imv_product_avatar_rcm);
            tv_product_name_rcm = itemView.findViewById(R.id.tv_product_name_rcm);
            onclick_item = itemView.findViewById(R.id.onclick_item);


        }
    }
}
