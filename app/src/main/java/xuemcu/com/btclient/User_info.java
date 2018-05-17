package xuemcu.com.btclient;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RestrictTo;
import android.support.v7.widget.ViewUtils;
import android.text.InputType;
import android.text.method.KeyListener;
import android.util.Log;
import android.view.View;
import android.widget.*;
import android.widget.EditText;

/**
 * Created by 吴勇 on 2018/5/1.
 */

public class User_info extends Activity implements View.OnClickListener, RadioGroup.OnCheckedChangeListener {

    private LinearLayout user_info;

    private LinearLayout register;
    private DataBases dataBases;
    private Button save_btn;
    private android.widget.EditText user_edt;
    private android.widget.EditText pwd_edt;
    private android.widget.EditText age_edt;
    private android.widget.EditText tall_edt;
    private android.widget.EditText weight_edt;
    private RadioGroup sexy_gp;

    Myapplication myapplication;

    boolean flag = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_info);

        user_edt = (android.widget.EditText)findViewById(R.id.editText_reg_username);
        pwd_edt = (android.widget.EditText)findViewById(R.id.editText_reg_pwd);
        sexy_gp = (RadioGroup)findViewById(R.id.edt_sex);
        age_edt = (android.widget.EditText)findViewById(R.id.editText_age);
        tall_edt = (android.widget.EditText)findViewById(R.id.editText_height);
        weight_edt = (EditText)findViewById(R.id.editText_weight);
        save_btn = (Button)findViewById(R.id.save_btn);

//        user_edt.setKeyListener(null);
//        pwd_edt.setKeyListener(null);
//        age_edt.setKeyListener(null);
//        tall_edt.setKeyListener(null);
//        weight_edt.setKeyListener(null);


        save_btn.setOnClickListener(this);
        sexy_gp.setOnCheckedChangeListener(this);

        myapplication = new Myapplication();

        initView();
        initData();
        showData();
    }


    /*
    * 设置标题栏
    * */
    public void initView(){
        user_info =(LinearLayout)findViewById(R.id.user_info);
    }
    public void initData(){
        TextView tv_title = (TextView)user_info.findViewById(R.id.headerTitle);
        Button edt_btn = (Button)user_info.findViewById(R.id.edt_btn);

        tv_title.setText("修改个人资料");
        edt_btn.setVisibility(View.VISIBLE);

    }


    /*
    * 保存按钮监听事件
    * */
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.save_btn:
                saveData();
                break;
            case R.id.edt_btn:

                break;
            default:
                break;
        }
    }


    /*
    * RadioGroup事件
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
    * 设置显示数据信息
    * */

    public void showData(){
        dataBases  = new DataBases(getApplicationContext());
        SQLiteDatabase db=dataBases.getWritableDatabase();  //创建可读写数据库实例
        String sql;
        if(myapplication.getUser_name().getClass().getName().equals("java.lang.String")){
             sql= "select * from User_info where user_name=" +"\""+myapplication.getUser_name()+"\"";
        }
        else {
            sql = "select * from User_info where user_name=" + myapplication.getUser_name();
        }

        Cursor cursor = db.rawQuery(sql,null);

        while (cursor.moveToNext()){

            user_edt.setText(cursor.getString(1));
            pwd_edt.setText(cursor.getString(2));
            trans();    //设置RadioGroup
            age_edt.setText(cursor.getString(4));
            tall_edt.setText(cursor.getString(5));
            weight_edt.setText(cursor.getString(6));
        }

        cursor.close();
        db.close();
    }


    /*
    * 保存数据信息
    * */
    public void saveData(){
        dataBases  = new DataBases(getApplicationContext());
        SQLiteDatabase db=dataBases.getWritableDatabase();  //创建可读写数据库实例
        String sql1 ="select * from User_info where user_name="+myapplication.getUser_name();

        Cursor cursor = db.rawQuery(sql1,null);

        while (cursor.moveToNext()){
            String sql2 = "update User_info set user_name ='"+user_edt.getText().toString()+"'," +
                    "user_pwd = '"+pwd_edt.getText().toString()+"' ," +
                    "sexy = '"+myapplication.getSexy()+"'," +
                    "age = '"+age_edt.getText().toString()+"'," +
                    "tall = '"+tall_edt.getText().toString()+"'," +
                    "weight = '"+weight_edt.getText().toString()+"' " +
                    "where _id ="+Integer.parseInt(cursor.getString(0));

            db.execSQL(sql2);

            Toast.makeText(this,"保存信息成功",Toast.LENGTH_SHORT).show();
        }
        cursor.close();
        db.close();
    }


    /*
    * 性别数据转换
    * */
    public void trans(){
        dataBases  = new DataBases(getApplicationContext());
        SQLiteDatabase db=dataBases.getWritableDatabase();  //创建可读写数据库实例
        String sql1 ="select * from User_info";

        Cursor cursor = db.rawQuery(sql1,null);

        if (cursor.moveToNext()){
            if (cursor.getString(3)=="男")
            {
                sexy_gp.check(R.id.rb_male);
            }
            else {
                sexy_gp.check(R.id.rb_famale);
            }
        }
    }


    /*
    * 设置编辑状态
    * */
//    public void editBle(){
//        if (flag==true)
//        {
//
//            user_edt.setCursorVisible(false);
//            user_edt.setFocusable(false);
//            user_edt.setFocusableInTouchMode(false);
//
//            pwd_edt.setCursorVisible(false);
//            pwd_edt.setFocusable(false);
//            pwd_edt.setFocusableInTouchMode(false);
//
//            flag = false;
//        }else
//        {
//            user_edt.setFocusable(true);
//            user_edt.setCursorVisible(true);
//            user_edt.setFocusableInTouchMode(true);
//            user_edt.requestFocus();
//
//            pwd_edt.setFocusable(true);
//            pwd_edt.setCursorVisible(true);
//            pwd_edt.setFocusableInTouchMode(true);
//            pwd_edt.requestFocus();
//
//            flag = true;
//        }
//
//    }
}
