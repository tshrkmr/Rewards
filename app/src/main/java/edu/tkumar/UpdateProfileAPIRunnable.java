package edu.tkumar;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.util.Base64;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static java.net.HttpURLConnection.HTTP_OK;

public class UpdateProfileAPIRunnable implements Runnable{

    private final EditProfileActivity editProfileActivity;
    private final String firstName, lastName, userName, department, story, position, password, location, apiValue, imageBytes;
    private static final String TAG = "UpdateProfileAPIRunnable";

    public UpdateProfileAPIRunnable(EditProfileActivity editProfileActivity, String firstName, String lastName, String userName, String department, String story,
                                    String position, String password, String location, String imageBytes, String apiValue) {
        this.editProfileActivity = editProfileActivity;
        this.firstName = firstName;
        this.lastName = lastName;
        this.userName = userName;
        this.department = department;
        this.story = story;
        this.position = position;
        this.password = password;
        this.location = location;
        this.imageBytes = imageBytes;
        //this.bitmap = bitmap;
        this.apiValue = apiValue;


    }

    @Override
    public void run() {
        HttpURLConnection connection = null;
        BufferedReader reader = null;

        try {
            String urlString = "http://christopherhield.org/api/Profile/UpdateProfile";

            Uri.Builder buildURL = Uri.parse(urlString).buildUpon();
            buildURL.appendQueryParameter("firstName", firstName);
            buildURL.appendQueryParameter("lastName", lastName);
            buildURL.appendQueryParameter("userName", userName);
            buildURL.appendQueryParameter("department", department);
            buildURL.appendQueryParameter("story", story);
            buildURL.appendQueryParameter("position", position);
            buildURL.appendQueryParameter("password", password);
            buildURL.appendQueryParameter("location", location);

            String urlToUse = buildURL.build().toString();
            URL url = new URL(urlToUse);

            Log.d(TAG, "run: " + urlToUse);

            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("PUT");
            connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestProperty("ApiKey", apiValue);
            connection.connect();

            OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream());
            out.write(imageBytes);
            out.close();

            int responseCode = connection.getResponseCode();
            Log.d(TAG, "run: response code" + responseCode);
            StringBuilder result = new StringBuilder();

            boolean error;
            if (responseCode == HTTP_OK) {
                error = false;
                reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

                String line;
                while (null != (line = reader.readLine())) {
                    result.append(line).append("\n");
                }
            } else {
                error = true;
                reader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));

                String line;
                while (null != (line = reader.readLine())) {
                    result.append(line).append("\n");
                }
            }
            Log.d(TAG, "run: result " + result.toString());
            process(result.toString(), error);
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

    private void process(String s, boolean error){
        if (error) {
            editProfileActivity.runOnUiThread(()->editProfileActivity.showError(s));
            return;
        }
        try {
            String rFirstName = "no value returned";
            String rLastName = "no value returned";
            String rUserName = "no value returned";
            String rDepartment = "no value returned";
            String rStory = "no value returned";
            String rPosition = "no value returned";
            String rPassword = "no value returned";
            String rRemainingPointsToReward = "no value returned";
            String rLocation = "no value returned";
            String rImageBytes = "no value returned";
            JSONObject jsonObject = new JSONObject(s);
            if(jsonObject.has("firstName"))
                rFirstName = jsonObject.getString("firstName");
            if(jsonObject.has("lastName"))
                rLastName = jsonObject.getString("lastName");
            if(jsonObject.has("userName"))
                rUserName = jsonObject.getString("userName");
            if(jsonObject.has("department"))
                rDepartment = jsonObject.getString("department");
            if(jsonObject.has("story"))
                rStory = jsonObject.getString("story");
            if(jsonObject.has("position"))
                rPosition = jsonObject.getString("position");
            if(jsonObject.has("password"))
                rPassword = jsonObject.getString("password");
            if(jsonObject.has("remainingPointsToAward"))
                rRemainingPointsToReward = jsonObject.getString("remainingPointsToAward");
            if(jsonObject.has("location"))
                rLocation = jsonObject.getString("location");
            if(jsonObject.has("imageBytes"))
                rImageBytes = jsonObject.getString("imageBytes");

            Employee employee = new Employee(rFirstName, rLastName, rUserName, rDepartment, rStory, rPosition, rImageBytes);
            employee.setPassword(rPassword);
            employee.setRemainingPointsToAward(rRemainingPointsToReward);
            employee.setLocation(rLocation);

            editProfileActivity.runOnUiThread(()->editProfileActivity.profileUpDated(employee));

            //Log.d(TAG, "process: " + rFirstName + "" + rLastName);
        }catch(JSONException e){
            e.printStackTrace();
        }catch (NullPointerException e){
            return;
        }
    }
}
