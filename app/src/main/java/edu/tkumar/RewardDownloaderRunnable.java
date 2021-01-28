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

public class RewardDownloaderRunnable implements Runnable{
    private final String apiValue;
    private final String userName;
    private final String password;
    private final ProfileActivity profileActivity;
    private static final String TAG = "LoginAPIRunnable";

    public RewardDownloaderRunnable(ProfileActivity profileActivity, String apiValue, String userName, String password) {
        this.apiValue = apiValue;
        this.userName = userName;
        this.password = password;
        this.profileActivity = profileActivity;
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
            Log.d(TAG, "run: result rewardDownload  " + result.toString());
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
            profileActivity.runOnUiThread(()->profileActivity.showError(s));
            return;
        }
        try {
            profileActivity.clearRewardList();
            JSONObject jsonObject = new JSONObject(s);

            JSONArray jsonArray = jsonObject.getJSONArray("rewardRecordViews");

            int length =jsonArray.length();
            int pointsAwarded = 0;
            for(int i =0; i<length; i++) {
                String rGiverName= "no value returned";
                String rAmount = "no value returned";
                String rNote = "no value returned";
                String rAwardDate = "no value returned";
                JSONObject details = jsonArray.getJSONObject(i);
                if(details.has("giverName"))
                    rGiverName = details.getString("giverName");
                if(details.has("amount"))
                    rAmount = details.getString("amount");
                if(details.has("note"))
                    rNote = details.getString("note");
                if(details.has("awardDate"))
                    rAwardDate= details.getString("awardDate");
                Reward reward = new Reward(rGiverName, rAmount, rNote, rAwardDate);
                pointsAwarded += Integer.parseInt(rAmount);

                profileActivity.runOnUiThread(()->profileActivity.updateRecyclerView(reward));
            }
            int finalPointsAwarded = pointsAwarded;
            profileActivity.runOnUiThread(()->profileActivity.setPointsAwarded(finalPointsAwarded));
            //Log.d(TAG, "process: " + rFirstName + "" + rLastName);
        }catch(JSONException e){
            e.printStackTrace();
        }catch (NullPointerException e){
            return;
        }
    }
}
