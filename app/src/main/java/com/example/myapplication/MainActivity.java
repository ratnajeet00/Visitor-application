package com.example.myapplication;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.ByteArrayOutputStream;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private EditText name, phone, email, idProof;
    private ImageView photoImageView;
    private Bitmap photoBitmap;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        name = findViewById(R.id.name);
        phone = findViewById(R.id.phone);
        email = findViewById(R.id.email);
        idProof = findViewById(R.id.idProof);
        photoImageView = findViewById(R.id.photoImageView);
        Button capturePhotoButton = findViewById(R.id.capturePhotoButton);
        Button submitButton = findViewById(R.id.submitButton);

        apiService = ApiClient.getRetrofitInstance().create(ApiService.class);

        capturePhotoButton.setOnClickListener(v -> capturePhoto());
        submitButton.setOnClickListener(v -> submitForm());
    }

    private void capturePhoto() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            photoBitmap = (Bitmap) extras.get("data");
            photoImageView.setImageBitmap(photoBitmap);
        }
    }

    private void submitForm() {
        // Validate inputs
        if (name.getText().toString().isEmpty() || phone.getText().toString().isEmpty() ||
                email.getText().toString().isEmpty() || idProof.getText().toString().isEmpty()) {
            Toast.makeText(MainActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create request parts from EditText fields
        RequestBody namePart = RequestBody.create(MediaType.parse("text/plain"), name.getText().toString());
        RequestBody phonePart = RequestBody.create(MediaType.parse("text/plain"), phone.getText().toString());
        RequestBody emailPart = RequestBody.create(MediaType.parse("text/plain"), email.getText().toString());
        RequestBody idProofPart = RequestBody.create(MediaType.parse("text/plain"), idProof.getText().toString());

        // Convert photo bitmap to MultipartBody.Part
        MultipartBody.Part photoPart = null;
        if (photoBitmap != null) {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            photoBitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
            byte[] photoBytes = byteArrayOutputStream.toByteArray();
            RequestBody photoRequestBody = RequestBody.create(MediaType.parse("image/jpeg"), photoBytes);
            photoPart = MultipartBody.Part.createFormData("photo", "photo.jpg", photoRequestBody);
        } else {
            Toast.makeText(this, "Please capture a photo", Toast.LENGTH_SHORT).show();
            return;
        }

        // Make API call
        apiService.registerVisitor(namePart, phonePart, emailPart, idProofPart, photoPart)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful()) {
                            // Clear form fields
                            name.setText("");
                            phone.setText("");
                            email.setText("");
                            idProof.setText("");
                            photoImageView.setImageResource(android.R.drawable.ic_menu_camera); // Reset to default camera icon
                            photoBitmap = null; // Reset the Bitmap

                            // Display success message
                            Toast.makeText(MainActivity.this, "Visitor registered successfully!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(MainActivity.this, "Registration failed: " + response.message(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Toast.makeText(MainActivity.this, "API call failed: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
