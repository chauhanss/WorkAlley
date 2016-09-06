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
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.apache.http.conn.ConnectTimeoutException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.net.SocketTimeoutException;
import java.util.Map;

/**
 * Created by vishal.raj on 9/5/16.
 */
public class Session {

    private static Session INSTANCE = null;
    private RequestQueue mRequestQueue;
    public static final String TAG = Session.class.getSimpleName();
    private final Context mContext;
    Long start = null, end = null, diff = null;
    private final Handler handler;
    private final SessionData mSessionData = new SessionData();

    public enum workAlleyModels {
        CCUserProgressStage,
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

        private boolean isPinAunthenticationCompleted = false;
        private int pinLength = 0;
        private String guId = null;
        private String userName, token, customerId = null;
        private String cardIdentifier = null;
        private String lastBillAmount, lastBillMinAmount = null;
        private int userProgressStage = -1;
        private JSONArray regexArray;
        private int appPinUpdateStatus = -1;
        private String appSessionId = null;
        private String mobile = null;

        public SessionData() {
            reset();
        }

        private void setRegexArray(JSONArray regexArr) {
            this.regexArray = regexArr;
        }

        private JSONArray getRegexArray() {
            return regexArray;
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

        private String getGuID() {
            return guId;
        }

        private boolean isPinAunthenticationCompleted() {
            return isPinAunthenticationCompleted;
        }

        private void setIsPinAunthenticationCompleted(boolean isCompleted) {
            this.isPinAunthenticationCompleted = isCompleted;
        }

        private void setGuId(String id) {
            this.guId = id;
        }

        private String getToken() {
            return token;
        }

        private int getAppPinUpdateStatus() {
            return appPinUpdateStatus;
        }

        private void setAppPinUpdateStatus(int appPinUpdateStatus) {
            this.appPinUpdateStatus = appPinUpdateStatus;
        }

        private String getLastBillAmount() {
            return lastBillAmount;
        }

        private void setLastBillAmount(String lastBillAmount) {
            this.lastBillAmount = lastBillAmount;
        }


        private String getLastBillMinAmount() {
            return lastBillMinAmount;
        }

        private void setLastBillMinAmount(String lastBillMinAmount) {
            this.lastBillMinAmount = lastBillMinAmount;
        }

        private void setToken(String token) {
            this.token = token;
        }

        private void reset() {
            token = null;
            customerId = null;
            cardIdentifier = null;
        }

        private void logOutReset() {
            token = null;
            guId = null;
        }

        private void setPinLength(int pinLength) {
            this.pinLength = pinLength;
        }

        private int getPinLength() {
            return pinLength;
        }

        private String getCardIdentifier() {
            return cardIdentifier;
        }

        private void setCardIdentifier(String cardIdentifier) {
            this.cardIdentifier = cardIdentifier;
        }

        private int getUserProgressStage() {
            return userProgressStage;
        }

        private void setUserProgressStage(int userProgressStage) {
            this.userProgressStage = userProgressStage;
        }

        private String getAppSessionId() {
            return appSessionId;
        }

        private void setAppSessionId(String appSessionId) {
            this.appSessionId = appSessionId;
        }
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

    public void setAppSessionId(String id) {
        mSessionData.setAppSessionId(id);
    }

    private void runSuccessOnHandlerThread(final Task task, final JSONObject jsonObject) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                task.onSuccess(jsonObject);
            }
        });
    }

    public void postFetch(final String url, final Map<String, String> params, final Task task, final int method) {

        if (Constants.DEBUG) {
            Log.d(Session.TAG, "SdkSession.postFetch: " + url + " " + params + " " + method);
        }

        StringRequest myRequest = new StringRequest(method, getAbsoluteUrl(url), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                diff = System.currentTimeMillis() - start;

                //Log.i(CCSession.TAG, "URL=" + url + "Time=" + diff);

                if (Constants.DEBUG) {
                    Log.d(Session.TAG, "SdkSession.postFetch.onSuccess: " + url + " " + params + " " + method + ": " + response);
                }

                try {
                    JSONObject object = new JSONObject(response);
                    if (object.has(Constants.APP_SESSION_ID) && object.isNull(Constants.APP_SESSION_ID)) {
                        String sessionId = object.getString(Constants.APP_SESSION_ID);
                        setAppSessionId(sessionId);

                    }
                    if (object.has("error")) {
                        onFailure(object.getString("error"), new Throwable(object.getString("error")));
                    } else {
                        runSuccessOnHandlerThread(task, object);
                    }
                } catch (JSONException e) {
                    // maybe this is a string?
                    onFailure(e.getMessage(), e);
                }
            }

            public void onFailure(String msg, Throwable e) {
                if (Constants.DEBUG) {
                    Log.e(Session.TAG, "Session...new JsonHttpResponseHandler() {...}.onFailure: " + e.getMessage() + " " + msg);
                }
                if (msg.contains("401")) {
                    /*not required in app*/

                    // logout("force");
                    cancelPendingRequests(TAG);

                }
                runErrorOnHandlerThread(task, e);
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (Constants.DEBUG) {
                    Log.e(Session.TAG, "Session...new JsonHttpResponseHandler() {...}.onFailure: " + error.getMessage());
                }
                if (error != null && error.networkResponse != null && error.networkResponse.statusCode == 401) {

                    //  logout("force");

                }
                runErrorOnHandlerThread(task, error);
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {


                return params;
            }

            @Override
            public String getBodyContentType() {
                return "application/x-www-form-urlencoded";
            }
        };
        myRequest.setShouldCache(false);
        myRequest.setRetryPolicy(new DefaultRetryPolicy(100000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        addToRequestQueue(myRequest);
        start = System.currentTimeMillis();

    }

}
