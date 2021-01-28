package edu.tkumar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.util.Locale;

public class RewardActivity extends AppCompatActivity {

    private static final int MAX_LEN = 80;
    private TextView rewardName, rewardPointsAwarded, rewardDepartment, rewardPosition, rewardStory, rewardCommentTitle;
    private EditText rewardPointsToSend, rewardComment;
    private String apiValue, pointsToSend, comment;
    private String empty = "empty", noImage = "noImage";
    private static final String TAG = "RewardActivity";
    private Employee employeeLoggedIn, employeeSelected;
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reward);

        getIntentData();

        initializeFields();

        enterFieldData();

        setUpStoryEditText();

        setUpActionBar();

        textToImage();
    }

    private void getIntentData(){
        Intent intent = getIntent();

        if(intent.hasExtra("apiValue")){
            apiValue = intent.getStringExtra("apiValue");
            Log.d(TAG, "getIntentData: " + apiValue);
        }

        employeeSelected = (Employee) intent.getSerializableExtra("employeeSelected");
        employeeLoggedIn = (Employee) intent.getSerializableExtra("employeeLoggedIn");
    }

    private void initializeFields(){
       rewardName = findViewById(R.id.rewardNameTextview);
       rewardPointsAwarded = findViewById(R.id.rewardPointsAwardedTextview);
       rewardDepartment = findViewById(R.id.rewardDepartmentTextview);
       rewardPosition = findViewById(R.id.rewardPositionTextview);
       rewardStory = findViewById(R.id.rewardStoryTextview);
       rewardCommentTitle = findViewById(R.id.rewardCommentTitleTextview);
       rewardPointsToSend = findViewById(R.id.rewardPointsToSendEditText);
       rewardComment = findViewById(R.id.rewardCommentEditText);
       imageView = findViewById(R.id.rewardImageView);
        //rewardPointsAwardedTitle, rewardDepartmentTitle, rewardPositionTitle;
    }

    private void enterFieldData(){
        rewardName.setText(String.format("%s, %s", employeeSelected.getLastName(), employeeSelected.getFirstName()));
        rewardPointsAwarded.setText(employeeSelected.getRemainingPointsToAward());
        rewardDepartment.setText(employeeSelected.getDepartment());
        rewardPosition.setText(employeeSelected.getPosition());
        rewardStory.setText(employeeSelected.getStory());
    }

    private void setUpStoryEditText() {
        rewardComment.setFilters(new InputFilter[] {
                new InputFilter.LengthFilter(MAX_LEN) // Specifies a max text length
        });

        rewardComment.addTextChangedListener(
                new TextWatcher() {

                    @Override
                    public void afterTextChanged(Editable s) {
                        // This one executes upon completion of typing a character
                        int len = s.toString().length();
                        String countText = "Comment: (" + len + " of " + MAX_LEN + ")";
                        rewardCommentTitle.setText(countText);
                    }

                    @Override
                    public void beforeTextChanged(CharSequence s, int start,
                                                  int count, int after) {
                        // Nothing to do here
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start,
                                              int before, int count) {
                        // Nothing to do here
                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.create_profile_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemID = item.getItemId();
        if(itemID == R.id.save_menu){
            getFieldData();
           if(pointsToSend.trim().isEmpty() || comment.trim().isEmpty()){
                profileDataIncorrect(empty);
           }else {
               saveChangesDialog();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void saveChangesDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Rewards Points?");
        builder.setMessage("Add rewards for " + employeeSelected.getFirstName() + " " + employeeSelected.getLastName());
        builder.setPositiveButton("OK", (dialog, which) -> addRewards());
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void addRewards(){
        String giverName = employeeLoggedIn.getFirstName() + " " + employeeLoggedIn.getLastName();
        RewardsAPIRunnable rewardsAPIRunnable = new RewardsAPIRunnable(this, apiValue, employeeSelected.getUsername(),
                employeeLoggedIn.getUsername(), giverName, pointsToSend, comment);
        new Thread(rewardsAPIRunnable).start();
    }

    private void profileDataIncorrect(String issue){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        if(issue.equals(empty)) {
            builder.setTitle("One or More Fields not Entered");
            builder.setMessage("Please enter data in all the fields");
        }
        else if(issue.equals(noImage)) {
            builder.setTitle("One or More Fields Incorrect");
            builder.setMessage("Please select an image");
        }
        builder.setPositiveButton("OK", (dialog, which) -> {
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void getFieldData(){
        pointsToSend = rewardPointsToSend.getText().toString();
        comment = rewardComment.getText().toString();
    }

    private void setUpActionBar(){
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.icon);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setTitle(String.format("%s %s",employeeSelected.getFirstName(), employeeSelected.getLastName()));
    }

    public void textToImage() {
        String imageString64 = employeeSelected.getImageBytes();
        if (imageString64 == null) return;

        byte[] imageBytes = Base64.decode(imageString64, Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
        imageView.setImageBitmap(bitmap);
    }

    public void updateLeaderboardActivity(){
        Intent intent = new Intent(this, LeaderboardActivity.class);
        startActivity(intent);
    }
}