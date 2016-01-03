package b5.project.medibro;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseUser;

/**
 * Created by Messi10 on 04-Jan-16.
 */
public class InitApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        // Enable Local Datastore.
        Parse.enableLocalDatastore(this);

        // Add your initialization code here
        Parse.initialize(this);

        ParseUser.enableAutomaticUser();

//        ParseACL defaultACL = new ParseACL();
//        // Optionally enable public read access.
//        // defaultACL.setPublicReadAccess(true);
//        ParseACL.setDefaultACL(defaultACL, true);
    }
}
