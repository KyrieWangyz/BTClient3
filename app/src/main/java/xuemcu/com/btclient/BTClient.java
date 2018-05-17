package xuemcu.com.btclient;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;
import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class BTClient extends Activity {

	private static final String TAG = "BTClient";
	private final static int REQUEST_CONNECT_DEVICE = 1;    //宏定义查询设备句柄
	private final static String MY_UUID = "00001101-0000-1000-8000-00805F9B34FB";   //SPP服务UUID号
	private InputStream is;    //输入流，用来接收蓝牙数据
	private EditText edit0;    //发送数据输入句柄
	private TextView tv_in;       //接收数据显示句柄
	private TextView tv_ins;
	private String smsg = "";    //显示用数据缓存
	private String smsgs = "";    //显示用数据缓存
	private String fmsg = "";    //保存用数据缓存
	BluetoothDevice _device = null;     //蓝牙设备
	BluetoothSocket _socket = null;      //蓝牙通信socket

	boolean _discoveryFinished = false;
	boolean bRun = true;
	boolean bThread = false;

	private BluetoothAdapter _bluetooth = BluetoothAdapter.getDefaultAdapter();    //获取本地蓝牙适配器，即蓝牙设备
	private SimpleDateFormat sdfdatetime;

	private int stature = 165;	//170cm
	private double weightVal = 0; //计算体重
	private double weight;


	Myapplication myapplication;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);   //设置画面为主画面 main.xml

		myapplication = new Myapplication();

		/* 解决兼容性问题，6.0以上使用新的API    使用动态申请权限的方式*/
		final int MY_PERMISSION_ACCESS_COARSE_LOCATION = 11;
		final int MY_PERMISSION_ACCESS_FINE_LOCATION = 12;
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			if(this.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
				requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},MY_PERMISSION_ACCESS_COARSE_LOCATION);
				Log.e("11111","ACCESS_COARSE_LOCATION");
			}
			if(this.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
				requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},MY_PERMISSION_ACCESS_FINE_LOCATION);
				Log.e("11111","ACCESS_FINE_LOCATION");
			}
		}
		tv_in = findViewById(R.id.in);      //得到数据显示句柄
		tv_ins = findViewById(R.id.ins);

		sdfdatetime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		//如果打开本地蓝牙设备不成功，提示信息，结束程序
		if (_bluetooth == null){
			Toast.makeText(this, "无法打开手机蓝牙，请确认手机是否有蓝牙功能！", Toast.LENGTH_LONG).show();
			finish();
			return;
		}
		// 设置设备可以被搜索
		new Thread(){
			public void run(){
				if(_bluetooth.isEnabled()==false){
					_bluetooth.enable();
				}
			}
		}.start();


	}


	//发送按键响应
	public void onSendButtonClicked(View v){
		int i=0;
		int n=0;
		if(_socket==null){
			Toast.makeText(this, "请先连接HC模块", Toast.LENGTH_SHORT).show();
			return;
		}
		if(edit0.getText().length()==0){
			Toast.makeText(this, "请先输入数据", Toast.LENGTH_SHORT).show();
			return;
		}
		try{
			OutputStream os = _socket.getOutputStream();   //蓝牙连接输出流
			byte[] bos = edit0.getText().toString().getBytes();
			for(i=0;i<bos.length;i++){
				if(bos[i]==0x0a)n++;
			}
			byte[] bos_new = new byte[bos.length+n];
			n=0;
			for(i=0;i<bos.length;i++){ //手机中换行为0a,将其改为0d 0a后再发送
				if(bos[i]==0x0a){
					bos_new[n]=0x0d;
					n++;
					bos_new[n]=0x0a;
				}else{
					bos_new[n]=bos[i];
				}
				n++;
			}
			os.write(bos_new);
		}catch(IOException e){
		}
	}

	//接收活动结果，响应startActivityForResult()
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch(requestCode){
			case REQUEST_CONNECT_DEVICE:     //连接结果，由DeviceListActivity设置返回
				// 响应返回结果
				if (resultCode == Activity.RESULT_OK) {   //连接成功，由DeviceListActivity设置返回
					// MAC地址，由DeviceListActivity设置返回
					String address = data.getExtras()
							.getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
					// 得到蓝牙设备句柄
					_device = _bluetooth.getRemoteDevice(address);
					// 用服务号得到socket
					try{
						_socket = _device.createRfcommSocketToServiceRecord(UUID.fromString(MY_UUID));
					}catch(IOException e){
						Toast.makeText(this, "连接失败！", Toast.LENGTH_SHORT).show();
					}
					//连接socket
					Button btn = (Button) findViewById(R.id.BtnConnect);
					try{
						_socket.connect();
						Toast.makeText(this, "连接"+_device.getName()+"成功！", Toast.LENGTH_SHORT).show();
						btn.setText("断开");
					}catch(IOException e){
						try{
							Toast.makeText(this, "连接失败！", Toast.LENGTH_SHORT).show();
							_socket.close();
							_socket = null;
						}catch(IOException ee){
							Toast.makeText(this, "连接失败！", Toast.LENGTH_SHORT).show();
						}
						return;
					}
					//打开接收线程
					try{
						is = _socket.getInputStream();   //得到蓝牙数据输入流
					}catch(IOException e){
						Toast.makeText(this, "接收数据失败！", Toast.LENGTH_SHORT).show();
						return;
					}
					if(bThread==false){
						readThread.start();
						bThread=true;
					}else{
						bRun = true;
					}
				}
				break;
			default:break;
		}
	}
	//接收数据线程
	Thread readThread=new Thread(){

		public void run(){
			int num = 0;
			byte[] buffer = new byte[1024];
			byte[] buffer_new = new byte[1024];
			int i = 0;
			int n = 0;
			bRun = true;
			//接收线程
			while(true){
				try{
					while(is.available()==0){
						while(bRun == false){}
					}
					while(true){
						if(!bThread)//跳出循环
							return;

						num = is.read(buffer);         //读入数据
						n=0;

						String s0 = new String(buffer,0,num);
						fmsg+=s0;    //保存收到数据
						for(i=0;i<num;i++){
							if((buffer[i] == 0x0d)&&(buffer[i+1]==0x0a)){
								buffer_new[n] = 0x0a;
								i++;
							}else{
								buffer_new[n] = buffer[i];
							}
							n++;
						}
						String s = new String(buffer_new,0,n);
						smsg=s;   //写入接收缓存
						if(is.available()==0)break;  //短时间没有数据才跳出进行显示
					}
					//发送显示消息，进行显示刷新
					handler.sendMessage(handler.obtainMessage());
				}catch(IOException e){
				}
			}
		}
	};

	@SuppressLint("HandlerLeak")	//在子线程里面  是不允许直接更新界面的
	Handler handler= new Handler(){
		public void handleMessage(Message msg){
			super.handleMessage(msg);
			Log.e(TAG, "handleMessage: "+smsg);
			if(Integer.parseInt(smsg) >= 10)
			{
				weight = Integer.parseInt(smsg);
				tv_in.setText("时间:"+sdfdatetime.format(new Date().getTime())+" 体重:"+Integer.parseInt(smsg)+"kg");   //显示数据
			}
			else{
				weight = Integer.parseInt(smsg)/10;
				tv_in.setText("时间:"+sdfdatetime.format(new Date().getTime())+" 体重:"+Integer.parseInt(smsg)/10+"."+Integer.parseInt(smsg)%10+"kg");   //显示数据
			}

			weightVal = (stature - 75)*0.65;	//公式计算

			Toast.makeText(getApplicationContext(),smsg,Toast.LENGTH_LONG).show();

			Log.d(TAG, "handle: "+ weight);
			Log.d(TAG, "Message: "+weightVal);


			if((weight >= (weightVal*0.9)) && weight <= (weightVal*1.1)) {

				tv_ins.setText("您的体重正常,请继续保持!");

			}else if((weight <= (weightVal*0.9)) && (weight >= (weightVal*0.8))) {

				tv_ins.setText("您的体重过轻,近期需要补充营养哦!");

			}else if( weight >= (weightVal*1.1) && (weight <= (weightVal*1.2))){

				tv_ins.setText("您的体重过重,请适当锻炼身体!");

			}else if(weight >= (weightVal*1.2)){

				tv_ins.setText("您的体重肥胖,请需要加强锻炼身体，多运动!");

			}else if(weight <= (weightVal*0.8)){

				tv_ins.setText("您太瘦了,需要加强营养补充!");

			}else{

				tv_ins.setText("失败");
			}
			DataBases myDataBase = new DataBases(BTClient.this);
			SQLiteDatabase db = myDataBase.getWritableDatabase();
			ContentValues values = new ContentValues();

			//values 负责往数据库写数据
			//Cursor 负责从数据库读数据
			values.put("Time",sdfdatetime.format(new Date().getTime()));
			if(Integer.parseInt(smsg) >= 10)
				values.put("Weight", Integer.parseInt(smsg));
			else
				values.put("Weight", Integer.parseInt(smsg)/10+"."+Integer.parseInt(smsg)%10);
			db.insert("Users",null,values);
//			db.insert(myapplication.getUser_name(),null,values);
			values.clear();

//			values.put("Time","2018-01-17 15:25:06");
//			values.put("Weight","60.1");
//			db.insert("Users",null,values);
//			values.clear();
//
//			values.put("Time","2018-01-30 18:43:20");
//			values.put("Weight","62.7");
//			db.insert("Users",null,values);
//			values.clear();
//
//			values.put("Time","2018-02-09 09:10:46");
//			values.put("Weight","61.5");
//			db.insert("Users",null,values);
//			values.clear();
//
//			values.put("Time","2018-02-16 11:28:55");
//			values.put("Weight","62.3");
//			db.insert("Users",null,values);
//			values.clear();
//
//			values.put("Time","2018-03-05 15:57:06");
//			values.put("Weight","59.2");
//			db.insert("Users",null,values);
//			values.clear();
//
//			values.put("Time","2018-03-11 08:24:15");
//			values.put("Weight","59.8");
//			db.insert("Users",null,values);
//			values.clear();
//
//			values.put("Time","2018-03-28 20:44:45");
//			values.put("Weight","61.5");
//			db.insert("Users",null,values);
//			values.clear();
//
//			values.put("Time","2018-04-15 10:26:19");
//			values.put("Weight","60.9");
//			db.insert("Users",null,values);
//			values.clear();

//			公式如下：
//			男性：(身高cm－80)×70﹪=标准体重 女性：(身高cm－70)×60﹪=标准体重
//			标准体重正负10﹪为正常体重
//			标准体重正负10﹪~ 20﹪为体重过重或过轻
//			标准体重正负20﹪以上为肥胖或体重不足
//					超重计算公式
//			超重%=[（实际体重－理想体重）/（理想体重）]×100%
//					页面放在那个曲线的下面就OK
			//	tv_in.setText(smsg);
		//	sv.scrollTo(0,tv_in.getMeasuredHeight()); //跳至数据最后一页
		}
	};

	//关闭程序调用处理部分
	public void onDestroy(){
		super.onDestroy();
		if(_socket!=null)  //关闭连接socket
			try{
				_socket.close();
			}catch(IOException e){}
		//	_bluetooth.disable();  //关闭蓝牙服务
	}

	//连接按键响应函数
	public void onConnectButtonClicked(View v){

		if(_bluetooth.isEnabled()==false){  //如果蓝牙服务不可用则提示
			Toast.makeText(this, " 打开蓝牙中...", Toast.LENGTH_LONG).show();
			return;
		}
		//如未连接设备则打开DeviceListActivity进行设备搜索
		Button btn = (Button) findViewById(R.id.BtnConnect);
		if(_socket==null){
			Intent serverIntent = new Intent(this, DeviceListActivity.class); //跳转程序设置
			startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);  					//设置返回宏定义
		}
		else{
			//关闭连接socket
			try{
				bRun = false;
				Thread.sleep(2000);

				is.close();
				_socket.close();
				_socket = null;
				btn.setText("连接");
			}catch(IOException e){}
			catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return;
	}

	//清除数据显示
	public void onClearButtonClicked(View v){
		tv_in.setText("");
		tv_ins.setText("");
	}

	public void onWeightButtonClicked(View v){

		Intent weightIntent = new Intent(BTClient.this,WeightActivity.class);
		startActivity(weightIntent);
	}

	//退出按键响应函数
	public void onQuitButtonClicked(View v){

		Intent user_intent=new Intent(BTClient.this,User_info.class);
		startActivity(user_intent);

		//---安全关闭蓝牙连接再退出，避免报异常----//
//		if(_socket!=null){
//			//关闭连接socket
//			try{
//				bRun = false;
//				Thread.sleep(2000);
//				is.close();
//				_socket.close();
//				_socket = null;
//			}catch(IOException e){}
//			catch (InterruptedException e) {
//				e.printStackTrace();
//			}
//		}
//		finish();

	}
}