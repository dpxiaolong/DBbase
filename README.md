一：功能简介

傻瓜式的数据库操作框架，简单三部完成增/删/改/查。

二：支持类型

*支持由八种基本数据类型封装的任意javabean

*自动完成数据库创建，名字都不用取

*自动生成表格，名字都不用取，自动用类名建表。

*带给你从未有过的数据库库使用体验


//使用示例

public class MainActivity extends AppCompatActivity {


    private static final String TAG = "MainActivity";
    private DbAutoWorker dbAutoWorker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //1创建一个数据库操作者
        dbAutoWorker = new DbAutoWorker(this);


        //2.1 插入数据
        //可以直接插入对象，内部会自动检查，没有表自动创建
        Student student = new Student("mengdatongling",18,true,2000);
        dbAutoWorker.insert(student);


        //2.2查询数据 会查询到之前插入的Student对象的所有数据
        List<Student> all = dbAutoWorker.findAll(Student.class);
        for (int i = 0; i < all.size(); i++) {
            Log.i(TAG,"all student "+all.get(i).toString());
        }


//        //2.3 删除数据演示

//        dbAutoWorker.deleteById(Student.class,2);


//        //2.4 修改某一条数据根据id

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
        //3 释放资源
        dbAutoWorker.release();
        super.onDestroy();
    }
}

