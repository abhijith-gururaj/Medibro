package b5.project.medibro.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.parse.ParseUser;

import b5.project.medibro.Dashboard;
import b5.project.medibro.R;


public class ProfileFragment extends Fragment {
    ParseUser user;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);
        user = ParseUser.getCurrentUser();
        ((Dashboard) getActivity()).getSupportActionBar().setTitle(user.getUsername());

        return rootView;
    }

}
