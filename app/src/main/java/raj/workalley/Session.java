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
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

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
        UserInfo,
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

        private UserInfo user;

        public SessionData() {
            reset();
        }

        public UserInfo getUser() {
            return user;
        }

        public void setUser(UserInfo user) {
            this.user = user;
        }

        private void reset() {
            user = null;
        }
    }

    public void setUser(UserInfo userInfo) {
        mSessionData.setUser(userInfo);
    }

    public UserInfo getUser() {
        return mSessionData.getUser();
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
            case UserInfo:
                classType = new TypeToken<UserInfo>() {
                }.getType();
                fromJson = (UserInfo) gson.fromJson(jsonObject.toString(), classType);
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

    public void postFetch(final String url, HashMap params, final Task task, final int method) {

        if (Constants.DEBUG) {
            Log.d("PayU", "SdkSession.postFetch: " + url + " " + params + " " + method);
        }

        JsonObjectRequest myRequest = new JsonObjectRequest
                (Request.Method.POST, getAbsoluteUrl(url), new JSONObject(params), new com.android.volley.Response
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
                headers.put("Content-Type", "application/json");
                return headers;
            }

        };
        myRequest.setShouldCache(false);
        myRequest.setRetryPolicy(new DefaultRetryPolicy(30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)

        );

        addToRequestQueue(myRequest);

        start = System.currentTimeMillis();

    }

    public void postFetch2(final String url, final Map<String, String> params, final Task task, final int method) {

        if (Constants.DEBUG) {
            Log.d(TAG, "SdkSession.postFetch: " + url + " " + params + " " + method);
        }
        StringRequest myRequest = new StringRequest(method, getAbsoluteUrl(url), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.d(TAG, "SdkSession.postFetch: " + "success");
                try {

                    JSONObject object = new JSONObject(response);
                    runSuccessOnHandlerThread(task, object);

                } catch (JSONException e) {
                    onFailure(e.getMessage(), e);
                }
            }

            public void onFailure(String msg, Throwable e) {
                runErrorOnHandlerThread(task, e);
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "SdkSession.postFetch: " + "error");
                runErrorOnHandlerThread(task, error);
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                return headers;
            }
        };
        myRequest.setShouldCache(false);
        myRequest.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        addToRequestQueue(myRequest);

    }


    public void getFetch(final String url, String params, final Task task, final int method) {

        if (Constants.DEBUG) {
            Log.d("PayU", "SdkSession.postFetch: " + url + " " + params + " " + method);
        }

        JsonObjectRequest myRequest = new JsonObjectRequest
                (method, getAbsoluteUrl(url), params, new com.android.volley.Response
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
        myRequest.setRetryPolicy(new DefaultRetryPolicy(30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)

        );

        addToRequestQueue(myRequest);

        start = System.currentTimeMillis();

    }


    public void signUpApi(String email, String name, String password, final boolean isHost) {
        HashMap<String,String> params = new HashMap<>();
            params.put(Constants.EMAIL, email);
            params.put(Constants.PASSWORD, password);
            params.put(Constants.NAME, name);

            if (isHost)
                params.put(Constants.ROLE, "PROVIDER");

        //final Map params = new HashMap<>();

        postFetch("auth/signup", params, new Task() {

            @Override
            public void onSuccess(JSONObject jsonObject) {
                try {
                    jsonObject.put("isProvider", isHost);
                    eventBus.post(new CobbocEvent(CobbocEvent.SIGNUP, true, jsonObject));
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

    public void login(String userName, String password) {
        HashMap<String, String> params = new HashMap<>();
        params.put(Constants.EMAIL, userName);
        params.put(Constants.PASSWORD, password);

        //final Map params = new HashMap<>();

        postFetch("auth/login/local", params, new Task() {

            @Override
            public void onSuccess(JSONObject jsonObject) {
                eventBus.post(new CobbocEvent(CobbocEvent.LOGIN, true, jsonObject));
            }

            @Override
            public void onSuccess(String response) {

            }

            @Override
            public void onError(Throwable throwable) {
                eventBus.post(new CobbocEvent(CobbocEvent.LOGIN, false, "An error occurred while trying to login. Please try again later."));
            }

            @Override
            public void onProgress(int percent) {

            }
        }, Request.Method.POST);

    }

    public void getUserWorkspaceData(String userId) {

        String getRequestUrl = "requests?user=" + userId;

        getFetch(getRequestUrl, null, new Task() {

            @Override
            public void onSuccess(JSONObject jsonObject) {
                eventBus.post(new CobbocEvent(CobbocEvent.GET_USER_DETAILS, true, jsonObject));
            }

            @Override
            public void onSuccess(String response) {

            }

            @Override
            public void onError(Throwable throwable) {
                eventBus.post(new CobbocEvent(CobbocEvent.GET_USER_DETAILS, false, "An error occurred while trying to login. Please try again later."));
            }

            @Override
            public void onProgress(int percent) {

            }
        }, Request.Method.GET);
    }

    public void getHostWorkspaceData(String userId) {
        String getRequestUrl = "spaces?owner=" + userId;

        getFetch(getRequestUrl, null, new Task() {

            @Override
            public void onSuccess(JSONObject jsonObject) {
                eventBus.post(new CobbocEvent(CobbocEvent.GET_HOST_DETAILS, true, jsonObject));
            }

            @Override
            public void onSuccess(String response) {

            }

            @Override
            public void onError(Throwable throwable) {
                eventBus.post(new CobbocEvent(CobbocEvent.GET_HOST_DETAILS, false, "An error occurred while trying to login. Please try again later."));
            }

            @Override
            public void onProgress(int percent) {

            }
        }, Request.Method.GET);
    }


}
