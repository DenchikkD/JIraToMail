package JiraToMail;

import JiraToMail.model.Issue;
import JiraToMail.model.Issues;
import com.google.gson.Gson;
import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.*;

/**
 * Project : JIraToMail
 * User: kondratiuk
 * Date: 18.10.2017
 * Time: 13:30
 */
public class JIraToMail {



    public static final String user = "aivanchik";
    public static final String request = "(status changed after startOfDay(-30h) OR created >= startOfDay(-30h)) AND status changed by aivanchik OR created >= startOfDay(-30h) AND creator = aivanchik ORDER BY updated DESC";
    public static final String fridayRequest = "(status changed after startOfDay(-74h) OR created >= startOfDay(-74h)) AND status changed by aivanchik OR created >= startOfDay(-74h) AND creator = aivanchik ORDER BY updated DESC";
    public static final String JIRA_URL = "https://jira.viaamadeus.com/rest/api/2/search?jql=";
    public static final String FCM_URL = "https://fcm.googleapis.com/fcm/send";
//    public static final String FCM_KEY = "key=AAAA5J7eHDs:APA91bHJMsHiudGLDoNqr1PSM25OoNKSGIL4LjTiOZ0KK1BKnMY1lM9o5Gksn0OqkFfmhtk4att2vGM-3_Hix0wrJhctAK2vreS2pO_WKjdDKUuY5kDi5-nTt82LF-qKb5Md_BNveI5-";
    public static final String FCM_KEY = "key=AAAAnaE4_S8:APA91bEsojrmGHftzsICHVPSCz25SjRWVBhNa_qe3-GMBlAfcWnQxjFMfUxJm4JjkdT230c2dAnp1VKwdrAxcPDkIXsmIY_RWZBJlwi6QqqC3XfK_6iX_WaYvrfUPAJZaW4FOCXxEmVV";
    public static final int  timeout = 20000;
    public String date;

    Map<String, String> cookieMap = new HashMap<>();



    @DataProvider(name = "dp")
    public Object [][] getData(){
        return new Object[][]{
                {"aivanchik", "ctCtrzUo2RM:APA91bF-VMAsLz8C2xI-w5WjOgYZUh2mH0cSnHOKecf5v-MM_quzTAPGIAtfD3dIm25tU8pl2g_FuSRboutRGFlFK7OIy9ykIEbEIBgoS8QoijFv1dFvU-qG8g5AhC4tMgI2A1CkSUR5"},
                {"okovalev", "e6cIvVKMIis:APA91bH1nTEsevGJLYNXstBw1RJYW2N6OSyUkWM6b_xjRl3nGnSorWzEaQBUCIGOgqAIqZjz9CAfGlDzOiJvPwN3IUDHx8odpteScKq0dBGWoM62s0TUccz79u1OWde4_A6NgujBQvN3"}

        };
    }



    @Test(dataProvider = "dp")
    public void getTasks(String user, String key){
        Calendar myDate = Calendar.getInstance();
        int dow = myDate.get (Calendar.DAY_OF_WEEK);
        String req = dow == 2 ? fridayRequest : request;
        int days = dow == 2 ? 3 : 1 ;
        date = new DateTime().minusDays(days).toString("yyyy-MM-dd");


        Connection.Response loginResponce = null;
        try {
            loginResponce = Jsoup.connect("https://jira.viaamadeus.com/login.jsp")
                    .timeout(timeout)
                    .data("os_username", "aivanchik")
                    .data("os_password", "esdzp1107")
                    .data("login", "Log In")
                    .ignoreContentType(true).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        cookieMap = loginResponce.cookies();

        Connection.Response searchResponse = null;
        try {

            searchResponse = Jsoup.connect(JIRA_URL + req.replaceAll("aivanchik", user))
                    .timeout(timeout)
                    .cookies(cookieMap)
                    .ignoreContentType(true).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }


        JSONObject response = new JSONObject(searchResponse.body());

        System.out.println("Total issues " + response.get("total"));

        Issues issues = parseData(response);
        Gson gson = new Gson();
        String tasks = gson.toJson(issues);
        System.out.println(tasks);

        String body = "{\n" +
                "\t\"to\": \""+key+"\",\n" +
                "  \"data\": " + tasks +
                "}";



        Document fcmResponce = null;
        try {
            fcmResponce = Jsoup.connect(FCM_URL)
                    .timeout(timeout)
                    .header("Authorization", FCM_KEY)
                    .header("Content-Type", "application/json")
                    .requestBody(body)
                    .ignoreContentType(true)
                    .post();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(fcmResponce.text());
    }

    private Issues parseData(JSONObject response){
        Issues issues = new Issues();
        List<Issue> issueList = new ArrayList<>();
        JSONArray array = response.getJSONArray("issues");
        for (int i = 0; i<array.length(); i++){
            JSONObject object = array.getJSONObject(i);
            Issue issue = new Issue();
            JSONObject fields = object.getJSONObject("fields");
            JSONObject issuetype = fields.getJSONObject("issuetype");
            issue.setData(date);
            issue.setExternalId(date+object.get("key").toString());
            issue.setJiraId(object.get("key").toString());
            issue.setType(issuetype.get("name").toString());
            issue.setSubject(fields.get("summary").toString());
            issueList.add(issue);
        }

        issues.setIssues(issueList);
        return issues;
    }
}
