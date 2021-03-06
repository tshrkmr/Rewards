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

public class GetStudentApiKeyRunnable implements  Runnable{

    private final MainActivity mainActivity;
    private final String firstName, lastName, emailID, studentID;
    private static final String base_url = "http://christopherhield.org/api/Profile/GetStudentApiKey";
    private boolean error = false;
    private static final String TAG = "GetStudentApiKeyRunnable";


    public GetStudentApiKeyRunnable(MainActivity mainActivity, String firstName, String lastName, String emailID, String studentID) {
        this.mainActivity = mainActivity;
        this.firstName = firstName;
        this.lastName = lastName;
        this.emailID = emailID;
        this.studentID = studentID;
    }

    @Override
    public void run() {

        HttpURLConnection connection = null;
        BufferedReader reader = null;

        Uri.Builder buildURL = Uri.parse(base_url).buildUpon();
        buildURL.appendQueryParameter("firstName", firstName);
        buildURL.appendQueryParameter("lastName", lastName);
        buildURL.appendQueryParameter("studentId", studentID);
        buildURL.appendQueryParameter("email", emailID);
        String urlToUse = buildURL.build().toString();
        //Log.d(TAG, "run: " + urlToUse);

        StringBuilder result = new StringBuilder();

        try {
            URL url = new URL(urlToUse);

            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            connection.setRequestProperty("Accept", "application/json");
            connection.connect();

            int responseCode = connection.getResponseCode();

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
            //Log.d(TAG, "run: " + result.toString());
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
        process(result.toString(), error);
    }

    private void process(String s, boolean error){
        if (error) {
            mainActivity.runOnUiThread(()->mainActivity.showError(s));
            return;
        }
        try {
            String email = "no value returned";
            String apiKey = "no value returned";
            JSONObject jsonObject = new JSONObject(s);
            if(jsonObject.has("email"))
                email = jsonObject.getString("email");
            if(jsonObject.has("apiKey"))
                apiKey = jsonObject.getString("apiKey");
            String apiValue = apiKey;
            mainActivity.runOnUiThread(()->mainActivity.saveApiKey(apiValue));

            Log.d(TAG, "process: " + email + "" + apiKey);
        }catch(JSONException e){
            e.printStackTrace();
        }catch (NullPointerException e){
            return;
        }
    }
}
