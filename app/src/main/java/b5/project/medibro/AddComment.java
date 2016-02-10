package b5.project.medibro;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.parse.ParseException;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_comment);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ButterKnife.bind(this);
        String jsonItem = getIntent().getStringExtra("feedItem");
        feedItem = new Gson().fromJson(jsonItem, FeedItem.class);
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
                final FeedComment comment = new FeedComment();
                comment.setParent(feedItem.getObjectId());
                comment.setComment(mAddComment.getText().toString());
                comment.setCommentedBy(ParseUser.getCurrentUser().getObjectId());
                comment.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            Intent intent = new Intent();
                            intent.putExtra("feedComment", new Gson().toJson(comment));
                            setResult(RESULT_OK, intent);
                            finish();
                        } else {
                            Toast.makeText(AddComment.this, "Something's Wrong.Please try again later",
                                    Toast.LENGTH_LONG)
                                    .show();
                        }
                    }
                });
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
