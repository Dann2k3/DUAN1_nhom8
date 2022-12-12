package poly.ph26873.coffeepoly.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

import poly.ph26873.coffeepoly.R;
import poly.ph26873.coffeepoly.models.Item_Bill;
import poly.ph26873.coffeepoly.models.Product;

public class ProductInBillRCVAdapter extends RecyclerView.Adapter<ProductInBillRCVAdapter.Item_BillHolder> {
    private Context context;
    private List<Item_Bill> list;

    public ProductInBillRCVAdapter(Context context) {
        this.context = context;
    }

    public void setData(List<Item_Bill> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ProductInBillRCVAdapter.Item_BillHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_item_bill_1, parent, false);
        return new Item_BillHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductInBillRCVAdapter.Item_BillHolder holder, int position) {
        Item_Bill item_bill = list.get(position);
        if (item_bill != null) {
            holder.tv_id_quan.setText("Số lượng: " + item_bill.getQuantity());
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference reference = database.getReference("coffee-poly").child("product").child(String.valueOf(item_bill.getId_product()));
            reference.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                    Product product = snapshot.getValue(Product.class);
                    if (product != null) {
                        Glide.with(context).load(Uri.parse(product.getImage())).error(R.color.red).into(holder.imv_ava_item_b);
                        holder.tv_ib_name.setText(product.getName());
                        holder.tv_ib_price.setText("Đơn giá: " + product.getPrice() + "K");
                        holder.tv_id_total.setText("Thành tiền: " + product.getPrice() * item_bill.getQuantity() + "K");
                    }
                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot snapshot) {

                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

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

    public class Item_BillHolder extends RecyclerView.ViewHolder {
        private ImageView imv_ava_item_b;
        private TextView tv_ib_name, tv_ib_price, tv_id_quan, tv_id_total;

        public Item_BillHolder(@NonNull View itemView) {
            super(itemView);
            imv_ava_item_b = itemView.findViewById(R.id.imv_ava_item_b);
            tv_ib_name = itemView.findViewById(R.id.tv_ib_name);
            tv_ib_price = itemView.findViewById(R.id.tv_ib_price);
            tv_id_quan = itemView.findViewById(R.id.tv_id_quan);
            tv_id_total = itemView.findViewById(R.id.tv_id_total);
        }
    }
}
