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
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.kaopiz.kprogresshud.KProgressHUD;

public class SignInFragment extends Fragment {

    private EditText etLEmail, etLPassword;
    private AppCompatButton btnSignIn;
    private TextView tvSignUpLink; // Link to switch to SignUpFragment
    private FirebaseAuth firebaseAuth;
    private KProgressHUD kProgressHUD;




    public SignInFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sign_in, container, false);

        etLEmail = view.findViewById(R.id.etLEmail);
        etLPassword = view.findViewById(R.id.etLPassword);
        btnSignIn = view.findViewById(R.id.btnSignIn);
        tvSignUpLink = view.findViewById(R.id.tvSignUpLink);
        // Initialize Firebase Auth
        firebaseAuth = FirebaseAuth.getInstance();
        // Click listener for SignIn button
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInUser();
            }
        });

        // Click listener for "Not registered yet? Sign Up" link
        tvSignUpLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to SignUpFragment
                ((MainActivity) requireActivity()).loadSignUpFragment();
            }
        });



        return view;
    }


    private void signInUser() {
        String email = etLEmail.getText().toString().trim();
        String password = etLPassword.getText().toString().trim();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(getContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
        } else {
            Progressbar();
            firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        // Sign in success, navigate to MainActivity or desired activity
                        kProgressHUD.dismiss();
                        Intent intent = new Intent(getContext(), HomeActivity.class);
                        startActivity(intent);
                    } else {
                        // Sign in failed
                        kProgressHUD.dismiss();
                        Toast.makeText(getContext(), "Sign in failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
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
