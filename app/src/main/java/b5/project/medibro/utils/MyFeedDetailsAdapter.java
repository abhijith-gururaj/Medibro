package b5.project.medibro.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import b5.project.medibro.R;
import b5.project.medibro.pojos.FeedComment;
import b5.project.medibro.pojos.FeedItem;
import butterknife.Bind;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Abhijith on 2/8/2016.
 */
public class MyFeedDetailsAdapter extends RecyclerView.Adapter<MyFeedDetailsAdapter.ViewHolder> {

    private static final String TAG = "FeedDetailsAdapter";
    private final LayoutInflater mInflater;
    Context context;
    FeedItem feedItem;
    ArrayList<FeedComment> feedComments;

    public MyFeedDetailsAdapter(Context context, FeedItem feedItem, ArrayList<FeedComment> comments) {
        this.context = context;
        this.feedItem = feedItem;
        mInflater = LayoutInflater.from(context);
        feedComments = comments;
        Log.d(TAG, "Size of comments list: " + feedComments.size());
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.layout_feed_item_comment, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Log.d(TAG, "called bindViewHolder");
        final FeedComment comment = feedComments.get(position);
        holder.bindComments(comment);
    }

    @Override
    public int getItemCount() {
        return feedComments.size();
    }

    public void addComment(FeedComment comment) {
        feedComments.add(comment);
        Log.d(TAG, "Size: " + feedComments.size());
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.comment_user_dp)
        CircleImageView mProfileImage;
        @Bind(R.id.comment_username)
        TextView username;
        @Bind(R.id.comment_ts)
        TextView mTimestamp;
        @Bind(R.id.feed_comment)
        TextView mComment;
        String commentUser;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bindComments(FeedComment comment) {
            commentUser = comment.getCommentedBy();
            Log.d(TAG, "comented by: " + commentUser);
            ParseQuery<ParseUser> query = ParseUser.getQuery();
            query.whereEqualTo("objectId", commentUser);
            query.findInBackground(new FindCallback<ParseUser>() {

                @Override
                public void done(List<ParseUser> object, ParseException e) {
                    // TODO Auto-generated method stub
                    if (e == null) {
                        Log.d(TAG, "List not null");
                        for (ParseUser obj : object) {
                            username.setText(obj.getUsername());
                            Log.d(TAG, obj.getUsername());
                            try {
                                ParseFile parseFile = obj.getParseFile("profileThumb");
                                byte[] data = parseFile.getData();
                                Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                                mProfileImage.setImageBitmap(bitmap);
                            } catch (Exception ex) {
                                e.printStackTrace();
                            }
                        }
                    } else {
                        Log.d(TAG, e.getMessage());
                    }
                }
            });
            mComment.setText(comment.getComment());
            String date = comment.getCreatedAt().getDay() + "/" +
                    (comment.getCreatedAt().getMonth() + 1) + "/" +
                    (comment.getCreatedAt().getYear() + 1900);

            mTimestamp.setText(date);
        }
    }
}
