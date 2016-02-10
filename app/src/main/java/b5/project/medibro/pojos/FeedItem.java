package b5.project.medibro.pojos;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Abhijith on 2/5/2016.
 */
@ParseClassName("FeedItem")
public class FeedItem extends ParseObject {

    private String feedQuestion, feedDescription, feedTopic;
    private ArrayList<FeedComment> feedComments;
    private String createdBy;
    private List<ParseUser> followedBy;
    private String feedItemChannel;

    public FeedItem() {
        super();
    }


    public String getFeedQuestion() {
        return getString("feedQuestion");
    }

    public void setFeedQuestion(String feedQuestion) {
        this.feedQuestion = feedQuestion;
        put("feedQuestion", feedQuestion);
    }

    public String getFeedDescription() {
        return getString("feedDesc");
    }

    public void setFeedDescription(String feedDescription) {
        this.feedDescription = feedDescription;
        put("feedDesc", feedDescription);
    }

    public String getFeedTopic() {
        return getString("feedTopic");
    }

    public void setFeedTopic(String feedTopic) {
        this.feedTopic = feedTopic;
        put("feedTopic", feedTopic);
    }

    public int getCommentCount() {
        return feedComments == null ? 0 : feedComments.size();
    }

    public int getFollowCount() {
        return followedBy == null ? 0 : followedBy.size();
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
        put("createdBy", createdBy);
    }

}
