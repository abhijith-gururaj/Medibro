package b5.project.medibro;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseFile;
import com.parse.ParseInstallation;
import com.parse.ParseTwitterUtils;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.twitter.Twitter;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import b5.project.medibro.utils.UtilManager;

public class Welcome extends AppCompatActivity {

    Button mSignUp;
    Button mLogin;
    ImageButton mFbLogin, mTwitterLogin;
    EditText editTextUserName, editTextPwd;
    ParseUser parseUser;
    ImageView mProfileImage;

    String name = null, email = null;
    public static final List<String> mPermissions = new ArrayList<String>() {{
        add("public_profile");
        add("email");
    }};
    private final String TAG = "Welcome";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        Log.d(TAG, "Init app");
        parseUser = ParseUser.getCurrentUser();
        if (parseUser.getUsername() != null) {
            startActivity(new Intent(this, Dashboard.class));
            finish();
        }

        mSignUp = (Button) findViewById(R.id.sign_up);
        mSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Welcome.this, SignUp.class);
                startActivity(i);
            }
        });

        mProfileImage = new ImageView(this);
        editTextUserName = (EditText) findViewById(R.id.user_name);
        editTextPwd = (EditText) findViewById(R.id.user_password);
        mLogin = (Button) findViewById(R.id.button_login);
        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String mUserName = editTextUserName.getText().toString();
                String mPassword = editTextPwd.getText().toString();

                if (mUserName.isEmpty() || mPassword.isEmpty()) {
                    Toast.makeText(Welcome.this, "Fields are empty. Please enter the credentials.",
                            Toast.LENGTH_LONG).show();
                    return;
                }

                UtilManager manager = new UtilManager(Welcome.this);
                if (!manager.isConnectingToInternet()) {
                    Toast.makeText(Welcome.this, "Cannot connect to Server. Please check your connection",
                            Toast.LENGTH_LONG).show();
                    return;
                }
                final ProgressDialog pDialog = new ProgressDialog(Welcome.this);
                pDialog.setTitle("Contacting Servers");
                pDialog.setMessage("Logging in ...");
                pDialog.setIndeterminate(false);
                pDialog.setCancelable(false);
                pDialog.show();

                ParseUser.logInInBackground(mUserName, mPassword, new LogInCallback() {
                    @Override
                    public void done(ParseUser user, ParseException e) {
                        if (user != null) {
                            pDialog.dismiss();
                            Toast.makeText(Welcome.this, "Successfully Logged In", Toast.LENGTH_LONG).show();
                            startActivity(new Intent(Welcome.this, Dashboard.class));
                            finish();
                        } else {
                            Toast.makeText(Welcome.this, "cannot login", Toast.LENGTH_LONG).show();
                            Log.d(TAG, e.getMessage());
                        }
                    }
                });
            }
        });

        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "b5.project.medibro",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException | NoSuchAlgorithmException e) {
            Log.d(TAG, "Error generating fb hash: " + e.getMessage());
        }

        mFbLogin = (ImageButton) findViewById(R.id.button_fbLogin);
        mFbLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseFacebookUtils.logInWithReadPermissionsInBackground(Welcome.this,
                        mPermissions,
                        new LogInCallback() {
                            @Override
                            public void done(ParseUser user, ParseException e) {
                                if (user == null) {
                                    Log.d(TAG, "Uh oh. The user cancelled the Facebook login. " + e.getMessage());
                                } else if (user.isNew()) {
                                    Log.d(TAG, "User signed up and logged in through Facebook!");
                                    getUserDetailsFromFB();
                                    ParseInstallation.getCurrentInstallation().put("user", ParseUser.getCurrentUser());
                                    ParseInstallation.getCurrentInstallation().saveEventually();
                                } else {
                                    Log.d(TAG, "User logged in through Facebook!");
                                    ParseInstallation.getCurrentInstallation().put("user", ParseUser.getCurrentUser());
                                    ParseInstallation.getCurrentInstallation().saveEventually();
                                    startActivity(new Intent(Welcome.this, Dashboard.class));
                                    finish();
                                }
                            }
                        });
            }
        });

        mTwitterLogin = (ImageButton) findViewById(R.id.button_twitterLogin);
        mTwitterLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseTwitterUtils.logIn(Welcome.this, new LogInCallback() {
                    @Override
                    public void done(ParseUser user, ParseException err) {
                        if (user == null) {
                            Log.d("MyApp", "Uh oh. The user cancelled the Twitter login.");
                        } else if (user.isNew()) {
                            Log.d("MyApp", "User signed up and logged in through Twitter!");
                            new TwitterInfoAsync().execute();
                            ParseInstallation.getCurrentInstallation().put("user", ParseUser.getCurrentUser());
                            ParseInstallation.getCurrentInstallation().saveEventually();
                        } else {
                            Log.d("MyApp", "User logged in through Twitter!");
                            ParseInstallation.getCurrentInstallation().put("user", ParseUser.getCurrentUser());
                            ParseInstallation.getCurrentInstallation().saveEventually();
                            startActivity(new Intent(Welcome.this, Dashboard.class));
                            finish();
                        }
                    }
                });
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_welcome, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void saveNewUser() {
        parseUser = ParseUser.getCurrentUser();
        parseUser.setUsername(name);
        parseUser.setEmail(email);

        Log.d(TAG, "name: " + parseUser.getUsername() + " email: " + parseUser.getEmail() + " objectId: " + parseUser.getObjectId());
        //        Saving profile photo as a ParseFile
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        Bitmap bitmap = ((BitmapDrawable) mProfileImage.getDrawable()).getBitmap();
        if (bitmap != null) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 70, stream);
            byte[] data = stream.toByteArray();

            String thumbName = parseUser.getUsername().replaceAll("\\s+", "");
            final ParseFile parseFile = new ParseFile(thumbName + "_thumb.jpg", data);
            parseFile.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {
                        Log.d(TAG, "Saved image");
                        parseUser.put("profileThumb", parseFile);
                        //Finally save all the user details
                        parseUser.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e == null) {
                                    Log.d(TAG, "Saved User");
                                    Toast.makeText(Welcome.this, "New user:" + name + " Signed up", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(Welcome.this, Dashboard.class));
                                    finish();
                                } else {
                                    Log.d(TAG, "saveNewuser: " + e.getMessage());
                                    e.printStackTrace();
                                }
                            }
                        });
                    } else {
                        Log.d(TAG, e.getMessage());
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    private void getUserDetailsFromFB() {

        // Suggested by https://disqus.com/by/dominiquecanlas/
        Bundle parameters = new Bundle();
        parameters.putString("fields", "email,name,picture");


        new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                "/me",
                parameters,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {
                        /* handle the result */
                        try {
                            Log.d("Response", response.getRawResponse());

                            email = response.getJSONObject().getString("email");
                            //mEmailID.setText(email);
                            name = response.getJSONObject().getString("name");
                            //mUsername.setText(name);

                            JSONObject picture = response.getJSONObject().getJSONObject("picture");
                            JSONObject data = picture.getJSONObject("data");
                            //  Returns a 50x50 profile picture
                            String pictureUrl = data.getString("url");
                            Log.d("Profile pic", "url: " + pictureUrl);
                            new ProfilePhotoAsync(pictureUrl).execute();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
        ).executeAsync();
    }


    private String getUserDetailsFromTwitter() {

        Uri.Builder b = Uri.parse("https://api.twitter.com/1.1/account/verify_credentials.json").buildUpon();
        b.appendQueryParameter("skip_status", "true");
        b.appendQueryParameter("include_email", "true");
        b.appendQueryParameter("include_entities", "false");
        String profileUrl = b.build().toString();
        Log.d(TAG, "twitter URL: " + profileUrl);
        Twitter twitter = ParseTwitterUtils.getTwitter();
        HttpGet httpGet = new HttpGet(profileUrl);

        twitter.signRequest(httpGet);

        HttpClient client = new DefaultHttpClient();


        try {
            HttpResponse response = client.execute(httpGet);
            BufferedReader input = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

            String result = input.readLine();
            Log.d(TAG, "getTwitterInfo: result=" + result);

            JSONObject JsonResponse = new JSONObject(result);

            String profileImageUrl = JsonResponse.getString("profile_image_url");
            name = JsonResponse.getString("name");
            email = JsonResponse.getString("email");

            return profileImageUrl;
        } catch (IOException | JSONException e) {
            e.printStackTrace();
            return null;
        } finally {

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ParseFacebookUtils.onActivityResult(requestCode, resultCode, data);
    }

    class TwitterInfoAsync extends AsyncTask<Void, Void, Void> {
        String url;
        Bitmap bitmap;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            url = getUserDetailsFromTwitter();
            if (url != null)
                bitmap = DownloadImageBitmap(url);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            mProfileImage.setImageBitmap(bitmap);
            saveNewUser();
        }
    }

    class ProfilePhotoAsync extends AsyncTask<String, String, String> {
        public Bitmap bitmap;
        String url;

        public ProfilePhotoAsync(String url) {
            this.url = url;
        }

        @Override
        protected String doInBackground(String... params) {
            // Fetching data from URI and storing in bitmap
            bitmap = DownloadImageBitmap(url);
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            mProfileImage.setImageBitmap(bitmap);
            saveNewUser();
        }
    }

    public static Bitmap DownloadImageBitmap(String url) {
        Bitmap bm = null;
        try {
            URL aURL = new URL(url);
            URLConnection conn = aURL.openConnection();
            conn.connect();
            InputStream is = conn.getInputStream();
            BufferedInputStream bis = new BufferedInputStream(is);
            bm = BitmapFactory.decodeStream(bis);
            bis.close();
            is.close();
        } catch (IOException e) {
            Log.e("IMAGE", "Error getting bitmap", e);
        }
        return bm;
    }
}
