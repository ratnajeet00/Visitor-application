package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    private EditText username, password;
    private Button loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        loginButton = findViewById(R.id.loginButton);

        loginButton.setOnClickListener(v -> performLogin());
    }

    private void performLogin() {
        String enteredUsername = username.getText().toString();
        String enteredPassword = password.getText().toString();

        if (enteredUsername.equals("admin") && enteredPassword.equals("admin")) {
            // Go to ApprovalActivity if credentials are "admin" for both fields
            Intent intent = new Intent(LoginActivity.this, ApprovalActivity.class);
            startActivity(intent);
            Toast.makeText(this, "Welcome, Admin!", Toast.LENGTH_SHORT).show();
        } else {
            // Go to MainActivity for any other credentials
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            Toast.makeText(this, "Welcome!", Toast.LENGTH_SHORT).show();
        }

        // Clear the login fields
        username.setText("");
        password.setText("");
    }
}
