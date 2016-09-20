package raj.workalley;

import java.util.regex.Pattern;

/**
 * Created by vishal.raj on 9/5/16.
 */
public class Constants {

    public static final boolean DEBUG = false;
    public static final String BASE_URL_IMAGE = "";
    public static final String BASE_URL = "http://api.workalley.in/";
    public static final String APP_SESSION_ID = "appSessionId";
    public static final String ACCESS_TOKEN = "token";
    public static final String DEVICE_ID = "deviceId";
    public static final String DEVICE_TYPE = "deviceType";
    public static final String ANDROID = "android";
    public static final String STATUS = "status";
    public static final String EMAIL = "email";
    public static final String NAME = "name";
    public static final String PASSWORD = "password";
    public static final String POPUP_DIALOG_OK_BUTTON_TAG = "popup_dialog_ok_button_tag";

    public static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,4}$", Pattern.CASE_INSENSITIVE);
    public static final String ROLE = "role";
    public static final String USER = "USER";
    public static final String PROVIDER = "PROVIDER";
    public static final String WORKSPACE_NAME = "name";
    public static final String LINE1 = "line1";
    public static final String LOCALITY = "locality";
    public static final String STATE = "state";
    public static final String CITY = "city";
    public static final String PINCODE = "pincode";
    public static final String LOCATION = "loc";
    public static final String ADDRESS = "address";
    public static final String OWNER = "owner";

    public static final String WORKSPACE_ID = "workspace_id";
    public static final String DATA = "data";
    public static final String USER_ID = "user";

    public static final String HOST_ID = "host";
    public static final String SPACE = "space";
    public static final String SESSION_COOKIES_ID = "sessionCookiesId";
    public static final String SP_NAME = "workAlleySharedPref";

    /**
     * Socket Requests
     */
    public static final String REQUEST_TYPE = "requestType";

    public static final String BOOKING_REQUEST = "BOOKING_REQUESTED";
    public static final String BOOKING_REJECT= "BOOKING_REJECTED";
    public static final String BOOKING_ACCEPT = "BOOKING_ACCEPTED";
    public static final String REQUEST_ID = "request_id";
}
