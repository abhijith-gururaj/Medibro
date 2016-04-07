package b5.project.medibro;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import b5.project.medibro.utils.UtilManager;

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

                if (mEmail.isEmpty() || mUserName.isEmpty() || mPassword.isEmpty()) {
                    Toast.makeText(SignUp.this, "Please fill the required fields.",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                UtilManager manager = new UtilManager(SignUp.this);
                if (!manager.isValidEmailAddress(mEmail)) {
                    Toast.makeText(SignUp.this, "Invalid format of email.Please check",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!manager.isValidName(mUserName)) {
                    Toast.makeText(SignUp.this, "User Name should not contain special characters",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                if (mPassword.length() < 5) {
                    Toast.makeText(SignUp.this, "Password length should be greater that 5",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                ProgressDialog progressDialog = new ProgressDialog(SignUp.this);
                progressDialog.setIndeterminate(true);
                progressDialog.setCancelable(false);
                progressDialog.setTitle("Contacting Servers");
                progressDialog.setMessage("Please wait...");

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
                            ParseInstallation.getCurrentInstallation().put("user", ParseUser.getCurrentUser());
                            ParseInstallation.getCurrentInstallation().saveEventually();
                            startActivity(new Intent(SignUp.this, Dashboard.class));
                            finish();
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
