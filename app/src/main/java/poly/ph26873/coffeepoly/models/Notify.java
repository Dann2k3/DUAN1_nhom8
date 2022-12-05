package poly.ph26873.coffeepoly.models;

public class Notify {
    private String id;
    private int Status;
    private String time;
    private int type;

    //status ==0 chua xem
    // status ==1 da xem
    //id == id don hang(==thoi gian dat)
    //time == thoi gian xac nhan hoan thanh
    //type == status bill
        // status = 0 - đã xac nhan
        // status = 1 - đang xac nhan
        // status = 2 - đã hủy
        // status = 3 - dang giao
        // status = 4 - giao thanh cong


    public Notify() {
    }

    public Notify(String id, int status, String time,int type) {
        this.id = id;
        Status = status;
        this.time = time;
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getStatus() {
        return Status;
    }

    public void setStatus(int status) {
        Status = status;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
