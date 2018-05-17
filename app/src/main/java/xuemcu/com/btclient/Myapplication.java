package xuemcu.com.btclient;

import android.app.Application;

/**
 * Created by 吴勇 on 2018/5/4.
 * 创建全局变量
 */

public class Myapplication extends Application{

    static String user_name;
    private String user_pwd;
    private String tall;
    private String weight;
    private String sexy;
    private int age;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    //用户名
    public void setUser_name(String user_name){
        this.user_name=user_name;
    }

    public String getUser_name(){
        return user_name;
    }


    //密码
    public void setUser_pwd(String user_pwd){
        this.user_pwd=user_pwd;
    }

    public String getUser_pwd(){
        return user_pwd;
    }


    //身高
    public void setTall(String tall){
        this.tall=tall;
    }

    public String getTall(){
        return tall;
    }

    //体重
    public void setWeight(String weight){
        this.weight=weight;
    }

    public String getWeight(){
        return weight;
    }

    //年龄
    public void setAge(int age){
        this.age=age;
    }

    public int getAge(){
        return age;
    }

    //性别
    public void setSexy(String sexy){
        this.sexy=sexy;
    }

    public String getSexy(){
        return sexy;
    }


}
