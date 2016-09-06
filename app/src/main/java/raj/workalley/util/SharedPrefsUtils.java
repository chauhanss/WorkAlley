package raj.workalley.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;

import java.util.Map;

/**
 * Created by vishal.raj on 9/5/16.
 */
public class SharedPrefsUtils {

    private SharedPrefsUtils() {
    }

    /**
     * @param context Calling activity context
     * @param key     Saved/Saving value key
     * @param file    The name of the shared preference file - ServerLogSharedPreference, UserSessionSharedPreference
     * @return
     */

    public static String getStringPreference(Context context, String key, String file) {
        String value = null;
        SharedPreferences preferences = context.getSharedPreferences(file, Context.MODE_PRIVATE);
        if (preferences != null) {
            value = preferences.getString(key, null);
        }
        return value;
    }

    public static boolean getBooleanPreference(Context context, String key, String file) {
        boolean value = false;
        SharedPreferences preferences = context.getSharedPreferences(file, Context.MODE_PRIVATE);
        if (preferences != null) {
            value = preferences.getBoolean(key, false);
        }
        return value;
    }

    public static float getFloatPreference(Context context, String key, String file) {
        float value = 0;
        SharedPreferences preferences = context.getSharedPreferences(file, Context.MODE_PRIVATE);
        if (preferences != null) {
            value = preferences.getFloat(key, 0);
        }
        return value;
    }

    public static long getLongPreference(Context context, String key, String file) {
        long value = 0;
        SharedPreferences preferences = context.getSharedPreferences(file, Context.MODE_PRIVATE);
        if (preferences != null) {
            value = preferences.getLong(key, 0);
        }
        return value;
    }

    public static int getIntPreference(Context context, String key, String file) {
        int value = 0;
        SharedPreferences preferences = context.getSharedPreferences(file, Context.MODE_PRIVATE);
        if (preferences != null) {
            value = preferences.getInt(key, 0);
        }
        return value;
    }

    public static boolean setStringPreference(Context context, String key, String value, String file) {
        SharedPreferences preferences = context.getSharedPreferences(file, Context.MODE_PRIVATE);
        if (preferences != null && !TextUtils.isEmpty(key)) {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString(key, value);
            return editor.commit();
        }
        return false;
    }

    public static boolean setBooleanPreference(Context context, String key, boolean value, String file) {
        SharedPreferences preferences = context.getSharedPreferences(file, Context.MODE_PRIVATE);
        if (preferences != null && !TextUtils.isEmpty(key)) {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean(key, value);
            return editor.commit();
        }
        return false;
    }

    public static boolean setFloatPreference(Context context, String key, float value, String file) {
        SharedPreferences preferences = context.getSharedPreferences(file, Context.MODE_PRIVATE);
        if (preferences != null && !TextUtils.isEmpty(key)) {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putFloat(key, value);
            return editor.commit();
        }
        return false;
    }

    public static boolean setIntPreference(Context context, String key, int value, String file) {
        SharedPreferences preferences = context.getSharedPreferences(file, Context.MODE_PRIVATE);
        if (preferences != null && !TextUtils.isEmpty(key)) {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putInt(key, value);
            return editor.commit();
        }
        return false;
    }

    public static boolean setLongPreference(Context context, String key, long value, String file) {
        SharedPreferences preferences = context.getSharedPreferences(file, Context.MODE_PRIVATE);
        if (preferences != null && !TextUtils.isEmpty(key)) {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putLong(key, value);
            return editor.commit();
        }
        return false;
    }

    public static boolean removePreferenceByKey(Context context, String key, String file) {
        SharedPreferences preferences = context.getSharedPreferences(file, Context.MODE_PRIVATE);
        if (preferences != null && !TextUtils.isEmpty(key)) {
            SharedPreferences.Editor editor = preferences.edit();
            editor.remove(key);
            editor.apply();
            return editor.commit();
        }
        return false;
    }

    public static boolean hasKey(Context context, String key, String file) {
        SharedPreferences preferences = context.getSharedPreferences(file, Context.MODE_PRIVATE);
        if (preferences != null && !TextUtils.isEmpty(key)) {
            return preferences.contains(key);
        }
        return false;
    }
}
