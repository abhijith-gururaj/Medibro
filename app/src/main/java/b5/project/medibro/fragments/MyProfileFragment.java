package b5.project.medibro.fragments;


import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

import b5.project.medibro.Chat;
import b5.project.medibro.Dashboard;
import b5.project.medibro.R;
import b5.project.medibro.ViewProfile;
import de.hdodenhof.circleimageview.CircleImageView;


public class MyProfileFragment extends Fragment {
    ParseUser user;
    CircleImageView mProfileImage;
    TextView mJobRole, mWorkEx, mEducation, mBio, mDiscPart;
    String userId, jobRole, workEx, education, bio;
    int discpart;
    Boolean parentDashboard;
    String TAG = MyProfileFragment.class.getSimpleName();

    public MyProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);

        mJobRole = (TextView) rootView.findViewById(R.id.job_role);
        mWorkEx = (TextView) rootView.findViewById(R.id.profile_workex);
        mEducation = (TextView) rootView.findViewById(R.id.profile_education);
        mBio = (TextView) rootView.findViewById(R.id.my_profile_bio);
        mDiscPart = (TextView) rootView.findViewById(R.id.my_profile_discPart);
        mProfileImage = (CircleImageView) rootView.findViewById(R.id.profile_image);

        Bundle args = getArguments();
        parentDashboard = args.getBoolean("parentDashboard");
        userId = args.getString("userId");
        Log.d(TAG, "userID: " + userId);

        FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.fab_msg);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!userId.equals(ParseUser.getCurrentUser().getObjectId())) {
                    Intent intent = new Intent(getActivity(), Chat.class);
                    intent.putExtra("toUserId", userId);
                    Log.d(TAG, "toUserId: " + userId);
                    getActivity().startActivity(intent);
                }
            }
        });

        if (ParseUser.getCurrentUser().getObjectId().equals(userId)) {
            user = ParseUser.getCurrentUser();
            if (parentDashboard) {
                fab.setVisibility(View.INVISIBLE);
                ((Dashboard) getActivity()).getSupportActionBar().setTitle(user.getUsername());
            } else {
                fab.setVisibility(View.VISIBLE);
                ((ViewProfile) getActivity()).getSupportActionBar().setTitle(user.getUsername());
            }
            setupProfile();
        } else {
            final ProgressDialog dialog = new ProgressDialog(getActivity());
            dialog.setIndeterminate(true);
            dialog.setCancelable(false);
            dialog.show();

            ParseQuery<ParseUser> query = ParseQuery.getQuery("_User");
            query.whereEqualTo("objectId", userId);
            query.findInBackground(new FindCallback<ParseUser>() {
                @Override
                public void done(List<ParseUser> objects, ParseException e) {
                    user = objects.get(0);
                    Log.d(TAG, "Found user: " + user.getUsername());
                    if (parentDashboard)
                        ((Dashboard) getActivity()).getSupportActionBar().setTitle(user.getUsername());
                    else
                        ((ViewProfile) getActivity()).getSupportActionBar().setTitle(user.getUsername());
                    dialog.dismiss();
                    setupProfile();
                }
            });
        }

        return rootView;
    }

    public void setupProfile() {
        education = (String) user.get("education");
        if (education != null)
            mEducation.setText(education);

        workEx = (String) user.get("workEx");
        if (workEx != null)
            mWorkEx.setText(workEx);

        jobRole = (String) user.get("jobRole");
        if (jobRole != null)
            mJobRole.setText(jobRole);

        bio = (String) user.get("bio");
        if (bio != null)
            mBio.setText(bio);

        discpart = user.getInt("discPart");
        if (discpart != 0)
            mDiscPart.setText(String.valueOf(discpart));
        else
            mDiscPart.setText("0");

        try {
            ParseFile parseFile = user.getParseFile("profileThumb");
            byte[] data = parseFile.getData();
            Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
            mProfileImage.setImageBitmap(bitmap);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
