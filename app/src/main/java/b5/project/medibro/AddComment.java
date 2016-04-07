package b5.project.medibro;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import b5.project.medibro.pojos.FeedComment;
import b5.project.medibro.pojos.FeedItem;
import butterknife.Bind;
import butterknife.ButterKnife;

public class AddComment extends AppCompatActivity {

    @Bind(R.id.add_comment)
    EditText mAddComment;
    FeedItem feedItem;
    public static final String TAG = AddComment.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_comment);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ButterKnife.bind(this);
        String feedItemId = getIntent().getStringExtra("feedItemId");
        Log.d(TAG, "feedItemId: " + feedItemId);
        feedItem = ParseObject.createWithoutData(FeedItem.class, feedItemId);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_comment, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_done) {
            if (!mAddComment.getText().toString().isEmpty()) {
                //feedItem.increment("feedComments");
//                feedItem.put("feedComments",feedItem.getCommentCount()+1);
//                feedItem.saveInBackground(new SaveCallback() {
//                    @Override
//                    public void done(ParseException e) {
//                        if(e!=null){
//                            Log.d("Feed Details","Error: "+e.getMessage());
//                            e.printStackTrace();
//                        }
//                    }
//                });
                final FeedComment comment = new FeedComment();
                comment.setParent(feedItem.getObjectId());
                comment.setComment(mAddComment.getText().toString());
                comment.setCommentedBy(ParseUser.getCurrentUser().getObjectId());
                comment.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            Log.d("Add Comment", "save comments: " + feedItem.getCommentCount());
                            Intent intent = new Intent();
                            intent.putExtra("commentObjectId", comment.getObjectId());
                            intent.putExtra("comment", comment.getComment());
                            intent.putExtra("commentedBy", comment.getCommentedBy());
                            intent.putExtra("parent", comment.getParent());
                            setResult(RESULT_OK, intent);
                            finish();
                        } else {
                            Toast.makeText(AddComment.this, "Something is Wrong." + e.getMessage(),
                                    Toast.LENGTH_LONG)
                                    .show();
                            e.printStackTrace();
                        }
                    }
                });
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
