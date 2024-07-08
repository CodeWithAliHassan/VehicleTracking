package com.example.vehicletracking;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfileFragment extends Fragment {

    private TextView tvUsername, tvEmail, tvPassword;
    private FirebaseFirestore db;
    private FirebaseUser currentUser;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Initialize views
        tvUsername = view.findViewById(R.id.tvUsername);
        tvEmail = view.findViewById(R.id.tvEmail);
        tvPassword = view.findViewById(R.id.tvPassword);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        // Fetch and display user details
        if (currentUser != null) {
            db.collection("Users")
                    .document(currentUser.getUid())
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String username = documentSnapshot.getString("userName");
                            String email = currentUser.getEmail(); // Use FirebaseUser for email
                            String password = documentSnapshot.getString("userPassword");

                            // Set text to TextViews
                            tvUsername.setText("Username: " + username);
                            tvEmail.setText("Email: " + email);
                            tvPassword.setText("Password: " + password);
                        }
                    })
                    .addOnFailureListener(e -> {
                        // Handle any errors
                        tvUsername.setText("Failed to fetch data");
                        tvEmail.setText("");
                        tvPassword.setText("");
                    });
        }

        return view;
    }
}
