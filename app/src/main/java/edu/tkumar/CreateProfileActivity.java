package edu.tkumar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.InputStream;

public class CreateProfileActivity extends AppCompatActivity {

    private EditText createStory, createUsername, createPassword, createFirstName, createLastName, createDepartmentName, createPositionTitle;
    private String story, userName, password, firstName, lastName, departmentName, positionTitle;
    private String empty = "empty";
    private TextView textSizeDisplay;
    private ImageButton imageButton;
    private static final int MAX_LEN = 360;
    private final int REQUEST_IMAGE_CAPTURE_THUMB = 3;
    private final int REQUEST_IMAGE_GALLERY = 1;
    private String locationValue = "", apiValue = "";
    private String imageBytes = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_profile);

        getIntentData();

        initializeFields();

        setupEditText();
    }

    private void getIntentData(){
        Intent intent = getIntent();

        if(intent.hasExtra("location")){
            locationValue = intent.getStringExtra("location");
        }
        if(intent.hasExtra("apiValue")){
            apiValue = intent.getStringExtra("apiValue");
        }
    }

    private void initializeFields(){
        createUsername = findViewById(R.id.createUsernameEditText);
        createPassword = findViewById(R.id.createPasswordEdittext);
        createFirstName = findViewById(R.id.createFirstName);
        createLastName = findViewById(R.id.createLastName);
        createDepartmentName = findViewById(R.id.createDepartmentName);
        createPositionTitle = findViewById(R.id.createPositionTitle);
        imageButton = findViewById(R.id.createProfileImageButton);
        createStory = findViewById(R.id.createStoryEdittext);
        textSizeDisplay = findViewById(R.id.createStoryTextview);
    }

    private void setupEditText() {
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
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Profile Picture");
        builder.setMessage("Take picture from:");
        builder.setPositiveButton("CAMERA", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                doThumb(v);
            }
        });
        builder.setNegativeButton("GALLERY", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                doGallery(v);
            }
        });
        builder.setNeutralButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
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
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.save_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemID = item.getItemId();
        if(itemID == R.id.save_menu){
            userName = createUsername.getText().toString();
            password = createPassword.getText().toString();
            firstName = createFirstName.getText().toString();
            lastName = createLastName.getText().toString();
            departmentName = createDepartmentName.getText().toString();
            positionTitle = createPositionTitle.getText().toString();
            story = createStory.getText().toString();
            if( userName.trim().isEmpty() || password.trim().isEmpty() || firstName.trim().isEmpty() || lastName.trim().isEmpty()
                    || departmentName.trim().isEmpty() || positionTitle.trim().isEmpty() || story.trim().isEmpty()){
                profileDataIncorrect(empty);
            }else {
                CreateProfileAPIRunnable createProfileAPIRunnable = new CreateProfileAPIRunnable(this, firstName,
                        lastName, userName, departmentName, story, positionTitle, password,"1000", locationValue, imageBytes);
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void profileDataIncorrect(String issue){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        if(issue.equals(empty)) {
            builder.setTitle("One or More Fields not Entered");
            builder.setMessage("Please enter data in all the fields");
        }
//        else if(issue.equals(nullValue)) {
//            builder.setTitle("One or More Fields Incorrect");
//            builder.setMessage("Null values cannot be accepted");
//        }
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}