package poly.ph26873.coffeepoly.service;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import poly.ph26873.coffeepoly.R;
import poly.ph26873.coffeepoly.activities.MainActivity;
import poly.ph26873.coffeepoly.listData.ListData;
import poly.ph26873.coffeepoly.models.Bill;
import poly.ph26873.coffeepoly.models.Product;
import poly.ph26873.coffeepoly.models.QuantitySoldInMonth;
import poly.ph26873.coffeepoly.models.User;

public class MyService extends Service {
    public MyService() {
    }


    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM_yyyy");
        String month = simpleDateFormat.format(calendar.getTime());
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        capNhatListProduct(database);
        capNhatListQuanProduct(database, month);
        layLoaiTaiKhoan(database);
        thongBao(database);
        laydanhsachUser(database);
        return START_NOT_STICKY;
    }

    private void laydanhsachUser(FirebaseDatabase database) {
        DatabaseReference reference = database.getReference("coffee-poly").child("user");
        reference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                User user = snapshot.getValue(User.class);
                if (user != null) {
                    ListData.listUser.add(user);
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

    private void thongBao(FirebaseDatabase database) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            List<Bill> list = new ArrayList<>();
            DatabaseReference reference = database.getReference("coffee-poly").child("bill").child(user.getEmail().replaceAll("@gmail.com", ""));
            reference.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                    Bill bill = snapshot.getValue(Bill.class);
                    if (bill != null) {
                        list.add(bill);
                        if (bill.getStatus() == 0) {
                            showMyNotify((int) new Date().getTime(), "Thông báo:", "Đã xác nhận đơn " + bill.getId() + " thành công", "bill");
                        } else if (bill.getStatus() == 1) {
                            showMyNotify((int) new Date().getTime(), "Thông báo:", "Đã đặt đơn " + bill.getId() + " thành công", "bill");
                        } else if (bill.getStatus() == 2) {
                            showMyNotify((int) new Date().getTime(), "Thông báo:", "Đã hủy đơn " + bill.getId() + " thành công", "history");
                        } else if (bill.getStatus() == 3) {
                            showMyNotify((int) new Date().getTime(), "Thông báo:", "Đang giao đơn " + bill.getId() + " thành công", "bill");
                        } else {
                            showMyNotify((int) new Date().getTime(), "Thông báo:", "Đã giao đơn " + bill.getId() + " thành công", "history");
                        }

                    }
                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                    Bill bill = snapshot.getValue(Bill.class);
                    if (bill != null || !list.isEmpty()) {
                        for (int i = 0; i < list.size(); i++) {
                            if (list.get(i).getId().equals(bill.getId())) {
                                list.set(i, bill);
                                if (bill.getStatus() == 0) {
                                    showMyNotify((int) new Date().getTime(), "Thông báo:", "Đã xác nhận đơn " + bill.getId() + " thành công", "bill");
                                } else if (bill.getStatus() == 1) {
                                    showMyNotify((int) new Date().getTime(), "Thông báo:", "Đã đặt đơn " + bill.getId() + " thành công", "bill");
                                } else if (bill.getStatus() == 2) {
                                    showMyNotify((int) new Date().getTime(), "Thông báo:", "Đã hủy đơn " + bill.getId() + " thành công", "history");
                                } else if (bill.getStatus() == 3) {
                                    showMyNotify((int) new Date().getTime(), "Thông báo:", "Đang giao đơn " + bill.getId() + " thành công", "bill");
                                } else {
                                    showMyNotify((int) new Date().getTime(), "Thông báo:", "Đã giao đơn " + bill.getId() + " thành công", "history");
                                }
                                break;
                            }
                        }
                    }
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

    private void layLoaiTaiKhoan(FirebaseDatabase database) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String chilgPath = user.getEmail().replaceAll("@gmail.com", "");
            Log.d("zzz", "layLoaiTaiKhoan: " + chilgPath);
            DatabaseReference readUser = database.getReference("coffee-poly").child("type_user").child(chilgPath);
            readUser.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.getValue(Integer.class) != null) {
                        ListData.type_user_current = snapshot.getValue(Integer.class);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
            DatabaseReference readUserE = database.getReference("coffee-poly").child("user").child(chilgPath).child("enable");
            readUserE.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    ListData.enable_user_current = snapshot.getValue(Integer.class);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    //ham update
    private void capNhatListQuanProduct(FirebaseDatabase database, String month) {
        DatabaseReference refQuanPrd = database.getReference("coffee-poly").child("turnover_product").child(month);
        refQuanPrd.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ListData.listQuanPrd.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    ListData.listQuanPrd.add(dataSnapshot.getValue(QuantitySoldInMonth.class));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void capNhatListProduct(FirebaseDatabase database) {
        DatabaseReference refPrd = database.getReference("coffee-poly").child("product");
        refPrd.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ListData.listPrd.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    ListData.listPrd.add(dataSnapshot.getValue(Product.class));
                }
                Log.d("zzz", "ListData.listPrd: = " + ListData.listPrd.size());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return null;
    }

    String CHANNEL_ID = "ID_chanel002";
    String channel_name = "Kênh notify trong service";
    String channel_description = "Mô tả về chanel";

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = channel_name;
            String description = channel_description;
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void showMyNotify(int notificationId, String _title, String _content, String frg) {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.putExtra("goto", frg);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.cppl_removebg);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                .setSmallIcon(R.drawable.cppl_removebg)
                .setLargeIcon(bitmap)
                .setContentTitle(_title)
                .setContentText(_content)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC);
        startForeground(notificationId, builder.build());
    }

}