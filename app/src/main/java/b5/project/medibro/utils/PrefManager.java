package b5.project.medibro.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Abhijith on 3/30/2016.
 */
public class PrefManager {
    // Shared Preferences
    SharedPreferences prefs;

    // Editor for Shared preferences
    SharedPreferences.Editor editor;

    // Context
    Context _context;

    // Shared prefs mode
    int PRIVATE_MODE = 0;

    // Shared prefs file name
    private static final String PREF_NAME = "AndroidHive";

    // All Shared Preferences Keys
    private static final String IS_LOGIN = "IsLoggedIn";

    // Email address
    private static final String KEY_EMAIL = "email";

    // Constructor
    public PrefManager(Context context) {
        this._context = context;
        prefs = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = prefs.edit();
    }

    public void putLatestNotifId(int notifId) {
        editor.putInt("latestNotifId", notifId);
        editor.apply();
    }

    public int getLatestNotifId() {
        return prefs.getInt("notifId", 0);
    }

    public void clearPrefs() {
        editor.clear();
        editor.apply();
    }
}
