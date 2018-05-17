package xuemcu.com.btclient;

import android.app.Dialog;
import android.content.Context;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;


//点下
public class ProgersssDialog extends Dialog {
	private Context context;
	private ImageView progress_img;
	private TextView progress_txt;

	// 方案一
	public ProgersssDialog(Context context) {

		super(context, R.style.progress_dialog);
		this.context = context;
		// 初始化页面
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		View view = inflater.inflate(R.layout.progress_dialog, null);

		progress_img = (ImageView) view.findViewById(R.id.progress_img);
		progress_txt = (TextView) view.findViewById(R.id.progress_txt);

		// 添加动态循环
		Animation anim = AnimationUtils.loadAnimation(context, R.anim.loading_dialog_progressbar);
		progress_img.setAnimation(anim);
		progress_txt.setText(R.string.progressbar_dialog_txt);

		// dialog填充视图
		setContentView(view);
		show();

	}

	public void setMsg(String msg) {
		progress_txt.setText(msg);
	}
	public void setMsg(int msgId) {
		progress_txt.setText(msgId);
	}

	// 可以屏蔽返回键
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			// return true;
			return false;
		}
		return super.onKeyDown(keyCode, event);
	}

}