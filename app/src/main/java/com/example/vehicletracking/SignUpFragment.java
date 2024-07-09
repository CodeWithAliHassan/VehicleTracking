package com.example.vehicletracking;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.kaopiz.kprogresshud.KProgressHUD;

public class SignUpFragment extends Fragment {

    private EditText etName, etEmail, etMobileNumber, etPassword;
    private AppCompatButton btnSignup;
    private TextView tvSignInLink; // Link to switch to SignInFragment
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;

    private KProgressHUD kProgressHUD;

    public SignUpFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sign_up, container, false);

        etName = view.findViewById(R.id.etName);
        etEmail = view.findViewById(R.id.etEmail);
        etMobileNumber = view.findViewById(R.id.etMobileNumber);
        etPassword = view.findViewById(R.id.etPassword);
        btnSignup = view.findViewById(R.id.btnSignUp);
        tvSignInLink = view.findViewById(R.id.tvSignInLink);

        // Initialize Firebase Auth
        firebaseAuth = FirebaseAuth.getInstance();

        // Click listener for SignUp button
        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUpUser();
            }
        });

        
        tvSignInLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ((MainActivity) requireActivity()).loadSignInFragment();
            }
        });

        return view;
    }





    private void signUpUser() {
        String name = etName.getText().toString().trim();
        String phone = etMobileNumber.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(phone) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(getContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
        } else {
            Progressbar();
            firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        // User registered successfully
                        if (firebaseAuth.getCurrentUser() != null) {
                            String userId = firebaseAuth.getCurrentUser().getUid();
                            // Save user info to Firestore
                            saveUserInfoToFirestore(userId, name, phone, email, password);
                        }
                    } else {
                        // Registration failed
                        kProgressHUD.dismiss();
                        Toast.makeText(getContext(), "Registration failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void saveUserInfoToFirestore(String userId, String name, String phone, String email, String password) {
        firebaseFirestore = FirebaseFirestore.getInstance();
        DocumentReference userInfo = firebaseFirestore.collection("Users").document(userId);
        User user = new User(name, phone, email, password, userId);

        userInfo.set(user, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                kProgressHUD.dismiss();
                Toast.makeText(getContext(), "User registered successfully", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getContext(), HomeActivity.class);
                startActivity(intent);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                kProgressHUD.dismiss();
                Toast.makeText(getContext(), "Failed to save user info: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void Progressbar() {
        kProgressHUD = KProgressHUD.create(requireContext())
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setMaxProgress(100)
                .setBackgroundColor(R.color.lightblue)
                .setLabel("Please wait")
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f)
                .show();
        kProgressHUD.setProgress(98);
    }
}
