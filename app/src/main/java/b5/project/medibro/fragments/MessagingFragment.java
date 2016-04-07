package b5.project.medibro.fragments;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import b5.project.medibro.Chat;
import b5.project.medibro.R;
import b5.project.medibro.utils.DatabaseHandler;

public class MessagingFragment extends Fragment {
    ListView userList;
    HashMap<Integer, String> buddyList;
    ArrayList<ParseUser> buddies;
    ProgressBar progressBar;
    View v;
    public static final String TAG = MessagingFragment.class.getSimpleName();
    public MessagingFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_messaging, container, false);
        progressBar = (ProgressBar) v.findViewById(R.id.progress_bar);
        buddies = new ArrayList<>();
        progressBar.setVisibility(View.VISIBLE);

        final DatabaseHandler handler = new DatabaseHandler(getActivity());
        userList = (ListView) v.findViewById(R.id.chat_user_list);
        final ParseUser currUser = ParseUser.getCurrentUser();
        buddyList = handler.getBuddyList(currUser.getObjectId());
        Log.d(TAG, "local db list size:" + buddyList.size());
        if (buddyList.isEmpty()) {
//            userList.setVisibility(View.GONE);
//            progressBar.setVisibility(View.GONE);
//            TextView errTv = (TextView) v.findViewById(R.id.noItems);
//            errTv.setVisibility(View.VISIBLE);

            ParseQuery<ParseObject> query1 = ParseQuery.getQuery("UsersMessaged");
            query1.whereEqualTo("user1", currUser.getObjectId());

            ParseQuery<ParseObject> query2 = ParseQuery.getQuery("UsersMessaged");
            query2.whereEqualTo("user2", currUser.getObjectId());

            List<ParseQuery<ParseObject>> queries = new ArrayList<ParseQuery<ParseObject>>();
            queries.add(query1);
            queries.add(query2);
            ParseQuery<ParseObject> mainQuery = ParseQuery.or(queries);
            mainQuery.orderByDescending("createdAt");
            mainQuery.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> objects, ParseException e) {
                    if (e == null) {

                        int listSize = objects.size();
                        Log.d("msgFrag", "Success: " + listSize);
                        if (listSize > 0) {
                            buddyList = new HashMap<>(listSize);
                            for (int i = 0; i < listSize; i++) {
                                ParseObject object = objects.get(i);
                                if (currUser.getObjectId().equals(object.getString("user1")))
                                    buddyList.put(i, object.getString("user2"));
                                else if (currUser.getObjectId().equals(object.getString("user2")))
                                    buddyList.put(i, object.getString("user1"));

                                handler.addChatUsers(currUser.getObjectId(), buddyList.get(i));
                                setupList();
                            }
                        } else {
                            progressBar.setVisibility(View.GONE);
                            TextView errTv = (TextView) v.findViewById(R.id.noItems);
                            errTv.setVisibility(View.VISIBLE);
                        }
                    }
                }
            });
        } else {
            progressBar.setVisibility(View.GONE);
            setupList();
        }

        return v;
    }

    private void setupList() {
        MyChatUsersAdapter adapter = new MyChatUsersAdapter();
        userList.setAdapter(adapter);
        userList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), Chat.class);
                String toUserId = buddyList.get(position);
                Log.d("MsgFrag", "pos : " + position);
                intent.putExtra("toUserId", toUserId);
                intent.putExtra("toUserName", buddies.get(position).getUsername());
                Log.d("MsgFrag", toUserId);
                startActivity(intent);
            }
        });
    }

    private class MyChatUsersAdapter extends BaseAdapter {
        View layout;
        TextView mUserNameOther;
        ImageView ivOther;

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            layout = inflater.inflate(R.layout.chat_users_item, parent, false);

            String otherObjectId = getItem(position);
            mUserNameOther = (TextView) layout.findViewById(R.id.userNameOther);
            ivOther = (ImageView) layout.findViewById(R.id.profileOther);

            ParseQuery<ParseUser> query = ParseQuery.getQuery("_User");
            query.whereEqualTo("objectId", otherObjectId);
            query.findInBackground(new FindCallback<ParseUser>() {
                @Override
                public void done(List<ParseUser> objects, ParseException e) {
                    if (e == null) {
                        ParseUser buddy = objects.get(0);
                        buddies.add(buddy);
                        mUserNameOther.setText(buddy.getUsername());
                        try {
                            ParseFile parseFile = buddy.getParseFile("profileThumb");
                            byte[] data = parseFile.getData();
                            Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                            ivOther.setImageBitmap(bitmap);
                        } catch (Exception e1) {
                            e1.printStackTrace();
                        }
                    } else {
                        Toast.makeText(getContext(), "Cannot connect to servers.",
                                Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                }
            });

            return layout;
        }

        @Override
        public String getItem(int position) {
            return buddyList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getCount() {
            return buddyList.size();
        }
    }
}
