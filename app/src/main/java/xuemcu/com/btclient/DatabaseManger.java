package xuemcu.com.btclient;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

//数据库操作类
public class DatabaseManger<T> {

    private DataBases dataBases ;
    private  static  DatabaseManger instance = null;
    private SQLiteDatabase sqLiteDatabase;

    private DatabaseManger(Context context)
    {
        dataBases = new DataBases(context);
        sqLiteDatabase = dataBases.getWritableDatabase();
    }
    public  static  final DatabaseManger getInstance(Context context)
    {
        if (instance == null)
        {
            if(context == null) {
                throw new RuntimeException("Context is null.");

            }
            instance = new DatabaseManger(context);
        }
        return instance;
    }

    //



    //关闭数据库
    public void close()
    {
        if(sqLiteDatabase.isOpen())
        {
            sqLiteDatabase.close();
            sqLiteDatabase=null;
        }
        if(dataBases!=null)
        {
            dataBases.close();
            dataBases=null;
        }
        if(instance != null)
        {
            instance = null;
        }
    }
    //游标、光标
    public Cursor queryDataCursor(String table){
//   Cursor cursor = db.query("Login",null,null,null,null,null,null);
     return  sqLiteDatabase.query(table,null,null,null,null,null,null);
    }

    public Cursor queryData2Cursor(String sql, String[] selectionArgs)throws Exception
    {
        Cursor cursor = null;
        if(sqLiteDatabase.isOpen())
        {
            cursor = sqLiteDatabase.rawQuery(sql,selectionArgs);
        }else
        {
            throw  new RuntimeException("The DataBase has already closed");
        }
        return cursor;
    }
    public int getDataCounts(String table)throws Exception
    {
        Cursor cursor = null;
        int counts = 0;
        if(sqLiteDatabase.isOpen())
        {
            cursor = queryData2Cursor("select * from "+ table,null);
            if(cursor != null && cursor.moveToFirst())
            {
                counts = cursor.getCount();
            }
        }else
        {
            throw  new RuntimeException("The DataBase has already closed");
        }
        return counts;
    }

    public  long insetData(String table, ContentValues values)throws Exception
    {
        long id=0;
        if(sqLiteDatabase.isOpen())
        {
            id=sqLiteDatabase.insertOrThrow(table,null,values);
        }else
        {
            throw  new RuntimeException("The DataBase has already closed");
        }
        return id;
    }

    public  int updateData(String table, ContentValues values, String whereClaause, String[] whereArgs)throws Exception
    {
        int rowsNum = 0;
        if(sqLiteDatabase.isOpen())
        {
            rowsNum = sqLiteDatabase.update(table,values,whereClaause,whereArgs);
        }else
        {
            throw  new RuntimeException("The DataBase has already closed");
        }
        return rowsNum;
    }

    public long deleteData(String table, String whereClause, String[] whereArgs)throws Exception
    {
        long rowsNum =0;
        if(sqLiteDatabase.isOpen())
        {
            rowsNum=sqLiteDatabase.delete(table,whereClause,whereArgs);
        }else
        {
            throw  new RuntimeException("The DataBase has already closed");
        }
        return rowsNum;
    }

}
