package DBframe;

import android.text.TextUtils;
import android.util.Log;

import java.util.Locale;

/**
 * 工具类，提供需要的方法
 * Created by luozhenlong on 2017/12/9.
 */

public class Utils {

    private static final String TAG = "Utils";

    /**
     * 根据类名获得表名
     * @param clazz
     * @return
     */
    public static String getTableName(Class<?> clazz){
        String tableName = clazz.getSimpleName();
        Log.i(TAG,"tableName"+tableName);
        return tableName;
    }

    /**
     * 根据字串的类型去决定存入数据库字段的类型
     * @param type
     * @return
     */
    public static String getColumnType(String type) {
        String value = null;
        if (type.contains("String")) {
            value = " text ";
        } else if (type.contains("int")) {
            value = " integer ";
        } else if (type.contains("boolean")) {
            value = " boolean ";
        } else if (type.contains("float")) {
            value = " float ";
        } else if (type.contains("double")) {
            value = " double ";
        } else if (type.contains("char")) {
            value = " varchar ";
        } else if (type.contains("long")) {
            value = " long ";
        }
        return value;
    }

    /**
     *
     * @param string
     * @return
     */
    public static String capitalize(String string) {
        if (!TextUtils.isEmpty(string)) {
            return string.substring(0, 1).toUpperCase(Locale.US) + string.substring(1);
        }
        return string == null ? null : "";
    }


}
