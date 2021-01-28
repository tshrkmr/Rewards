package edu.tkumar;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class LeaderboardActivity extends AppCompatActivity implements View.OnClickListener {

    private EmployeeAdapter employeeAdapter;
    private RecyclerView recyclerView;
    private final List<Employee> employeeList = new ArrayList<>();
    private String apiValue;
    private Employee employeeLoggedIn;
    private ProgressBar progressBar;
    private static final String TAG = "LeaderboardActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);

        setUpLeaderboardRecyclerView();

        getIntentData();

        setUpActionBar();

        downloadData();
    }

    private void downloadData(){
        progressBar.setVisibility(View.VISIBLE);
        GetAllProfilesAPIRunnable getAllProfilesAPIRunnable = new GetAllProfilesAPIRunnable(this, apiValue);
        new Thread(getAllProfilesAPIRunnable).start();
    }

    private void getIntentData(){
        Intent intent = getIntent();

        if(intent.hasExtra("apiValue")){
            apiValue = intent.getStringExtra("apiValue");
            Log.d(TAG, "getIntentData: " + apiValue);
        }

        employeeLoggedIn = (Employee) intent.getSerializableExtra("employeeLoggedIn");

    }

    public void clearEmployeeList(){
        employeeList.clear();
    }

    public void updateEmployeeList(Employee employee){
        progressBar.setVisibility(View.INVISIBLE);
        employeeList.add(employee);
        //Collections.sort(employeeList);
        employeeAdapter.notifyDataSetChanged();
    }

    private void setUpLeaderboardRecyclerView(){
        recyclerView = findViewById(R.id.leaderboardRecyclerView);
        employeeAdapter = new EmployeeAdapter(employeeList, this);
        recyclerView.setAdapter(employeeAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        progressBar = findViewById(R.id.leaderboardProgressBar);
        //Log.d(TAG, "setUpR   ecyclerView: employee updated");
    }

    @Override
    public void onClick(View v) {
        int position = recyclerView.getChildAdapterPosition(v);
        Intent intent = new Intent(this, RewardActivity.class);
        intent.putExtra("employeeSelected", employeeList.get(position));
        intent.putExtra("employeeLoggedIn", employeeLoggedIn);
        intent.putExtra("apiValue", apiValue);
        startActivity(intent);
    }

    private void setUpActionBar(){
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.icon);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setTitle("Leaderboard");
    }

    public void showError(String s){
        progressBar.setVisibility(View.INVISIBLE);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Download Failed");
        builder.setMessage(s);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

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
}