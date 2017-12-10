package DBframe;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.lang.reflect.Field;

import DBframe.Bean.Student;

/**
 * 数据库基本的创建升级类
 * Created by luozhenlong on 2017/12/9.
 */

public class MySQLiteHelper extends SQLiteOpenHelper {

    private static final String TAG = "MySQLiteHelper";
    public MySQLiteHelper(Context context, String DBname, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DBname, factory, version);
        Log.i(TAG,"MySQLiteHelper");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.i(TAG,"onCreate enter");
        createTable(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    /**
     * 根据从传入的javabean直接生成SQL建表语句
     * @param clazz 要存入数据对应的javabean
     * @return SQL建表语句
     */
    private String getSQLString(Class<?> clazz){
        StringBuilder sb = new StringBuilder();

        String tabName = Utils.getTableName(clazz);
        //开始构造建表语句
        sb.append("CREATE TABLE IF NOT EXISTS ").append(tabName).append(" (id  INTEGER PRIMARY KEY AUTOINCREMENT, ");

        //得到类中所有属性对象数组
        Field[] fields = clazz.getDeclaredFields();

        for (Field fd : fields) {
            String fieldName = fd.getName();
            Log.i(TAG,"fieldName"+fieldName);
            String fieldType = fd.getType().getName();
            //过滤掉编译器自动生成的成员变量
            if(fd.isSynthetic() || fd.getName().equals("serialVersionUID")){
                Log.i(TAG,"fd.isSynthetic()"+fd.getName());
                continue;
            }
            if (fieldName.equalsIgnoreCase("_id") || fieldName.equalsIgnoreCase("id")) {
                continue;
            } else {
                sb.append(fieldName).append(Utils.getColumnType(fieldType)).append(", ");
            }
        }
        int len = sb.length();
        sb.replace(len - 2, len, ")");
        Log.i(TAG, "the SQL is " + sb.toString());
        String SQLString = new String(sb);
        return SQLString;
    }

    public void createTable(SQLiteDatabase db){
        Log.i(TAG,"createTable");
        db.execSQL(getSQLString(Student.class));
    }

    public void createTable(SQLiteDatabase db,Object obj){
        Log.i(TAG,"createTable by obj");
        db.execSQL(getSQLString(obj.getClass()));

    }

}
