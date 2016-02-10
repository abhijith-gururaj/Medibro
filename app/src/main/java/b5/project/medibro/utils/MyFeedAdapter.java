package b5.project.medibro.utils;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;

import b5.project.medibro.FeedItemDetails;
import b5.project.medibro.R;
import b5.project.medibro.pojos.FeedItem;
import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Abhijith on 2/5/2016.
 */
public class MyFeedAdapter extends RecyclerView.Adapter<MyFeedAdapter.ViewHolder> {

    ArrayList<FeedItem> feedItems;
    Context context;
    LayoutInflater mInflater;

    public MyFeedAdapter(Context context, ArrayList<FeedItem> feedItems) {
        mInflater = LayoutInflater.from(context);
        this.context = context;
        this.feedItems = feedItems;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.layout_feed_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final FeedItem item = feedItems.get(position);
        holder.bind(item);
        holder.fiQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "Clicked :" + item.getFeedQuestion(),
                        Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(context, FeedItemDetails.class);
                intent.putExtra("feedItem", new Gson().toJson(item));
                context.startActivity(intent);
            }
        });

        holder.followLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "Clicked follow", Toast.LENGTH_SHORT).show();
            }
        });

        holder.commentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "Clicked Comment", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return feedItems.size();
    }

    public void addFeedItem(FeedItem newItem) {
        feedItems.add(newItem);
    }


    class ViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.fi_topic)
        TextView fiTopic;
        @Bind(R.id.fi_question)
        TextView fiQuestion;
        @Bind(R.id.fi_followIcon)
        ImageView follow;
        @Bind(R.id.follow_layout)
        RelativeLayout followLayout;
        @Bind(R.id.fi_followCount)
        TextView followCount;
        @Bind(R.id.fi_commentCount)
        TextView commentCount;
        @Bind(R.id.comment_layout)
        RelativeLayout commentLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bind(FeedItem item) {
            fiTopic.setText(item.getFeedTopic());
            fiQuestion.setText(item.getFeedQuestion());
            followCount.setText(String.valueOf(item.getFollowCount()));
            commentCount.setText(String.valueOf(item.getCommentCount()));
        }

    }
}
