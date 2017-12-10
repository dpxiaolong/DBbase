package DBframe;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 用于处理数据库操作，实现傻瓜式的操作
 * Created by luozhenlong on 2017/12/9.
 */

public class DbAutoWorker {

    private static final String TAG = "DbAutoWorker";
    private SQLiteDatabase db = null;
    private MySQLiteHelper mHelper;
    private Context mContext;
    private String mDBname;

    public DbAutoWorker(Context context, String DBname, int DBversion) {
        Log.i(TAG, "DbAutoWorker" + "DBname" + DBname + "DBversion" + DBversion);
        mHelper = new MySQLiteHelper(context, DBname, null, DBversion);
        this.mDBname = DBname;
        this.mContext = context;
    }

    /**
     * 如果开发者没有指定String DBname,int DBversion，则设置为默认defaulDB",1
     */
    public DbAutoWorker(Context context) {
        this(context, "defaulDB", 1);
    }

    /**
     * 通过类查询数据库所有数据
     *
     * @param clazz
     * @param <T>
     * @return
     */
    public <T> List<T> findAll(Class<T> clazz) {
        db = mHelper.getReadableDatabase();
        Cursor cursor = db.query(clazz.getSimpleName(), null, null, null, null, null, null);
        List result = getEntity(cursor, clazz);
        return result;
    }

