package c.zju.jianshu.activity;

import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.List;

import c.zju.jianshu.model.bean.Book;
import es.dmoral.toasty.Toasty;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by c on 2017/12/26.
 */

public class Net {
    private static String getBooksInfo() throws JSONException {
        List<Book> books = DataSupport.findAll(Book.class);
        String s = "";
        for (int i = 0; i < books.size(); i++) {
            Book item = books.get(i);
            JSONObject json = new JSONObject();
            json.put("isbn", item.getIsbn13());
            json.put("note", item.getNote());
            json.put("date", item.getNote_date());
            String t = item.isFavourite() ? "True" : "False";
            json.put("favorite", t);
            s += json.toString();
            if (i != books.size() - 1) {
                s += ";";
            }
        }
        return s;
    }

    public static void upLoad(String username, String password, String url) throws JSONException {
        OkHttpClient mOkHttpClient = new OkHttpClient();
        RequestBody formBody = new FormBody.Builder()
                .add("username", username)
                .add("password", password)
                .add("json", getBooksInfo())
                .build();
        Request request = new Request.Builder()
                .url("http://120.77.80.167:8000/" + url)
                .post(formBody)
                .build();
        Call call = mOkHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        Toasty.error(getApplicationContext(), "网络状况不佳，更改未能同步到云端", Toast.LENGTH_SHORT, true).show();
//                    }
//                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                    }
//                });
            }

        });
    }
}
