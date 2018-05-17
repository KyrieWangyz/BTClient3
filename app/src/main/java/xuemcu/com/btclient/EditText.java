package xuemcu.com.btclient;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.AppCompatEditText;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

public class EditText extends AppCompatEditText {

    private Context context;
    private Drawable leftImg;
    private Drawable fork;
    public boolean isForkClickable() {
        return isForkClickable;
    }

    public void setForkClickable(boolean forkClickable) {
        isForkClickable = forkClickable;
    }

    private boolean isForkClickable = true;

    public EditText(Context context) {
        super(context);
    }

    public EditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public EditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        context = getContext();
        Drawable[] drawables = getCompoundDrawables();
        leftImg = drawables[0];
        setPadding(10, 0, 10, 0);
        setCompoundDrawablePadding(10);//设置内边距
    }
    //设置右侧图片
    private void setDelDrawable() {
        setCompoundDrawablesWithIntrinsicBounds(leftImg, null, fork, null);
    }

    private void setClearDrawableVisible() {
        setCompoundDrawablesWithIntrinsicBounds(leftImg, null, null, null);
    }
//    public void startWarningAnimation() {
//        Animation animation = AnimationUtils.loadAnimation(context, R.anim.edit_text_warning);
//        startAnimation(animation);
//    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (fork != null && event.getAction() == MotionEvent.ACTION_UP) {
            int x = (int) event.getRawX();
            int y = (int) event.getRawY();
            Rect rect = new Rect();
            getGlobalVisibleRect(rect);
            rect.left = rect.right - 50;
            if (rect.contains(x, y)) {
                if (isForkClickable) {
                    setText("");
                }
            }
        }
        return super.onTouchEvent(event);
    }
}
