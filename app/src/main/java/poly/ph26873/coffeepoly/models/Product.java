package poly.ph26873.coffeepoly.models;

import java.io.Serializable;

public class Product implements Serializable {
    private int id;
    private String name;
    private int image;
    private int price;
    private String content;
    private String status;
    private int quantitySold;
    private int type;

    public Product() {
    }

    public Product(int id, String name, int image, int price, String content, String status, int quantitySold, int type) {
        this.id = id;
        this.name = name;
        this.image = image;
        this.price = price;
        this.content = content;
        this.status = status;
        this.quantitySold = quantitySold;
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
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

    public void setStatus(String status) {
        this.status = status;
    }

    public int getQuantitySold() {
        return quantitySold;
    }

    public void setQuantitySold(int quantitySold) {
        this.quantitySold = quantitySold;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
