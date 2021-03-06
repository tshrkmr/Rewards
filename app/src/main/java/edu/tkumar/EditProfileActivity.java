package edu.tkumar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Objects;

public class EditProfileActivity extends AppCompatActivity {

    private EditText editStory, editPassword, editFirstName, editLastName, editDepartmentName, editPosition;
    private TextView editUserName;
    private String story, password, firstName, lastName, departmentName, position, userName;
    private final String empty = "empty", noImage = "noImage";
    private TextView textSizeDisplay;
    private ImageButton imageButton;
    private static final int MAX_LEN = 360;
    private final int REQUEST_IMAGE_CAPTURE_THUMB = 3;
    private final int REQUEST_IMAGE_GALLERY = 1;
    private String apiValue = "";
    private String imageBytes = "";
    private Employee employeeLoggedIn;
    private Bitmap bitmap;
    private ProgressBar progressBar;
    private static String locationString = "Unspecified Location";
    private static final String TAG = "EditProfileActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        getIntentData();

        convertTextToImage();

        initializeFields();

        setOriginalDetails();

        setUpStoryEditText();

        setUpActionBar();
    }

    private void initializeFields(){
        editUserName = findViewById(R.id.editUserNameTextView);
        editPassword = findViewById(R.id.editPasswordEditText);
        editFirstName = findViewById(R.id.editFirstNameEditText);
        editLastName = findViewById(R.id.editLastNameEditText);
        editDepartmentName = findViewById(R.id.editDepartmentNameEditText);
        editPosition = findViewById(R.id.editPositionEditText);
        imageButton = findViewById(R.id.editProfileImageButton);
        editStory = findViewById(R.id.editStoryEditText);
        textSizeDisplay = findViewById(R.id.editStoryTitleTextView);
        progressBar = findViewById(R.id.editProgressBar);
    }

    private void setOriginalDetails(){
        editUserName.setText(employeeLoggedIn.getUsername());
        editFirstName.setText(employeeLoggedIn.getFirstName());
        editLastName.setText(employeeLoggedIn.getLastName());
        editDepartmentName.setText(employeeLoggedIn.getDepartment());
        editPosition.setText(employeeLoggedIn.getPosition());
        imageButton.setImageBitmap(bitmap);
        editStory.setText(employeeLoggedIn.getStory());
    }

    private void setUpStoryEditText() {
        editStory.setFilters(new InputFilter[] {
                new InputFilter.LengthFilter(MAX_LEN) // Specifies a max text length
        });

        editStory.addTextChangedListener(
                new TextWatcher() {

                    @Override
                    public void afterTextChanged(Editable s) {
                        // This one executes upon completion of typing a character
                        int len = s.toString().length();
                        String countText = "Your Story: (" + len + " of " + MAX_LEN + ")";
                        textSizeDisplay.setText(countText);
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

    public void GalleryOrCamera(View v){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Profile Picture");
        builder.setMessage("Take picture from:");
        builder.setPositiveButton("CAMERA", (dialog, which) -> doThumb(v));
        builder.setNegativeButton("GALLERY", (dialog, which) -> doGallery(v));
        builder.setNeutralButton("CANCEL", (dialog, which) -> {
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void doGallery(View v) {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, REQUEST_IMAGE_GALLERY);
    }

    public void doThumb(View v) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE_THUMB);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.create_profile_menu, menu);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_GALLERY && resultCode == RESULT_OK) {
            try {
                processGallery(data);
            } catch (Exception e) {
                Toast.makeText(this, "onActivityResult: " + e.getMessage(), Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        } else if (requestCode == REQUEST_IMAGE_CAPTURE_THUMB && resultCode == RESULT_OK) {
            try {
                processCameraThumb(data.getExtras());
            } catch (Exception e) {
                Toast.makeText(this, "onActivityResult: " + e.getMessage(), Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        }
    }

    private void processCameraThumb(Bundle extras) {
        Bitmap imageBitmap = (Bitmap) extras.get("data");
        imageButton.setImageBitmap(imageBitmap);
        //imageToBase64();
    }

    private void processGallery(Intent data) {
        Uri galleryImageUri = data.getData();
        if (galleryImageUri == null)
            return;

        InputStream imageStream = null;
        try {
            imageStream = getContentResolver().openInputStream(galleryImageUri);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
        imageButton.setImageBitmap(selectedImage);
        //imageToBase64();
    }

    private void imageToBase64(){
        ByteArrayOutputStream byteArrayOutputStream;
        int value = 50;
        while (value > 0) {
            byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, value, byteArrayOutputStream);
            byte[] byteArray = byteArrayOutputStream.toByteArray();
            imageBytes= Base64.encodeToString(byteArray, Base64.DEFAULT);
            if (imageBytes.length() > 100000) {
                value -= 10;
            }else{
                break;
            }
        }
        Log.d(TAG, "imageToBase64: " + imageBytes.length());
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemID = item.getItemId();
        if(itemID == R.id.save_menu){
            getFieldData();
            if(password.trim().isEmpty() || firstName.trim().isEmpty() || lastName.trim().isEmpty()
                    || departmentName.trim().isEmpty() || position.trim().isEmpty() || story.trim().isEmpty()){
                profileDataIncorrect(empty);
            }else {
                saveChangesDialog();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void saveChangesDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Save Changes?");
        builder.setPositiveButton("OK", (dialog, which) -> updateProfile());
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void updateProfile(){
        progressBar.setVisibility(View.VISIBLE);
        UpdateProfileAPIRunnable updateProfileAPIRunnable = new UpdateProfileAPIRunnable(this, firstName, lastName,
                userName, departmentName, story, position, password, locationString, imageBytes, apiValue);
        new Thread(updateProfileAPIRunnable).start();
    }

    private void getFieldData(){
        password = editPassword.getText().toString();
        firstName = editFirstName.getText().toString();
        lastName = editLastName.getText().toString();
        departmentName = editDepartmentName.getText().toString();
        position = editPosition.getText().toString();
        story = editStory.getText().toString();
        userName = editUserName.getText().toString();
        BitmapDrawable drawable = (BitmapDrawable) imageButton.getDrawable();
        bitmap = drawable.getBitmap();
        imageToBase64();
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

    private void convertTextToImage(){
        imageBytes = employeeLoggedIn.getImageBytes();
        TextToImage textToImage = new TextToImage(imageBytes);
        bitmap = textToImage.textToImage();
    }

    public void profileUpDated(Employee employee1){
        progressBar.setVisibility(View.INVISIBLE);
        Intent intent= new Intent(this, ProfileActivity.class);
        intent.putExtra("employeeLoggedIn", employee1);
        intent.putExtra("apiValue", apiValue);
        startActivity(intent);
    }

    private void setUpActionBar(){
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.icon);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setTitle("Edit Profile");
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
}