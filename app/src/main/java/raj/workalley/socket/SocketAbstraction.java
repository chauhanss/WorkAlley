package raj.workalley.socket;

import android.text.TextUtils;

import java.net.URISyntaxException;

import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

/**
 * Created by vishal.raj on 9/15/16.
 */
public class SocketAbstraction {

    public Socket startSocket() {
        Socket mSocket = null;
        {
            try {
                mSocket = IO.socket("http://chat.socket.io");
            } catch (URISyntaxException e) {
            }
        }
        return mSocket;
    }

    private void attemptSend(String message, Socket mSocket) {

        if (TextUtils.isEmpty(message)) {
            return;
        }
        mSocket.emit("new message", message);
    }
}
