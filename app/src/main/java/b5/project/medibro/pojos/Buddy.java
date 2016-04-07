package b5.project.medibro.pojos;

/**
 * Created by Abhijith on 3/30/2016.
 */
public class Buddy {
    String userId;
    byte[] imageData;
    String userName;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public byte[] getImageData() {
        return imageData;
    }

    public void setImageData(byte[] imageData) {
        this.imageData = imageData;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
