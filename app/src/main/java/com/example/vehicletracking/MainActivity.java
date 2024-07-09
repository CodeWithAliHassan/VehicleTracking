package com.example.vehicletracking;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Replace the container with SignUpFragment by default
        loadSignInFragment();
    }

    // Method to replace fragments within the container
    public void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container, fragment);
        fragmentTransaction.addToBackStack(null); // Optional: Adding to back stack
        fragmentTransaction.commit();
    }

    // Method to switch to SignInFragment
    public void loadSignInFragment() {
        replaceFragment(new SignInFragment());
    }

    // Method to switch to SignUpFragment
    public void loadSignUpFragment() {
        replaceFragment(new SignUpFragment());
    }
}
