package raj.workalley.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import raj.workalley.Constants;
import raj.workalley.user.fresh.UserInfo;

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

    public static Set getHashSetPreference(Context context, String key, String file) {
        Set<String> value = null;
        SharedPreferences preferences = context.getSharedPreferences(file, Context.MODE_PRIVATE);
        if (preferences != null) {
            value = preferences.getStringSet(key, null);
        }
        return value;
    }

    public static String getHashSetTokenValueForUser(Context context, String key, String userId, String file) {
        SharedPreferences preferences = context.getSharedPreferences(file, Context.MODE_PRIVATE);

        String endToken = "";
        Set<String> set = preferences.getStringSet(key, null);
        if (set != null && preferences != null && !TextUtils.isEmpty(key)) {

            for (String stringUser : set) {

                String user = stringUser.substring(0, stringUser.lastIndexOf("|"));
                if (user.equalsIgnoreCase(userId)) {
                    return stringUser.substring(stringUser.lastIndexOf("|") + 1, stringUser.length());
                }
            }
        }
        return endToken;
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
            editor.apply();
            return editor.commit();
        }
        return false;
    }

    public static boolean setHashSetPreference(Context context, String key, Set values, String file) {
        SharedPreferences preferences = context.getSharedPreferences(file, Context.MODE_PRIVATE);
        Set<String> set = preferences.getStringSet(key, null);
        if (preferences != null && !TextUtils.isEmpty(key)) {
            SharedPreferences.Editor editor = preferences.edit();
            if (set != null)
                values.addAll(set);
            editor.putStringSet(key, values);
            return editor.commit();
        }
        return false;
    }

    public static boolean removeSetInHashSetPreference(Context context, String key, String user, String file) {
        SharedPreferences preferences = context.getSharedPreferences(file, Context.MODE_PRIVATE);

        try {
            JSONObject deleteUser = new JSONObject(user);
            Set<String> set = preferences.getStringSet(key, null);
            if (preferences != null && !TextUtils.isEmpty(key)) {

                for (String stringUser : set) {

                    JSONObject users = new JSONObject(stringUser);
                    if (users.has("_id") && deleteUser.has("_id")) {
                        if (users.getString("_id").equalsIgnoreCase(deleteUser.getString("_id"))) {
                            set.remove(stringUser);
                        }
                    }
                }
                SharedPreferences.Editor editor = preferences.edit();
                if (set != null)
                    set.addAll(set);
                editor.putStringSet(key, set);
                return editor.commit();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;

    }

    public static boolean hasUserInHashSetPreference(Context context, String key, String user, String file) {
        SharedPreferences preferences = context.getSharedPreferences(file, Context.MODE_PRIVATE);

        try {
            JSONObject searchUser = new JSONObject(user);
            Set<String> set = preferences.getStringSet(key, null);
            if (set != null && preferences != null && !TextUtils.isEmpty(key)) {

                for (String stringUser : set) {

                    JSONObject users = new JSONObject(stringUser);
                    if (users.has("_id") && searchUser.has("_id")) {
                        if (users.getString("_id").equalsIgnoreCase(searchUser.getString("_id"))) {
                            return true;
                        }
                    }
                }
                return false;
            } else
                return false;
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void clearSharedPreferenceFile(Context context) {

        SharedPreferences preferences = context.getSharedPreferences(Constants.SP_NAME, Context.MODE_PRIVATE);
        preferences.edit().clear().commit();

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
        if (preferences != null)
            if (!TextUtils.isEmpty(key)) {
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