    /**
     * 根据类名查询所对应表所有的内容
     *
     * @param cursor
     * @param clazz  要查询的类/表
     * @param <T>
     * @return
     */
    private <T> List<T> getEntity(Cursor cursor, Class<T> clazz) {
        List<T> list = new ArrayList<>();
        try {
            while (cursor.moveToNext()) {
                Log.i(TAG, "cursor.moveToNext()");
                Field[] fields = clazz.getDeclaredFields();
                T modeClass = clazz.newInstance();
                for (Field field : fields) {

                    Log.i(TAG, "getEntity fields");
                    //过滤掉编译器自动生成的成员变量
                    if (field.isSynthetic() || field.getName().equals("serialVersionUID")) {
                        Log.i(TAG, "fd.isSynthetic()" + field.getName());
                        continue;
                    }

                    Class<?> cursorClass = cursor.getClass();
                    String columnMethodName = getColumnMethodName(field.getType());
                    Method cursorMethod = cursorClass.getMethod(columnMethodName, int.class);
                    Log.i(TAG, "cursorMethod :" + cursorMethod.getName());

                    Object value = cursorMethod.invoke(cursor, cursor.getColumnIndex(field.getName()));

                    if (field.getType() == boolean.class || field.getType() == Boolean.class) {
                        if ("0".equals(String.valueOf(value))) {
                            value = false;
                        } else if ("1".equals(String.valueOf(value))) {
                            value = true;
                        }
                    } else if (field.getType() == char.class || field.getType() == Character.class) {
                        value = ((String) value).charAt(0);
                    } else if (field.getType() == Date.class) {
                        long date = (Long) value;
                        if (date <= 0) {
                            value = null;
                        } else {
                            value = new Date(date);
                        }
                    }
                    String methodName = makeSetterMethodName(field);
                    Method method = clazz.getDeclaredMethod(methodName, field.getType());
                    Log.i(TAG, "method.invoke" + "modeClass" + modeClass.getClass().getSimpleName() + "value" + value);
                    method.invoke(modeClass, value);
                }
                list.add(modeClass);
            }

        } catch (Exception e) {
            Log.i(TAG, "getEntity Exception e" + e.toString());
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return list;
    }

    /**
     * 根据字段的类型获得SQL中取字段的方法名
     *
     * @param fieldType 字段类型
     * @return 方法名
     */

    private String getColumnMethodName(Class<?> fieldType) {
        String typeName;
        if (fieldType.isPrimitive()) {
            typeName = Utils.capitalize(fieldType.getName());
            Log.i(TAG, "fieldType.isPrimitive() typeName" + typeName);
        } else {
            typeName = fieldType.getSimpleName();
            Log.i(TAG, "typeName " + typeName);
        }
        String methodName = "get" + typeName;
        if ("getBoolean".equals(methodName)) {
            methodName = "getInt";
        } else if ("getChar".equals(methodName) || "getCharacter".equals(methodName)) {
            methodName = "getString";
        } else if ("getDate".equals(methodName)) {
            methodName = "getLong";
        } else if ("getInteger".equals(methodName)) {
            methodName = "getInt";
        }
        return methodName;
    }

    /**
     * 获得set函数的正确名字
     *
     * @param field 对应字段
     * @return
     */
    private String makeSetterMethodName(Field field) {
        String setterMethodName;
        String setterMethodPrefix = "set";
        if (isPrimitiveBooleanType(field) && field.getName().matches("^is[A-Z]{1}.*$")) {
            setterMethodName = setterMethodPrefix + field.getName().substring(2);
        } else if (field.getName().matches("^[a-z]{1}[A-Z]{1}.*")) {
            setterMethodName = setterMethodPrefix + field.getName();
        } else {
            setterMethodName = setterMethodPrefix + Utils.capitalize(field.getName());
        }
        Log.i(TAG, "setterMethodName" + setterMethodName);
        return setterMethodName;
    }

    private boolean isPrimitiveBooleanType(Field field) {
        Class<?> fieldType = field.getType();
        if ("boolean".equals(fieldType.getName())) {
            return true;
        }
        return false;
    }

    /**
     * 往数据库插入数据的方法
     *
     * @param obj
     * @return
     */
    public long insert(Object obj) {
        Log.i(TAG, "insert enter");
        //每次进入更新db对象
        db = mHelper.getWritableDatabase();
        //每次都去检查，没有表则建表
        mHelper.createTable(db, obj);
        //取得要插入对象的所有字段
        Class<?> modeClass = obj.getClass();
        Field[] fields = modeClass.getDeclaredFields();
        ContentValues values = new ContentValues();

        for (Field fd : fields) {
            //设置字段权限，可以获得
            fd.setAccessible(true);
            //取得字段名字
            String fieldName = fd.getName();
            //剔除主键id值得保存，由于框架默认设置id为主键自动增长
            if (fieldName.equalsIgnoreCase("id") || fieldName.equalsIgnoreCase("_id")) {
                continue;
            }
            //过滤掉编译器自动生成的成员变量
            if (fd.isSynthetic() || fd.getName().equals("serialVersionUID")) {
                Log.i(TAG, "fd.isSynthetic()" + fd.getName());
                continue;
            }
            //将对象的数据放到ContentValues 里面
            putValues(values, fd, obj);
        }
        String tableName = Utils.getTableName(modeClass);
        Long result = db.insert(tableName, null, values);
        Log.i(TAG, "result" + result);
        return result;
    }

    /**
     * 把字段放入ContentValues。
     *
     * @param values
     * @param fd
     * @param obj
     */
    private void putValues(ContentValues values, Field fd, Object obj) {
        Class<?> clazz = values.getClass();
        try {
            //获得参数类型。用于确定ContentValues的put方法为哪一个。因为put方法重载了很多次
            Object[] parameters = new Object[]{fd.getName(), fd.get(obj)};
            Class<?>[] parameterTypes = getParameterTypes(fd, fd.get(obj), parameters);
            //根据ContentValues的put方法传入的参数获得对应的方法。
            Method method = clazz.getDeclaredMethod("put", parameterTypes);
            //设置方法权限为可以调用
            method.setAccessible(true);
            //将字段放入values
            method.invoke(values, parameters);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * 得到反射方法中的参数类型
     *
     * @param field
     * @param fieldValue
     * @param parameters
     * @return
     */
    private Class<?>[] getParameterTypes(Field field, Object fieldValue, Object[] parameters) {
        Class<?>[] parameterTypes;
        if (isCharType(field)) {
            parameters[1] = String.valueOf(fieldValue);
            parameterTypes = new Class[]{String.class, String.class};
        } else {
            if (field.getType().isPrimitive()) {
                parameterTypes = new Class[]{String.class, getObjectType(field.getType())};
            } else if ("java.util.Date".equals(field.getType().getName())) {
                parameterTypes = new Class[]{String.class, Long.class};
            } else {
                parameterTypes = new Class[]{String.class, field.getType()};
            }
        }
        return parameterTypes;
    }

    /**
     * 字段是否是char类型的
     *
     * @param field
     * @return
     */
    private boolean isCharType(Field field) {
        String type = field.getType().getName();
        return type.equals("char") || type.endsWith("Character");
    }

    /**
     * 得到对象的类型
     *
     * @param primitiveType
     * @return
     */
    private Class<?> getObjectType(Class<?> primitiveType) {
        if (primitiveType != null) {
            if (primitiveType.isPrimitive()) {
                String basicTypeName = primitiveType.getName();
                if ("int".equals(basicTypeName)) {
                    return Integer.class;
                } else if ("short".equals(basicTypeName)) {
                    return Short.class;
                } else if ("long".equals(basicTypeName)) {
                    return Long.class;
                } else if ("float".equals(basicTypeName)) {
                    return Float.class;
                } else if ("double".equals(basicTypeName)) {
                    return Double.class;
                } else if ("boolean".equals(basicTypeName)) {
                    return Boolean.class;
                } else if ("char".equals(basicTypeName)) {
                    return Character.class;
                }
            }
        }
        return null;
    }


    /**
     * 删除数据库一条方法
     *
     * @param clazz 要删除的表名/对象名字
     * @param id    要删除的对象的id
     * @return 删除的条目
     */
    public int deleteById(Class<?> clazz, long id) {
        db = mHelper.getWritableDatabase();
        int deleteItem = db.delete(Utils.getTableName(clazz), " id=" + id, null);
        return deleteItem;
    }

    /**
     * 更新数据的接口
     *
     * @param clazz  表名
     * @param values 更新的数据
     * @param id     要更新的对象的id
     * return 修改的条数
     */
    public int updateById(Class<?> clazz, ContentValues values, long id) {
        db = mHelper.getWritableDatabase();
        int result = db.update(clazz.getSimpleName(), values, "id=" + id, null);
        return result;
    }

    /**
     * 释放资源的接口
     */
    public void release(){
        db.close();
    }


}
