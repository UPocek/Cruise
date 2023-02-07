package com.cruisemobile.cruise.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import com.cruisemobile.cruise.R;
import com.cruisemobile.cruise.models.UserChangesDTO;
import com.cruisemobile.cruise.models.UserDTO;
import com.cruisemobile.cruise.services.ServiceUtils;
import com.cruisemobile.cruise.tools.Helper;
import com.google.android.material.card.MaterialCardView;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;

public class EditAccountActivity extends AppCompatActivity {
    public static final String EXTRA_MESSAGE =
            "com.example.android.twoactivities.extra.MESSAGE";
    private Button addImageBtn;
    private ImageView userImage;
    private View backBtn;
    private MaterialCardView passengerNameCard;
    private MaterialCardView passengerSurNameCard;
    private MaterialCardView passengerPhoneCard;
    private MaterialCardView passengerEmailCard;
    private MaterialCardView passengerAddressCard;
    private MaterialCardView passengerPasswordCard;
    private SharedPreferences sharedPreferences;
    private ActivityResultLauncher<Intent> launchUserGetImageActivity;
    private ExecutorService executorService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_passenger_account);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        passengerNameCard = findViewById(R.id.firstNamePerson);
        passengerSurNameCard = findViewById(R.id.lastNamePerson);
        passengerPhoneCard = findViewById(R.id.phonePerson);
        passengerEmailCard = findViewById(R.id.emailPerson);
        passengerAddressCard = findViewById(R.id.addressPerson);
        passengerPasswordCard = findViewById(R.id.passwordPerson);
        addImageBtn = findViewById(R.id.editPhoto);
        userImage = findViewById(R.id.userImageField);
        launchUserGetImageActivity = getLaunchUserGetImageActivity();
        executorService = Executors.newSingleThreadExecutor();

        addImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userSetNewImage();
            }
        });

        backBtn = findViewById(R.id.backBtnPassengerEditAccount);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        passengerNameCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EditAccountActivity.this, ChangeAccountActivity.class);
                intent.putExtra(EXTRA_MESSAGE, "Name");
                startActivity(intent);

            }
        });
        passengerSurNameCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EditAccountActivity.this, ChangeAccountActivity.class);
                intent.putExtra(EXTRA_MESSAGE, "Surname");
                startActivity(intent);
            }
        });
        passengerPhoneCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EditAccountActivity.this, ChangeAccountActivity.class);
                intent.putExtra(EXTRA_MESSAGE, "Phone");
                startActivity(intent);
            }
        });
        passengerEmailCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EditAccountActivity.this, ChangeAccountActivity.class);
                intent.putExtra(EXTRA_MESSAGE, "Email");
                startActivity(intent);
            }
        });
        passengerAddressCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EditAccountActivity.this, ChangeAccountActivity.class);
                intent.putExtra(EXTRA_MESSAGE, "Address");
                startActivity(intent);
            }
        });
        passengerPasswordCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EditAccountActivity.this, ChangeAccountActivity.class);
                intent.putExtra(EXTRA_MESSAGE, "Password");
                startActivity(intent);
            }
        });

        String profilePicture = sharedPreferences.getString("picture", "");
        if (!profilePicture.equals("")) {
            Bitmap bitmapPicture = Helper.stringToBitMap(profilePicture);
            userImage.setImageBitmap(bitmapPicture);
        }


    }

    @Override
    protected void onResume() {
        super.onResume();
        setUserData();
    }

    private void userSetNewImage() {
        Intent selectImageIntent = new Intent();
        selectImageIntent.setType("image/*");
        selectImageIntent.setAction(Intent.ACTION_GET_CONTENT);

        launchUserGetImageActivity.launch(selectImageIntent);
    }

    private ActivityResultLauncher<Intent> getLaunchUserGetImageActivity() {
        return registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null && data.getData() != null) {
                            processPictureUpdates(data.getData());
                        }
                    }
                });
    }

    private void processPictureUpdates(Uri selectedImageUri) {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    UserChangesDTO userChangesDTO = prepareUserForUpdate(selectedImageUri);
                    UserDTO user = updateUserPicture(userChangesDTO);
                    Helper.setUserDataInSharedPreferences(user, sharedPreferences);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            setPicturePreview(selectedImageUri);
                        }
                    });
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    private void setPicturePreview(Uri selectedImageUri) {
        try {
            if (Build.VERSION.SDK_INT < 28) {
                Bitmap selectedImageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
                userImage.setImageBitmap(selectedImageBitmap);
            } else {
                ImageDecoder.Source source = ImageDecoder.createSource(this.getContentResolver(), selectedImageUri);
                Bitmap selectedImageBitmap = ImageDecoder.decodeBitmap(source);
                userImage.setImageBitmap(selectedImageBitmap);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private UserChangesDTO prepareUserForUpdate(Uri selectedImageUri) throws FileNotFoundException {
        InputStream imageStream = getContentResolver().openInputStream(selectedImageUri);
        Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
        String encodedImage = Helper.encodeImage(selectedImage);
        UserChangesDTO user = Helper.getUserInfoFromSharedPreferences(sharedPreferences);
        user.setProfilePicture(encodedImage);
        return user;
    }

    private UserDTO updateUserPicture(UserChangesDTO user) throws IOException {
        Call<UserDTO> call = ServiceUtils.passengerEndpoints.updatePassenger(user, sharedPreferences.getLong("id", -1L));
        return call.execute().body();
    }

    private void setUserData() {
        TextView nameField = findViewById(R.id.user_detail_name_field);
        nameField.setText(sharedPreferences.getString("name", ""));

        TextView surnameField = findViewById(R.id.user_detail_surname_field);
        surnameField.setText(sharedPreferences.getString("surname", ""));

        TextView numberField = findViewById(R.id.user_detail_number_field);
        numberField.setText(sharedPreferences.getString("number", ""));

        TextView addressField = findViewById(R.id.user_detail_address_field);
        addressField.setText(sharedPreferences.getString("address", ""));

        TextView emailField = findViewById(R.id.user_detail_email_field);
        emailField.setText(sharedPreferences.getString("email", ""));
    }
}