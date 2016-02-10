package b5.project.medibro;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.google.gson.Gson;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

import b5.project.medibro.pojos.FeedComment;
import b5.project.medibro.pojos.FeedItem;
import b5.project.medibro.utils.MyFeedDetailsAdapter;
import butterknife.Bind;
import butterknife.ButterKnife;

public class FeedItemDetails extends AppCompatActivity {

    @Bind(R.id.item_details_list)
    RecyclerView recyclerView;
    MyFeedDetailsAdapter adapter;
    ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_item_details);
        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final FeedItem item = new Gson().fromJson(getIntent().getStringExtra("feedItem"), FeedItem.class);
        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Fetching Data.Please Wait...");
        pDialog.setIndeterminate(true);
        pDialog.show();
        ParseQuery<FeedComment> query = ParseQuery.getQuery(FeedComment.class);
        query.whereEqualTo("parent", item.getObjectId());
        query.findInBackground(new FindCallback<FeedComment>() {
            @Override
            public void done(List<FeedComment> objects, ParseException e) {
                if (e == null) {
                    ArrayList<FeedComment> feedComments = new ArrayList<FeedComment>(objects);
                    adapter = new MyFeedDetailsAdapter(FeedItemDetails.this, item, feedComments);
                    recyclerView.setLayoutManager(new LinearLayoutManager(FeedItemDetails.this));
                    recyclerView.setAdapter(adapter);
                    pDialog.dismiss();
                }
            }
        });


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_add_comment);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(FeedItemDetails.this, AddComment.class);
                intent.putExtra("feedItem", new Gson().toJson(item));
                startActivityForResult(intent, 111);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("FeedItemDetails", "Finished OnResult");
        if (requestCode == 111) {
            if (resultCode == RESULT_OK) {
                //TODO
                if (data != null) {
                    String json = data.getStringExtra("feedComment");
                    FeedComment comment = new Gson().fromJson(json, FeedComment.class);
                    adapter.addComment(comment);
                    Log.d("FeedItemDetails", "adding comment" + json);
                }
            } else if (resultCode == RESULT_CANCELED) {
                //TODO
            }
        }
    }
}
