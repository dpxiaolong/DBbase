package com.example.luozhenlong.myapplication;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.util.List;

import DBframe.DbAutoWorker;
import DBframe.Bean.Student;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private DbAutoWorker dbAutoWorker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //1创建一个数据库操作者
        dbAutoWorker = new DbAutoWorker(this);


        //3.1 插入数据
        //可以直接插入对象，内部会自动检查，没有表自动创建
        Student student = new Student("mengdatongling",18,true,2000);
        dbAutoWorker.insert(student);


        //3.2查询数据 会查询到之前插入的Student对象的所有数据
        List<Student> all = dbAutoWorker.findAll(Student.class);
        for (int i = 0; i < all.size(); i++) {
            Log.i(TAG,"all student "+all.get(i).toString());
        }


//        //3.3 删除数据演示
//        dbAutoWorker.deleteById(Student.class,2);


//        //3.4 修改某一条数据根据id
//        ContentValues values = new ContentValues();
//        values.put("age",30);
//        values.put("isMale",true);
//        values.put("name","meichangsu");
//        values.put("salary",5000);
//        int result = dbAutoWorker.updateById(Student.class,values,4);
//        Log.i(TAG,"upadate result"+result);
    }

    @Override
    protected void onDestroy() {
        //4 释放资源
        dbAutoWorker.release();
        super.onDestroy();
    }
}
