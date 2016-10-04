package raj.workalley;

/**
 * Created by sagar on 29/03/16.
 */
public class CobbocEvent {
    public static final int LOGIN = 1;
    public static final int SIGNUP = 2;
    public static final int GET_USER_DETAILS = 3;
    public static final int GET_HOST_DETAILS = 4;
    public static final int CREATE_WORKSPACE = 5;
    public static final int GET_ALL_WORKSPACES = 6;
    public static final int REQUEST_SEAT = 7;
    public static final int ACCEPT_REJECT_BOOKING_REQUEST = 8;
    public static final int GET_ALL_ACTIVE_USERS = 9;
    public static final int CANCEL_BOOKING_REQUEST = 10;
    public static final int ACTIVE_SESSIONS = 11;
    public static final int END_SESSION = 12;
    public static final int END_SESSION_CONFIRMED = 13;
    public static final int LOGOUT = 14;
    public static final int LAST_STATUS = 15;
    public static final int UPLOAD_IMAGE = 16;
    public static final int GET_WORKSPACE_FROM_ID = 17;

    private boolean STATUS;

    private int TYPE;

    private Object VALUE;

    public CobbocEvent(int type) {
        this(type, true, null);
    }

    public CobbocEvent(int type, boolean status) {
        this(type, status, null);
    }

    public CobbocEvent(int type, boolean status, Object value) {
        TYPE = type;
        STATUS = status;
        VALUE = value;
    }

    public CobbocEvent(int type, boolean status, int value) {
        TYPE = type;
        STATUS = status;
        VALUE = Integer.valueOf(value);
    }

    public boolean getStatus() {
        return STATUS;
    }


    public int getType() {
        return TYPE;
    }

    public Object getValue() {
        return VALUE;
    }
}
