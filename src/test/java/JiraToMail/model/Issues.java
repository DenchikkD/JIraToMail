package JiraToMail.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Project : JIraToMail
 * User: kondratiuk
 * Date: 18.10.2017
 * Time: 13:31
 */
public class Issues {

    @SerializedName("issues")
    @Expose
    private List<Issue> issues = new ArrayList<Issue>();

    public List<Issue> getIssues() {
        return issues;
    }

    public void setIssues(List<Issue> issues) {
        this.issues = issues;
    }

}
