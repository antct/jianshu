package c.zju.jianshu.fragment;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.design.widget.Snackbar;
import android.view.View;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

import c.zju.jianshu.R;
import c.zju.jianshu.activity.TypefaceUtil;
import c.zju.jianshu.model.bean.Book;


/**
 * Created by HaPBoy on 16/5/31.
 */
public class TotalFragment extends PreferenceFragment implements Preference.OnPreferenceClickListener {


    private Preference key;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.preference_total);
        List<Book> books_1 = DataSupport.order("id desc").find(Book.class);
        key = findPreference("total");
        key.setSummary("至今，你一共添加了" + String.valueOf(books_1.size()) + "本书");
        List<Book> books_2 = DataSupport.where("favourite = ?", "1").order("id desc").find(Book.class);
        key = findPreference("totalFavorite");
        key.setSummary("至今，你一共收藏了" + String.valueOf(books_2.size()) + "本书");

        key = findPreference("first");
        key.setSummary( (books_1.size() != 0) ? ("《" +books_1.get(books_1.size()-1).getTitle() + "》" ) :"还没有呢");
        key = findPreference("firstFavorite");
        key.setSummary( (books_2.size() != 0) ? ("《" +books_2.get(books_2.size()-1).getTitle() + "》" ) :"还没有呢");

        key = findPreference("last");
        key.setSummary( (books_1.size() != 0) ? ("《" +books_1.get(0).getTitle()  + "》" ) :"还没有呢");
        key = findPreference("lastFavorite");
        key.setSummary( (books_2.size() != 0) ? ("《" +books_2.get(0).getTitle()  + "》" ) :"还没有呢");

        int len = 0;
        int id = -1;
        for (int i = 0; i < books_1.size(); i++) {
            if (len < books_1.get(i).getNote().length()) {
                len = books_1.get(i).getNote().length();
                id = i;
            }
        }
        key = findPreference("most");
        if (id == -1) {
            key.setSummary("还没有呢");
        } else {
            key.setSummary("《" +books_1.get(id).getTitle()  + "》\n关于这本书，你总共写下了" + String.valueOf(len) + "个字" );
        }



    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        copyToClipboard(getView(), preference.getSummary().toString());
        return false;
    }

    private void copyToClipboard(View view, String info) {
        ClipboardManager manager = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clipData = ClipData.newPlainText("msg", info);
        manager.setPrimaryClip(clipData);
        Snackbar.make(view, "已经复制到剪切板", Snackbar.LENGTH_SHORT).show();
    }
}
