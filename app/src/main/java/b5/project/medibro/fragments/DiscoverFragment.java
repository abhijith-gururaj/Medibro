package b5.project.medibro.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;

import b5.project.medibro.R;
import b5.project.medibro.utils.MySearchAdapter;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 */
public class DiscoverFragment extends Fragment {

    MySearchAdapter adapter;
    RecyclerView mSearchList;
    ArrayList<String> topics;
    public DiscoverFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_discover, container, false);
        mSearchList = (RecyclerView) v.findViewById(R.id.search_list);
        topics = new ArrayList<>();
        topics.add("Ayurveda");
        topics.add("Cardiology");
        topics.add("Dermatology");
        topics.add("ENT");
        topics.add("Homeopathy");
        topics.add("Psychology");
        topics.add("Orthopedics");
        topics.add("Ophthalmology");
        topics.add("Gynaecology");
        topics.add("Others");
        GridLayoutManager manager = new GridLayoutManager(getActivity(), 2);

        mSearchList.setLayoutManager(manager);
        adapter = new MySearchAdapter(getActivity(), topics);
        mSearchList.setAdapter(adapter);
        adapter.setItemClickListener(new MySearchAdapter.ItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Toast.makeText(getActivity(), "Clicked at: " + topics.get(position),
                        Toast.LENGTH_LONG)
                        .show();
            }
        });

        return v;
    }
}
