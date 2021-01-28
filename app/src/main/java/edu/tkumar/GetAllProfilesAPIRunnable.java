package edu.tkumar;

import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import static java.net.HttpURLConnection.HTTP_CREATED;
import static java.net.HttpURLConnection.HTTP_OK;

public class GetAllProfilesAPIRunnable implements Runnable{

    private final LeaderboardActivity leaderboardActivity;
    private final String apiValue;
    private static final String TAG = "GetAllProfilesAPIRunnable";

    public GetAllProfilesAPIRunnable(LeaderboardActivity leaderboardActivity, String apiValue) {
        this.leaderboardActivity = leaderboardActivity;
        this.apiValue = apiValue;
    }

    @Override
    public void run() {
        HttpURLConnection connection = null;
        BufferedReader reader = null;

        try {
            String urlString = "http://christopherhield.org/api/Profile/GetAllProfiles";

            Uri.Builder buildURL = Uri.parse(urlString).buildUpon();

            String urlToUse = buildURL.build().toString();
            URL url = new URL(urlToUse);

            Log.d(TAG, "run: " + urlToUse);

            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestProperty("ApiKey", apiValue);
            connection.connect();


            int responseCode = connection.getResponseCode();
            Log.d(TAG, "run: response code" + responseCode);
            StringBuilder result = new StringBuilder();

            boolean error;
            if (responseCode == HTTP_OK || responseCode == HTTP_CREATED) {
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
            leaderboardActivity.runOnUiThread(() -> leaderboardActivity.showError(s));
            return;
        }
        try {
            leaderboardActivity.clearEmployeeList();

            JSONArray jEmployeeArray = new JSONArray(s);
            int employeeLength = jEmployeeArray.length();
            //Log.d(TAG, "process: employeeLength" + employeeLength);
            for(int i = 0; i<employeeLength; i++) {
                String rFirstName = "no value returned";
                String rLastName = "no value returned";
                String rUserName = "no value returned";
                String rDepartment = "no value returned";
                String rStory = "no value returned";
                String rPosition = "no value returned";
                String rImageBytes = "no value returned";
                JSONObject employeeDetails = jEmployeeArray.getJSONObject(i);
                if(employeeDetails.has("firstName"))
                    rFirstName = employeeDetails.getString("firstName");
                if(employeeDetails.has("lastName"))
                    rLastName = employeeDetails.getString("lastName");
                if(employeeDetails.has("userName"))
                    rUserName = employeeDetails.getString("userName");
                if(employeeDetails.has("department"))
                    rDepartment = employeeDetails.getString("department");
                if(employeeDetails.has("story"))
                    rStory = employeeDetails.getString("story");
                if(employeeDetails.has("position"))
                    rPosition = employeeDetails.getString("position");
                if(employeeDetails.has("imageBytes"))
                    rImageBytes = employeeDetails.getString("imageBytes");
                Employee employee = new Employee(rFirstName, rLastName, rUserName, rDepartment, rStory, rPosition, rImageBytes);

                JSONArray jsonRewardArray = employeeDetails.getJSONArray("rewardRecordViews");

                int length =jsonRewardArray.length();
                int pointsAwarded = 0;
                for(int j =0; j<length; j++) {
                    String rAmount = "0";
                    JSONObject rewardDetails = jsonRewardArray.getJSONObject(j);
                    if(rewardDetails.has("amount"))
                        rAmount = rewardDetails.getString("amount");
                    pointsAwarded += Integer.parseInt(rAmount);
                }
                int finalPointsAwarded = pointsAwarded;
                employee.setPointsAwarded(finalPointsAwarded);
                leaderboardActivity.runOnUiThread(()->leaderboardActivity.updateEmployeeList(employee));
            }
            //Log.d(TAG, "process: " + rFirstName + "" + rLastName);
        }catch(JSONException e){
            e.printStackTrace();
        }catch (NullPointerException e){
            return;
        }
    }
}
