package edu.tkumar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {

    private RewardAdapter rewardAdapter;
    private RecyclerView recyclerView;
    private List<Reward> rewardList= new ArrayList<>();
    private Employee employeeLoggedIn;
    private TextView profileName, profileLocation, profilePointsAwarded, profileDepartment, profilePosition, profilePointsToAward, profileStory, profileRewardHistoryTitleTextview;
    private ImageView profileImageView;
    private Bitmap bitmap;
    private String apiValue;
    private static final String TAG = "ProfileActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        setUpProfileRecyclerview();

        getIntentData();

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

        employeeLoggedIn = (Employee) intent.getSerializableExtra("employeeLoggedIn");
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
        TextToImage textToImage = new TextToImage(employeeLoggedIn.getImageBytes());
        bitmap = textToImage.textToImage();
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

        }else if(itemID == R.id.edit_menu){
            openEditActivity();
        }else if(itemID == R.id.show_leaderboard_menu) {
            setLeaderboardData();
        }
        return super.onOptionsItemSelected(item);
    }

    private void openEditActivity(){
        Intent intent = new Intent(this, EditProfileActivity.class);
        intent.putExtra("employeeLoggedIn", employeeLoggedIn);
        intent.putExtra("apiValue", apiValue);
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
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.icon);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setTitle("Your Profile");
    }
}