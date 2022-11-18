package poly.ph26873.coffeepoly.models;

public class Turnover {
    private String id;
    private int total;
    private String time;

    public Turnover() {
    }

    public Turnover(String id, int total,String time) {
        this.id = id;
        this.total = total;
        this.time  = time;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
