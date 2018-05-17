package xuemcu.com.btclient;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.*;
import android.widget.EditText;

/**
 * Created by 吴勇 on 2018/4/29.
 */

public class Register extends Activity implements View.OnClickListener,
        RadioGroup.OnCheckedChangeListener {

    private LinearLayout register;
    private DataBases dataBases;
    private Button reg_btn;
    private android.widget.EditText user_edt;
    private android.widget.EditText pwd_edt;
    private EditText age_edt;
    private EditText tall_edt;
    private EditText weight_edt;
    private RadioGroup sexy_gp;
    String name;

    //全局变量myapplication
    Myapplication myapplication;


    private boolean isExit;   //用户名是否存在
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);

        user_edt = (EditText)findViewById(R.id.editText_reg_username);
        pwd_edt = (EditText)findViewById(R.id.editText_reg_pwd);
        sexy_gp = (RadioGroup)findViewById(R.id.rg_sex);
        age_edt = (EditText)findViewById(R.id.editText_age);
        tall_edt = (EditText)findViewById(R.id.editText_height);
        weight_edt = (EditText)findViewById(R.id.editText_weight);
        reg_btn = (Button)findViewById(R.id.register_btn);


        reg_btn.setOnClickListener(this);
        sexy_gp.setOnCheckedChangeListener(this);


        initView();
        initData();

        myapplication = new Myapplication();

    }


    /*
    * 设置标题栏
    * */
    public void initView(){
        register=(LinearLayout)findViewById(R.id.register);
    }

    public void initData(){
        TextView tv_title=(TextView)register.findViewById(R.id.headerTitle);
        tv_title.setText("用户注册");
    }


    /*
    * 注册按钮监听事件
    * */
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.register_btn:
                insert();
                break;
            default:
                break;
        }
    }


    /*
    * RadioGroup监听事件
    * */
    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId){
            case R.id.rb_male:
                myapplication.setSexy("男");
                break;
            case R.id.rb_famale:
                myapplication.setSexy("女");
                break;
            default:
                break;
        }
    }

    /*
    *用户注册，插入数据、验证
     */
    public void insert(){

        dataBases  = new DataBases(getApplicationContext());
        SQLiteDatabase db=dataBases.getWritableDatabase();  //创建可读写数据库实例

        String sql1 ="select * from User_info";

        Cursor cursor = db.rawQuery(sql1,null);

        while (cursor.moveToNext()){

            name = cursor.getString(1);    //第一列为id
//            name = cursor.getString(cursor.getColumnIndex("user_name"));

            //注册内容不能为空
            if ((user_edt.getText().toString().isEmpty()) ||
                 pwd_edt.getText().toString().isEmpty() ||
                 myapplication.getSexy().isEmpty() ||
                 age_edt.getText().toString().isEmpty() ||
                 tall_edt.getText().toString().isEmpty() ||
                 weight_edt.getText().toString().isEmpty()){

                isExit = true;
                Toast.makeText(this,"注册内容不能为空，请重新输入",Toast.LENGTH_SHORT).show();
                break;
            }


            if (user_edt.getText().toString().equals(name)){
                Toast.makeText(this,"已存在此用户,请重新注册",Toast.LENGTH_SHORT).show();
                isExit = true;
                break;
            }
        }


        if (isExit){
            isExit = false;
        }
        else {
            String sql2 = "insert into User_info(user_name,user_pwd,sexy,age,tall,weight) " +
                    "values ('"+user_edt.getText().toString()+"','"+pwd_edt.getText().toString()+"','" +
                    ""+myapplication.getSexy()+"','"+age_edt.getText().toString()+"','" +
                    ""+tall_edt.getText().toString()+"','"+weight_edt.getText().toString()+"')";

            db.execSQL(sql2);

            //Log.d("执行SQL",sql2);

            //获取体重,用作图例
            myapplication.setWeight(weight_edt.getText().toString());

            Intent intent=new Intent(Register.this,LoginActivity.class);
            startActivity(intent);
            Toast.makeText(this,"注册成功, 请重新登录",Toast.LENGTH_SHORT).show();
            finish();
        }

        cursor.close();
        db.close();
    }


}
