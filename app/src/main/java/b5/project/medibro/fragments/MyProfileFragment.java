package b5.project.medibro.fragments;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.parse.ParseFile;
import com.parse.ParseUser;

import b5.project.medibro.Dashboard;
import b5.project.medibro.R;
import de.hdodenhof.circleimageview.CircleImageView;


public class MyProfileFragment extends Fragment {
    ParseUser user;
    CircleImageView mProfileImage;
    TextView mJobRole, mWorkEx, mEducation, mBio, mDiscPart;
    String jobRole, workEx, education, bio;
    int discpart;

    public MyProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);
        user = ParseUser.getCurrentUser();
        ((Dashboard) getActivity()).getSupportActionBar().setTitle(user.getUsername());

        mProfileImage = (CircleImageView) rootView.findViewById(R.id.profile_image);

        try {
            ParseFile parseFile = user.getParseFile("profileThumb");
            byte[] data = parseFile.getData();
            Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
            mProfileImage.setImageBitmap(bitmap);
        } catch (Exception e) {
            e.printStackTrace();
        }

        mJobRole = (TextView) rootView.findViewById(R.id.job_role);
        mWorkEx = (TextView) rootView.findViewById(R.id.profile_workex);
        mEducation = (TextView) rootView.findViewById(R.id.profile_education);
        mBio = (TextView) rootView.findViewById(R.id.my_profile_bio);
        mDiscPart = (TextView) rootView.findViewById(R.id.my_profile_discPart);

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

        return rootView;
    }

}
