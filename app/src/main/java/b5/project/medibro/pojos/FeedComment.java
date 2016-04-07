package b5.project.medibro.pojos;

import com.parse.ParseClassName;
import com.parse.ParseObject;

/**
 * Created by Abhijith on 2/5/2016.
 */

@ParseClassName("FeedComment")
public class FeedComment extends ParseObject {
    private String parent;
    private String commentedBy;
    private String comment;

    public FeedComment() {
        super();
    }

    public String getCommentedBy() {
        return getString("commentedBy");
    }

    public void setCommentedBy(String commentedBy) {
        this.commentedBy = commentedBy;
        put("commentedBy", this.commentedBy);
    }

    public String getComment() {
        return getString("comment");
    }

    public void setComment(String comment) {
        this.comment = comment;
        put("comment", comment);
    }

    public String getParent() {
        FeedItem item = (FeedItem) get("parent");

        return item == null ? null : item.getObjectId();
    }

    public void setParent(String parent) {
        this.parent = parent;
        put("parent", ParseObject.createWithoutData("FeedItem", parent));
    }
}
