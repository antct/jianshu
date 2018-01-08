package c.zju.jianshu.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;
import com.yarolegovich.lovelydialog.LovelyStandardDialog;

import org.json.JSONException;
import org.litepal.crud.DataSupport;

import java.util.List;

import c.zju.jianshu.R;
import c.zju.jianshu.activity.BookInfoActivity;
import c.zju.jianshu.activity.Data;
import c.zju.jianshu.activity.MainActivity;
import c.zju.jianshu.activity.Net;
import c.zju.jianshu.activity.SignupActivity;
import c.zju.jianshu.activity.TypefaceUtil;
import c.zju.jianshu.model.bean.Book;
import c.zju.jianshu.view.CustomTextView;
import cn.pedant.SweetAlert.SweetAlertDialog;
import c.zju.jianshu.adapter.BookGridAdapter;
import es.dmoral.toasty.Toasty;

/**
 * Created by HaPBoy on 5/18/16.
 */
public class BookGridFragment extends Fragment implements AdapterView.OnItemClickListener {

    public static final int TYPE_ALL = 1;
    public static final int TYPE_FAVORITE = 2;

    private static final String ARG_TYPE = "type";
    private int type = TYPE_ALL; // 显示的数据（全部、收藏）
    
    private GridView gridView; // 网格列表
    private BookGridAdapter bookGridAdapter; // 数据适配器
    private int gridPosition = -1; // 选中项的position
    private Handler handler;

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        if (this.isVisible()) {
            if (isVisibleToUser) {
                fetchData();
                bookGridAdapter.notifyDataSetChanged();
            }
        }
        super.setUserVisibleHint(isVisibleToUser);
    }

    public static BookGridFragment newInstance(int type) {
        BookGridFragment fragment = new BookGridFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_TYPE, type);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            type = getArguments().getInt(ARG_TYPE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(c.zju.jianshu.R.layout.fragment_book_grid, container, false);

        // gridView
        gridView = (GridView) view.findViewById(c.zju.jianshu.R.id.gridView);

        // ItemClickListener
        gridView.setOnItemClickListener(this);

        // ContextMenu - 'Delete' Function
        gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                gridPosition = position;
                Log.i("HB", "onItemLongClick:gridPosition: " + gridPosition);
                return false;
            }
        });
        registerForContextMenu(gridView);

        // EmptyView
        View emptyView = view.findViewById(c.zju.jianshu.R.id.empty);
        ImageView ivIcon = (ImageView) emptyView.findViewById(c.zju.jianshu.R.id.iv_icon);
        CustomTextView tvText = (CustomTextView) emptyView.findViewById(c.zju.jianshu.R.id.tv_text);
        if (type == TYPE_FAVORITE) {
            ivIcon.setImageDrawable(new IconicsDrawable(getContext()).icon(GoogleMaterial.Icon.gmd_favorite).colorRes(c.zju.jianshu.R.color.grid_empty_icon).sizeDp(40));
            tvText.setText("尚未收藏图书");
        } else {
            ivIcon.setImageDrawable(new IconicsDrawable(getContext()).icon(GoogleMaterial.Icon.gmd_import_contacts).colorRes(c.zju.jianshu.R.color.grid_empty_icon).sizeDp(48));
            tvText.setText("尚未添加图书");
        }
        gridView.setEmptyView(emptyView);

        return view;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        bookGridAdapter = new BookGridAdapter(getContext());
        fetchData();
        bookGridAdapter.notifyDataSetChanged();
        gridView.setAdapter(bookGridAdapter);
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(getActivity(), BookInfoActivity.class);
        intent.putExtra("id", (int) bookGridAdapter.getItemId(position));
        startActivity(intent);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        menu.add(1, 1, 1, (type == TYPE_ALL) ? "删除选中": "删除选中");
        super.onCreateContextMenu(menu, v, menuInfo);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        Log.i("HB", "onContextItemSelected:adapter.getCount(): " + bookGridAdapter.getCount());
        Log.i("HB", "onContextItemSelected:gridPosition: " + gridPosition);
        if (item.getItemId() == 1 && gridPosition != -1) {
            new LovelyStandardDialog(getContext())
                    .setTopColorRes(R.color.colorPrimary)
                    .setButtonsColorRes(R.color.cang_huang)
                    .setIcon(R.drawable.ic_face)
                    .setTitleGravity(1)
                    .setTitle("删除图书")
                    .setMessageGravity(1)
                    .setMessage("确定要删除这本图书吗？")
                    .setPositiveButton("确定", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // 刷新数据
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    DataSupport.delete(Book.class, bookGridAdapter.getItemId(gridPosition));
                                    fetchData();
                                    bookGridAdapter.notifyDataSetChanged();
                                    try {
                                        Net.upLoad(Data.getUsername(), Data.getPassword(), "book/upload");
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }, 800);
                            Toasty.info(getContext(), "删除成功", Toast.LENGTH_SHORT, true).show();
                        }
                    })
                    .setNegativeButton("返回", null)
                    .show();
        } else {
            return super.onContextItemSelected(item);
        }
        return true;

