package c.zju.jianshu.view;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

import c.zju.jianshu.view.FontCache;

/**
 * Created by c on 2017/12/19.
 */

public class CustomTextView extends android.support.v7.widget.AppCompatTextView {

    public CustomTextView(Context context) {
        super(context);
        applyCustomFont(context);
    }

    public CustomTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        applyCustomFont(context);
    }

    public CustomTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        applyCustomFont(context);
    }

    private void applyCustomFont(Context context) {
        Typeface customFont = FontCache.getTypeface("fonts/default.otf", context);
        setTypeface(customFont);
    }
}
