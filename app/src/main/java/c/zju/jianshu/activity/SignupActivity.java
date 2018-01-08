package c.zju.jianshu.activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.yarolegovich.lovelydialog.LovelyProgressDialog;
import com.yarolegovich.lovelydialog.LovelyStandardDialog;

import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import c.zju.jianshu.R;
import c.zju.jianshu.fragment.BookCoverFragment;
import c.zju.jianshu.fragment.BookInfoItemFragment;
import c.zju.jianshu.fragment.BookIntroFragment;
import c.zju.jianshu.model.bean.Book;
import c.zju.jianshu.model.data.DataManager;
import cn.pedant.SweetAlert.SweetAlertDialog;
import es.dmoral.toasty.Toasty;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by c on 2017/12/18.
 */

public class SignupActivity extends AppCompatActivity implements View.OnClickListener {
    TextView login, register, skip;
    EditText account, password;
    private Book book;
    private Handler handler;
    private Timer timer;
    private TimerTask task;
    private LovelyProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        TypefaceUtil.replaceFont(this, "fonts/default.otf");
        bindViews();
        dialog = new LovelyProgressDialog(this)
                .setIcon(R.drawable.ic_aa)
                .setTitleGravity(1)
                .setTitle("正在登录，请耐心等待")
                .setTopColorRes(R.color.colorPrimary);
    }

    private void bindViews() {
        login = findViewById(R.id.login);
        register = findViewById(R.id.signup);
        skip = findViewById(R.id.skip);
        account = findViewById(R.id.account);
        password = findViewById(R.id.password);
        login.setOnClickListener(this);
        register.setOnClickListener(this);
        skip.setOnClickListener(this);
        skip.setVisibility(View.GONE);
    }



    private void saveBookFromISBN(String ISBN, final String Favorite, final String content, final String date) {
        DataManager.getBookInfoFromISBN(ISBN, new com.android.volley.Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                book = DataManager.doubanBook2Book(DataManager.jsonObject2DoubanBook(response));
                Boolean isAdded = false;
                if (Favorite.equals("True")) {
                    book.setFavourite(true);}
                else {
                    book.setFavourite(false);
                }
                book.setNote(content);
                book.setNote_date(date);
                List<Book> books = DataSupport.findAll(Book.class);
                for (int i = 0; i < books.size(); i++) {
                    Book book_db = books.get(i);
                    if ((book_db.getAuthor() + book_db.getTitle()).equals(book.getAuthor() + book.getTitle())) {
                        if (Favorite.equals("True")) {
                            book_db.setFavourite(true);
                        } else {
                            book_db.setFavourite(false);
                        }
                        book_db.setNote(content);
                        book_db.setNote_date(date);
                        book_db.save();
                        isAdded = true;
                        break;
                    } else {
                        isAdded = false;
                    }
                }
                if (isAdded) {
                } else {
                    if (book.save()) {
                    } else {
                    }
                }
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "图书不存在或网络连接错误", Toast.LENGTH_SHORT).show();
                findViewById(c.zju.jianshu.R.id.errorView).setVisibility(View.VISIBLE);
            }
        });
    }



    private boolean compareLocalAndNet(String str) {
        List<Book> books = DataSupport.findAll(Book.class);
        final String[] isbn = str.split(";");
        for (int i = 0; !str.equals("") && i < isbn.length; i++) {
            boolean flag = false;
            JSONObject json = null;
            try {
                json = new JSONObject(isbn[i]);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            for (Book book : books) {
                String t = book.isFavourite() ? "True" : "False";
                try {
                    if (t.equals(json.getString("favorite")) && book.getIsbn13().equals(json.getString("isbn")) && book.getNote().equals(json.getString("note")))
                        flag = true;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            if (!flag)
                return false;
        }
        return true;
    }

    private void dropBooks() {
        List<Book> books = DataSupport.findAll(Book.class);
        for (Book book : books) {
            book.delete();
        }
    }


    private void postAsynHttp(final String username, final String password, String url) {
        OkHttpClient mOkHttpClient = new OkHttpClient();
        RequestBody formBody = new FormBody.Builder()
                .add("username", username)
                .add("password", password)
                .build();
        Request request = new Request.Builder()
                .url("http://120.77.80.167:8000/" + url)
                .post(formBody)
                .build();
        Call call = mOkHttpClient.newCall(request);
        if (url.equals("user/login")) {
            dialog.show();
        }
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dialog.dismiss();
                        Toasty.error(getApplicationContext(), "请检查网络连接", Toast.LENGTH_SHORT, true).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String str = response.body().string();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (str.equals("error")) {
                            dialog.dismiss();
                            Toasty.error(getApplicationContext(), "用户名不存在", Toast.LENGTH_SHORT, true).show();
                            return;
                        }
                        if (str.equals("error1")) {
                            dialog.dismiss();
                            Toasty.error(getApplicationContext(), "用户名密码不匹配", Toast.LENGTH_SHORT, true).show();
                            return;
                        } else if (str.equals("error2")) {
                            dialog.dismiss();
                            Toasty.error(getApplicationContext(), "用户名已经被注册", Toast.LENGTH_SHORT, true).show();
                            return;
                        } else if (str.equals("signup")) {
                            dialog.dismiss();
                            Toasty.success(getApplicationContext(), "注册成功", Toast.LENGTH_SHORT, true).show();
                            return;
                        } else {
                            dropBooks();
                            dialog.setTitle("正在同步，请耐心等待");
                            final String[] isbn = str.split(";");
                            handler = new Handler() {
                                public void handleMessage(Message msg) {
                                    super.handleMessage(msg);
                                    if (msg.what == 1) {
                                        timer.cancel();
                                        dialog.dismiss();
                                        Intent Intent = new Intent(SignupActivity.this, MainActivity.class);
                                        Intent.putExtra("username", username);
                                        Intent.putExtra("password", password);
                                        SignupActivity.this.startActivity(Intent);
                                        SignupActivity.this.finish();
                                    }
                                }
                            };

                            timer = new Timer(true);
                            task = new TimerTask() {
                                public void run() {
                                    if (str.equals("") || compareLocalAndNet(str)) {
                                        Message msg = new Message();
                                        msg.what = 1;
                                        handler.sendMessage(msg);
                                    }
                                }
                            };
                            timer.schedule(task, 0, 500);
                            for (int i = 0; !str.equals("") && i < isbn.length; i++) {
                                JSONObject json = null;
                                try {
                                    json = new JSONObject(isbn[i]);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                try {
                                    saveBookFromISBN(json.getString("isbn"), json.getString("favorite"), json.getString("note"), json.getString("date"));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                });
            }
        });
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.login:
                if (checkForLogin()) {
                    postAsynHttp(getAccount(), getPassword(), "user/login");
                }
                break;
            case R.id.signup:
                if (checkForSignup()) {
                    postAsynHttp(getAccount(), getPassword(), "user/signup");
                }
                break;
            case R.id.skip:
                Intent Intent = new Intent(SignupActivity.this, MainActivity.class);
                SignupActivity.this.startActivity(Intent);
                SignupActivity.this.finish();
                break;
        }
    }

    private boolean checkForLogin() {
        if (getAccount().length() == 0 || getPassword().length() == 0) {
            Toast.makeText(getApplicationContext(), "用户名和密码不能为空", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private boolean checkForSignup() {
        if (getAccount().length() == 0 || getPassword().length() == 0) {
            Toast.makeText(getApplicationContext(), "用户名和密码不能为空", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (getPassword().length() <= 6) {
            Toast.makeText(getApplicationContext(), "密码过短，请大于6个字符", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private String getAccount() {
        return account.getText().toString().trim();
    }

    private String getPassword() {
        return password.getText().toString();
    }


}

