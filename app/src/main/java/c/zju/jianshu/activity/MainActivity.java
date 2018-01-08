package c.zju.jianshu.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.mikepenz.iconics.context.IconicsContextWrapper;
import com.yarolegovich.lovelydialog.LovelyProgressDialog;

import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.widget.Toast;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import c.zju.jianshu.R;
import c.zju.jianshu.fragment.BookGridFragment;
import c.zju.jianshu.model.bean.Book;
import c.zju.jianshu.view.ViewPagerIndicator;
import es.dmoral.toasty.Toasty;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends BaseActivity {

    // ViewPager
    private ViewPager viewPager;
    private FragmentPagerAdapter pagerAdapter;

    private Book book;
    private Handler handler;
    private Timer timer;
    private TimerTask task;
    private LovelyProgressDialog dialog;

    // ViewPagerIndicator
    private ViewPagerIndicator viewPagerIndicator;
    private List<String> titles = Arrays.asList("所有书籍", "特别收藏");

    // Fragment
    private List<Fragment> fragments = new ArrayList<Fragment>();

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(IconicsContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Data.setUsername(getIntent().getStringExtra("username"));
        Data.setPassword(getIntent().getStringExtra("password"));
        final SharedPreferences userSettings = getSharedPreferences("setting", 0);
        userSettings.edit().putString("mode", "auto-login").putString("username", Data.getUsername()).putString("password", Data.getPassword()).commit();
        // 不设置标题
        setTitle("");


        // 右下角浮动菜单
        final FloatingActionMenu fabMenu = (FloatingActionMenu) findViewById(c.zju.jianshu.R.id.fabmenu);
        fabMenu.setClosedOnTouchOutside(true);


        // 右下角浮动按钮 - 本地搜索
        final FloatingActionButton fabBtnTotal = (FloatingActionButton) findViewById(R.id.fab_total);
        fabBtnTotal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fabMenu.close(true);
                Intent intent = new Intent(MainActivity.this, TotalActivity.class);
                startActivity(intent);
            }
        });


//        // 右下角浮动按钮 - 本地搜索
//        final FloatingActionButton fabBtnLocal = (FloatingActionButton) findViewById(R.id.fab_local);
//        fabBtnLocal.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                fabMenu.close(true);
//                Intent intent = new Intent(MainActivity.this, ScannerActivity.class);
//                startActivity(intent);
//            }
//        });

        // 右下角浮动按钮 - 扫一扫
        final FloatingActionButton fabBtnScanner = (FloatingActionButton) findViewById(c.zju.jianshu.R.id.fab_scanner);
        fabBtnScanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fabMenu.close(true);
                IntentIntegrator integrator = new IntentIntegrator(MainActivity.this);
                // 设置要扫描的条码类型，ONE_D_CODE_TYPES：一维码，QR_CODE_TYPES-二维码
                integrator.setDesiredBarcodeFormats(IntentIntegrator.ONE_D_CODE_TYPES);
                integrator.setCaptureActivity(ScannerActivity.class); //设置打开摄像头的Activity
                integrator.setPrompt("请扫描ISBN"); //底部的提示文字，设为""可以置空
                integrator.setCameraId(0); //前置或者后置摄像头
                integrator.setBeepEnabled(true); //扫描成功的「哔哔」声，默认开启
                integrator.setBarcodeImageEnabled(true);
                integrator.initiateScan();
            }
        });


        // 右下角浮动按钮 - 添加
        FloatingActionButton fabBtnAdd = (FloatingActionButton) findViewById(c.zju.jianshu.R.id.fab_add);
        fabBtnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fabMenu.close(true);
                Intent intent = new Intent(MainActivity.this, SearchActivity.class);
                startActivity(intent);
            }
        });

        // 右下角浮动按钮 - 更换账号
        FloatingActionButton fabBtnChange = (FloatingActionButton) findViewById(R.id.fab_change);
        fabBtnChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fabMenu.close(true);
                userSettings.edit().putString("mode", "login").putString("username", null).putString("password", null).commit();
                Intent intent = new Intent(MainActivity.this, SignupActivity.class);
                MainActivity.this.finish();
                startActivity(intent);
            }
        });

        // 右下角浮动按钮 - 关于
        FloatingActionButton fabBtnAbout = (FloatingActionButton) findViewById(c.zju.jianshu.R.id.fab_about);
        fabBtnAbout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fabMenu.close(true);
                Intent intent = new Intent(MainActivity.this, AboutActivity.class);
                startActivity(intent);
            }
        });


        // ViewPager
        viewPager = (ViewPager) findViewById(c.zju.jianshu.R.id.view_pager);

        // ViewPagerIndicator
        viewPagerIndicator = (ViewPagerIndicator) findViewById(c.zju.jianshu.R.id.indicator);
        viewPagerIndicator.setTabItemTitles(titles);

        // Fragment
        fragments.add(BookGridFragment.newInstance(BookGridFragment.TYPE_ALL));
        fragments.add(BookGridFragment.newInstance(BookGridFragment.TYPE_FAVORITE));

        // PagerAdapter
        pagerAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public int getCount() {
                return fragments.size();
            }

            @Override
            public Fragment getItem(int position) {
                return fragments.get(position);
            }
        };

        // 设置数据适配器
        viewPager.setAdapter(pagerAdapter);
        viewPagerIndicator.setViewPager(viewPager, 0);


