package edu.tkumar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {

    private RewardAdapter rewardAdapter;
    private RecyclerView recyclerView;
    private final List<Reward> rewardList= new ArrayList<>();
    private Employee employeeLoggedIn;
    private TextView profileName, profileLocation, profilePointsAwarded, profileDepartment, profilePosition, profilePointsToAward, profileStory, profileRewardHistoryTitleTextview;
    private ImageView profileImageView;
    private Bitmap bitmap;
    private String apiValue;
    private static String locationString = "Unspecified Location";
    private SharedPreferences sharedPreferences;
    private ProgressBar progressBar;
    private static final String TAG = "ProfileActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        setUpProfileRecyclerview();

        getIntentData();

        //getSharedPreferences();

        setUpProfileRecyclerview();

        convertTextToImage();

        initializeFields();

        enterFieldData();

        updateRewardHistoryTitle();

        setUpActionBar();

        downloadRewardData();
    }

    private void getIntentData(){
        Intent intent = getIntent();

        if(intent.hasExtra("apiValue")){
            apiValue = intent.getStringExtra("apiValue");
            Log.d(TAG, "getIntentData: " + apiValue);
        }

        if(intent.hasExtra("location")){
            locationString = intent.getStringExtra("location");
        }

        employeeLoggedIn = (Employee) intent.getSerializableExtra("employeeLoggedIn");
    }

    private void getSharedPreferences(){
        sharedPreferences = getSharedPreferences("myPrefs", Context.MODE_PRIVATE);
        apiValue = sharedPreferences.getString("apiValue", "noAPI");
        if(apiValue.equals("noAPI")){
            Toast.makeText(this, "API Corrupted", Toast.LENGTH_LONG).show();
        }
    }

    private void downloadRewardData(){
        RewardDownloaderRunnable rewardDownloaderRunnable = new RewardDownloaderRunnable(this, apiValue, employeeLoggedIn.getUsername(), employeeLoggedIn.getPassword());
        new Thread(rewardDownloaderRunnable).start();
    }

    private void initializeFields(){
        profileName = findViewById(R.id.profileNameTextview);
        profileLocation = findViewById(R.id.profileLocationTextview);
        profilePointsAwarded = findViewById(R.id.profilePointsAwardedTextview);
        profileDepartment = findViewById(R.id.profileDepartmentTextview);
        profilePosition = findViewById(R.id.profilePositionTextview);
        profilePointsToAward = findViewById(R.id.profilePointsToAwardTextview);
        profileStory = findViewById(R.id.profileStrotyTextview);
        profileImageView = findViewById(R.id.profileImageview);
        profileRewardHistoryTitleTextview = findViewById(R.id.profileRewardHistoryTitleTextview);
        progressBar = findViewById(R.id.profileProgressBar);
    }

    private void enterFieldData(){
        profileName.setText(String.format("%s %s  (%s)", employeeLoggedIn.getFirstName(), employeeLoggedIn.getLastName(), employeeLoggedIn.getUsername()));
        profileLocation.setText(employeeLoggedIn.getLocation());
        profileDepartment.setText(employeeLoggedIn.getDepartment());
        profilePosition.setText(employeeLoggedIn.getPosition());
        profilePointsToAward.setText(employeeLoggedIn.getRemainingPointsToAward());
        profileStory.setText(employeeLoggedIn.getStory());
        profileImageView.setImageBitmap(bitmap);
    }
    private void setUpProfileRecyclerview(){
        recyclerView = findViewById(R.id.profileRecyclerView);
        rewardAdapter = new RewardAdapter(rewardList, this);
        recyclerView.setAdapter(rewardAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void convertTextToImage(){
        String imageString64 = employeeLoggedIn.getImageBytes();
        if (imageString64 == null) return;

        byte[] imageBytes = Base64.decode(imageString64, Base64.DEFAULT);
        bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
        //profileImageView.setImageBitmap(bitmap);
    }

    private void updateRewardHistoryTitle(){
        profileRewardHistoryTitleTextview.setText(String.format(Locale.getDefault(),"Reward History (%d):", rewardList.size()));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.profile_activity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemID = item.getItemId();
        if(itemID == R.id.delete_menu){
            confirmDelete();
        }else if(itemID == R.id.edit_menu){
            openEditActivity();
        }else if(itemID == R.id.show_leaderboard_menu) {
            setLeaderboardData();
        }
        return super.onOptionsItemSelected(item);
    }

    private void confirmDelete(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete Profile?");
        builder.setPositiveButton("OK", (dialog, which) -> deleteProfile());
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void deleteProfile(){
        progressBar.setVisibility(View.VISIBLE);
        DeleteProfileAPIRunnable deleteProfileAPIRunnable = new DeleteProfileAPIRunnable(this, employeeLoggedIn.getUsername(), apiValue);
        new Thread(deleteProfileAPIRunnable).start();
    }

    private void openEditActivity(){
        Intent intent = new Intent(this, EditProfileActivity.class);
        intent.putExtra("employeeLoggedIn", employeeLoggedIn);
        intent.putExtra("apiValue", apiValue);
        intent.putExtra("location", locationString);
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
    }

    public void setLeaderboardData(){
        Intent intent = new Intent(this, LeaderboardActivity.class);
        intent.putExtra("apiValue", apiValue);
        intent.putExtra("employeeLoggedIn", employeeLoggedIn);
        startActivity(intent);
    }

    public void updateRecyclerView(Reward reward){
        rewardList.add(reward);
        rewardAdapter.notifyDataSetChanged();
    }

    public void clearRewardList(){
        rewardList.clear();
    }

    public void setPointsAwarded(int pointsAwarded){
        String pA = Integer.toString(pointsAwarded);
        profilePointsAwarded.setText(pA);
    }

    private void setUpActionBar(){
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.icon);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setTitle("Your Profile");
    }

    public void showError(String s){
        progressBar.setVisibility(View.INVISIBLE);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Action Failed");
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

    public void profileDeleted(String s){
        progressBar.setVisibility(View.INVISIBLE);
        Toast.makeText(this, s, Toast.LENGTH_LONG).show();
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("deleted", "1");
        startActivity(intent);

    }
}