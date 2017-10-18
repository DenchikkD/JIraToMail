package JiraToMail.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * Project : JIraToMail
 * User: kondratiuk
 * Date: 18.10.2017
 * Time: 13:31
 */
public class Issue {
    @SerializedName("subject")
    @Expose
    private String subject;
    @SerializedName("external_id")
    @Expose
    private String externalId;
    @SerializedName("jira_id")
    @Expose
    private String jiraId;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("data")
    @Expose
    private String data;

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    public String getJiraId() {
        return jiraId;
    }

    public void setJiraId(String jiraId) {
        this.jiraId = jiraId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("subject", subject).append("externalId", externalId).append("jiraId", jiraId).append("type", type).append("data", data).toString();
    }

}
