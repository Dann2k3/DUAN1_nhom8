package poly.ph26873.coffeepoly.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Collections;
import java.util.List;

import poly.ph26873.coffeepoly.R;
import poly.ph26873.coffeepoly.models.Notify;

public class NotifyRCVAdapter extends RecyclerView.Adapter<NotifyRCVAdapter.NotifyHolder> {
    private Context context;
    private List<Notify> list;

    public NotifyRCVAdapter(Context context) {
        this.context = context;
    }

    public void setData(List<Notify> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public NotifyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_item_noti, parent, false);
        return new NotifyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotifyHolder holder, int position) {
        Notify notify = list.get(position);
        if (notify != null) {
            holder.tv_time.setText("Thời gian: " + notify.getTime());
            String trangthai = "";
            if (notify.getType() == 1) {
                trangthai = "đã đặt hàng thành công";
            } else if (notify.getType() == 0) {
                trangthai = "đang vận chuyển";
            } else if (notify.getType() == 2) {
                trangthai = "đã hủy";
            } else {
                trangthai = "đã giao thành công";
            }
            holder.tv_content.setText("Đơn hàng " + notify.getId() + " " + trangthai);
            if (notify.getStatus() == 0) {
                holder.imv_status.setVisibility(View.VISIBLE);
            } else {
                holder.imv_status.setVisibility(View.INVISIBLE);
            }
            holder.item_noti.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                    builder.setCancelable(false);
                    builder.setTitle("Xác nhận xóa thông báo này?");
                    builder.setPositiveButton("Xóa", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            String email = user.getEmail().replaceAll("@gmail.com", "");
                            FirebaseDatabase database = FirebaseDatabase.getInstance();
                            DatabaseReference reference = database.getReference("coffee-poly").child("notify").child(email).child(notify.getTime());
                            reference.removeValue();
                            list.remove(notify);
                            notifyDataSetChanged();
                        }
                    });

                    builder.setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                    return true;
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

    public class NotifyHolder extends RecyclerView.ViewHolder {
        private LinearLayout item_noti;
        private TextView tv_time, tv_content;
        private ImageView imv_status;

        public NotifyHolder(@NonNull View itemView) {
            super(itemView);
            item_noti = itemView.findViewById(R.id.item_noti);
            tv_time = itemView.findViewById(R.id.tv_time);
            tv_content = itemView.findViewById(R.id.tv_content);
            imv_status = itemView.findViewById(R.id.imv_status);
        }
    }
}
