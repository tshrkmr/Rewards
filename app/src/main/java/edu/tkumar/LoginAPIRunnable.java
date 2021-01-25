package edu.tkumar;

import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static java.net.HttpURLConnection.HTTP_OK;

public class LoginAPIRunnable implements Runnable{

    private String apiValue, userName, password;
    private MainActivity mainActivity;
    private static final String TAG = "LoginAPIRunnable";
    private  List<Reward> rewardList= new ArrayList<>();

    public LoginAPIRunnable(String apiValue, String userName, String password, MainActivity mainActivity) {
        this.apiValue = apiValue;
        this.userName = userName;
        this.password = password;
        this.mainActivity = mainActivity;
    }

    @Override
    public void run() {
        HttpURLConnection connection = null;
        BufferedReader reader = null;

        try {
            String urlString = "http://christopherhield.org/api/Profile/Login";

            Uri.Builder buildURL = Uri.parse(urlString).buildUpon();
            buildURL.appendQueryParameter("userName", userName);
            buildURL.appendQueryParameter("password", password);

            String urlToUse = buildURL.build().toString();
            URL url = new URL(urlToUse);

            Log.d(TAG, "run: " + urlToUse + userName + password);

            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestProperty("ApiKey", apiValue);
            connection.connect();


            int responseCode = connection.getResponseCode();
            Log.d(TAG, "run: response code" + responseCode);
            StringBuilder result = new StringBuilder();

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

            Employee employee = new Employee(rFirstName, rLastName, rUserName, rDepartment, rStory, rPosition, rPassword, rRemainingPointsToReward, rLocation, rImageBytes);

            JSONArray jsonArray = jsonObject.getJSONArray("rewardRecordViews");

            int length =jsonArray.length();

            for(int i =0; i<length; i++) {
                JSONObject details = jsonArray.getJSONObject(i);
                String rGivenName = details.getString("givenName");
                String rAmount = details.getString("amount");
                String rNote = details.getString("note");
                String rAwardDate= details.getString("awardDate");
                Reward reward = new Reward(rGivenName, rAmount, rNote, rAwardDate);
                rewardList.add(reward);
            }
            employee.setRewardList(rewardList);
            mainActivity.runOnUiThread(()->mainActivity.getEmployeeDetails(employee));

            //Log.d(TAG, "process: " + rFirstName + "" + rLastName);
        }catch(JSONException e){
            e.printStackTrace();
        }catch (NullPointerException e){
            return;
        }
    }
}
