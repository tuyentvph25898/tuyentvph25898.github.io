package tuyentv.fpoly.duan1.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.bumptech.glide.Glide;
import com.google.android.material.navigation.NavigationView;
import com.nex3z.notificationbadge.NotificationBadge;

import java.util.ArrayList;
import java.util.List;

import io.paperdb.Paper;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import tuyentv.fpoly.duan1.R;
import tuyentv.fpoly.duan1.adapter.LoaiSpAdapter;
import tuyentv.fpoly.duan1.adapter.SanPhamMoiAdapter;
import tuyentv.fpoly.duan1.model.LoaiSp;
import tuyentv.fpoly.duan1.model.SanPhamMoi;
import tuyentv.fpoly.duan1.model.SanPhamMoiModel;
import tuyentv.fpoly.duan1.model.User;
import tuyentv.fpoly.duan1.retrofit.ApiBanHang;
import tuyentv.fpoly.duan1.retrofit.RetrofitClient;
import tuyentv.fpoly.duan1.utils.Utils;

public class MainActivity extends AppCompatActivity {
    Toolbar toolbar;
    ViewFlipper viewFlipper;
    RecyclerView recyclerViewManHinhChinh;
    NavigationView navigationView;
    ListView listViewManHinhChinh;
    DrawerLayout drawerLayout;
    LoaiSpAdapter loaiSpAdapter;
    List<LoaiSp> mangloaisp;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    ApiBanHang apiBanHang;
    List<SanPhamMoi> mangSpMoi;
    SanPhamMoiAdapter spAdapter;
    NotificationBadge badge;
    FrameLayout frameLayout;
    ImageView imgsearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        apiBanHang = RetrofitClient.getInstance(Utils.BASE_URL).create(ApiBanHang.class);
        Paper.init(this);
        if (Paper.book().read("user")!=null){
            User user = Paper.book().read("user");
            Utils.user_current = user;
        }
        Anhxa();
        ActionBar();
        if (isConnected(this)){

            ActionViewFlipper();
            getLoaiSanPham();
            getSpMoi();
            getEventClick();
        }else {
            Toast.makeText(getApplicationContext(), "khong co ket noi intenet", Toast.LENGTH_SHORT).show();
        }
    }

    private void getEventClick() {
        listViewManHinhChinh.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0:
                        Intent trangchu = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(trangchu);
                        break;
                    case 1:
                        Intent dienthoai = new Intent(getApplicationContext(), DienThoaiActivity.class);
                        dienthoai.putExtra("loai", 1);
                        startActivity(dienthoai);
                        break;
                    case 2:
                        Intent laptop = new Intent(getApplicationContext(), DienThoaiActivity.class);
                        laptop.putExtra("loai", 2);
                        startActivity(laptop);
                        break;
                    case 3:
                        Intent lienhe = new Intent(getApplicationContext(), LienHeActivity.class);
                        startActivity(lienhe);
                        break;
                    case 4:
                        Intent inform = new Intent(getApplicationContext(), InformActivity.class);
                        startActivity(inform);
                        break;
                    case 5:
                        Intent donhang = new Intent(getApplicationContext(), XemDonActivity.class);
                        startActivity(donhang);
                        break;
                    case 6:
                        //xoa key user
                        Paper.book().delete("user");
                        Intent dangnhap = new Intent(getApplicationContext(), DangNhapActivity.class);
                        startActivity(dangnhap);
                        finish();
                        break;
                }
            }
        });
    }

    private void getSpMoi() {
        compositeDisposable.add(apiBanHang.getSpMoii()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        sanPhamMoiModel -> {
                            if (sanPhamMoiModel.isSuccess()){
                                mangSpMoi = sanPhamMoiModel.getResult();
                                spAdapter = new SanPhamMoiAdapter(getApplicationContext(), mangSpMoi);
                                recyclerViewManHinhChinh.setAdapter(spAdapter);
                            }
                        }
                ));
    }

    private void getLoaiSanPham() {
        compositeDisposable.add(apiBanHang.getLoaiSp().subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(loaiSpModel -> {
                    if (loaiSpModel.isSuccess()){
                        mangloaisp = loaiSpModel.getResult();
                        mangloaisp.add(new LoaiSp("Đăng xuất","http://cdn.onlinewebfonts.com/svg/img_72532.png"));
                        loaiSpAdapter = new LoaiSpAdapter(getApplicationContext(),mangloaisp);
                        listViewManHinhChinh.setAdapter(loaiSpAdapter);

                    }
                }));
    }

    private void ActionViewFlipper(){
        List<String> mangquangcao = new ArrayList<>();
        mangquangcao.add("https://fptshop.com.vn/Uploads/images/2015/SANPHAM/MinhtriBin/11_02_dell-vostro-v5568i5-7200u-25ghz/dell-vostro-v5568i5-7200u-25ghz1.jpg");
        mangquangcao.add("https://www.lenovo.com/medias/ww-lenovo-thinkpad-x1-carbon-2017-feature4.png?context=bWFzdGVyfHJvb3R8OTAwMzd8aW1hZ2UvcG5nfGhhYi9oMzgvOTM1NzAyODIyOTE1MC5wbmd8NmY4MTY3OWY0NGI2ZDkzOTQ0MWI4NTBiZTJkNjg1OWRjM2JhMjI4MDBmNWU2OWZkMTJmOWMzMWU4ZjI3MWQ3NA&w=1920");
        mangquangcao.add("https://www.lenovo.com/medias/ww-lenovo-laptop-thinkpad-x1-carbon5-gallery-13.png?context=bWFzdGVyfHJvb3R8MTAyMzg0fGltYWdlL3BuZ3xoY2IvaGRiLzkzNTcwMjU3Mzg3ODIucG5nfDE0N2I5OWFkODllYjNjZmE0YTJiMWY3OTlkMmVhMzkwMTdhYmVlZWRiZDAyMDAzNGJlMTlmYTY0NGE0ZmNlNDY");
        mangquangcao.add("https://store.storeimages.cdn-apple.com/4981/as-images.apple.com/is/image/AppleInc/aos/published/images/i/ph/iphone/xs/iphone-xs-max-gold-select-2018?wid=1200&hei=630&fmt=jpeg&qlt=95&op_usm=0.5,0.5&.v=1535655075417");
        for (int i = 0; i < mangquangcao.size(); i++){
            ImageView imageView = new ImageView(getApplicationContext());
            Glide.with(getApplicationContext()).load(mangquangcao.get(i)).into(imageView);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            viewFlipper.addView(imageView);
        }
        viewFlipper.setFlipInterval(3000);
        viewFlipper.setAutoStart(true);
        Animation slide_in = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_in_right);
        Animation slide_out = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_out_right);
        viewFlipper.setInAnimation(slide_in);
        viewFlipper.setOutAnimation(slide_out);

    }
    private void Anhxa(){
        imgsearch = findViewById(R.id.imgsearch);
        toolbar = findViewById(R.id.toolbarmanhinhchinh);
        viewFlipper = findViewById(R.id.viewlipper);
        recyclerViewManHinhChinh = findViewById(R.id.recycleview);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, 2 );
        recyclerViewManHinhChinh.setLayoutManager(layoutManager);
        recyclerViewManHinhChinh.setHasFixedSize(true);
        listViewManHinhChinh = findViewById(R.id.listviewmanhinhchinh);
        navigationView = findViewById(R.id.navigationview);
        drawerLayout = findViewById(R.id.drawerlayout);
        badge = findViewById(R.id.menu_sl);
        frameLayout = findViewById(R.id.framegiohang);
        //khơi tạo list
        mangloaisp = new ArrayList<>();
        mangSpMoi = new ArrayList<>();
        if (Utils.manggiohang == null){
            Utils.manggiohang = new ArrayList<>();
        }else {
            int totalItem = 0;
            for (int i = 0; i < Utils.manggiohang.size(); i++){
                totalItem = totalItem+ Utils.manggiohang.get(i).getSoluong();
            }
            badge.setText(String.valueOf(totalItem));
        }
        frameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent giohang = new Intent(getApplicationContext(), GioHangActivity.class);
                startActivity(giohang);
            }
        });

        imgsearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SearchActivity.class);
                startActivity(intent);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        int totalItem = 0;
        for (int i = 0; i < Utils.manggiohang.size(); i++){
            totalItem = totalItem+ Utils.manggiohang.get(i).getSoluong();
        }
        badge.setText(String.valueOf(totalItem));
    }

    private void ActionBar(){
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationIcon(android.R.drawable.ic_menu_sort_by_size);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
    }
    private boolean isConnected(Context context){
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);
        NetworkInfo wifi = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);// nhớ thêm quyên không bị lỗi
        NetworkInfo mobile = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if((wifi!= null && wifi.isConnected()) || (mobile != null && mobile.isConnected())){
            return true;
        }else {
            return false;
        }
    }

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }
}