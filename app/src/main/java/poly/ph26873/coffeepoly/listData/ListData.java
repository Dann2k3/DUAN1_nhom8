package poly.ph26873.coffeepoly.listData;

import java.util.ArrayList;
import java.util.List;

import poly.ph26873.coffeepoly.models.Notify;
import poly.ph26873.coffeepoly.models.Product;
import poly.ph26873.coffeepoly.models.QuantitySoldInMonth;

public class ListData {
    public static List<Product> listPrd = new ArrayList<>();
    public static List<QuantitySoldInMonth> listQuanPrd = new ArrayList<>();
    public static int type_user_current = -1;
    public static List<Notify> listNTF = new ArrayList<>();
}
