package b5.project.medibro;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import b5.project.medibro.pojos.Message;
import b5.project.medibro.utils.DatabaseHandler;
import b5.project.medibro.utils.TouchEffect;
import butterknife.Bind;
import butterknife.ButterKnife;

public class Chat extends AppCompatActivity {

    static final String TAG = Chat.class.getSimpleName();
    static final String USER_ID_KEY = "userId";
    static final String BODY_KEY = "body";
    public static final TouchEffect TOUCH = new TouchEffect();

    static final int MAX_CHAT_MESSAGES_TO_SHOW = 30;
    private ArrayList<Message> convList;
    static final int POLL_INTERVAL = 3000; // milliseconds
    Handler handler;

    private Date lastMsgDate;

    /**
     * Flag to hold if the activity is running or not.
     */
    private boolean isRunning;

    @Bind(R.id.etMessage)
    EditText etMessage;
    @Bind(R.id.btSend)
    Button btSend;
    @Bind(R.id.lvChat)
    ListView lvChat;
    @Bind(R.id.progress_bar)
    ProgressBar progressBar;
    ArrayList<Message> mMessages;
    ChatAdapter mAdapter;
    DatabaseHandler handler1;
    String toUserId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Log.d(TAG, "Started Chat");
        convList = new ArrayList<>();
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String toUserName = getIntent().getStringExtra("toUserName");
        toUserId = getIntent().getStringExtra("toUserId");

