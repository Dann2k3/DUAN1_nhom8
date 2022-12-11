package poly.ph26873.coffeepoly.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import poly.ph26873.coffeepoly.R;
import poly.ph26873.coffeepoly.activities.DetailUserActivity;
import poly.ph26873.coffeepoly.activities.MessagerActivity;
import poly.ph26873.coffeepoly.models.User;

public class ListMessagerRCVAdapter extends RecyclerView.Adapter<ListMessagerRCVAdapter.UserHolder> {
    private Context context;
    private List<User> list;

    public ListMessagerRCVAdapter(Context context) {
        this.context = context;
    }

    public void setData(List<User> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ListMessagerRCVAdapter.UserHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_item_list_mess, parent, false);
        return new UserHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ListMessagerRCVAdapter.UserHolder holder, int position) {
        User user = list.get(position);
        if (user != null) {
            Glide.with(context).load(Uri.parse(user.getImage())).error(R.drawable.image_guest).into(holder.imv_avatar_lm);
            holder.tv_name_lm.setText(user.getName());
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference reference = database.getReference("coffee-poly").child("Notify_messager").child(user.getId()).child("status");
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    int status = snapshot.getValue(Integer.class);
                    if (status == 0) {
                        holder.imv_new.setVisibility(View.VISIBLE);
                    } else {
                        holder.imv_new.setVisibility(View.INVISIBLE);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            holder.onclick_mess.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, MessagerActivity.class);
                    intent.putExtra("id_user", user.getId());
                    context.startActivity(intent);
                }
            });
            holder.imv_avatar_lm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, DetailUserActivity.class);
                    intent.putExtra("user", user);
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

    public class UserHolder extends RecyclerView.ViewHolder {
        private ImageView imv_avatar_lm, imv_new;
        private TextView tv_name_lm;
        private CardView onclick_mess;

        public UserHolder(@NonNull View itemView) {
            super(itemView);
            imv_avatar_lm = itemView.findViewById(R.id.imv_avatar_lm);
            imv_new = itemView.findViewById(R.id.imv_new);
            tv_name_lm = itemView.findViewById(R.id.tv_name_lm);
            onclick_mess = itemView.findViewById(R.id.onclick_mess);
        }
    }
}
