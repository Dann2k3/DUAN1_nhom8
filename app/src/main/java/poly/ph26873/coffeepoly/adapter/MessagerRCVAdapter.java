package poly.ph26873.coffeepoly.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import poly.ph26873.coffeepoly.R;
import poly.ph26873.coffeepoly.listData.ListData;
import poly.ph26873.coffeepoly.models.Message;

public class MessagerRCVAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private List<Message> list;

    public MessagerRCVAdapter(Context context) {
        this.context = context;
    }

    public void setData(List<Message> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        Message message = list.get(position);
        return message.getType();
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == ListData.type_user_current || (viewType == 1 && ListData.type_user_current == 0)) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_item_mess_user, parent, false);
            return new MessagerUserHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_item_mess_nv, parent, false);
            return new MessagerNVHolder(view);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Message message = list.get(position);
        if (message != null) {
            if (holder.getItemViewType() == ListData.type_user_current || (holder.getItemViewType()==1&&ListData.type_user_current==0)) {
                MessagerUserHolder userHolder = (MessagerUserHolder) holder;
                userHolder.tv_mess_user.setText(message.getContent());
            } else {
                MessagerNVHolder nvHolder = (MessagerNVHolder) holder;
                nvHolder.tv_mess_nv.setText(message.getContent());
            }
        }
    }


    @Override
    public int getItemCount() {
        if (list != null) {
            return list.size();
        }
        return 0;
    }

    public class MessagerUserHolder extends RecyclerView.ViewHolder {
        private TextView tv_mess_user;

        public MessagerUserHolder(@NonNull View itemView) {
            super(itemView);
            tv_mess_user = itemView.findViewById(R.id.tv_mess_user);
        }
    }

    public class MessagerNVHolder extends RecyclerView.ViewHolder {
        private TextView tv_mess_nv;

        public MessagerNVHolder(@NonNull View itemView) {
            super(itemView);
            tv_mess_nv = itemView.findViewById(R.id.tv_mess_nv);
        }
    }
}
