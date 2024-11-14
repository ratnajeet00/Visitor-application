package com.example.myapplication;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import com.bumptech.glide.Glide;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ApprovalActivity extends AppCompatActivity {

    private TextView visitorName, visitorPhone, visitorEmail, visitorIdProof, visitorIdText;
    private ImageView visitorPhoto;
    private Button approveButton, rejectButton;
    private static final String CHANNEL_ID = "visitor_approval_channel";
    private int visitorId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_approval);

        visitorName = findViewById(R.id.visitorName);
        visitorPhone = findViewById(R.id.visitorPhone);
        visitorEmail = findViewById(R.id.visitorEmail);
        visitorIdProof = findViewById(R.id.visitorIdProof);
        visitorPhoto = findViewById(R.id.visitorPhoto);
        visitorIdText = findViewById(R.id.visitorIdText);
        approveButton = findViewById(R.id.approveButton);
        rejectButton = findViewById(R.id.rejectButton);

        // Initialize the visitor ID to an invalid value (can be set when the data is loaded)
        visitorId = -1;

        // Load visitor data dynamically from the frame (e.g., using a data source or method to get visitor details)
        loadVisitorData();

        approveButton.setOnClickListener(v -> handleApproval("approved"));
        rejectButton.setOnClickListener(v -> handleApproval("rejected"));

        // Create notification channel
        createNotificationChannel();
    }

    private void loadVisitorData() {
        ApiService apiService = ApiClient.getRetrofitInstance().create(ApiService.class);

        // Fetch visitor data from the server (replace with your actual API endpoint)
        Call<Visitor> call = apiService.getVisitorById(visitorId);

        call.enqueue(new Callback<Visitor>() {
            @Override
            public void onResponse(@NonNull Call<Visitor> call, @NonNull Response<Visitor> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Visitor visitor = response.body();

                    // Set the visitor data in the UI
                    visitorId = visitor.getId(); // Set the visitor ID dynamically
                    visitorIdText.setText("Visitor ID: " + visitorId);

                    visitorName.setText(visitor.getName());
                    visitorPhone.setText(visitor.getPhone());
                    visitorEmail.setText(visitor.getEmail());
                    visitorIdProof.setText(visitor.getIdProof());

                    // Handle the photo URL (if available)
                    String photoPath = visitor.getPhotoPath();
                    if (photoPath != null && !photoPath.isEmpty()) {
                        String baseUrl = "https://vulture-on-treefrog.ngrok-free.app/"; // Base URL of your API
                        String fullPhotoUrl = baseUrl + photoPath.replace("\\", "/");

                        Glide.with(ApprovalActivity.this)
                                .load(fullPhotoUrl)
                                .into(visitorPhoto);
                    }
                } else {
                    Toast.makeText(ApprovalActivity.this, "Failed to load visitor data", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Visitor> call, @NonNull Throwable t) {
                Log.e("API Request", "API call failed", t);
                Toast.makeText(ApprovalActivity.this, "Failed to load visitor data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void handleApproval(String status) {

        Log.d("API Request", "Sending approval/rejection for visitorId: " + visitorId + " with status: " + status);
        Log.d("ApprovalActivity", "visitorId: " + visitorId);


        // Check if visitorId is valid
        if (visitorId == -1) {
            Toast.makeText(this, "Invalid Visitor ID", Toast.LENGTH_SHORT).show();
            return;
        }

        ApiService apiService = ApiClient.getRetrofitInstance().create(ApiService.class);
        Call<ResponseBody> call = apiService.approveVisitor(visitorId, status);
        Log.d("API URL", call.request().url().toString());

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    String message = "Visitor " + status + " successfully.";
                    sendApprovalNotification(message);
                    Toast.makeText(ApprovalActivity.this, message, Toast.LENGTH_SHORT).show();

                    hideVisitorDetails();
                    loadNextVisitor();
                } else {
                    Toast.makeText(ApprovalActivity.this, "Failed to update status", Toast.LENGTH_SHORT).show();
                    Log.d("response",""+response.toString());
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                Log.e("API Request", "API call failed", t);
                Toast.makeText(ApprovalActivity.this, "API call failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void hideVisitorDetails() {
        visitorName.setVisibility(View.GONE);
        visitorPhone.setVisibility(View.GONE);
        visitorEmail.setVisibility(View.GONE);
        visitorIdProof.setVisibility(View.GONE);
        visitorPhoto.setVisibility(View.GONE);
        approveButton.setVisibility(View.GONE);
        rejectButton.setVisibility(View.GONE);
        visitorIdText.setVisibility(View.GONE);
    }

    private void loadNextVisitor() {
        ApiService apiService = ApiClient.getRetrofitInstance().create(ApiService.class);

        // Fetch the next visitor from the server (replace with your actual API call)
        Call<Visitor> call = apiService.getNextVisitor(visitorId);

        call.enqueue(new Callback<Visitor>() {
            @Override
            public void onResponse(@NonNull Call<Visitor> call, @NonNull Response<Visitor> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Visitor nextVisitor = response.body();
                    if (nextVisitor != null) {
                        visitorId = nextVisitor.getId();  // Update the visitorId dynamically
                        visitorIdText.setText("Visitor ID: " + visitorId);

                        visitorName.setVisibility(View.VISIBLE);
                        visitorPhone.setVisibility(View.VISIBLE);
                        visitorEmail.setVisibility(View.VISIBLE);
                        visitorIdProof.setVisibility(View.VISIBLE);
                        visitorPhoto.setVisibility(View.VISIBLE);
                        approveButton.setVisibility(View.VISIBLE);
                        rejectButton.setVisibility(View.VISIBLE);
                        visitorIdText.setVisibility(View.VISIBLE);

                        visitorName.setText(nextVisitor.getName());
                        visitorPhone.setText(nextVisitor.getPhone());
                        visitorEmail.setText(nextVisitor.getEmail());
                        visitorIdProof.setText(nextVisitor.getIdProof());

                        String photoPath = nextVisitor.getPhotoPath();
                        if (photoPath != null && !photoPath.isEmpty()) {
                            String baseUrl = "https://vulture-on-treefrog.ngrok-free.app/";
                            String fullPhotoUrl = baseUrl + photoPath.replace("\\", "/");

                            Glide.with(ApprovalActivity.this)
                                    .load(fullPhotoUrl)
                                    .into(visitorPhoto);
                        }
                    }
                } else {
                    Toast.makeText(ApprovalActivity.this, "No next visitor found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Visitor> call, @NonNull Throwable t) {
                Toast.makeText(ApprovalActivity.this, "Failed to load next visitor", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sendApprovalNotification(String message) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle("Visitor Approval")
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setDefaults(NotificationCompat.DEFAULT_ALL);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Create notification channel for devices running Android O or higher
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Visitor Approval Channel";
            String description = "Channel for visitor approval notifications";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            notificationManager.createNotificationChannel(channel);
        }

        // Show notification
        notificationManager.notify(0, builder.build());
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Visitor Approval Channel";
            String description = "Channel for visitor approval notifications";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
