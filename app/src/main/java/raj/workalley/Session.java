package raj.workalley;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.apache.http.conn.ConnectTimeoutException;
import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.Map;

import raj.workalley.user.fresh.UserInfo;

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
    private String sessionIdCookies;

    public enum workAlleyModels {
        UserInfo,
        Workspaces,
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
        private WorkspaceList workspaceList;
        private String sessionIdCookies;

        private String getSessionIdCookies() {
            return sessionIdCookies;
        }

        private void setSessionIdCookies(String sessionIdCookies) {
            this.sessionIdCookies = sessionIdCookies;
        }

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

        public WorkspaceList getWorkspaceList() {
            return workspaceList;
        }

        public void setWorkspaceList(WorkspaceList workspaceList) {
            this.workspaceList = workspaceList;
        }
    }


    public String getSessionIdCookies() {
        return mSessionData.getSessionIdCookies();
    }

    public void setSessionIdCookies(String sessionIdCookies) {
        mSessionData.setSessionIdCookies(sessionIdCookies);
    }

    public void setUser(UserInfo userInfo) {
        mSessionData.setUser(userInfo);
    }

    public UserInfo getUser() {
        return mSessionData.getUser();
    }

    public void setWorkspaces(WorkspaceList workpsace) {
        mSessionData.setWorkspaceList(workpsace);
    }

    public WorkspaceList getWorkspaces() {
        return mSessionData.getWorkspaceList();
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
            CookieHandler.setDefault(new CookieManager(null, CookiePolicy.ACCEPT_ALL));
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
            case Workspaces:
                classType = new TypeToken<WorkspaceList>() {
                }.getType();
                fromJson = (WorkspaceList) gson.fromJson(jsonObject.toString(), classType);
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
                headers.put("Content-Type", "application/json");
                //String sessionId = Hawk.get("connect.sid", "");
                return headers;
            }

            @Override
            protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                try {
                    String jsonString = new String(response.data,
                            HttpHeaderParser.parseCharset(response.headers, PROTOCOL_CHARSET));
                    JSONObject jsonResponse = new JSONObject(jsonString);
                    String sessionId = response.headers.get("set-cookie");
                    setSessionIdCookies(sessionId);
                    jsonResponse.put("headers", new JSONObject(response.headers));
                    return Response.success(jsonResponse,
                            HttpHeaderParser.parseCacheHeaders(response));
                } catch (UnsupportedEncodingException e) {
                    return Response.error(new ParseError(e));
                } catch (JSONException je) {
                    return Response.error(new ParseError(je));
                }
            }

        };
        myRequest.setShouldCache(false);
        myRequest.setRetryPolicy(new DefaultRetryPolicy(60000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)

        );

        addToRequestQueue(myRequest);

        start = System.currentTimeMillis();

    }

    public void putFetch(final String url, String params, final Task task, final int method) {

        JsonObjectRequest myRequest = new JsonObjectRequest
                (Request.Method.PUT, getAbsoluteUrl(url), params, new com.android.volley.Response
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
                //String sessionId = Hawk.get("connect.sid", "");
                return headers;
            }

            @Override
            protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                try {
                    String jsonString = new String(response.data,
                            HttpHeaderParser.parseCharset(response.headers, PROTOCOL_CHARSET));
                    JSONObject jsonResponse = new JSONObject(jsonString);
                    String sessionId = response.headers.get("set-cookie");
                    setSessionIdCookies(sessionId);
                    jsonResponse.put("headers", new JSONObject(response.headers));
                    return Response.success(jsonResponse,
                            HttpHeaderParser.parseCacheHeaders(response));
                } catch (UnsupportedEncodingException e) {
                    return Response.error(new ParseError(e));
                } catch (JSONException je) {
                    return Response.error(new ParseError(je));
                }
            }

        };
        myRequest.setShouldCache(false);
        myRequest.setRetryPolicy(new DefaultRetryPolicy(60000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)

        );

        addToRequestQueue(myRequest);
    }


    public void getFetch(final String url, String params, final Task task, final int method) {

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
        JSONObject params = new JSONObject();
        try {
            params.put(Constants.EMAIL, email);
            params.put(Constants.PASSWORD, password);
            params.put(Constants.NAME, name);
            if (isHost)
                params.put(Constants.ROLE, "PROVIDER");
        } catch (JSONException e) {
            e.printStackTrace();
        }


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
        JSONObject params = new JSONObject();
        try {
            params.put(Constants.EMAIL, userName);
            params.put(Constants.PASSWORD, password);
        } catch (JSONException e) {
            e.printStackTrace();
        }


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

    public void logout() {
        reset();
        getFetch("auth/logout", null, new Task() {

            @Override
            public void onSuccess(JSONObject jsonObject) {
                eventBus.post(new CobbocEvent(CobbocEvent.LOGOUT, true, jsonObject));
            }

            @Override
            public void onSuccess(String response) {

            }

            @Override
            public void onError(Throwable throwable) {
                eventBus.post(new CobbocEvent(CobbocEvent.LOGOUT, false, "An error occurred while trying to login. Please try again later."));
            }

            @Override
            public void onProgress(int percent) {

            }
        }, Request.Method.GET);
    }

    public void createWorkSpaceApi(String workspaceName, String[] address, double[] loc) {
        JSONObject params = new JSONObject();
        try {
            params.put(Constants.WORKSPACE_NAME, workspaceName);

            JSONObject jsonAddress = new JSONObject();
            jsonAddress.put(Constants.LINE1, address[0]);
            jsonAddress.put(Constants.LOCALITY, address[1]);
            jsonAddress.put(Constants.STATE, address[2]);
            jsonAddress.put(Constants.CITY, address[3]);
            jsonAddress.put(Constants.PINCODE, address[4]);
            jsonAddress.put(Constants.LOCATION, new JSONArray(loc));

            params.put(Constants.ADDRESS, jsonAddress);

            params.put(Constants.OWNER, /*new Gson().toJson(getUser())*/getUser().get_id());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        postFetch("spaces", params, new Task() {

            @Override
            public void onSuccess(JSONObject jsonObject) {
                eventBus.post(new CobbocEvent(CobbocEvent.CREATE_WORKSPACE, true, jsonObject));
            }

            @Override
            public void onSuccess(String response) {

            }

            @Override
            public void onError(Throwable throwable) {
                eventBus.post(new CobbocEvent(CobbocEvent.CREATE_WORKSPACE, false, "An error occurred while creating workspace. Please try again later."));
            }

            @Override
            public void onProgress(int percent) {

            }
        }, Request.Method.POST);


    }

    public void requestSeat(String userId, String hostId) {
        JSONObject params = new JSONObject();
        try {
            params.put(Constants.USER_ID, userId);
            params.put(Constants.SPACE, hostId);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        postFetch("requests", params, new Task() {

            @Override
            public void onSuccess(JSONObject jsonObject) {
                eventBus.post(new CobbocEvent(CobbocEvent.REQUEST_SEAT, true, jsonObject));
            }

            @Override
            public void onSuccess(String response) {

            }

            @Override
            public void onError(Throwable throwable) {
                eventBus.post(new CobbocEvent(CobbocEvent.REQUEST_SEAT, false, "An error occurred while creating workspace. Please try again later."));
            }

            @Override
            public void onProgress(int percent) {

            }
        }, Request.Method.GET);


    }

    public void getUserWorkspaceData(String userId) {

        String getRequestUrl = "users/" + userId + "/spaces";

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

    public void getAllActiveWorkspace() {
        String getRequestUrl = "spaces";

        getFetch(getRequestUrl, null, new Task() {

            @Override
            public void onSuccess(JSONObject jsonObject) {
                eventBus.post(new CobbocEvent(CobbocEvent.GET_ALL_WORKSPACES, true, jsonObject));
            }

            @Override
            public void onSuccess(String response) {

            }

            @Override
            public void onError(Throwable throwable) {
                eventBus.post(new CobbocEvent(CobbocEvent.GET_ALL_WORKSPACES, false, "An error occurred while trying to login. Please try again later."));
            }

            @Override
            public void onProgress(int percent) {

            }
        }, Request.Method.GET);
    }

    public void saveUserDetails() {
    }

    public void deleteWorkspace(String id) {
        JSONObject params = new JSONObject();
        try {
            params.put(Constants.WORKSPACE_ID, id);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        postFetch("spaces", params, new Task() {

            @Override
            public void onSuccess(JSONObject jsonObject) {
                eventBus.post(new CobbocEvent(CobbocEvent.REQUEST_SEAT, true, jsonObject));
            }

            @Override
            public void onSuccess(String response) {

            }

            @Override
            public void onError(Throwable throwable) {
                eventBus.post(new CobbocEvent(CobbocEvent.REQUEST_SEAT, false, "An error occurred while creating workspace. Please try again later."));
            }

            @Override
            public void onProgress(int percent) {

            }
        }, Request.Method.DELETE);
    }


    public void saveHostDetails() {
    }

    public void acceptRejectWorkspaceBookSeatRequest(final UserInfo user, final boolean isReject, String requestId) {

        String getRequestUrl = "";
        if (isReject) {
            getRequestUrl = "requests/" + requestId + "/reject";
        } else {
            getRequestUrl = "requests/" + requestId + "/accept";
        }

        putFetch(getRequestUrl, null, new Task() {

            @Override
            public void onSuccess(JSONObject jsonObject) {
                try {
                    jsonObject.put("isRejectRequest", isReject);
                    jsonObject.put("user", user);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                eventBus.post(new CobbocEvent(CobbocEvent.ACCEPT_REJECT_BOOKING_REQUEST, true, jsonObject));
            }

            @Override
            public void onSuccess(String response) {

            }

            @Override
            public void onError(Throwable throwable) {
                eventBus.post(new CobbocEvent(CobbocEvent.ACCEPT_REJECT_BOOKING_REQUEST, false, "An error occurred while trying to login. Please try again later."));
            }

            @Override
            public void onProgress(int percent) {

            }
        }, Request.Method.PUT);
    }

}
