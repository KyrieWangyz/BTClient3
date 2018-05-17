package xuemcu.com.btclient;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends Activity implements View.OnClickListener {
    private DataBases dataBases;
    private Button login;
    private Button log_reg;
    private EditText user;
    private EditText pwd;
    String passwd;
    String name;

    Myapplication myapplication;

    private boolean isRight;  //登陆时判断用户密码是否正确
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        login = (Button) findViewById(R.id.btn_login);
        log_reg = (Button)findViewById(R.id.login_register);
        user = (EditText) findViewById(R.id.editText_login_username);
        pwd = (EditText) findViewById(R.id.editText_login_userpwd);

        login.setOnClickListener(this);
        log_reg.setOnClickListener(this);

        myapplication = new Myapplication();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_login:
                select();
//                delete();
                break;
            case R.id.login_register:
                reg();
                break;
            default:
                break;
        }
    }

    //进入注册页面
    public void reg(){
        Intent reg_intent=new Intent(LoginActivity.this,Register.class);
        startActivity(reg_intent);
        Toast.makeText(this,"进入注册",Toast.LENGTH_SHORT).show();
    }


    //查询、验证
    public void select(){
        isRight =false;
        dataBases = new DataBases(getApplicationContext());
        SQLiteDatabase db=dataBases.getWritableDatabase();

        String sql = "select * from User_info";

        Cursor cursor = db.rawQuery(sql, null);
        while (cursor.moveToNext()) {

            //第一列为id
            name =  cursor.getString(1); //获取第2列的值,第一列的索引从0开始
            passwd = cursor.getString(2);//获取第3列的值


            if((user.getText().toString().equals(name))&&(pwd.getText().toString().equals(passwd)))
            {
                myapplication.setUser_name(user.getText().toString());      //获取用户名
                Toast.makeText(this, "用户验证成功", Toast.LENGTH_SHORT).show();
                isRight=true;
                Intent login_intent=new Intent(LoginActivity.this,BTClient.class);
                startActivity(login_intent);
                finish();
            }
        }

        if((user.getText().toString().isEmpty())||(pwd.getText().toString().isEmpty())){

            Toast.makeText(this, "不能为空，请重新输入", Toast.LENGTH_SHORT).show();
//            isRight =false;
        }

        if (isRight!=true){
            Toast.makeText(this, "账号或者密码错误,请重新输入", Toast.LENGTH_SHORT).show();
        }
        cursor.close();
        db.close();     //关闭数据库
    }


    /*
    *删除
    * */
//    public void delete(){
//        dataBases = new DataBases(getApplicationContext());
//        SQLiteDatabase db=dataBases.getWritableDatabase();
//
//        String sql1 = "delete from User_info where _id = 2";
//        String sql2 = "delete from User_info where _id = 3";
//        String sql3 = "delete from User_info where _id = 12";
//        String sql4 = "delete from User_info where _id = 13";
//        String sql5 = "delete from User_info where _id = 14";
//
//        db.execSQL(sql1);
//        db.execSQL(sql2);
//        db.execSQL(sql3);
//        db.execSQL(sql4);
//        db.execSQL(sql5);
//
//        db.close();
//    }

}
