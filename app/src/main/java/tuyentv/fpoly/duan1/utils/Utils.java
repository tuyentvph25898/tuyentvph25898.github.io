package tuyentv.fpoly.duan1.utils;

import java.util.ArrayList;
import java.util.List;

import tuyentv.fpoly.duan1.model.GioHang;
import tuyentv.fpoly.duan1.model.User;

public class Utils {
    public static final String BASE_URL = "http://192.168.42.117/banhang/";

    public static List<GioHang> manggiohang;
    public static List<GioHang> mangmuahang = new ArrayList<>();
    public static User user_current = new User();

}
