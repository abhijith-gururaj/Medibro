package b5.project.medibro;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import b5.project.medibro.pojos.FeedComment;
import b5.project.medibro.pojos.FeedItem;
import b5.project.medibro.utils.MyFeedDetailsAdapter;
import butterknife.Bind;
import butterknife.ButterKnife;

public class FeedItemDetails extends AppCompatActivity {

    @Bind(R.id.item_details_list)
    RecyclerView recyclerView;
    @Bind(R.id.progress_bar)
    ProgressBar progressBar;
    MyFeedDetailsAdapter adapter;
    FeedItem item;
    String feedTopic, feedQuestion, feedDesc, feedItemId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_item_details);
        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Intent i = getIntent();

        feedItemId = i.getStringExtra("feedItemId");
        feedTopic = i.getStringExtra("feedTopic");
        feedDesc = i.getStringExtra("feedDesc");
        feedQuestion = i.getStringExtra("feedQuestion");

        Log.d("Feed Details", "feedItemId: " + feedItemId);
        item = ParseObject.createWithoutData(FeedItem.class, feedItemId);
        progressBar.setVisibility(View.VISIBLE);
        ParseQuery<FeedComment> query = ParseQuery.getQuery(FeedComment.class);
        query.whereEqualTo("parent", item);
        query.findInBackground(new FindCallback<FeedComment>() {
            @Override
            public void done(List<FeedComment> objects, ParseException e) {
                if (e == null) {
                    HashMap<String, String> feedItemDetails = new HashMap<>();
                    feedItemDetails.put("feedTopic", feedTopic);
                    feedItemDetails.put("feedQuestion", feedQuestion);
                    feedItemDetails.put("feedDesc", feedDesc);

                    ArrayList<FeedComment> feedComments = new ArrayList<FeedComment>(objects);
                    adapter = new MyFeedDetailsAdapter(FeedItemDetails.this, feedItemDetails, feedComments, progressBar);
                    recyclerView.setLayoutManager(new LinearLayoutManager(FeedItemDetails.this));
                    recyclerView.setAdapter(adapter);
                    progressBar.setVisibility(View.GONE);
                }
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_add_comment);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(FeedItemDetails.this, AddComment.class);
                intent.putExtra("feedItemId", feedItemId);
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
                    String commentObjectId = data.getStringExtra("commentObjectId");
                    String comm = data.getStringExtra("comment");
                    String commentedBy = data.getStringExtra("commentedBy");
                    String parent = data.getStringExtra("parent");
                    FeedComment comment = ParseObject.createWithoutData(FeedComment.class, commentObjectId);
                    comment.setComment(comm);
                    comment.setCommentedBy(commentedBy);
                    comment.setParent(parent);
                    //FeedComment comment = new Gson().fromJson(json, FeedComment.class);
                    adapter.addComment(comment);
                    ParsePush.subscribeInBackground("feed_" + feedItemId);

                    JSONObject object = new JSONObject();
                    try {
                        object.put("type", "feed");
                        object.put("feedTopic", feedTopic);
                        object.put("feedQuestion", feedQuestion);
                        object.put("feedDesc", feedDesc);
                        object.put("feedItemId", feedItemId);
                        object.put("senderId", ParseUser.getCurrentUser().getObjectId());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                    ParsePush push = new ParsePush();
                    push.setChannel("feed_" + feedItemId);
                    push.setData(object);
                    push.sendInBackground();
                }
            } else if (resultCode == RESULT_CANCELED) {
                //TODO
            }
        }
    }
}
