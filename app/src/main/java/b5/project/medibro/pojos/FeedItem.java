package b5.project.medibro.pojos;

import android.util.Log;

import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Abhijith on 2/5/2016.
 */
@ParseClassName("FeedItem")
public class FeedItem extends ParseObject {


    private String feedItemChannel;

    public FeedItem() {
        super();
        feedItemChannel = this.getObjectId();
    }

    public String getFeedQuestion() {
        return getString("feedQuestion");
    }

    public void setFeedQuestion(String feedQuestion) {
        put("feedQuestion", feedQuestion);
    }

//    public void addFollowedBy(String userId){
//        if(followedBy==null) this.followedBy=new ArrayList<>();
//
//        this.followedBy.add(userId);
//        addAllUnique("followedBy", followedBy);
//    }

    public String getFeedDescription() {
        return getString("feedDesc");
    }

    public void setFeedDescription(String feedDescription) {
        put("feedDesc", feedDescription);
    }

    public String getFeedTopic() {
        return getString("feedTopic");
    }

    public void setFeedTopic(String feedTopic) {
        put("feedTopic", feedTopic);
    }

    public int getCommentCount() {
        return getInt("commentCount");
    }

    public int getFollowCount() {
        List<String> followedBy = getList("followedBy");

        return followedBy == null ? 0 : followedBy.size();
    }

    public String getCreatedBy() {
        return getString("createdBy");
    }

    public void setCreatedBy(String createdBy) {
        put("createdBy", createdBy);
    }

    public void setCommentCount(int commentCount) {
        put("commentCount", commentCount);
    }

    public void saveIt() {
        this.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    e.printStackTrace();
                    Log.d("feed item", "" + e.getMessage());
                }
            }
        });
    }

    public boolean followedByContains(String objectId) {
        List<String> followedBy = getList("followedBy");
        return followedBy != null && followedBy.contains(objectId);
    }

    public void initFollowedBy() {
        put("followedBy", new ArrayList<String>());
    }
}
