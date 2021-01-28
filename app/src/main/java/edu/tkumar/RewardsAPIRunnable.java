package edu.tkumar;

import android.net.Uri;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import static java.net.HttpURLConnection.HTTP_CREATED;
import static java.net.HttpURLConnection.HTTP_OK;

public class RewardsAPIRunnable implements Runnable{

    private RewardActivity rewardActivity;
    private final String apiValue, receiverUser, giverUser, giverName, amount, note;
    private static final String TAG = "RewardsAPIRunnable";

    public RewardsAPIRunnable(RewardActivity rewardActivity, String apiValue, String receiverUser, String giverUser, String giverName, String amount, String note) {
        this.rewardActivity = rewardActivity;
        this.apiValue = apiValue;
        this.receiverUser = receiverUser;
        this.giverUser = giverUser;
        this.giverName = giverName;
        this.amount = amount;
        this.note = note;
    }

    @Override
    public void run() {
        HttpURLConnection connection = null;
        BufferedReader reader = null;

        try {
            String urlString = "http://christopherhield.org/api/Rewards/AddRewardRecord";

            Uri.Builder buildURL = Uri.parse(urlString).buildUpon();
            buildURL.appendQueryParameter("receiverUser", receiverUser);
            buildURL.appendQueryParameter("giverUser", giverUser);
            buildURL.appendQueryParameter("giverName", giverName);
            buildURL.appendQueryParameter("amount", amount);
            buildURL.appendQueryParameter("note", note);

            String urlToUse = buildURL.build().toString();
            URL url = new URL(urlToUse);

            Log.d(TAG, "run: " + urlToUse);

            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestProperty("ApiKey", apiValue);
            connection.connect();

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
            Log.d(TAG, "run: reward result " + result.toString());
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
            rewardActivity.runOnUiThread(()->rewardActivity.updateLeaderboardActivity());

            //Log.d(TAG, "process: " + rFirstName + "" + rLastName);
        }catch (NullPointerException e){
            return;
        }
    }
}
