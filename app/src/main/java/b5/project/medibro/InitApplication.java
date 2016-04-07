package b5.project.medibro;

import android.app.Application;
import android.util.Log;

import com.facebook.FacebookSdk;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseTwitterUtils;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import b5.project.medibro.pojos.FeedComment;
import b5.project.medibro.pojos.FeedItem;
import b5.project.medibro.pojos.Message;

/**
 * Created by Messi10 on 04-Jan-16.
 */
public class InitApplication extends Application {

    final int mFacebookCallbackCode = 12321;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("Application", "Init parse");
        // Enable Local Datastore.
        Parse.enableLocalDatastore(this);

        // Add your initialization code here

        ParseObject.registerSubclass(FeedItem.class);
        ParseObject.registerSubclass(FeedComment.class);
        ParseObject.registerSubclass(Message.class);
        Parse.initialize(this);
        ParseUser.enableAutomaticUser();
        ParseInstallation.getCurrentInstallation().saveInBackground();
        ParseFacebookUtils.initialize(getApplicationContext());
        FacebookSdk.sdkInitialize(getApplicationContext());
        ParseTwitterUtils.initialize("vFG0O9nIpp2CBKLk98p94g29x", "LE9LGHgY6V6jIB9e9yIUCNI8TkPBNiBWmiwagiMO5UF0dssg1H");
        ParsePush.subscribeInBackground("", new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Log.d("com.parse.push", "successfully subscribed to the broadcast channel.");
                } else {
                    Log.e("comparse.push", "failed to subscribe for push", e);
                }
            }
        });

        ParseInstallation.getCurrentInstallation().saveInBackground();
//        ParseACL defaultACL = new ParseACL();
//        // Optionally enable public read access.
//        // defaultACL.setPublicReadAccess(true);
//        ParseACL.setDefaultACL(defaultACL, true);
    }
}
