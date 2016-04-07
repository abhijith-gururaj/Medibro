package b5.project.medibro.fragments;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParsePush;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

import b5.project.medibro.AddQuestion;
import b5.project.medibro.R;
import b5.project.medibro.pojos.FeedItem;
import b5.project.medibro.utils.MyFeedAdapter;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FeedFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class FeedFragment extends Fragment {

    private static final String TAG = FeedFragment.class.getSimpleName();
    private OnFragmentInteractionListener mListener;
    RecyclerView recyclerView;
    private MyFeedAdapter adapter;
    ProgressBar progressBar;
    View v;
    ParseQuery<FeedItem> query;
    TextView tv;
    public FeedFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_feed, container, false);
        tv = (TextView) v.findViewById(R.id.noItems);
        progressBar = (ProgressBar) v.findViewById(R.id.progress_bar);
        FloatingActionButton fab = (FloatingActionButton) v.findViewById(R.id.fab_addQuestion);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getParentFragment().startActivityForResult(new Intent(getActivity(), AddQuestion.class), 12321);
            }
        });

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
        progressBar.setVisibility(View.VISIBLE);
        query = ParseQuery.getQuery(FeedItem.class);
        query.findInBackground(new FindCallback<FeedItem>() {
            @Override
            public void done(List<FeedItem> objects, ParseException e) {
                if (e == null) {
                    if (getActivity() == null)
                        query.cancel();
                    else {
                        if (objects.isEmpty()) {
                            progressBar.setVisibility(View.GONE);
                            recyclerView.setVisibility(View.GONE);
                            tv.setVisibility(View.VISIBLE);
                        } else {
                            progressBar.setVisibility(View.GONE);
                            ArrayList<FeedItem> list = new ArrayList<>(objects);
                            recyclerView = (RecyclerView) v.findViewById(R.id.feed_list);
                            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                            adapter = new MyFeedAdapter(getActivity(), list);
                            recyclerView.setAdapter(adapter);
                        }
                    }
                } else {
                    Log.d(TAG, e.getMessage());
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
        query.cancel();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("feed Fragment", "back to recycler view" + requestCode);
        if (requestCode == 12321) {

            getActivity();
            if (resultCode == Activity.RESULT_OK) {
                String jsonObject;
                if (data != null) {
                    jsonObject = data.getStringExtra("feedItem");
                    Log.d("Feed Fragment", "Received object:" + jsonObject);
                    FeedItem newItem = new Gson().fromJson(jsonObject, FeedItem.class);
                    adapter.addFeedItem(newItem);
                    adapter.notifyDataSetChanged();
                    ParsePush.subscribeInBackground("feed_" + newItem.getObjectId());
                }
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //ToDo nothing?
            }
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

}