        if (toUserName != null)
            getSupportActionBar().setTitle(toUserName);
        handler1 = new DatabaseHandler(this);
        etMessage.setInputType(InputType.TYPE_CLASS_TEXT
                | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        setTouch(btSend);

        mMessages = new ArrayList<>();
        // Automatically scroll to the bottom when a data set change notification is received and only if the last item is already visible on screen. Don't scroll to the bottom otherwise.
        lvChat.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        lvChat.setStackFromBottom(true);

        //final String userId = ParseUser.getCurrentUser().getObjectId();
        mAdapter = new ChatAdapter();
        lvChat.setAdapter(mAdapter);
        lvChat.setDivider(null);
        lvChat.setDividerHeight(0);

        btSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });

        handler = new Handler();
    }

    public void sendMessage() {
        if (etMessage.length() == 0)
            return;

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.
                INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(etMessage.getWindowToken(), 0);

        final String data = etMessage.getText().toString();
        final Message message = new Message();
        message.setFromUser(ParseUser.getCurrentUser().getObjectId());
        message.setToUser(toUserId);
        message.setBody(data);
        message.setStatus(Message.STATUS_SENDING);

        mAdapter.notifyDataSetChanged();
        etMessage.setText(null);

        message.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {

                if (e == null) {
                    if (convList.size() == 0) {
                        //This is the first Message
                        HashMap<String, Object> params = new HashMap<>();
                        params.put("fromUserId", message.getFromUserId());
                        params.put("toUserId", message.getToUserId());
                        params.put("message", message.getBody());
                        ParseCloud.callFunctionInBackground("addChatUsersIfNotExists",
                                params,
                                new FunctionCallback<Boolean>() {
                                    public void done(Boolean isSaved, ParseException e) {
                                        if (e == null) {
                                            if (isSaved) {
                                                if (!handler1.hasBuddyRow(toUserId))
                                                    handler1.addChatUsers(ParseUser.
                                                                    getCurrentUser()
                                                                    .getObjectId(),
                                                            toUserId);
                                            }
                                        } else {
                                            Log.d(TAG, e.getMessage());
                                            e.printStackTrace();
                                        }
                                    }
                                });
                    }
                    convList.add(message);

                    HashMap<String, Object> params = new HashMap<>();
                    params.put("recipientId", toUserId);
                    params.put("message", message.getBody());
                    params.put("fromUserName", ParseUser.getCurrentUser().getUsername());
                    Log.d(TAG, "calling send push function");
                    ParseCloud.callFunctionInBackground("sendMessageToUser", params,
                            new FunctionCallback<String>() {
                                public void done(String success, ParseException e) {
                                    if (e == null) {
                                        // Push sent successfully
                                        Log.d(TAG, success);
                                    } else {
                                        Log.d(TAG, e.getMessage());
                                        e.printStackTrace();
                                    }
                                }
                            });
                    message.setStatus(Message.STATUS_SENT);
                } else {
                    message.setStatus(Message.STATUS_FAILED);
                    Log.d(TAG, e.getMessage());
                    e.printStackTrace();
                }
//                String separator = ", ";
//                int total = convList.size() * separator.length();
//                for (Message s : convList) {
//                    total += s.getBody().length();
//                }
//
//                StringBuilder sb = new StringBuilder(total);
//                for (Message s : convList) {
//                    sb.append(separator).append(s.getBody());
//                }
//
//                String result = sb.substring(separator.length());
//                Log.d(TAG, "List: " + result);

                mAdapter.notifyDataSetChanged();
            }
        });
    }

    public View setTouch(View v) {
        if (v != null)
            v.setOnTouchListener(TOUCH);
        return v;
    }

    @Override
    protected void onResume() {
        super.onResume();
        isRunning = true;
        progressBar.setVisibility(View.VISIBLE);
        loadConversationList();
    }

    /* (non-Javadoc)
     * @see android.support.v4.app.FragmentActivity#onPause()
     */
    @Override
    protected void onPause() {
        super.onPause();
        isRunning = false;
    }

    private void loadConversationList() {
        ParseQuery<ParseObject> q = ParseQuery.getQuery("Message");
        if (convList.size() == 0) {
            // load all messages...
            ArrayList<String> al = new ArrayList<>();
            al.add(toUserId);
            al.add(ParseUser.getCurrentUser().getObjectId());
            q.whereContainedIn(Message.FROM_USER_ID, al);
            q.whereContainedIn(Message.TO_USER_ID_KEY, al);
        } else {
            // load only newly received message..
            if (lastMsgDate != null)
                q.whereGreaterThan("createdAt", lastMsgDate);
            q.whereEqualTo(Message.TO_USER_ID_KEY, ParseUser.getCurrentUser().getObjectId());
            q.whereEqualTo(Message.FROM_USER_ID, toUserId);
        }
        q.orderByDescending("createdAt");
        q.setLimit(MAX_CHAT_MESSAGES_TO_SHOW);
        q.findInBackground(new FindCallback<ParseObject>() {

            @Override
            public void done(List<ParseObject> li, ParseException e) {
                progressBar.setVisibility(View.GONE);
                if (li != null && li.size() > 0) {
                    for (int i = li.size() - 1; i >= 0; i--) {
                        Message m = (Message) li.get(i);
                        convList.add(m);
                        if (lastMsgDate == null || lastMsgDate.before(m.getCreatedAt()))
                            lastMsgDate = m.getCreatedAt();
                        Log.d(TAG, "lastMsgDate: " + lastMsgDate.toString());
                        mAdapter.notifyDataSetChanged();
                    }
                }
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (isRunning)
                            loadConversationList();
                    }
                }, POLL_INTERVAL);
            }
        });
    }

    private class ChatAdapter extends BaseAdapter {
        /* (non-Javadoc)
         * @see android.widget.Adapter#getCount()
         */
        @Override
        public int getCount() {
            return convList.size();
        }

        /* (non-Javadoc)
         * @see android.widget.Adapter#getItem(int)
         */
        @Override
        public Message getItem(int pos) {
            return convList.get(pos);
        }

        /* (non-Javadoc)
         * @see android.widget.Adapter#getItemId(int)
         */
        @Override
        public long getItemId(int pos) {
            return pos;
        }

        /* (non-Javadoc)
         * @see android.widget.Adapter#getView(int, android.view.View, android.view.ViewGroup)
         */
        @Override
        public View getView(int pos, View v, ViewGroup arg2) {
            Message message = getItem(pos);
            if (message != null)
                Log.d(TAG, "msg: " + message.getBody() + " . " + message.getCreatedAt().toString());

            if (message.isSent())
                v = getLayoutInflater().inflate(R.layout.chat_item_sent, null, false);
            else
                v = getLayoutInflater().inflate(R.layout.chat_item_rcv, null, false);

            TextView lbl = (TextView) v.findViewById(R.id.lbl1);
            lbl.setText(DateUtils.getRelativeDateTimeString(Chat.this,
                    message.getCreatedAt().getTime(),
                    DateUtils.SECOND_IN_MILLIS,
                    DateUtils.DAY_IN_MILLIS, 0));

            lbl = (TextView) v.findViewById(R.id.lbl2);
            lbl.setText(message.getBody());

            lbl = (TextView) v.findViewById(R.id.lbl3);
            if (message.isSent()) {
                if (message.getStatus() == Message.STATUS_SENT)
                    lbl.setText("Delivered");
                else if (message.getStatus() == Message.STATUS_SENDING)
                    lbl.setText("Sending...");
                else
                    lbl.setText("Failed");
            } else
                lbl.setText("");

            return v;
        }
    }
}
