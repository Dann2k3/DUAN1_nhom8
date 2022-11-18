package poly.ph26873.coffeepoly.models;

import java.util.List;

public class Bill {
    private String id;
    private List<Item_Bill> list;
    private int total;
    private String address;
    private String note;
    private int status;
    // status = 0 - đã nhận hàng
    // status = 1 - đang giao hàng
    // status = 2 - đã hủy
    //id chính là thời gian đặt hàng định dạng: dd_MM_yyyy kk:mm:ss


    public Bill() {
    }

    public Bill(String id, List<Item_Bill> list, int total, String address, String note, int status) {
        this.id = id;
        this.list = list;
        this.total = total;
        this.address = address;
        this.note = note;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<Item_Bill> getList() {
        return list;
    }

    public void setList(List<Item_Bill> list) {
        this.list = list;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
