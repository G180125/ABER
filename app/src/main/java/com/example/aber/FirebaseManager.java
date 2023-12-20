package com.example.aber;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.annotation.NonNull;

import com.example.aber.Models.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.util.Objects;

public class FirebaseManager {
    public final String COLLECTION_USERS = "users";
    public final String COLLECTION_LOCATIONS = "locations";
    public final String DOCUMENTID = "documentID";
    public FirebaseAuth mAuth;
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
                            listener.onTaskSuccess("Login Successfully");
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

    public void getUserByID(String userID, OnFetchUserListener listener) {
        new Thread(() -> {
            this.db.collection(this.COLLECTION_USERS)
                    .document(userID)  // Use document() instead of whereEqualTo
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                User user = document.toObject(User.class);
                                listener.onFetchUserSuccess(user);
                            } else {
                                listener.onFetchUserFailure("User Data not found");
                            }
                        } else {
                            listener.onFetchUserFailure("Error: " + Objects.requireNonNull(task.getException()).getMessage());
                        }
                    });
        }).start();
    }

    public void uploadImage(Bitmap bitmap, String imagePath, OnTaskCompleteListener listener) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        new Thread(() -> {
            this.storageRef.child(imagePath)
                    .putBytes(data)
                    .addOnSuccessListener(taskSnapshot -> {
                        listener.onTaskSuccess(imagePath);
                    })
                    .addOnFailureListener(e -> {
                        listener.onTaskFailure("Error: " + Objects.requireNonNull(e.getMessage()));
                    });
        }).start();
    }

    public void retrieveImage(String path, OnRetrieveImageListener listener){
        final long ONE_MEGABYTE = 1024 * 1024;

        new Thread(() -> {
            this.storageRef.child(path)
                    .getBytes(ONE_MEGABYTE)
                    .addOnSuccessListener(bytes -> {
                        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        listener.onRetrieveImageSuccess(bitmap);
                    })
                    .addOnFailureListener(exception -> {
                        listener.onRetrieveImageFailure("Error: " + exception.getMessage());
                    });
        }).start();
    }

    public void updateUser(String userID, User user, OnTaskCompleteListener listener) {
        new Thread(() -> {
            this.db.collection(this.COLLECTION_USERS)
                    .document(userID)
                    .set(user)
                    .addOnSuccessListener(aVoid -> {
                        listener.onTaskSuccess("User updated successfully");
                    })
                    .addOnFailureListener(e -> {
                        listener.onTaskFailure("Error updating user: " + e.getMessage());
                    });
        }).start();
    }

    public interface OnTaskCompleteListener {
        void onTaskSuccess(String message);
        void onTaskFailure(String message);
    }

    public interface OnFetchUserListener {
        void onFetchUserSuccess(User user);
        void onFetchUserFailure(String message);
    }

    public interface OnRetrieveImageListener {
        void onRetrieveImageSuccess(Bitmap bitmap);
        void onRetrieveImageFailure(String message);
    }
}

