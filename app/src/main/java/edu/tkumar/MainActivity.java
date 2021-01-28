package edu.tkumar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private SharedPreferences sharedPreferences;
    private EditText firstName, lastName, emailID, studentID, mainUserName, mainPassword;
    private String fName, lName, eID, sID, userName, password;
    private final String apiValueNull = "apiValueNull", empty = "empty", incorrectEmail = "incorrectEmail", nullValue = "nullValue";
    private FusedLocationProviderClient mFusedLocationClient;
    private static final int LOCATION_REQUEST = 111;
    private CheckBox checkBox;

    private static String locationString = "Unspecified Location";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkSharedPreferences();
        initializeFields();
        findLocation();
        enterSavedDetails();
    }

    private void enterSavedDetails(){
        sharedPreferences = getSharedPreferences("myPrefs", Context.MODE_PRIVATE);
        String myUserName = sharedPreferences.getString("loginUserName", "noUserName");
        String myPassword = sharedPreferences.getString("loginPassword", "noPassword");
        if(myUserName.equals("noUserName"))
            mainUserName.setText("");
        else mainUserName.setText(myUserName);
        if(myPassword.equals("noPassword"))
            mainPassword.setText("");
        else mainPassword.setText(myPassword);
    }

    private void initializeFields(){
        mainUserName = findViewById(R.id.mainUsernameEditText);
        mainPassword = findViewById(R.id.mainPasswordEditText);
        checkBox = findViewById(R.id.mainRememberCredentialsCheckbox);
    }

    private void findLocation(){
        mFusedLocationClient =
                LocationServices.getFusedLocationProviderClient(this);

        determineLocation();
    }

    private void determineLocation() {
        if (checkPermission()) {
            mFusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, location -> {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            locationString = getPlace(location);
                            Log.d(TAG, "determineLocation: " + locationString);
                        }
                    })
                    .addOnFailureListener(this, e -> Toast.makeText(MainActivity.this,
                            e.getMessage(), Toast.LENGTH_LONG).show());
        }
    }

    private boolean checkPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION
                    }, LOCATION_REQUEST);
            return false;
        }
        return true;
    }

    private String getPlace(Location loc) {

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses;

        try {
            addresses = geocoder.getFromLocation(loc.getLatitude(), loc.getLongitude(), 1);
            String city = addresses.get(0).getLocality();
            String state = addresses.get(0).getAdminArea();
            return city + ", " + state;

        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == LOCATION_REQUEST) {
            if (permissions[0].equals(Manifest.permission.ACCESS_FINE_LOCATION)) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    determineLocation();
                } else {
                    //textView.setText(R.string.deniedText);
                }
            }
        }
    }

    private void checkSharedPreferences(){
        //setContentView(R.layout.activity_main);
        sharedPreferences = getSharedPreferences("myPrefs", Context.MODE_PRIVATE);
        String myAPI = sharedPreferences.getString("apiValue", "noAPI");
        String checked = sharedPreferences.getString("checked", "false");
//        if(checked.equals("true"))
//            checkBox.setChecked(true);
        if(myAPI.equals("noAPI")) {
            createApiNeededDialog();
        }
        Log.d(TAG, "checkSharedPreferences: " + myAPI);
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
                    showError(empty);
                }else if(!eID.trim().matches(emailPattern)){
                    showError(incorrectEmail);
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

    private void showError(String issue){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        switch (issue) {
            case empty:
                builder.setTitle("One or More Fields not Entered");
                builder.setMessage("Please enter data in all the fields");
                break;
            case incorrectEmail:
                builder.setTitle("Incorrect Email");
                builder.setMessage("Only .edu emails can be accepted");
                break;
            case apiValueNull:
                builder.setTitle("API could not be retrieved");
                builder.setMessage("Please try again later");
                break;
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
            showError(apiValueNull);
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

    public void clearSavedApi(View v){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
        Toast.makeText(MainActivity.this, "API Cleared", Toast.LENGTH_LONG).show();
    }

    public void createProfile(View v){
        String myAPI = sharedPreferences.getString("apiValue", "noAPI");
        if(myAPI.equals("noAPI")) {
            createApiNeededDialog();
            return;
        }
        Intent intent = new Intent(this, CreateProfileActivity.class);
        intent.putExtra("location", locationString);
        intent.putExtra("apiValue", myAPI);
        startActivity(intent);
    }

    public void loginEmployee(View v){
        String myAPI = sharedPreferences.getString("apiValue", "noAPI");
        if(myAPI.equals("noAPI")) {
            createApiNeededDialog();
            return;
        }
        userName = mainUserName.getText().toString();
        password = mainPassword.getText().toString();
        if(userName.trim().isEmpty() || password.trim().isEmpty()){
            showError(empty);
            return;
        }
//        Intent intent = new Intent(this, ProfileActivity.class);
//        intent.putExtra("apiValue", myAPI);
//        startActivity(intent);
        LoginAPIRunnable loginAPIRunnable = new LoginAPIRunnable(myAPI, userName, password, this);
        new Thread(loginAPIRunnable).start();
    }

    public void setEmployeeDetails(Employee employee){
        if(checkBox.isChecked()){
            SharedPreferences.Editor prefsEditor = sharedPreferences.edit();
            prefsEditor.putString("loginUserName", employee.getUsername());
            prefsEditor.putString("loginPassword", employee.getPassword());
            prefsEditor.putString("checked", "true");
            prefsEditor.apply();
        }else if(!checkBox.isChecked()){
            SharedPreferences.Editor prefsEditor = sharedPreferences.edit();
            prefsEditor.remove("loginUserName");
            prefsEditor.remove("loginPassword");
            prefsEditor.putString("checked", "false");
            prefsEditor.apply();
        }
        String myAPI = sharedPreferences.getString("apiValue", "noAPI");
        Intent intent = new Intent(this, ProfileActivity.class);
        intent.putExtra("employeeLoggedIn", employee);
        intent.putExtra("apiValue", myAPI);
        startActivity(intent);
    }

}