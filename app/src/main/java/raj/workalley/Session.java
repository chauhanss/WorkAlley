package raj.workalley;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.apache.http.conn.ConnectTimeoutException;
import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by vishal.raj on 9/5/16.
 */
public class Session {

    private static Session INSTANCE = null;
    private RequestQueue mRequestQueue;
    private final EventBus eventBus;
    public static final String TAG = Session.class.getSimpleName();
    private final Context mContext;
    Long start = null, end = null, diff = null;
    private final Handler handler;
    private final SessionData mSessionData = new SessionData();

    public enum workAlleyModels {
        CCUserInfo,
        CCUserPreferences,
        CCCSRFToken,
        CCCardDetails,
        CCTransactionHistoryDetails,
        CCLastBillSummary,
        CCUserLogin,
        CCDashboardData,
        CCNetBanking,
        CCTransactionHistoryList
    }


    private Session(Context context) {
        eventBus = EventBus.getDefault();
        mContext = context;
        handler = new Handler(Looper.getMainLooper());
    }

    public static synchronized Session getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = new Session(context);
        }
        return INSTANCE;
    }

    private class SessionData {

        private String userName, token, customerId = null;
        private String mobile = null;

        public SessionData() {
            reset();
        }

        private String getUserName() {
            return userName;
        }

        private void setUserName(String name) {
            this.userName = name;
        }

        private String getMobile() {
            return mobile;
        }

        private void setMobile(String mobileNumber) {
            this.mobile = mobileNumber;
        }

        private String getCustomerId() {
            return customerId;
        }

        private void setCustomerId(String customerId) {
            this.customerId = customerId;
        }

        private String getToken() {
            return token;
        }

        private void setToken(String tokenValue) {
            this.token = tokenValue;
        }


        private void reset() {
            token = null;
            customerId = null;
        }
    }

    public String getToken() {
        return mSessionData.getToken();
    }

    public void setUserId(String user) {
        mSessionData.setUserName(user);
    }

    public String getUserId() {
        return mSessionData.getUserName();
    }


    /**
     * Get the cached login state
     */
    public boolean isLoggedIn() {
        return getToken() != null;
    }

    public void setToken(String token) {
        mSessionData.setToken(token);
    }


    public void reset() {
        mSessionData.reset();
    }

    public interface Task {
        void onSuccess(JSONObject object);

        void onSuccess(String response);

        void onError(Throwable throwable);

        void onProgress(int percent);
    }

    /*Volley Section Start*/
    public RequestQueue getRequestQueue(Context context) {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(context);
        }
        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TextUtils.isEmpty(Session.TAG) ? TAG : Session.TAG);
        getRequestQueue(mContext).add(req);
    }

    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }
    /*VOlley Section End*/

    private void runErrorOnHandlerThread(final Task task, final Throwable e) {
        if (e instanceof ConnectTimeoutException || e instanceof SocketTimeoutException) {
            final Throwable x = new Throwable("time out error");
            handler.post(new Runnable() {
                @Override
                public void run() {
                    task.onError(x);
                }
            });
        } else {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    task.onError(e);
                }
            });
        }
    }

    public Object getParsedResponseFromGSON(JSONObject jsonObject, workAlleyModels type) {

        Type classType;
        Gson gson = new Gson();
        Object fromJson = null;
        switch (type) {
            case CCUserLogin:
                break;
        }
        return fromJson;
    }

    private static String getAbsoluteUrl(String relativeUrl) {
        if (relativeUrl.equals("/payuPaisa/up.php"))
            return Constants.BASE_URL_IMAGE + relativeUrl;
        else
            return Constants.BASE_URL + relativeUrl;
    }

    private void runSuccessOnHandlerThread(final Task task, final JSONObject jsonObject) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                task.onSuccess(jsonObject);
            }
        });
    }

    public void postFetch(final String url, JSONObject params, final Task task, final int method) {

        if (Constants.DEBUG) {
            Log.d("PayU", "SdkSession.postFetch: " + url + " " + params + " " + method);
        }

        JsonObjectRequest myRequest = new JsonObjectRequest
                (Request.Method.POST, getAbsoluteUrl(url), params, new com.android.volley.Response
                        .Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e(TAG, response + "");
                        runSuccessOnHandlerThread(task, response);
                    }
                }, new com.android.volley.Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, error.getMessage() + "");
                        runErrorOnHandlerThread(task, error);
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                return headers;
            }
        };
        myRequest.setShouldCache(false);
        myRequest.setRetryPolicy(new

                DefaultRetryPolicy(
                30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)

        );

        addToRequestQueue(myRequest);

        start = System.currentTimeMillis();

    }


    public void signUpUserApi(String email, String name, String password) {
        JSONObject params = new JSONObject();
        try {
            params.put(Constants.EMAIL, email);
            params.put(Constants.PASSWORD, password);
            params.put(Constants.NAME, name);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //final Map params = new HashMap<>();

        postFetch("auth/signup", params, new Task() {

            @Override
            public void onSuccess(JSONObject jsonObject) {
                try {
                    int status = jsonObject.getInt(Constants.STATUS);
                    if (status < 0) {
                        eventBus.post(new CobbocEvent(CobbocEvent.SIGNUP, false, jsonObject));
                    } else {
                        eventBus.post(new CobbocEvent(CobbocEvent.SIGNUP, true, jsonObject));
                    }
                } catch (JSONException e) {
                    eventBus.post(new CobbocEvent(CobbocEvent.SIGNUP, false));
                }
            }

            @Override
            public void onSuccess(String response) {

            }

            @Override
            public void onError(Throwable throwable) {
                eventBus.post(new CobbocEvent(CobbocEvent.SIGNUP, false, "An error occurred while trying to login. Please try again later."));
            }

            @Override
            public void onProgress(int percent) {

            }
        }, Request.Method.POST);
    }


}
