package com.example.myapplication;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ApprovalActivity extends AppCompatActivity {

    private RecyclerView visitorsRecyclerView;
    private VisitorAdapter visitorAdapter;
    private List<Visitor> visitorList = new ArrayList<>();
    private static final String CHANNEL_ID = "visitor_approval_channel";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_approval);

        visitorsRecyclerView = findViewById(R.id.visitorsRecyclerView);
        visitorsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        visitorAdapter = new VisitorAdapter(visitorList, this::handleApproval);
        visitorsRecyclerView.setAdapter(visitorAdapter);

        createNotificationChannel();
        loadVisitors();
    }

    private void loadVisitors() {
        ApiService apiService = ApiClient.getRetrofitInstance().create(ApiService.class);
        Call<List<Visitor>> call = apiService.getPendingVisitors();

        call.enqueue(new Callback<List<Visitor>>() {
            @Override
            public void onResponse(@NonNull Call<List<Visitor>> call, @NonNull Response<List<Visitor>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    visitorList.clear();
                    visitorList.addAll(response.body());
                    visitorAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(ApprovalActivity.this, "Failed to load visitors", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Visitor>> call, @NonNull Throwable t) {
                Toast.makeText(ApprovalActivity.this, "Failed to load visitors", Toast.LENGTH_SHORT).show();
                Log.e("API Request", "Failed to load visitors", t);
            }
        });
    }

    private void handleApproval(int visitorId, String status) {
        ApiService apiService = ApiClient.getRetrofitInstance().create(ApiService.class);

        // Create a proper JSON request body
        ApprovalRequest request = new ApprovalRequest(status);
        Call<ResponseBody> call = apiService.approveVisitor(visitorId, request);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    String message = "Visitor " + status + " successfully";
                    sendApprovalNotification(message);
                    loadVisitors(); // Reload the list after approval
                } else {
                    Toast.makeText(ApprovalActivity.this, "Failed to update status", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                Toast.makeText(ApprovalActivity.this, "Failed to update status", Toast.LENGTH_SHORT).show();
                Log.e("API Request", "Failed to update status", t);
            }
        });
    }

    private void sendApprovalNotification(String message) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification) // Replace with your notification icon
                .setContentTitle("Visitor Approval")
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setDefaults(NotificationCompat.DEFAULT_ALL);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Visitor Approval Channel";
            String description = "Channel for visitor approval notifications";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            notificationManager.createNotificationChannel(channel);
        }

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