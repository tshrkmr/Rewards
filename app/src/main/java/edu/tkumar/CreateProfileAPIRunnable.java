package edu.tkumar;

import android.net.Uri;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import static java.net.HttpURLConnection.HTTP_BAD_REQUEST;
import static java.net.HttpURLConnection.HTTP_CREATED;
import static java.net.HttpURLConnection.HTTP_OK;

public class CreateProfileAPIRunnable implements Runnable{

    private final CreateProfileActivity createProfileActivity;
    private final String firstName, lastName, userName, department, story, position, password, remainingPointsToAward, location, imageBytes, apiValue;
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

        HttpURLConnection connection = null;
        BufferedReader reader = null;

        try {
            String urlString = "http://christopherhield.org/api/Profile/CreateProfile";

            Uri.Builder buildURL = Uri.parse(urlString).buildUpon();
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
            URL url = new URL(urlToUse);

            Log.d(TAG, "run: " + urlToUse);

            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestProperty("ApiKey", apiValue);
            connection.connect();

            OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream());
            out.write(imageBytes);
            out.close();

            int responseCode = connection.getResponseCode();
            Log.d(TAG, "run: response code " + responseCode);
            StringBuilder result = new StringBuilder();

            if (responseCode == HTTP_OK || responseCode == HTTP_CREATED) {
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
            Log.d(TAG, "run: result " + result.toString());
            process(result.toString());
        } catch (Exception e) {
            e.printStackTrace();
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
    }

    private void process(String s){
        try {
            JSONObject jsonObject = new JSONObject(s);
            String rFirstName = jsonObject.getString("firstName");
            String rLastName = jsonObject.getString("lastName");
            String rUserName = jsonObject.getString("userName");
            String rDepartment = jsonObject.getString("department");
            String rStory = jsonObject.getString("story");
            String rPosition = jsonObject.getString("position");
            String rPassword = jsonObject.getString("password");
            String rRemainingPointsToReward = jsonObject.getString("remainingPointsToAward");
            String rLocation = jsonObject.getString("location");
            String rImageBytes = jsonObject.getString("imageBytes");

            Employee employee = new Employee(rFirstName, rLastName, rUserName, rDepartment, rStory, rPosition, rImageBytes);
            employee.setPassword(rPassword);
            employee.setRemainingPointsToAward(rRemainingPointsToReward);
            employee.setLocation(rLocation);
            createProfileActivity.runOnUiThread(()->createProfileActivity.profileCreated(employee));

            Log.d(TAG, "process: " + rFirstName + "" + rLastName);
        }catch(JSONException e){
            e.printStackTrace();
        }catch (NullPointerException e){
            return;
        }
    }
}
