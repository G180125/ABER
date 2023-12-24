package com.example.aber.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.aber.Activities.Main.MainActivity;
import com.example.aber.Activities.Register.RegisterActivity;
import com.example.aber.FirebaseManager;
import com.example.aber.R;

public class LoginActivity extends AppCompatActivity {
    private Button loginButton;
    private EditText emailEditText, passwordEditText;
    private ProgressDialog progressDialog;
    private FirebaseManager firebaseManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        firebaseManager = new FirebaseManager();

        loginButton = findViewById(R.id.login_button);

        emailEditText = findViewById(R.id.email_edit_text);
        passwordEditText = findViewById(R.id.password_edit_text);

        Intent intent = getIntent();
        if (intent.hasExtra("email") && intent.hasExtra("password")) {
            String email = intent.getStringExtra("email");
            String password = intent.getStringExtra("password");

            emailEditText.setText(email);
            passwordEditText.setText(password);
        }

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLoadingDialog();
                String email = emailEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                if (!email.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    if(!password.isEmpty()) {
                        firebaseManager.login(email, password, new FirebaseManager.OnTaskCompleteListener() {
                            @Override
                            public void onTaskSuccess(String message) {
                                hideLoadingDialog();
                                showToast(message);
                                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                finish();
                            }

                            @Override
                            public void onTaskFailure(String message) {
                                hideLoadingDialog();
                                message = "You have enter invalid information!";
                                showToast(message);
                                emailEditText.setError("Please enter your email again!");
                                passwordEditText.setError("Please enter your password again!");
                            }
                        });
                    } else {
                        hideLoadingDialog();
                        passwordEditText.setError("Password cannot be empty");
                    }
                } else if (email.isEmpty()){
                    hideLoadingDialog();
                    emailEditText.setError("Email cannot be empty");
                } else {
                    hideLoadingDialog();
                    emailEditText.setError("Please enter a valid email");
                }
            }
        });

    }

    private void showLoadingDialog() {
        runOnUiThread(() -> {
            progressDialog = new ProgressDialog(LoginActivity.this);
            progressDialog.setMessage("Loading...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        });
    }

    private void hideLoadingDialog() {
        runOnUiThread(() -> {
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        });
    }

    private void showToast(String message){
        Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
    }

    public void onClickRegister(View view) {
        startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
        finish();
    }
}