//        else if (item.getTitle().toString().equals("取消收藏")) {
//            if (item.getItemId() == 2 && gridPosition != -1) {
//                new LovelyStandardDialog(getContext())
//                        .setTopColorRes(R.color.colorPrimary)
//                        .setButtonsColorRes(R.color.cang_huang)
//                        .setIcon(R.drawable.ic_face)
//                        .setTitleGravity(1)
//                        .setTitle("取消收藏")
//                        .setMessageGravity(1)
//                        .setMessage("确定不再收藏这本图书吗？")
//                        .setPositiveButton("确定", new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//
//                                // 刷新数据
//                                Handler handler = new Handler();
//                                handler.postDelayed(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        Book t = DataSupport.find(Book.class, bookGridAdapter.getItemId(gridPosition));
//                                        t.setFavourite(false);
//                                        t.save();
//                                        fetchData();
//                                        bookGridAdapter.notifyDataSetChanged();
//                                    }
//                                }, 800);
//                                Toasty.info(getContext(), "取消收藏成功", Toast.LENGTH_SHORT, true).show();
//                            }
//                        })
//                        .setNegativeButton("返回", null)
//                        .show();
//            }
//            return true;
//        } else if (item.getTitle().toString().equals("添加收藏")) {
//            if (item.getItemId() == 2 && gridPosition != -1) {
//                final int[] flag = {1};
//                new LovelyStandardDialog(getContext())
//                        .setTopColorRes(R.color.colorPrimary)
//                        .setButtonsColorRes(R.color.cang_huang)
//                        .setIcon(R.drawable.ic_face)
//                        .setTitleGravity(1)
//                        .setTitle("添加收藏")
//                        .setMessageGravity(1)
//                        .setMessage("确定收藏这本图书吗？")
//                        .setPositiveButton("确定", new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                // 刷新数据
//
//                                handler = new Handler(){
//                                    public void handleMessage(Message msg) {
//                                        super.handleMessage(msg);
//                                        if(msg.what == 1){
//                                            fetchData();
//                                            bookGridAdapter.notifyDataSetChanged();
//                                            Toasty.info(getContext(), (flag[0] == 1 ?"收藏成功" : "已经收藏该书"), Toast.LENGTH_SHORT, true).show();
//                                        }
//                                    }
//                                };
//                                new Thread(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        Book t = DataSupport.find(Book.class, bookGridAdapter.getItemId(gridPosition));
//                                        if (t.isFavourite())
//                                            flag[0] = 0;
//                                        else {
//                                            t.setFavourite(true);
//                                            flag[0] = 1;
//                                        }
//                                        t.save();
//                                        Message msg = new Message();
//                                        msg.what = 1;
//                                        handler.sendMessage(msg);
//                                    }
//                                }).start();
//
//
//                            }
//                        })
//                        .setNegativeButton("返回", null)
//                        .show();
//            }
//        }
    }

    public void fetchData() {
        Log.i("HB", type + "GridFragment.fetchData");
        if (type == TYPE_FAVORITE) {
            bookGridAdapter.setData(DataSupport.where("favourite = ?", "1").order("id desc").find(Book.class));
        } else {
            bookGridAdapter.setData(DataSupport.order("id desc").find(Book.class));
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i("HB", type + "GridFragment.onResume");
        fetchData();
        bookGridAdapter.notifyDataSetChanged();
    }
}
