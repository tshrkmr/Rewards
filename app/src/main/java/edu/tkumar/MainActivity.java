package edu.tkumar;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private SharedPreferences sharedPreferences;
    private EditText firstName, lastName, emailID, studentID;
    private String fName, lName, eID, sID, location;
    private LocationManager locationManager;
    private Criteria criteria;
    private static final int MY_LOCATION_REQUEST_CODE_ID = 111;
    private Location currentLocation = null;
    private final String apiValueNull = "apiValueNull", empty = "empty", incorrectEmail = "incorrectEmail", nullValue = "nullValue";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkSharedPreferences();
        findLocation();
    }

    private void findLocation(){
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        criteria = new Criteria();

        // use gps for location
        criteria.setPowerRequirement(Criteria.POWER_HIGH);
        criteria.setAccuracy(Criteria.ACCURACY_FINE);

        // use network for location
        //criteria.setPowerRequirement(Criteria.POWER_LOW);
        //criteria.setAccuracy(Criteria.ACCURACY_MEDIUM);

        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setSpeedRequired(false);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION
                    },
                    MY_LOCATION_REQUEST_CODE_ID);
        } else {
            setLocation();
        }
    }

    @SuppressLint("MissingPermission")
    private void setLocation() {

        String bestProvider = locationManager.getBestProvider(criteria, true);

        if (bestProvider != null) {
            currentLocation = locationManager.getLastKnownLocation(bestProvider);
        }
        if (currentLocation != null) {
            findCitySate(currentLocation.getLatitude(), currentLocation.getLongitude());
            Log.d(TAG, "setLocation: " + currentLocation.getLatitude() + " " + currentLocation.getLongitude());
        } else {
            Log.d(TAG, "setLocation: " + "No Location");
        }
    }

    public void findCitySate(double latitude, double longitude){
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            String city = addresses.get(0).getLocality();
            String state = addresses.get(0).getAdminArea();
            location = city.concat(", ").concat(state);
            //Log.d(TAG, "findPostalCode: " + postalCode + "  " + city);
            Toast.makeText(MainActivity.this, location ,Toast.LENGTH_LONG ).show();

        }catch (IOException e){
            Log.d(TAG, "findPostalCode: " + " address not found");
        }
    }

    private void checkSharedPreferences(){
        sharedPreferences = getSharedPreferences("myPrefs", Context.MODE_PRIVATE);
        String myAPI = sharedPreferences.getString("apiValue", "noAPI");
        if(myAPI == ("noAPI")) {
            createApiNeededDialog();
        }
    }

    private void createApiNeededDialog(){
        LayoutInflater inflater = LayoutInflater.from(this);
        final View view = inflater.inflate(R.layout.dialog_api_key_needed, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("You need to request an API key");
        builder.setTitle("API Key Needed");
        builder.setIcon(R.drawable.logo);
        builder.setView(view);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.edu";
                firstName = view.findViewById(R.id.apiFirstName);
                lastName = view.findViewById(R.id.apiLastName);
                emailID = view.findViewById(R.id.apiEmail);
                studentID = view.findViewById(R.id.apiStudentID);
                fName = firstName.getText().toString();
                lName = lastName.getText().toString();
                eID = emailID.getText().toString();
                sID = studentID.getText().toString();
                if(fName.trim().isEmpty() || lName.trim().isEmpty() || eID.trim().isEmpty() || sID.trim().isEmpty()){
                    apiDataIncorrect(empty);
                }else if(fName == null || lName == null || eID == null || sID == null){
                    apiDataIncorrect(nullValue);
                } else if(!eID.trim().matches(emailPattern)){
                    apiDataIncorrect(incorrectEmail);
                }else{
                    getApiKey(fName, lName, eID, sID);
                }
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void apiDataIncorrect(String issue){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        if(issue.equals(empty)) {
            builder.setTitle("One or More Fields not Entered");
            builder.setMessage("Please enter data in all the fields");
        }else if(issue.equals(incorrectEmail)){
            builder.setTitle("Incorrect Email");
            builder.setMessage("Only .edu emails can be accepted");
        }else if(issue.equals(apiValueNull)){
            builder.setTitle("API could not be retrieved");
            builder.setMessage("Please try again later");
        }else if(issue.equals(nullValue)) {
            builder.setTitle("One or More Fields Incorrect");
            builder.setMessage("Null values cannot be accepted");
        }
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                createApiNeededDialog();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void getApiKey(String firstName, String lastName, String emailID, String studentID){
        GetStudentApiKeyRunnable getStudentApiKeyRunnable = new GetStudentApiKeyRunnable(this, firstName, lastName, emailID, studentID);
        new Thread(getStudentApiKeyRunnable).start();
    }

    public void saveApiKey(String api){
        if(api!=null){
            SharedPreferences.Editor prefsEditor = sharedPreferences.edit();
            prefsEditor.putString("apiValue", api);
            prefsEditor.apply();
            createApiStoredDialog(api);
        }else {
            apiDataIncorrect(apiValueNull);
        }

    }

    public void createApiStoredDialog(String api){
        LayoutInflater inflater = LayoutInflater.from(this);
        final View view = inflater.inflate(R.layout.dialog_api_key_stored, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("API Key Received and Stored");
        builder.setIcon(R.drawable.logo);
        builder.setView(view);
        TextView name = view.findViewById(R.id.nameTextView);
        TextView studentID = view.findViewById(R.id.studentIdTextView);
        TextView email = view.findViewById(R.id.emailTextView);
        TextView apiKey = view.findViewById(R.id.apiKeyTextView);
        name.setText(String.format("Name: %s %s", fName, lName));
        studentID.setText(String.format("Student ID: %s", sID));
        email.setText(String.format("Email: %s", eID));
        apiKey.setText(String.format("API Key: %s", api));
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

//    public void clearSavedApi(View v){
//        SharedPreferences.Editor editor = sharedPreferences.edit();
//        editor.clear();
//        editor.apply();
//        Toast.makeText(MainActivity.this, "API Cleared", Toast.LENGTH_LONG).show();
//    }

    public void createProfile(View v){
        String myAPI = sharedPreferences.getString("apiValue", "noAPI");
        if(myAPI == ("noAPI")) {
            createApiNeededDialog();
            return;
        }
        Intent intent = new Intent(this, CreateProfileActivity.class);
        intent.putExtra("location", location);
        intent.putExtra("apiValue", myAPI);
        startActivity(intent);
    }
}