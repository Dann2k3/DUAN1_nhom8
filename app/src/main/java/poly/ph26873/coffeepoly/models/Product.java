package poly.ph26873.coffeepoly.models;

import java.io.Serializable;

public class Product implements Serializable {
    private long id;
    private String name;
    private long price;
    private String content;
    private String status;
    private long quantitySold;
    private int type;

    public Product() {
    }

    public Product(long id, String name, long price, String content, String status, long quantitySold, int type) {

        this.id = id;
        this.name = name;
        this.price = price;
        this.content = content;
        this.status = status;
        this.quantitySold = quantitySold;
        this.type = type;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public long getPrice() {
        return price;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getStatus() {
        return status;
    }

    public long getQuantitySold() {
        return quantitySold;
    }

    public int getType() {
        return type;
    }

}
