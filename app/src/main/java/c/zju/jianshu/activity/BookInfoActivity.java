package c.zju.jianshu.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.mikepenz.iconics.context.IconicsContextWrapper;
import com.xyzlf.share.library.bean.ShareEntity;
import com.xyzlf.share.library.interfaces.ShareConstant;
import com.xyzlf.share.library.util.ShareUtil;
import com.yarolegovich.lovelydialog.LovelyChoiceDialog;
import com.yarolegovich.lovelydialog.LovelyInfoDialog;

import org.json.JSONException;
import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import c.zju.jianshu.R;
import c.zju.jianshu.fragment.BookCoverFragment;
import c.zju.jianshu.fragment.BookInfoItemFragment;
import c.zju.jianshu.view.ViewPagerIndicator;
import cn.pedant.SweetAlert.SweetAlertDialog;
import c.zju.jianshu.fragment.BookIntroFragment;
import c.zju.jianshu.fragment.BookNoteFragment;
import c.zju.jianshu.model.bean.Book;


/**
 * Created by HaPBoy on 5/18/16.
 */
public class BookInfoActivity extends BaseActivity {

    private Context context;

    private ViewPager viewPager;
    private FragmentPagerAdapter pagerAdapter;

    private ViewPagerIndicator viewPagerIndicator;
    private List<String> titles = Arrays.asList("关于图书", "关于内容", "你的记忆");

    private List<Fragment> fragments = new ArrayList<>();
    private Book book;
    // favorite[0]: 取消收藏, favorite[1]: 收藏
    private int favorite[] = {R.drawable.ic_favorite_border_white_24dp, R.drawable.ic_favorite_white_24dp};

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(IconicsContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_info);

        context = this;
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // 图书ID
        int bookId = getIntent().getIntExtra("id", -1);

        // 图书Obj
        book = DataSupport.find(Book.class, bookId);

        // Activity标题
        setTitle(book.getTitle());

        // ViewPager
        viewPager = (ViewPager) findViewById(R.id.view_pager);

        // ViewPagerIndicator
        viewPagerIndicator = (ViewPagerIndicator) findViewById(R.id.indicator);
        viewPagerIndicator.setTabItemTitles(titles);
        viewPagerIndicator.setVisibleTabCount(3);

        // 基本信息 Fragment
        fragments.add(BookInfoItemFragment.newInstance(bookId));

        // 图书简介 Fragment
        fragments.add(BookIntroFragment.newInstance(bookId));

        // 我的笔记 Fragment
        fragments.add(BookNoteFragment.newInstance(bookId));

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

        // 图书封面
        Fragment bookCoverragment = BookCoverFragment.newInstance(bookId);
        getSupportFragmentManager().beginTransaction().add(c.zju.jianshu.R.id.fragment_book_cover, bookCoverragment).commit();
        TypefaceUtil.replaceFont(this, "fonts/default.otf");
    }

    public void showShareDialog() {
        ShareEntity testBean = new ShareEntity("分享书籍", "我是内容，描述内容。");
        testBean.setUrl("https://www.baidu.com"); //分享链接
        testBean.setImgUrl("https://www.baidu.com/img/bd_logo1.png");
        ShareUtil.showShareDialog(this, testBean, ShareConstant.REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        /**
         * 分享回调处理
         */
        if (requestCode == ShareConstant.REQUEST_CODE) {
            if (data != null) {
                int channel = data.getIntExtra(ShareConstant.EXTRA_SHARE_CHANNEL, -1);
                int status = data.getIntExtra(ShareConstant.EXTRA_SHARE_STATUS, -1);
                onShareCallback(channel, status);
            }
        }
    }

    /**
     * 分享回调处理
     * @param channel
     * @param status
     */
    private void onShareCallback(int channel, int status) {
        new ShareCallBack().onShareCallback(channel, status);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                startActivity(new Intent(this, MainActivity.class));
                return true;
            case c.zju.jianshu.R.id.action_favorite:
                book.setFavourite(!book.isFavourite());
                book.save();
                invalidateOptionsMenu();
                new LovelyInfoDialog(this)
                        .setTopColorRes(R.color.colorPrimary)
                        .setIcon(R.drawable.ic_face)
                        //This will add Don't show again checkbox to the dialog. You can pass any ID as argument
                        .setTitleGravity(1)
                        .setTitle(book.isFavourite() ? "收藏成功" : "取消收藏")
                        .setMessageGravity(1)
                        .setMessage(book.isFavourite() ? "图书已收藏" : "图书已取消收藏")
                        .setConfirmButtonText("确定")
                        .show();
                try {
                    Net.upLoad(Data.getUsername(), Data.getPassword(), "book/upload");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return true;
            case R.id.action_share:
                showShareDialog();
                return true;
            case c.zju.jianshu.R.id.action_browser:
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(book.getAlt()));
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(c.zju.jianshu.R.menu.book_info_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem menuItem = menu.findItem(c.zju.jianshu.R.id.action_favorite);
        menuItem.setIcon(favorite[book.isFavourite() ? 1 : 0]);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            startActivity(new Intent(this, MainActivity.class));
            return true;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }
}
