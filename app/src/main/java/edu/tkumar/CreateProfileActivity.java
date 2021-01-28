package edu.tkumar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
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

public class CreateProfileActivity extends AppCompatActivity {

    private EditText createStory, createUsername, createPassword, createFirstName, createLastName, createDepartmentName, createPositionTitle;
    private String story, userName, password, firstName, lastName, departmentName, positionTitle;
    private final String empty = "empty", noImage = "noImage", noPermission = "noPermission";
    private TextView textSizeDisplay;
    private ImageButton imageButton;
    private static final int MAX_LEN = 360;
    private final int REQUEST_IMAGE_CAPTURE_THUMB = 3;
    private final int REQUEST_IMAGE_GALLERY = 1;
    private static String locationString = "Unspecified Location";
    private String apiValue = "";
    private String imageBytes = "";
    private static final int READ_STORAGE_REQUEST = 112;
    private static final String TAG = "CreateProfileActivity";
    private Bitmap bitmap, newBitmap;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_profile);

        getIntentData();

        initializeFields();

        setUpStoryEditText();

        setUpActionBar();

    }

    private void getIntentData(){
        Intent intent = getIntent();

        if(intent.hasExtra("location")){
            locationString = intent.getStringExtra("location");
        }
        if(intent.hasExtra("apiValue")){
            apiValue = intent.getStringExtra("apiValue");
            Log.d(TAG, "getIntentData: " + apiValue);
        }
    }

    private void initializeFields(){
        createUsername = findViewById(R.id.createUsernameEditText);
        createPassword = findViewById(R.id.createPasswordEditText);
        createFirstName = findViewById(R.id.createFirstNameEditText);
        createLastName = findViewById(R.id.createLastNameEditText);
        createDepartmentName = findViewById(R.id.createDepartmentNameEditText);
        createPositionTitle = findViewById(R.id.createPositionEditText);
        imageButton = findViewById(R.id.createProfileImageButton);
        createStory = findViewById(R.id.createStoryEditText);
        textSizeDisplay = findViewById(R.id.createStoryTitleTextview);
        progressBar = findViewById(R.id.createProgressBar);
        BitmapDrawable drawable = (BitmapDrawable) imageButton.getDrawable();
        bitmap = drawable.getBitmap();
        //progressBar.setVisibility(View.GONE);
    }

    private void setUpStoryEditText() {
        createStory.setFilters(new InputFilter[] {
                new InputFilter.LengthFilter(MAX_LEN) // Specifies a max text length
        });

        createStory.addTextChangedListener(
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

    public void GalleryOrCamera(View v){
        if(checkPermission()) {
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
    }

    private boolean checkPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{
                            Manifest.permission.READ_EXTERNAL_STORAGE
                    }, READ_STORAGE_REQUEST);
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == READ_STORAGE_REQUEST) {
            if (permissions[0].equals(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    imageButton.performClick();
                } else {
                    profileDataIncorrect(noPermission);
                }
            }
        }
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
            if(bitmap == newBitmap){
                profileDataIncorrect(noImage);
            }else if( userName.trim().isEmpty() || password.trim().isEmpty() || firstName.trim().isEmpty() || lastName.trim().isEmpty()
                    || departmentName.trim().isEmpty() || positionTitle.trim().isEmpty() || story.trim().isEmpty()){
                profileDataIncorrect(empty);
            }else {
                saveChangesDialog();
                //progressBar.setVisibility(View.VISIBLE);
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void saveChangesDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Save Changes?");
        builder.setPositiveButton("OK", (dialog, which) -> createProfile());
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void createProfile(){
        progressBar.setVisibility(View.VISIBLE);
        CreateProfileAPIRunnable createProfileAPIRunnable = new CreateProfileAPIRunnable(this, firstName,
                lastName, userName, departmentName, story, positionTitle, password,"1000", locationString, imageBytes, apiValue);
        new Thread(createProfileAPIRunnable).start();
    }

    private void getFieldData(){
        userName = createUsername.getText().toString();
        password = createPassword.getText().toString();
        firstName = createFirstName.getText().toString();
        lastName = createLastName.getText().toString();
        departmentName = createDepartmentName.getText().toString();
        positionTitle = createPositionTitle.getText().toString();
        story = createStory.getText().toString();
        BitmapDrawable drawable = (BitmapDrawable) imageButton.getDrawable();
        newBitmap = drawable.getBitmap();
        imageToBase64();
    }

    private void imageToBase64() {
        ByteArrayOutputStream byteArrayOutputStream;
        int value = 50;
        while (value > 0) {
            byteArrayOutputStream = new ByteArrayOutputStream();
            newBitmap.compress(Bitmap.CompressFormat.PNG, value, byteArrayOutputStream);
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

    private void profileDataIncorrect(String issue){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("One or More Fields Incorrect");
        switch (issue) {
            case empty:
                builder.setMessage("Please enter data in all the fields");
                break;
            case noImage:
                builder.setMessage("Please select an image");
                break;
            case noPermission:
                builder.setMessage("Please grant permission in Settings");
                break;
        }
        builder.setPositiveButton("OK", (dialog, which) -> {
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void profileCreated(Employee employee1){
        progressBar.setVisibility(View.INVISIBLE);
        Intent intent= new Intent(this, ProfileActivity.class);
        intent.putExtra("employeeLoggedIn", employee1);
        intent.putExtra("apiValue", apiValue);
        intent.putExtra("location", employee1.getLocation());
        startActivity(intent);
    }

    private void setUpActionBar(){
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.icon);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setTitle("Create Profile");
    }

    public void showError(String s){
        progressBar.setVisibility(View.INVISIBLE);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Creation Failed");
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