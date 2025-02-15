package com.example.aber.Activities;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.aber.Activities.Main.MainActivity;
import com.example.aber.Activities.Register.RegisterActivity;
import com.example.aber.Models.User.User;
import com.example.aber.Utils.FirebaseUtil;
import com.example.aber.R;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract;
import com.firebase.ui.auth.IdpResponse;
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthCredential;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;


public class LoginActivity extends AppCompatActivity {
    private MaterialButton loginButton,sentButton;
    private TextInputEditText emailEditText, passwordEditText,forgetEmailEditText;

    private TextInputLayout emailTextLayout,passwordTextLayout;
    private ProgressDialog progressDialog;
    private FirebaseUtil firebaseManager;

    private TextView forgetpassword;

    private ImageView close, signInWIthGG;

    Dialog dialog;

    private Spinner spinnerLanguage;
    public static final String[] languages = {"Language","English","Tiếng Việt"};

    private LinearLayout loginBackground;
    private GoogleSignInClient mGoogleSignInClient;
    private int RC_SIGN_IN =20;
    private FirebaseAuth auth;
    private FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        firebaseManager = new FirebaseUtil();

        loginButton = findViewById(R.id.login_button);

        emailEditText = findViewById(R.id.email_edit_text);
        emailTextLayout = findViewById(R.id.email_layout_text);
        passwordTextLayout = findViewById(R.id.password_layout_text);
        passwordEditText = findViewById(R.id.password_edit_text);
        forgetpassword = findViewById(R.id.forget_password_text);
        spinnerLanguage = findViewById(R.id.language_spinner);
        signInWIthGG = findViewById(R.id.sign_in_with_google);


        dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_forget);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.dialog_background));
        dialog.setCancelable(false);


        forgetEmailEditText = dialog.findViewById(R.id.email_forget_edit_text);
        sentButton = dialog.findViewById(R.id.sent_button);
        close = dialog.findViewById(R.id.close);

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginBackground.setAlpha(1.0F);
                dialog.hide();
            }
        });

//        Language Changer
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,languages);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerLanguage.setAdapter(arrayAdapter);
        spinnerLanguage.setSelection(0);
        spinnerLanguage.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedLang = parent.getItemAtPosition(position).toString();
                if(selectedLang.equals("English")){
                    setLocal(LoginActivity.this,"en");
                    finish();
                    startActivity(getIntent());

                } else if (selectedLang.equals("Tiếng Việt")){
                    setLocal(LoginActivity.this,"vi");
                    finish();
                    startActivity(getIntent());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


//        Window window = dialog.getWindow();
//       WindowManager.LayoutParams wlp = window.getAttributes();
//
//        wlp.gravity = Gravity.BOTTOM;
//        wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
//        window.setAttributes(wlp);

        sentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emailAddress = forgetEmailEditText.getText().toString();
                Log.d("Forget Password","User : " + emailAddress);
                auth.sendPasswordResetEmail(emailAddress)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(LoginActivity.this, "Email sent", Toast.LENGTH_SHORT).show();
                                    Log.d("Forget Password", "Email sent.");
                                }
                            }
                        });
            }
        });


        Intent intent = getIntent();
        if (intent.hasExtra("email") && intent.hasExtra("password")) {
            String email = intent.getStringExtra("email");
            String password = intent.getStringExtra("password");

            emailEditText.setText(email);
            passwordEditText.setText(password);
        }



        signInWIthGG.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
                createSignInIntent();
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLoadingDialog();
                String email = emailEditText.getText().toString();
                String password = passwordEditText.getText().toString();

                if (!email.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    if(!password.isEmpty()) {
                        firebaseManager.login(email, password, new FirebaseUtil.OnTaskCompleteListener() {
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

        loginBackground = findViewById(R.id.login_background);

    forgetpassword.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            loginBackground.setAlpha(0.2F);
            dialog.show();
        }
    });
    }



    //Set local for language option
    public void setLocal(Activity activity, String langCode){
        Locale locale = new Locale(langCode);
        Resources resources = activity.getResources();
        Configuration config = resources.getConfiguration();
        config.setLocale(locale);
        resources.updateConfiguration(config,resources.getDisplayMetrics());


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

    private final ActivityResultLauncher<Intent> signInLauncher = registerForActivityResult(
            new FirebaseAuthUIActivityResultContract(),
            new ActivityResultCallback<FirebaseAuthUIAuthenticationResult>() {
                @Override
                public void onActivityResult(FirebaseAuthUIAuthenticationResult result) {
                    onSignInResult(result);
                }
            }
    );

    public void createSignInIntent() {
        // [START auth_fui_create_intent]
        // Choose authentication providers
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.GoogleBuilder().build());
        Log.d("Google signin", "1");

        // Create and launch sign-in intent
        Intent signInIntent = AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .build();
        Log.d("Google signin", "2");
        signInLauncher.launch(signInIntent);
        // [END auth_fui_create_intent]
    }

    // [START auth_fui_result]
    private void onSignInResult(FirebaseAuthUIAuthenticationResult result) {
        IdpResponse response = result.getIdpResponse();
        if (result.getResultCode() == RESULT_OK) {
            // Successfully signed in
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

            // Check if the user is signing in for the first time
            if (response != null && response.isNewUser()) {
                // User is signing in for the first time
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
                // Perform additional actions for first-time login if needed
            } else {
                // Existing user
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
            }
            // ...
        } else {
            // Sign in failed. If response is null the user canceled the
            // sign-in flow using the back button. Otherwise check
            // response.getError().getErrorCode() and handle the error.
            // ...
        }
    }
    // [END auth_fui_result]

    public void signOut() {
        // [START auth_fui_signout]
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    public void onComplete(@NonNull Task<Void> task) {
                        // ...
                    }
                });
        // [END auth_fui_signout]
    }


}