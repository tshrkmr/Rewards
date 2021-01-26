package edu.tkumar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class LeaderboardActivity extends AppCompatActivity implements View.OnClickListener {

    private EmployeeAdapter employeeAdapter;
    private RecyclerView recyclerView;
    private List<Employee> employeeList = new ArrayList<>();
    private String apiValue;
    private static final String TAG = "LeaderboardActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);


//        for(int i = 0;i<10;i++){
//            Employee employee = new Employee("employee", "shipping", "CEO", "1000", "HI");
//            employeeList.add(employee);
//            //Log.d(TAG, "onCreate: employee updated");
//        }

        setUpLeaderboardRecyclerView();

        getIntentData();

        GetAllProfilesAPIRunnable getAllProfilesAPIRunnable = new GetAllProfilesAPIRunnable(this, apiValue);
        new Thread(getAllProfilesAPIRunnable).start();

    }

    private void getIntentData(){
        Intent intent = getIntent();

        if(intent.hasExtra("apiValue")){
            apiValue = intent.getStringExtra("apiValue");
            Log.d(TAG, "getIntentData: " + apiValue);
        }

    }

    public void clearEmployeeList(){
        employeeList.clear();
    }

    public void updateEmployeeList(Employee employee){
        employeeList.add(employee);
        employeeAdapter.notifyDataSetChanged();
    }

    private void setUpLeaderboardRecyclerView(){
        recyclerView = findViewById(R.id.leaderboardRecyclerView);
        employeeAdapter = new EmployeeAdapter(employeeList, this);
        recyclerView.setAdapter(employeeAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        //Log.d(TAG, "setUpRecyclerView: employee updated");
    }

    @Override
    public void onClick(View v) {

    }

    private void setUpActionBar(){
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.icon);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setTitle("Leaderboard");
    }
}