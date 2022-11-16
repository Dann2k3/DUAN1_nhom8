package poly.ph26873.coffeepoly.models;

import java.util.List;

public class Favorite {
    private String id_user;
    private List<Integer> list_id_product;

    public Favorite() {
    }

    public Favorite(String id_user, List<Integer> list_id_product) {
        this.id_user = id_user;
        this.list_id_product = list_id_product;
    }


    public String getId_user() {
        return id_user;
    }

    public void setId_user(String id_user) {
        this.id_user = id_user;
    }

    public List<Integer> getList_id_product() {
        return list_id_product;
    }

    public void setList_id_product(List<Integer> list_id_product) {
        this.list_id_product = list_id_product;
    }
}
