package b5.project.medibro;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseFile;
import com.parse.ParseUser;

import b5.project.medibro.fragments.Home;

public class Dashboard extends AppCompatActivity {

    TextView userName, userEmail;
    String user_name, user_email;
    ImageView mProfileImage;
    NavigationView mNavigationView;
    DrawerLayout mDrawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mNavigationView = (NavigationView) findViewById(R.id.nvView);
        View v = mNavigationView.inflateHeaderView(R.layout.nav_header);


        mProfileImage = (ImageView) v.findViewById(R.id.circle_profileImage);
        userName = (TextView) v.findViewById(R.id.userName);
        userEmail = (TextView) v.findViewById(R.id.userEmail);

        ParseUser user = ParseUser.getCurrentUser();
        userName.setText(user.getUsername());
        userEmail.setText(user.getEmail());
        try {
            ParseFile parseFile = user.getParseFile("profileThumb");
            byte[] data = parseFile.getData();
            Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
            mProfileImage.setImageBitmap(bitmap);

        } catch (Exception e) {
            e.printStackTrace();
        }

        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            FragmentManager fm = getSupportFragmentManager();
            Fragment mFragment = null;
            Bundle args = new Bundle();
            String mTag = null;

            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                if (menuItem.isChecked()) menuItem.setChecked(false);
                else menuItem.setChecked(true);

                //Closing drawer on item click
                mDrawerLayout.closeDrawers();

                if (!getSupportActionBar().isShowing())
                    getSupportActionBar().show();

                //Check to see which item was being clicked and perform appropriate action
                Toast.makeText(Dashboard.this, "Item Selected", Toast.LENGTH_SHORT).show();
                switch (menuItem.getItemId()) {
                    case R.id.drawer_home:
                        mFragment = new Home();
                        args.putString("type", "Home");
                        mTag = "Home";
                        fm.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                        fm.beginTransaction().replace(R.id.container, mFragment, mTag).commit();
                        break;

                }

                if (mFragment != null) {
                    if (!mTag.equals("Home")) {
                        fm.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                        fm.beginTransaction().replace(R.id.container, mFragment, mTag)
                                .addToBackStack(mTag)
                                .commit();
                    }
                }
                return true;
            }
        });

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.openDrawer, R.string.closeDrawer) {

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };

        //Setting the actionbarToggle to drawer layout
        mDrawerLayout.setDrawerListener(actionBarDrawerToggle);

        //calling sync state is necessary or else your hamburger icon wont show up
        actionBarDrawerToggle.syncState();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_dashboard, menu);
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

}
