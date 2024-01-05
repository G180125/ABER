package com.example.aber;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.aber.Models.Booking.Booking;
import com.example.aber.Models.Message.MyMessage;
import com.example.aber.Models.Staff.Admin;
import com.example.aber.Models.Staff.Driver;
import com.example.aber.Models.Staff.Staff;
import com.example.aber.Models.User.User;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;


public class FirebaseManager {
    public final String COLLECTION_USERS = "users";
    public final String COLLECTION_DRIVERS = "drivers";
    public final String COLLECTION_CHATS = "Chats";
    public final String COLLECTION_BOOKINGS = "Bookings";
    public final String COLLECTION_ADMINS = "admins";
    public final String COLLECTION_DRIVER = "drivers";
    public final String DOCUMENTID = "documentID";
    public FirebaseAuth mAuth;
    private FirebaseFirestore firestore;
    private StorageReference storageRef;
    private FirebaseDatabase database;


    public FirebaseManager() {
        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        storageRef = FirebaseStorage.getInstance().getReference();
        database = FirebaseDatabase.getInstance();
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
            this.firestore.collection(this.COLLECTION_USERS)
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

    public void getUserByID(String userID, OnFetchListener<User> listener) {
        new Thread(() -> {
            this.firestore.collection(this.COLLECTION_USERS)
                    .document(userID)  // Use document() instead of whereEqualTo
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                User user = document.toObject(User.class);
                                listener.onFetchSuccess(user);
                            } else {
                                listener.onFetchFailure("User Data not found");
                            }
                        } else {
                            listener.onFetchFailure("Error: " + Objects.requireNonNull(task.getException()).getMessage());
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
            this.firestore.collection(this.COLLECTION_USERS)
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

    public void sendMessage(String sender, String receiver, String message){
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender", sender);
        hashMap.put("receiver", receiver);
        hashMap.put("message", message);

        this.database.getReference().child(COLLECTION_CHATS)
                .push()
                .setValue(hashMap);
    }

    public void addBooking(String userID, Booking booking){
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("user", userID);
        hashMap.put("booking", booking);

        this.database.getReference().child(COLLECTION_BOOKINGS)
                .push()
                .setValue(hashMap);
    }

    public void readMessage(final String myID, final String userID, OnReadingMessageListener listener){
        List<MyMessage> messageList = new ArrayList<>();

        DatabaseReference reference =  this.database.getReference(COLLECTION_CHATS);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                messageList.clear();
                for (DataSnapshot s: snapshot.getChildren()){
                    MyMessage message = s.getValue(MyMessage.class);
                    if(message.getReceiver().equals(myID) && message.getSender().equals(userID) ||
                            message.getReceiver().equals(userID) && message.getSender().equals(myID)){
                        messageList.add(message);
                    }
                    listener.OnMessageDataChanged(messageList);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void updateCurrentLocation(LatLng latLng, String time, String id){
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("Latlng", latLng);
        hashMap.put("time", time);

        this.database.getReference().child(id)
                .push()
                .setValue(hashMap);
    }

    public LatLng getLatestLocation(String userId) {
        final LatLng[] latestLocation = {null};

        DatabaseReference reference = this.database.getReference(userId);
        reference.limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot s : snapshot.getChildren()) {
                    HashMap<String, Object> data = (HashMap<String, Object>) s.getValue();
                    if (data != null && data.containsKey("Latlng")) {
                        HashMap<String, Double> latLngData = (HashMap<String, Double>) data.get("Latlng");
                        if (latLngData != null && latLngData.containsKey("latitude") && latLngData.containsKey("longitude")) {
                            double latitude = latLngData.get("latitude");
                            double longitude = latLngData.get("longitude");
                            latestLocation[0] = new LatLng(latitude, longitude);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle the error
            }
        });

        return latestLocation[0];
    }

    public void getDriverByID(String driverID, OnFetchListener<Driver> listener) {
        new Thread(() -> {
            this.firestore.collection(this.COLLECTION_DRIVERS)
                    .document(driverID)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                Driver driver = document.toObject(Driver.class);
                                listener.onFetchSuccess(driver);
                            } else {
                                listener.onFetchFailure("User Data not found");
                            }
                        } else {
                            listener.onFetchFailure("Error: " + Objects.requireNonNull(task.getException()).getMessage());
                        }
                    });
        }).start();
    }

    public void getAllDrivers(OnFetchDriverListListener listener){
        List<Driver> list = new ArrayList<>();
        new Thread(() -> {
            this.firestore.collection(this.COLLECTION_DRIVERS)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot document : task.getResult()) {
                                Driver driver = document.toObject(Driver.class);
                                list.add(driver);
                            }
                            listener.onFetchDriverListSuccess(list);
                        } else {
                            listener.onFetchDriverListFailure("Error fetching users: " + Objects.requireNonNull(task.getException()).getMessage());
                        }
                    });
        }).start();
    }

    public interface OnTaskCompleteListener {
        void onTaskSuccess(String message);
        void onTaskFailure(String message);
    }

    public interface OnFetchListener<T> {
        void onFetchSuccess(T object);
        void onFetchFailure(String message);
    }

    public interface OnFetchListListener<T>{
        void onFetchSuccess(List<T> object);
        void onFetchFailure(String message);
    }

    public interface OnFetchDriverListListener{
        void onFetchDriverListSuccess(List<Driver> list);
        void onFetchDriverListFailure(String message);
    }

    public interface OnRetrieveImageListener {
        void onRetrieveImageSuccess(Bitmap bitmap);
        void onRetrieveImageFailure(String message);
    }

    public interface OnReadingMessageListener{
        void OnMessageDataChanged(List<MyMessage> messageList);
    }
}

