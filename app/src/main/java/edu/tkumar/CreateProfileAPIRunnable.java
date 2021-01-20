package edu.tkumar;

import android.net.Uri;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import static java.net.HttpURLConnection.HTTP_BAD_REQUEST;
import static java.net.HttpURLConnection.HTTP_OK;

public class CreateProfileAPIRunnable implements Runnable{

    private CreateProfileActivity createProfileActivity;
    private final String firstName, lastName, userName, department, story, position, password, remainingPointsToAward, location, imageBytes, apiValue;
    private static final String base_url = "http://christopherhield.org/api/Profile/CreateProfile";
    private static final String TAG = "CreateProfileAPIRunnable";

    public CreateProfileAPIRunnable(CreateProfileActivity createProfileActivity, String firstName, String lastName,
                                    String userName, String department, String story, String position, String password,
                                    String remainingPointsToAward, String location, String imageBytes, String apiValue) {
        this.createProfileActivity = createProfileActivity;
        this.firstName = firstName;
        this.lastName = lastName;
        this.userName = userName;
        this.department = department;
        this.story = story;
        this.position = position;
        this.password = password;
        this.remainingPointsToAward = remainingPointsToAward;
        this.location = location;
        this.imageBytes = imageBytes;
        this.apiValue = apiValue;
    }

    @Override
    public void run() {

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("Profile image", imageBytes);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        HttpURLConnection connection = null;
        BufferedReader reader = null;

        Uri.Builder buildURL = Uri.parse(base_url).buildUpon();
        buildURL.appendQueryParameter("firstName", firstName);
        buildURL.appendQueryParameter("lastName", lastName);
        buildURL.appendQueryParameter("userName", userName);
        buildURL.appendQueryParameter("department", department);
        buildURL.appendQueryParameter("story", story);
        buildURL.appendQueryParameter("position", position);
        buildURL.appendQueryParameter("password", password);
        buildURL.appendQueryParameter("remainingPointsToAward", remainingPointsToAward);
        buildURL.appendQueryParameter("location", location);
        String urlToUse = buildURL.build().toString();
        Log.d(TAG, "run: " + urlToUse);

        StringBuilder result = new StringBuilder();

        try {
            URL url = new URL(urlToUse);

            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestProperty("ApiKey", apiValue);
            connection.connect();

            OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream());
            out.write(jsonObject.toString());
            out.close();

            int responseCode = connection.getResponseCode();

            if (responseCode == HTTP_OK) {
                reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

                String line;
                while (null != (line = reader.readLine())) {
                    result.append(line).append("\n");
                }
            } else {
                reader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));

                String line;
                while (null != (line = reader.readLine())) {
                    result.append(line).append("\n");
                }
            }
            Log.d(TAG, "run: " + result.toString());
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "run: error");
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    Log.e(TAG, "doInBackground: Error closing stream: " + e.getMessage());
                }
            }
        }
        //process(result.toString());
    }
}
