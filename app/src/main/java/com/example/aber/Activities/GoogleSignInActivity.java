//package com.example.aber.Activities;
//
//import androidx.appcompat.app.AppCompatActivity;
//
//import android.os.Bundle;
//
//import com.example.aber.R;
//import com.google.android.gms.auth.api.identity.BeginSignInRequest;
//
//public class GoogleSignInActivity extends LoginActivity {
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//        signInRequest = BeginSignInRequest.builder()
//                .setGoogleIdTokenRequestOptions(GoogleIdTokenRequestOptions.builder()
//                        .setSupported(true)
//                        // Your server's client ID, not your Android client ID.
//                        .setServerClientId(getString(R.string.default_web_client_id))
//                        // Only show accounts previously used to sign in.
//                        .setFilterByAuthorizedAccounts(true)
//                        .build())
//                .build();
//    }
//}