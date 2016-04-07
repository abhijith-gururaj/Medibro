package b5.project.medibro;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.lapism.searchview.adapter.SearchAdapter;
import com.lapism.searchview.adapter.SearchItem;
import com.lapism.searchview.history.SearchHistoryTable;
import com.lapism.searchview.view.SearchCodes;
import com.lapism.searchview.view.SearchView;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

import b5.project.medibro.pojos.FeedItem;
import b5.project.medibro.utils.MyFeedAdapter;

public class SearchActivity extends AppCompatActivity {

    private SearchHistoryTable mHistoryDatabase;
    private List<SearchItem> mSuggestionsList;
    private int mVersion = SearchCodes.VERSION_MENU_ITEM;
    private int mStyle = SearchCodes.STYLE_MENU_ITEM_CLASSIC;
    private int mTheme = SearchCodes.THEME_LIGHT;
    SearchView mSearchView;
    RecyclerView searchList;
    MyFeedAdapter feedAdapter;
    ArrayList<String> topics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_search);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mHistoryDatabase = new SearchHistoryTable(this);
        mSuggestionsList = new ArrayList<>();

        // SearchView basic attributes  ------------------------------------------------------------
        mSearchView = (SearchView) findViewById(R.id.searchView);
        mSearchView.setVersion(mVersion);
        mSearchView.setStyle(mStyle);
        mSearchView.setTheme(mTheme);

        // -----------------------------------------------------------------------------------------
        mSearchView.setDivider(true);
        mSearchView.setHint("Search");
        mSearchView.setHintSize(getResources().getDimension(R.dimen.search_text_medium));
        mSearchView.setVoice(true);
        mSearchView.setVoiceText("Voice");
        mSearchView.setAnimationDuration(300);

        String topic = getIntent().getStringExtra("topic");
        topics = getIntent().getStringArrayListExtra("topics");

        List<SearchItem> mResultsList = new ArrayList<>();
        SearchAdapter mSearchAdapter = new SearchAdapter(this, mResultsList, mSuggestionsList, mTheme);
        mSearchAdapter.setOnItemClickListener(new SearchAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                TextView textView = (TextView) view.findViewById(R.id.textView_item_text);
                CharSequence text = textView.getText();
                mHistoryDatabase.addItem(new SearchItem(text));
                Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
                final ProgressDialog pDialog = new ProgressDialog(SearchActivity.this);
                pDialog.setIndeterminate(true);
                pDialog.setCancelable(false);
                pDialog.show();

                String query = text.toString();

                ParseQuery<FeedItem> searchQuery = ParseQuery.getQuery(FeedItem.class);

                if (query.equals("Others")) {
                    searchQuery.whereNotContainedIn("feedTopic", topics);
                } else {
                    searchQuery.whereMatches("feedTopic", "(" + query + ")", "i");
                }
                searchQuery.findInBackground(new FindCallback<FeedItem>() {
                    @Override
                    public void done(List<FeedItem> objects, ParseException e) {
                        pDialog.dismiss();
                        if (objects.isEmpty()) {
                            feedAdapter.clearData();
                            Toast.makeText(SearchActivity.this,
                                    "Empty Results.Please try again with different input",
                                    Toast.LENGTH_LONG)
                                    .show();

                        } else {
                            ArrayList<FeedItem> list = new ArrayList<FeedItem>(objects);
                            feedAdapter = new MyFeedAdapter(SearchActivity.this, list);
                            searchList.setAdapter(feedAdapter);
                        }
                    }
                });
                mSearchView.hide(true);
            }
        });

        mSearchView.setAdapter(mSearchAdapter);

        searchList = (RecyclerView) findViewById(R.id.search_list);
        searchList.setLayoutManager(new LinearLayoutManager(this));

        Log.d("SearchActivity", topics.toString());
        ParseQuery<FeedItem> query = ParseQuery.getQuery(FeedItem.class);
        if (topic.equals("Others"))
            query.whereNotContainedIn("feedTopic", topics);
        else
            query.whereEqualTo("feedTopic", topic);
        query.findInBackground(new FindCallback<FeedItem>() {
            @Override
            public void done(List<FeedItem> objects, ParseException e) {
                ArrayList<FeedItem> list = new ArrayList<FeedItem>(objects);
                feedAdapter = new MyFeedAdapter(SearchActivity.this, list);
                searchList.setAdapter(feedAdapter);
            }
        });

        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                mHistoryDatabase.addItem(new SearchItem(query));
                Toast.makeText(getApplicationContext(), query, Toast.LENGTH_SHORT).show();
                final ProgressDialog pDialog = new ProgressDialog(SearchActivity.this);
                pDialog.setIndeterminate(true);
                pDialog.setCancelable(false);
                pDialog.show();
                ParseQuery<FeedItem> searchQuery = ParseQuery.getQuery(FeedItem.class);

                if (query.equals("Others")) {
                    searchQuery.whereNotContainedIn("feedTopic", topics);
                } else {
                    searchQuery.whereMatches("feedTopic", "(" + query + ")", "i");
                }
                searchQuery.findInBackground(new FindCallback<FeedItem>() {
                    @Override
                    public void done(List<FeedItem> objects, ParseException e) {
                        pDialog.dismiss();
                        if (objects.isEmpty()) {
                            feedAdapter.clearData();
                            Toast.makeText(SearchActivity.this,
                                    "Empty Results.Please try again with different input",
                                    Toast.LENGTH_LONG)
                                    .show();

                        } else {
                            ArrayList<FeedItem> list = new ArrayList<FeedItem>(objects);
                            feedAdapter = new MyFeedAdapter(SearchActivity.this, list);
                            searchList.setAdapter(feedAdapter);
                        }
                    }
                });
                mSearchView.hide(true);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    private void showSearchView() {
        mSuggestionsList.clear();
        mSuggestionsList.addAll(mHistoryDatabase.getAllItems());
        mSuggestionsList.add(new SearchItem("Google"));
        mSuggestionsList.add(new SearchItem("Android"));
        mSearchView.show(true);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SearchView.SPEECH_REQUEST_CODE && resultCode == RESULT_OK) {
            List<String> results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if (results != null && results.size() > 0) {
                String searchWrd = results.get(0);
                if (!TextUtils.isEmpty(searchWrd)) {
                    mSearchView.setQuery(searchWrd);
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_search, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search:
                showSearchView();
                return true;
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                // NavUtils.navigateUpTo();
                // DatabaseUtils ... finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
