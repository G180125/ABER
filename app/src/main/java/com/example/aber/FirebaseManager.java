package com.example.aber;

import android.app.ProgressDialog;
import android.graphics.Bitmap;

import com.example.aber.Activities.Register.RegisterActivity;
import com.example.aber.Models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Objects;

public class FirebaseManager {
    public final String COLLECTION_USERS = "users";
    public final String COLLECTION_LOCATIONS = "locations";
    public final String DOCUMENTID = "documentID";
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private StorageReference storageRef;

    public FirebaseManager() {
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        storageRef = FirebaseStorage.getInstance().getReference();
    }

    public void register(final String email, final String password, OnTaskCompleteListener listener) {
        new Thread(() -> {
            this.mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            FirebaseUser firebaseUser = this.mAuth.getCurrentUser();
                            assert firebaseUser != null;
                            listener.onTaskSuccess(firebaseUser.getUid());
                        } else {
                            listener.onTaskFailure("Error: " + Objects.requireNonNull(task.getException()).getMessage());
                        }
                    });
        }).start();
    }

    public void login(String email, String password, OnTaskCompleteListener listener){
        new Thread(() -> {
            this.mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            // Sign in success, get the user from Firestore
                            FirebaseUser firebaseUser = this.mAuth.getCurrentUser();
                            assert firebaseUser != null;
                            listener.onTaskSuccess(firebaseUser.getUid());
                        } else {
                            listener.onTaskFailure("Error: " + Objects.requireNonNull(task.getException()).getMessage());
                        }
                    });
        }).start();
    }

    public void addUser(String userID, User user, OnTaskCompleteListener listener){
        new Thread(() -> {
            this.db.collection(this.COLLECTION_USERS)
                    .document(userID)
                    .set(user)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            listener.onTaskSuccess("Register Successfully");
                        } else {
                            listener.onTaskFailure("Error: " + Objects.requireNonNull(task.getException()).getMessage());
                        }
                    });
        }).start();
    }

    public interface OnTaskCompleteListener {
        void onTaskSuccess(String message);
        void onTaskFailure(String message);
    }
}