//        handler = new Handler() {
//            public void handleMessage(Message msg) {
//                super.handleMessage(msg);
//                if (msg.what == 1) {
//                    try {
//                        upload(username, password, "book/upload");
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        };
//
//        timer = new Timer(true);
//        task = new TimerTask() {
//            public void run() {
//                Message msg = new Message();
//                msg.what = 1;
//                handler.sendMessage(msg);
//            }
//        };
//        timer.schedule(task, 0, 10000);
        dialog = new LovelyProgressDialog(this)
                .setIcon(R.drawable.ic_aa)
                .setTitleGravity(1)
                .setTitle("正在登录，请耐心等待")
                .setTopColorRes(R.color.colorPrimary);
    }



//    private void upLoad(String username, String password, String url) throws JSONException {
//        OkHttpClient mOkHttpClient = new OkHttpClient();
//        RequestBody formBody = new FormBody.Builder()
//                .add("username", username)
//                .add("password", password)
//                .add("json", getBooksInfo())
//                .build();
//        Request request = new Request.Builder()
//                .url("http://120.77.80.167:8000/" + url)
//                .post(formBody)
//                .build();
//        Call call = mOkHttpClient.newCall(request);
//        call.enqueue(new Callback() {
//            @Override
//            public void onFailure(Call call, IOException e) {
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        Toasty.error(getApplicationContext(), "网络状况不佳，更改未能同步到云端", Toast.LENGTH_SHORT, true).show();
//                    }
//                });
//            }
//
//            @Override
//            public void onResponse(Call call, Response response) throws IOException {
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                    }
//                });
//            }
//
//        });
//    }
//
//    private String getBooksInfo() throws JSONException {
//        List<Book> books = DataSupport.findAll(Book.class);
//        String s = "";
//        for (int i = 0; i < books.size(); i++) {
//            Book item = books.get(i);
//            JSONObject json = new JSONObject();
//            json.put("isbn", item.getIsbn13());
//            json.put("note", item.getNote());
//            String t = item.isFavourite() ? "True" : "False";
//            json.put("favorite", t);
//            s += json.toString();
//            if (i != books.size() - 1) {
//                s += ";";
//            }
//        }
//        return s;
//    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (scanResult != null) {
            String result = scanResult.getContents();
            Intent intent = new Intent(this, BookInfoAddActivity.class);
            intent.putExtra("ISBN", result);
            startActivity(intent);
        } else {
            Toasty.info(getApplicationContext(), "云端尚未找到该书的信息", Toast.LENGTH_SHORT, true).show();
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}
