package gnpt.app.fcomediavilla.com.gnpt;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by SalmonManin on 11/02/2017.
 */

public class MyTextView extends android.support.v7.widget.AppCompatTextView {

    public MyTextView(Context context) {
        super(context);
        init();
    }

    public MyTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MyTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init(){
        Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "fonts/chico_bold.ttf");
        setTypeface(tf);

    }

}
