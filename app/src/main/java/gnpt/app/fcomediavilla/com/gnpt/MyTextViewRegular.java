package gnpt.app.fcomediavilla.com.gnpt;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by SalmonManin on 15/02/2017.
 */

public class MyTextViewRegular extends TextView {

    public MyTextViewRegular(Context context) {
        super(context);
        init();
    }

    public MyTextViewRegular(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MyTextViewRegular(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init(){
        Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "fonts/chico_regular.ttf");
        setTypeface(tf);
    }

}
