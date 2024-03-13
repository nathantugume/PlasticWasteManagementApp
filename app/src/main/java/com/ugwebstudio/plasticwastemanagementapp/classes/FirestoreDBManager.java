package com.ugwebstudio.plasticwastemanagementapp.classes;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;


import java.util.Map;

public class FirestoreDBManager {

    private static final String TAG = "FirestoreDBManager";

    private FirebaseFirestore db;
    private CollectionReference collectionReference;
    private CollectionReference usersCollection;

    public FirestoreDBManager() {
        // Initialize Firestore instance
        db = FirebaseFirestore.getInstance();
        // Get a reference to the "items" collection
        collectionReference = db.collection("items");
        usersCollection = db.collection("users"); // Initialize usersCollection

    }

    // Add a new document with a generated ID
    public void addItem(Map<String, Object> itemData, final OnCompleteListener<DocumentReference> onCompleteListener) {
        collectionReference.add(itemData)
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "Document added with ID: " + task.getResult().getId());
                            onCompleteListener.onComplete(task);
                        } else {
                            Log.w(TAG, "Error adding document", task.getException());
                        }
                    }
                });
    }

    // Get all items from the collection
    public void getAllItems(final OnCompleteListener<QuerySnapshot> onCompleteListener) {
        collectionReference.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            onCompleteListener.onComplete(task);
                        } else {
                            Log.w(TAG, "Error getting documents", task.getException());
                        }
                    }
                });
    }

    // Update an existing item
    public void updateItem(String itemId, Map<String, Object> updatedData, final OnCompleteListener<Void> onCompleteListener) {
        collectionReference.document(itemId).update(updatedData)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "Document updated successfully");
                            onCompleteListener.onComplete(task);
                        } else {
                            Log.w(TAG, "Error updating document", task.getException());
                        }
                    }
                });
    }

    // Delete an item
    public void deleteItem(String itemId, final OnCompleteListener<Void> onCompleteListener) {
        collectionReference.document(itemId).delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "Document deleted successfully");
                            onCompleteListener.onComplete(task);
                        } else {
                            Log.w(TAG, "Error deleting document", task.getException());
                        }
                    }
                });
    }

    public void addUser(User user, OnUserAddedListener listener) {
        usersCollection.document(user.getEmail()).set(user)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(Task<Void> task) {
                        if (task.isSuccessful()) {
                            listener.onUserAdded(true);
                        } else {
                            listener.onUserAdded(false);
                        }
                    }
                });
    }

    // Define other database operations here as needed

    public interface OnUserAddedListener {
        void onUserAdded(boolean success);
    }
}
