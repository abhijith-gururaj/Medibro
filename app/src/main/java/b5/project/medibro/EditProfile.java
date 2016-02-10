package b5.project.medibro;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;

import b5.project.medibro.utils.ImagePicker;
import butterknife.Bind;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfile extends AppCompatActivity {

    ParseUser user;
    @Bind(R.id.btn_edit_profile_image)
    Button editProfile;
    @Bind(R.id.profile_image)
    CircleImageView profileImage;
    //@Bind(R.id.edit_pwd_card)CardView pwdCard;
    @Bind(R.id.edit_userName)
    EditText mEditUserName;
    @Bind(R.id.edit_education)
    EditText mEditEducation;
    @Bind(R.id.edit_bio)
    EditText mEditBio;
    @Bind(R.id.edit_workEx)
    EditText mEditWorkEx;
    //@Bind(R.id.edit_currentPwd) EditText mCurrentPwd;
    //@Bind(R.id.edit_newPwd) EditText mEditNewPwd;
    //@Bind(R.id.edit_confirmNewPwd) EditText mConfirmNewPwd;
    @Bind(R.id.edit_job_role)
    EditText mEditJobRole;
    String userName, education, jobRole, workEx, bio, userPwd, newPwd, confNewPwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ButterKnife.bind(this);
        user = ParseUser.getCurrentUser();

        try {
            ParseFile parseFile = user.getParseFile("profileThumb");
            byte[] data = parseFile.getData();
            Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
            profileImage.setImageBitmap(bitmap);
        } catch (Exception e) {
            e.printStackTrace();
        }

        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPickImage(v);
            }
        });

        userName = user.getUsername();
        mEditUserName.setText(userName);

        education = (String) user.get("education");
        if (education != null)
            mEditEducation.setText(education);

        workEx = (String) user.get("workEx");
        if (workEx != null)
            mEditWorkEx.setText(workEx);

        jobRole = (String) user.get("jobRole");
        if (jobRole != null)
            mEditJobRole.setText(jobRole);

        bio = (String) user.get("bio");
        if (bio != null)
            mEditBio.setText(bio);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveDetails();
            }
        });
    }

    private void saveDetails() {

        userName = mEditUserName.getText().toString();
        if (userName.isEmpty()) {
            Toast.makeText(this, "Username should not be empty", Toast.LENGTH_SHORT).show();
            return;
        }
        education = mEditEducation.getText().toString();
        if (!education.isEmpty()) {
            user.put("education", education);
        } else {
            user.put("education", "None");
        }

        workEx = mEditWorkEx.getText().toString();
        if (!workEx.isEmpty()) {
            user.put("workEx", workEx);
        } else {
            user.put("workEx", "None");
        }

        jobRole = mEditJobRole.getText().toString();
        if (!jobRole.isEmpty()) {
            user.put("jobRole", jobRole);
        } else {
            user.put("jobRole", "None");
        }

        bio = mEditBio.getText().toString();
        if (!bio.isEmpty()) {
            user.put("bio", bio);
        } else {
            user.put("bio", "None");
        }

        user.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                Toast.makeText(EditProfile.this, "Details Updated Successfully", Toast.LENGTH_LONG).show();
            }
        });
    }

    private static final int PICK_IMAGE_ID = 12345;

    public void onPickImage(View view) {
        Intent chooseImageIntent = ImagePicker.getPickImageIntent(this);
        startActivityForResult(chooseImageIntent, PICK_IMAGE_ID);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case PICK_IMAGE_ID:
                Bitmap bitmap = ImagePicker.getImageFromResult(this, resultCode, data);

                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                if (bitmap != null) {
                    profileImage.setImageBitmap(bitmap);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 70, stream);
                    byte[] imageData = stream.toByteArray();

                    String thumbName = user.getUsername().replaceAll("\\s+", "");
                    final ParseFile parseFile = new ParseFile(thumbName + "_thumb.jpg", imageData);
                    parseFile.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            user.put("profileThumb", parseFile);
                            user.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    Toast.makeText(EditProfile.this, "Updated Profile image", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    });
                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }

    }
}
