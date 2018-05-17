package xuemcu.com.btclient;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by 吴勇 on 2018/4/28.
 */

public class TitleBar extends RelativeLayout {
    private ImageButton mLeftBtn;
    private Button edt_btn;
    private TextView title;


    public TitleBar(Context context, AttributeSet attributeSet){
        super(context,attributeSet);

        //加载布局
        LayoutInflater.from(context).inflate(R.layout.titlebar, this);

        //获取控件
        mLeftBtn=(ImageButton)findViewById(R.id.back_btn);
        edt_btn=(Button)findViewById(R.id.edt_btn);
        title=(TextView)findViewById(R.id.headerTitle);


        mLeftBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ((Activity)getContext()).finish();
                Toast.makeText(getContext(),"点击返回",Toast.LENGTH_SHORT).show();
            }
        });

        edt_btn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(),"点击编辑",Toast.LENGTH_SHORT).show();
            }
        });

    }


}
