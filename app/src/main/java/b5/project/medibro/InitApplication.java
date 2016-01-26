package b5.project.medibro;

import android.app.Application;
import android.util.Log;

import com.facebook.FacebookSdk;
import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseTwitterUtils;
import com.parse.ParseUser;

import b5.project.medibro.utils.ParseFacebookUtils;

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


        Parse.initialize(this);
        ParseUser.enableAutomaticUser();
        ParseFacebookUtils.initialize(getApplicationContext());
        FacebookSdk.sdkInitialize(getApplicationContext());
        ParseTwitterUtils.initialize("vFG0O9nIpp2CBKLk98p94g29x", "LE9LGHgY6V6jIB9e9yIUCNI8TkPBNiBWmiwagiMO5UF0dssg1H");
//        ParseACL defaultACL = new ParseACL();
//        // Optionally enable public read access.
//        // defaultACL.setPublicReadAccess(true);
//        ParseACL.setDefaultACL(defaultACL, true);
    }
}
