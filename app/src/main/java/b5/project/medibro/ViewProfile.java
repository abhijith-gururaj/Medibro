package b5.project.medibro;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import b5.project.medibro.fragments.MyProfileFragment;

public class ViewProfile extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String userId = getIntent().getStringExtra("userId");

        FragmentManager fm = getSupportFragmentManager();
        Bundle args = new Bundle();
        Fragment mFragment = new MyProfileFragment();
        String mTag = "Profile";
        args.putBoolean("parentDashboard", false);
        args.putString("userId", userId);
        mFragment.setArguments(args);
        fm.beginTransaction().replace(R.id.container, mFragment, mTag).commit();
    }

}
