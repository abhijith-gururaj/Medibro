package b5.project.medibro;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

public class SignUp extends AppCompatActivity {

    EditText editTextName, editTextUserName, editTextPassword, editTextEmail;
    String mName, mUserName, mPassword, mEmail;
    private final String TAG = "SignUp";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Log.d(TAG, "Init SignUp");

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        editTextName = (EditText) findViewById(R.id.name);
        editTextUserName = (EditText) findViewById(R.id.user_name);
        editTextPassword = (EditText) findViewById(R.id.user_password);
        editTextEmail = (EditText) findViewById(R.id.user_email);

        Button mSubmitBtn = (Button) findViewById(R.id.submit);

        mSubmitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Clicked");
                mName = editTextName.getText().toString();
                mUserName = editTextUserName.getText().toString();
                mEmail = editTextEmail.getText().toString();
                mPassword = editTextPassword.getText().toString();

                ParseUser mUser = new ParseUser();
                mUser.setUsername(mUserName);
                mUser.setPassword(mPassword);
                mUser.setEmail(mEmail);
                mUser.put("Name", mName);
                mUser.signUpInBackground(new SignUpCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            Toast.makeText(SignUp.this, "Successfully Registered", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(SignUp.this, "Something's wrong : " + e.getMessage(), Toast.LENGTH_LONG).show();
                            Log.d(TAG, e.getMessage());
                        }
                    }
                });
            }
        });
        Log.d(TAG, "End SignUp");
    }

}
