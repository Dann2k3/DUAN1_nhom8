package poly.ph26873.coffeepoly.adapter;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.Normalizer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.regex.Pattern;

import poly.ph26873.coffeepoly.R;
import poly.ph26873.coffeepoly.listData.ListData;
import poly.ph26873.coffeepoly.models.Bill;
import poly.ph26873.coffeepoly.models.Item_Bill;
import poly.ph26873.coffeepoly.models.QuantitySoldInMonth;
import poly.ph26873.coffeepoly.models.Turnover;
import poly.ph26873.coffeepoly.service.MyReceiver;

//ShipingRCVAdapter
public class ShipingRCVAdapter extends RecyclerView.Adapter<ShipingRCVAdapter.HistoryHolder> implements Filterable {
    private Context context;
    private List<Bill> list;
    private List<Bill> listNew;

    public ShipingRCVAdapter(Context context) {
        this.context = context;
    }

    public void setData(List<Bill> list) {
        this.list = list;
        this.listNew = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ShipingRCVAdapter.HistoryHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_bill_shiping, parent, false);
        return new HistoryHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull ShipingRCVAdapter.HistoryHolder holder, int position) {
        Bill bill = list.get(position);
        if (bill != null) {
            holder.tv_his_time1s.setText("Thời gian: " + bill.getId());
            holder.tv_his_name1s.setText("Họ và tên: " + bill.getName());
            String note = bill.getNote();
            note.substring(0, note.length() - 2);
            note.replaceAll("-", "\n");
            holder.tv_his_note1s.setText(note);
            holder.tv_his_address1s.setText("Địa chỉ: " + bill.getAddress());
            holder.tv_his_number_phone1s.setText("Số điện thoại: " + bill.getNumberPhone());
            holder.tv_bill_mess_s.setText("Ghi chú: " + bill.getMess());
            holder.tv_his_total1s.setText("Tổng tiền: " + bill.getTotal() + "K");
            holder.btn_bill_cancle_s.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("Xác nhận giao hàng thành công");
                    builder.setCancelable(false);
                    builder.setPositiveButton("Xác nhận", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (MyReceiver.isConnected == false) {
                                Toast.makeText(context, "Không có kết nối mạng", Toast.LENGTH_LONG).show();
                                return;
                            }
                            ProgressDialog progressDialog = new ProgressDialog(context);
                            progressDialog.setCancelable(false);
                            progressDialog.setTitle("Đang xác nhận giao hàng thành công...");
                            progressDialog.show();
                            FirebaseDatabase database = FirebaseDatabase.getInstance();
                            DatabaseReference reference = database.getReference("coffee-poly").child("bill").child(bill.getId_user()).child(bill.getId()).child("status");
                            reference.setValue(4, new DatabaseReference.CompletionListener() {
                                @Override
                                public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                                    Toast.makeText(context, "Xác nhận thành công", Toast.LENGTH_SHORT).show();
                                    capNhatSoLuongSanPhamTheoThang(bill.getList());
                                    capNhatSoLuongSanPham(bill.getList());
                                    capNhatDoanthu(bill.getTotal(), bill.getId(), bill.getId_user());
                                    progressDialog.dismiss();
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

    private void capNhatSoLuongSanPhamTheoThang(List<Item_Bill> list) {
        if (list != null) {
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM_yyyy");
            String month_key = simpleDateFormat.format(calendar.getTime());
            if (ListData.listQuanPrd.size() == 0) {
                for (Item_Bill item_bill : list) {
                    QuantitySoldInMonth quantitySoldInMonth = new QuantitySoldInMonth(item_bill.getId_product(), item_bill.getQuantity());
                    DatabaseReference reference1 = database.getReference("coffee-poly").child("turnover_product").child(month_key);
                    reference1.child(String.valueOf(item_bill.getId_product())).setValue(quantitySoldInMonth);
                }
                return;
            }

            for (int i = 0; i < list.size(); i++) {
                int index = 0;
                for (int k = 0; k < ListData.listQuanPrd.size(); k++) {
                    if (list.get(i).getId_product() == ListData.listQuanPrd.get(k).getId_Product()) {
                        index++;
                        Item_Bill item_bill = list.get(i);
                        QuantitySoldInMonth quantitySoldInMonth = new QuantitySoldInMonth(item_bill.getId_product(), item_bill.getQuantity() + ListData.listQuanPrd.get(k).getQuantitySold());
                        DatabaseReference reference1 = database.getReference("coffee-poly").child("turnover_product").child(month_key).child(String.valueOf(item_bill.getId_product()));
                        reference1.setValue(quantitySoldInMonth);
                    }
                }
                if (index == 0) {
                    Item_Bill item_bill = list.get(i);
                    QuantitySoldInMonth quantitySoldInMonth = new QuantitySoldInMonth(item_bill.getId_product(), item_bill.getQuantity());
                    DatabaseReference reference1 = database.getReference("coffee-poly").child("turnover_product").child(month_key).child(String.valueOf(item_bill.getId_product()));
                    reference1.setValue(quantitySoldInMonth);
                }
            }
        }
    }

    private void capNhatDoanthu(int total, String id, String id_user) {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM_yyyy");
        String month_key = simpleDateFormat.format(calendar.getTime());
        Turnover turnover = new Turnover(id_user + " " + id, total, id, id_user);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference("coffee-poly/turnover_bill").child(month_key).child(id_user + " " + id);
        reference.setValue(turnover);
    }

    private void capNhatSoLuongSanPham(List<Item_Bill> list) {
        if (list != null) {
            for (int i = 0; i < list.size(); i++) {
                for (int j = 0; j < ListData.listPrd.size(); j++) {
                    if (list.get(i).getId_product() == ListData.listPrd.get(j).getId()) {
                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                        DatabaseReference refQ = database.getReference("coffee-poly").child("product").child(String.valueOf(list.get(i).getId_product())).child("quantitySold");
                        refQ.setValue(list.get(i).getQuantity() + ListData.listPrd.get(j).getQuantitySold());
                    }
                }
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
        private TextView tv_bill_mess_s, tv_his_time1s, tv_his_name1s, tv_his_number_phone1s, tv_his_note1s, tv_his_address1s, tv_his_total1s, tv_his_status1s;
        private Button btn_bill_cancle_s;

        public HistoryHolder(@NonNull View itemView) {
            super(itemView);
            tv_his_number_phone1s = itemView.findViewById(R.id.tv_bill_number_phone_s);
            tv_bill_mess_s = itemView.findViewById(R.id.tv_bill_mess_s);
            tv_his_name1s = itemView.findViewById(R.id.tv_bill_name_s);
            tv_his_time1s = itemView.findViewById(R.id.tv_bill_time_s);
            tv_his_note1s = itemView.findViewById(R.id.tv_bill_note_s);
            tv_his_address1s = itemView.findViewById(R.id.tv_bill_address_s);
            tv_his_total1s = itemView.findViewById(R.id.tv_bill_total_s);
            tv_his_status1s = itemView.findViewById(R.id.tv_bill_status_s);
            btn_bill_cancle_s = itemView.findViewById(R.id.btn_bill_cancle_s);
        }
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String strSearch = constraint.toString();
                if (strSearch.isEmpty()) {
                    list = listNew;
                } else {
                    List<Bill> list1 = new ArrayList<>();
                    for (Bill bill : listNew) {
                        if (covertToString(bill.getId_user().toLowerCase()).contains(covertToString(strSearch.toLowerCase().trim()))) {
                            list1.add(bill);
                        }
                    }
                    list = list1;
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = list;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                list = (List<Bill>) results.values;
                notifyDataSetChanged();
            }
        };
    }

    public static String covertToString(String value) {
        try {
            String temp = Normalizer.normalize(value, Normalizer.Form.NFD);
            Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
            return pattern.matcher(temp).replaceAll("");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }


}
