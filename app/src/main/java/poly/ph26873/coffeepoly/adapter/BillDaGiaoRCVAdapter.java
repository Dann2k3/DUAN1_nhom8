package poly.ph26873.coffeepoly.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import poly.ph26873.coffeepoly.R;
import poly.ph26873.coffeepoly.models.Bill;

public class BillDaGiaoRCVAdapter extends RecyclerView.Adapter<BillDaGiaoRCVAdapter.HistoryHolder> {
    private Context context;
    private List<Bill> list;

    public BillDaGiaoRCVAdapter(Context context) {
        this.context = context;
    }

    public void setData(List<Bill> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public BillDaGiaoRCVAdapter.HistoryHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_item_bill_da_giao, parent, false);
        return new HistoryHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BillDaGiaoRCVAdapter.HistoryHolder holder, int position) {
        Bill bill = list.get(position);
        if (bill != null) {
            if (bill.getStatus() == 0) {

                holder.tv_his_time1.setText("Thời gian: " + bill.getId());
                holder.tv_his_name1.setText("Họ và tên: " + bill.getName());
                String note = bill.getNote();
                note.substring(0, note.length() - 2);
                note.replaceAll("-", "\n");
                holder.tv_his_note1.setText(note);
                holder.tv_his_address1.setText("Địa chỉ: " + bill.getAddress());
                holder.tv_his_number_phone1.setText("Số điện thoại: " + bill.getNumberPhone());
                holder.tv_his_total1.setText("Tổng tiền: " + bill.getTotal() + "K");
                if (bill.getStatus() == 0) {
                    holder.tv_his_status1.setTextColor(Color.GREEN);
                } else if (bill.getTotal() == 1) {
                    holder.tv_his_status1.setTextColor(Color.YELLOW);
                } else if (bill.getStatus() == 2) {
                    holder.tv_his_status1.setTextColor(Color.RED);
                }
                holder.tv_his_status1.setText("Trạng thái đơn hàng: " + bill.getTrangThai());
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

    public class HistoryHolder extends RecyclerView.ViewHolder {
        private TextView tv_his_time1, tv_his_name1, tv_his_number_phone1, tv_his_note1, tv_his_address1, tv_his_total1, tv_his_status1;

        public HistoryHolder(@NonNull View itemView) {
            super(itemView);
            tv_his_number_phone1 = itemView.findViewById(R.id.tv_his_number_phone_1);
            tv_his_name1 = itemView.findViewById(R.id.tv_his_name_1);
            tv_his_time1 = itemView.findViewById(R.id.tv_his_time_1);
            tv_his_note1 = itemView.findViewById(R.id.tv_his_note_1);
            tv_his_address1 = itemView.findViewById(R.id.tv_his_address_1);
            tv_his_total1 = itemView.findViewById(R.id.tv_his_total_1);
            tv_his_status1 = itemView.findViewById(R.id.tv_his_status_1);
        }
    }
}
