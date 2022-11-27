package poly.ph26873.coffeepoly.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

import poly.ph26873.coffeepoly.R;
import poly.ph26873.coffeepoly.models.Bill;
import poly.ph26873.coffeepoly.service.MyReceiver;

public class ManagementRCVAdapter extends RecyclerView.Adapter<ManagementRCVAdapter.BillHolder> {
    private Context context;
    private List<Bill> list;

    public ManagementRCVAdapter(Context context) {
        this.context = context;
    }

    public void setData(List<Bill> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ManagementRCVAdapter.BillHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_bill_management, parent, false);
        return new BillHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ManagementRCVAdapter.BillHolder holder, int position) {
        Bill bill = list.get(position);
        if (bill != null) {
            if (bill.getStatus() == 1) {
                holder.tv_bill_time_m.setText("Thời gian: " + bill.getId());
                holder.tv_bill_name_m.setText("Họ và tên: " + bill.getName());
                String note = bill.getNote().toString();
                note.substring(0, note.length() - 2);
                note.replaceAll("-", "\n");
                holder.tv_bill_note_m.setText(note);
                holder.tv_bill_address_m.setText("Địa chỉ: " + bill.getAddress());
                holder.tv_bill_number_phone_m.setText("Số điện thoại: " + bill.getNumberPhone());
                holder.tv_bill_mess_m.setText("Ghi chú: " + bill.getMess());
                holder.tv_bill_total_m.setText("Tổng tiền: " + bill.getTotal() + "K");
                holder.tv_bill_status_m.setText("Trạng thái: Đang chờ đang xác nhận");
                holder.btn_bill_cancle_m.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                        builder.setTitle("Xác nhận đơn hàng thành công?");
                        builder.setMessage("Hãy nhấn xác nhận");
                        builder.setCancelable(true);
                        builder.setPositiveButton("Xác nhận", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (MyReceiver.isConnected == false) {
                                    Toast.makeText(builder.getContext(), "Không có kết nối mạng", Toast.LENGTH_LONG).show();
                                    return;
                                }
                                FirebaseDatabase database = FirebaseDatabase.getInstance();
                                DatabaseReference reference = database.getReference("coffee-poly").child("bill").child(bill.getId_user()).child(bill.getId()).child("status");
                                reference.setValue(0, new DatabaseReference.CompletionListener() {
                                    @Override
                                    public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                                        Toast.makeText(builder.getContext(), "Xác nhân hàng thành công", Toast.LENGTH_SHORT).show();
//                                        capNhatDoanthu(bill.getTotal(), bill.getId(), bill.getId_user());
                                        notifyDataSetChanged();
                                    }
                                });
                            }
                        });
                        builder.setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                        AlertDialog alertDialog = builder.create();
                        alertDialog.show();
                    }
                });
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

    public class BillHolder extends RecyclerView.ViewHolder {
        private TextView tv_bill_mess_m, tv_bill_time_m, tv_bill_name_m, tv_bill_number_phone_m, tv_bill_note_m, tv_bill_address_m, tv_bill_total_m, tv_bill_status_m;
        private Button btn_bill_cancle_m;

        public BillHolder(@NonNull View itemView) {
            super(itemView);
            tv_bill_number_phone_m = itemView.findViewById(R.id.tv_bill_number_phone_m);
            tv_bill_mess_m = itemView.findViewById(R.id.tv_bill_mess_m);
            tv_bill_name_m = itemView.findViewById(R.id.tv_bill_name_m);
            tv_bill_time_m = itemView.findViewById(R.id.tv_bill_time_m);
            tv_bill_note_m = itemView.findViewById(R.id.tv_bill_note_m);
            tv_bill_address_m = itemView.findViewById(R.id.tv_bill_address_m);
            tv_bill_total_m = itemView.findViewById(R.id.tv_bill_total_m);
            tv_bill_status_m = itemView.findViewById(R.id.tv_bill_status_m);
            btn_bill_cancle_m = itemView.findViewById(R.id.btn_bill_cancle_m);
        }
    }
}
