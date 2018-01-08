package c.zju.jianshu.activity;

import android.content.Context;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;

import com.yarolegovich.lovelydialog.LovelyInfoDialog;

import org.json.JSONException;
import org.litepal.crud.DataSupport;

import java.text.SimpleDateFormat;
import java.util.Date;

import c.zju.jianshu.R;
import c.zju.jianshu.model.bean.Book;
import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Created by HaPBoy on 5/21/16.
 */
public class BookNoteEditActivity extends BaseActivity {

    // Context
    private Context context;

    // Book
    private Book book;

    // 笔记输入框
    private EditText etNote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(c.zju.jianshu.R.layout.activity_book_note_edit);

        // Context
        context = this;

        // 返回按钮
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // 图书ID
        int itemId = getIntent().getIntExtra("id", -1);

        // 图书Obj
        book = DataSupport.find(Book.class, itemId);

        // Activity标题
        setTitle("");

        // 笔记输入框
        etNote = (EditText) findViewById(c.zju.jianshu.R.id.et_note);

        // 显示笔记内容
        etNote.setText(book.getNote());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            case c.zju.jianshu.R.id.action_save:
                book.setNote(etNote.getText().toString().trim());
                book.setNote_date(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
                book.save();
                new LovelyInfoDialog(this)
                        .setTopColorRes(R.color.colorPrimary)
                        .setIcon(R.drawable.ic_face)
                        //This will add Don't show again checkbox to the dialog. You can pass any ID as argument
                        .setTitleGravity(1)
                        .setTitle("保存成功")
                        .setMessageGravity(1)
                        .setMessage("笔记已保存")
                        .setConfirmButtonText("确定")
                        .show();
                try {
                    Net.upLoad(Data.getUsername(), Data.getPassword(), "book/upload");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(c.zju.jianshu.R.menu.book_note_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }
}
