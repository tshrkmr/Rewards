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

public class DeleteProfileAPIRunnable implements Runnable{

    private final String userName;
    private final String apiValue;
    private static final String TAG = "DeleteProfileAPIRunnabl";
    private final ProfileActivity profileActivity;

    public DeleteProfileAPIRunnable(ProfileActivity profileActivity, String userName, String apiValue) {
        this.profileActivity = profileActivity;
        this.userName = userName;
        this.apiValue = apiValue;
    }

    @Override
    public void run() {

        HttpURLConnection connection = null;
        BufferedReader reader = null;

        try {
            String urlString = "http://christopherhield.org/api/Profile/DeleteProfile";

            Uri.Builder buildURL = Uri.parse(urlString).buildUpon();
            buildURL.appendQueryParameter("username", userName);
            String urlToUse = buildURL.build().toString();
            URL url = new URL(urlToUse);

            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("DELETE");
            connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestProperty("ApiKey", apiValue);
            connection.connect();

            int responseCode = connection.getResponseCode();

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
            Log.d(TAG, "run: " + result.toString());
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
    private void process(String s, boolean error) {
        if (error) {
            profileActivity.runOnUiThread(() -> profileActivity.showError(s));
            return;
        }
        try {
                profileActivity.runOnUiThread(()->profileActivity.profileDeleted(s));
            } catch (NullPointerException e) {
                return;
            }
    }
}

