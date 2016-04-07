package b5.project.medibro.pojos;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

/**
 * Created by Abhijith on 3/27/2016.
 */
@ParseClassName("Message")
public class Message extends ParseObject {

    /**
     * The Constant STATUS_SENDING.
     */
    public static final int STATUS_SENDING = 0;

    /**
     * The Constant STATUS_SENT.
     */
    public static final int STATUS_SENT = 1;

    /**
     * The Constant STATUS_FAILED.
     */
    public static final int STATUS_FAILED = 2;
    /**
     * The status.
     */
    private int status = STATUS_SENT;


    public static final String FROM_USER_ID = "fromUserId";
    public static final String TO_USER_ID_KEY = "toUserId";
    public static final String BODY_KEY = "body";


    public String getFromUserId() {
        return getString(FROM_USER_ID);
    }

    public String getBody() {
        return getString(BODY_KEY);
    }

    public void setFromUser(String userId) {
        put(FROM_USER_ID, userId);
    }

    public void setBody(String body) {
        put(BODY_KEY, body);
    }

    public String getToUserId() {
        return getString(TO_USER_ID_KEY);
    }

    public void setToUser(String toUserId) {
        put(TO_USER_ID_KEY, toUserId);
    }

    /**
     * Gets the status.
     *
     * @return the status
     */
    public int getStatus() {
        return status;
    }

    /**
     * Sets the status.
     *
     * @param status the new status
     */
    public void setStatus(int status) {
        this.status = status;
    }

    public boolean isSent() {
        return getFromUserId().equals(ParseUser.getCurrentUser().getObjectId());
    }
}
