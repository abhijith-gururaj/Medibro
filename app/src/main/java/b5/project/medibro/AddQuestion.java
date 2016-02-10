package b5.project.medibro;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import b5.project.medibro.pojos.FeedItem;
import butterknife.Bind;
import butterknife.ButterKnife;

public class AddQuestion extends AppCompatActivity {

    @Bind(R.id.question_topic)
    EditText mQTopic;
    @Bind(R.id.question)
    EditText mQuestion;
    @Bind(R.id.question_desc)
    EditText mQDesc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_question);
        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final FeedItem item = new FeedItem();
                item.setFeedTopic(mQTopic.getText().toString());
                item.setFeedQuestion(mQuestion.getText().toString());
                item.setFeedQuestion(mQDesc.getText().toString());
                item.setCreatedBy(ParseUser.getCurrentUser().getObjectId());
                item.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            Toast.makeText(AddQuestion.this, "Added Successfully", Toast.LENGTH_SHORT)
                                    .show();
                            Log.d("AddQuestion", "finishing up");
                            Intent returnIntent = new Intent();
                            returnIntent.putExtra("feedItem", new Gson().toJson(item));
                            setResult(RESULT_OK, returnIntent);
                            finish();
                        }
                    }
                });
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

}
