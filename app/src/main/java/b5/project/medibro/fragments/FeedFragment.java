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

import com.google.gson.Gson;
import com.parse.FindCallback;
import com.parse.ParseException;
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

    private OnFragmentInteractionListener mListener;
    RecyclerView recyclerView;
    private MyFeedAdapter adapter;

    public FeedFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View v = inflater.inflate(R.layout.fragment_feed, container, false);


        ParseQuery<FeedItem> query = ParseQuery.getQuery(FeedItem.class);
        query.findInBackground(new FindCallback<FeedItem>() {
            @Override
            public void done(List<FeedItem> objects, ParseException e) {
                ArrayList<FeedItem> list = new ArrayList<FeedItem>(objects);
                recyclerView = (RecyclerView) v.findViewById(R.id.feed_list);
                recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                adapter = new MyFeedAdapter(getActivity(), list);
                recyclerView.setAdapter(adapter);
            }
        });

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
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

}
