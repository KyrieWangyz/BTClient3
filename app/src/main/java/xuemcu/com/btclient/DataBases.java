package xuemcu.com.btclient;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

public class DataBases extends SQLiteOpenHelper {

    private Context mContext;
    static Myapplication myapplication;

    //数据库名称
    private static  final String DATABASE_NAME = "DataBase.db";
    //数据库版本号
    private static final int DATABASE_VERSION=1;

    public static String Users = "create table  Users("
            + "ID INTEGER primary key autoincrement, "
            + "Time TEXT,"               //时间
            + "Weight REAL)";           //体重Weight


//    '"+myapplication.getUser_name()+"'
    //用户信息表 user_info
    public static String User_info = "create table User_info ("
            + "_id INTEGER primary key autoincrement,"
            + "user_name TEXT,"
            + "user_pwd TEXT,"
            + "sexy TEXT,"
            + "age INTEGER,"
            + "tall REAL,"
            + "weight REAL)";


    public DataBases (Context context)
    {

        this(context,DATABASE_NAME,null,DATABASE_VERSION);
        mContext = context;

    }

    public DataBases(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public DataBases(Context context, String name, SQLiteDatabase.CursorFactory factory, int version, DatabaseErrorHandler errorHandler) {
        super(context, name, factory, version, errorHandler);
    }


    //执行sql语句
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(Users);
        db.execSQL(User_info);
        Toast.makeText(mContext,"Create User and User_info Succeeded", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if(newVersion > oldVersion)
        {
            db.execSQL("DROP TABLE IF EXISTS "+ Users);
            db.execSQL("DROP TABLE IF EXISTS "+ User_info);
            Toast.makeText(mContext,"drop table succed",Toast.LENGTH_SHORT).show();
            onCreate(db);
        }
    }

    public boolean deleteDatabase(Context context) {
        return context.deleteDatabase(DATABASE_NAME);
    }
}