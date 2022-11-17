package poly.ph26873.coffeepoly.models;

import androidx.annotation.NonNull;

public class Item_Bill {
    private int id_product;
    private int quantity;

    public Item_Bill() {
    }

    public Item_Bill(int id_product, int quantity) {
        this.id_product = id_product;
        this.quantity = quantity;
    }

    public int getId_product() {
        return id_product;
    }

    public void setId_product(int id_product) {
        this.id_product = id_product;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }



}
