package com.soulter.floatgame;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;

/**
 * @author Soulter
 * @author's qq: 905617992
 *
 */
public class ShaPreUtils {
    public static String SHARED_FILE_NAME="spfs";

    public static void putToSpfs(Context context, String key, Object value){
        SharedPreferences.Editor editor = context.getSharedPreferences(SHARED_FILE_NAME,Context.MODE_PRIVATE).edit();
        if (value instanceof Integer)
            editor.putInt(key, (Integer) value);

        editor.apply();
    }
    public static Object getFromSpfs(Context context, String key, Object defVal){
        SharedPreferences spfs = context.getSharedPreferences(SHARED_FILE_NAME,Context.MODE_PRIVATE);
        if (defVal instanceof Integer)
            return spfs.getInt(key, (Integer) defVal);
        return null;
    }
}
