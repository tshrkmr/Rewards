package edu.tkumar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
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
    private final List<Reward> rewardList= new ArrayList<>();
    private Employee employee;
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

        convertTextToImage();

        initializeFields();

        enterFieldData();

        updateRewardHistoryTitle();

        setUpActionBar();

        setUpProfileRecyclerview();
    }

    private void getIntentData(){
        Intent intent = getIntent();

        if(intent.hasExtra("apiValue")){
            apiValue = intent.getStringExtra("apiValue");
            Log.d(TAG, "getIntentData: " + apiValue);
        }

        employee = (Employee) intent.getSerializableExtra("employee");
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
        profileImageView.setImageBitmap(bitmap);
        profileRewardHistoryTitleTextview = findViewById(R.id.profileRewardHistoryTitleTextview);
    }

    private void enterFieldData(){
        profileName.setText(String.format("%s %s  (%s)", employee.getFirstName(), employee.getLastName(), employee.getUsername()));
        profileLocation.setText(employee.getLocation());
        //profilePointsAwarded.setText();
        profileDepartment.setText(employee.getDepartment());
        profilePosition.setText(employee.getPosition());
        profilePointsToAward.setText(employee.getRemainingPointsToAward());
        profileStory.setText(employee.getStory());

    }
    private void setUpProfileRecyclerview(){
        recyclerView = findViewById(R.id.profileRecyclerView);
        rewardAdapter = new RewardAdapter(rewardList, this);
        recyclerView.setAdapter(rewardAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        rewardAdapter.notifyDataSetChanged();
    }

    private void convertTextToImage(){
        TextToImage textToImage = new TextToImage(employee.getImageBytes());
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
        }
        return super.onOptionsItemSelected(item);
    }

    private void openEditActivity(){
        Intent intent = new Intent(this, EditProfileActivity.class);
        intent.putExtra("employee", employee);
        intent.putExtra("apiValue", apiValue);
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {

    }

    private void setUpActionBar(){
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.icon);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setTitle("Your Profile");
    }
}