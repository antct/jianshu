package c.zju.jianshu.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;

import java.util.ArrayList;
import java.util.List;

import c.zju.jianshu.model.bean.Book;

/**
 * Created by HaPBoy on 5/15/16.
 */
public class BookGridAdapter extends BaseAdapter {

    private List<Book> list;
    private LayoutInflater inflater;
    Context context;

    public BookGridAdapter(Context context) {
        this.context = context;
        list = new ArrayList<>();
        inflater = LayoutInflater.from(context);
    }

    public void setData(List<Book> list) {
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return list.get(position).getId();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        BookGridViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new BookGridViewHolder();
            convertView = inflater.inflate(c.zju.jianshu.R.layout.fragment_book_grid_item, null);
            viewHolder.ivCover = (ImageView) convertView.findViewById(c.zju.jianshu.R.id.iv_cover);
            viewHolder.tvTitle = (TextView) convertView.findViewById(c.zju.jianshu.R.id.tv_title);
            viewHolder.rbRate = (RatingBar) convertView.findViewById(c.zju.jianshu.R.id.rb_rate);
            viewHolder.tvRate = (TextView) convertView.findViewById(c.zju.jianshu.R.id.tv_rate);
            convertView.setTag(viewHolder);
        }

        viewHolder = (BookGridViewHolder) convertView.getTag();
        Book bean = list.get(position);

        // 设置图片
        Glide.with(viewHolder.ivCover.getContext())
                .load(bean.getImage())
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(new IconicsDrawable(viewHolder.ivCover.getContext()).icon(GoogleMaterial.Icon.gmd_import_contacts).color(Color.GRAY).paddingDp(10))
                .into(viewHolder.ivCover);

        // 设置其他
        viewHolder.tvTitle.setText(bean.getTitle());
        viewHolder.rbRate.setRating((Float.parseFloat(bean.getAverage())/2));
        viewHolder.tvRate.setText(bean.getAverage());

        return convertView;
    }
}

class BookGridViewHolder {
    public ImageView ivCover;
    public TextView tvTitle;
    public RatingBar rbRate;
    public TextView tvRate;
}
