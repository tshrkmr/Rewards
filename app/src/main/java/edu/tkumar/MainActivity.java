package edu.tkumar;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private SharedPreferences sharedPreferences;
    private EditText firstName, lastName, emailID, studentID;
    private String fName, lName, eID, sID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkSharedPreferences();
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
                if(fName.isEmpty() || lName.isEmpty() || eID.isEmpty() || sID.isEmpty()){
                    apiDataIncorrect("empty");
                }else if(!eID.trim().matches(emailPattern)){
                    apiDataIncorrect("emailID");
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
        if(issue.equals("empty")) {
            builder.setTitle("One or More Fields not Entered");
            builder.setMessage("Please enter data in all the fields");
        }else {
        builder.setTitle("Incorrect Email");
        builder.setMessage("Only .edu emails can be accepted");
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
        SharedPreferences.Editor prefsEditor = sharedPreferences.edit();
        prefsEditor.putString("apiValue", api);
        prefsEditor.apply();
        if(api!=null){
            createApiStoredDialog(api);
        }else
            createApiStoredDialog("API could not be retrieved");
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
        startActivity(intent);
    }
}