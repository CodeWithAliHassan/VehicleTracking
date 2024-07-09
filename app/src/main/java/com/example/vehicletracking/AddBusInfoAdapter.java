package com.example.vehicletracking;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.kaopiz.kprogresshud.KProgressHUD;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class AddBusInfoAdapter extends Fragment {

    private EditText etBusName, etBusModel, etBusNumber;
    private SwitchMaterial getLocation;
    private AppCompatButton btnUpload;
    private TextView tvBusLocation;
    private MaterialCardView selectPhoto;
    private ImageView ivBusPhoto;
    private Uri imageUri;
    private Bitmap bitmap;
    private FirebaseStorage firebaseStorage;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;
    private StorageReference storageReference;
    private String photoUrl;
    private String currentUserId;
    private String docId;
    private KProgressHUD kProgressHUD;
    private LocationManager locationManager;
    private static final int REQUEST_LOCATION = 1;
    private String busLongitude, busLattitude,address;

    public AddBusInfoAdapter() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_addbus_info, container, false);

        etBusName = view.findViewById(R.id.etBusName);
        etBusModel = view.findViewById(R.id.etBusModel);
        etBusNumber = view.findViewById(R.id.etBusNumber);
        tvBusLocation = view.findViewById(R.id.tvLocation);
        btnUpload = view.findViewById(R.id.btnUpload);
        selectPhoto = view.findViewById(R.id.selectPhoto);
        ivBusPhoto = view.findViewById(R.id.ivBusPhoto);
        getLocation = view.findViewById(R.id.getLocation);
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        currentUserId = firebaseAuth.getCurrentUser().getUid();

        selectPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkStoragePermission();
            }
        });

        getLocation.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (compoundButton.isChecked()) {
                    checkLocationPermission();
                }
            }
        });

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProgressbar();
                uploadImage();
                uploadBusInfo();
                getCurrentLocation();
            }
        });

        return view;
    }

    private void checkLocationPermission() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
        }
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            promptEnableGPS();
        } else {
            getCurrentLocation();
        }
    }



    private void showProgressbar() {
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

    private void checkStoragePermission() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            } else {
                pickImageFromGallery();
            }
        } else {
            pickImageFromGallery();
        }
    }

    private void promptEnableGPS() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage("Enable GPS").setCancelable(false)
                .setPositiveButton(
                        "Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                            }
                        }
                ).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        final AlertDialog dialog = builder.create();
        dialog.show();
    }
    private void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(
                getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
        } else {
            Location gpsLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (gpsLocation != null) {
                double latitude = gpsLocation.getLatitude();
                double longitude = gpsLocation.getLongitude();

                busLattitude = String.valueOf(latitude);
                busLongitude = String.valueOf(longitude);

                String location = busLattitude + ", " + busLongitude;
                tvBusLocation.setText(location);

                getLocationfromLongLat(getContext(),longitude,latitude);
            } else {
                Toast.makeText(getContext(), "Unable to find location", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private String getLocationfromLongLat(Context context, double longitude, double latitude) {
        try {
            Geocoder geocoder = new Geocoder(context, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses != null && addresses.size() > 0) {
                address = addresses.get(0).getAddressLine(0);
                // Update UI on the main thread
                requireActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tvBusLocation.setText(address);
                    }
                });
            }
        } catch (IOException e) {
            e.printStackTrace();
            // Log the error for debugging
            Log.e("GeocodingError", "Error getting address from coordinates", e);
            // Handle the error appropriately
            requireActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getContext(), "Error getting address: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
        return address;
    }


    private final ActivityResultLauncher<Intent> pickImageLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    if (data != null && data.getData() != null) {
                        imageUri = data.getData();
                        try {
                            bitmap = MediaStore.Images.Media.getBitmap(
                                    getActivity().getContentResolver(),
                                    imageUri
                            );
                            ivBusPhoto.setImageBitmap(bitmap);
                        } catch (IOException e) {
                            e.printStackTrace();
                            Toast.makeText(getContext(), "Failed to load image", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
    );

    private void pickImageFromGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        pickImageLauncher.launch(intent);
    }

    private void uploadImage() {
        if (imageUri != null) {
            final StorageReference myRef = storageReference.child("photo/" + imageUri.getLastPathSegment());
            myRef.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> {
                        myRef.getDownloadUrl().addOnSuccessListener(uri -> {
                            if (uri != null) {
                                photoUrl = uri.toString();
                            } else {
                                kProgressHUD.dismiss();
                                Toast.makeText(getContext(), "Failed to get download URL", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(e -> {
                            kProgressHUD.dismiss();
                            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
                    })
                    .addOnFailureListener(e -> {
                        kProgressHUD.dismiss();
                        Toast.makeText(getContext(), "Failed to upload image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        } else {
            kProgressHUD.dismiss();
            Toast.makeText(getContext(), "Please select an image", Toast.LENGTH_SHORT).show();
        }
    }


    private void uploadBusInfo() {
        String name = etBusName.getText().toString().trim();
        String model = etBusModel.getText().toString().trim();
        String number = etBusNumber.getText().toString().trim();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(model) || TextUtils.isEmpty(number)) {
            kProgressHUD.dismiss();
            Toast.makeText(getContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        DocumentReference documentReference = firebaseFirestore.collection("BusInfo").document();
        BusModel bus = new BusModel(name, model, number, busLongitude, busLattitude, photoUrl, documentReference.getId(),currentUserId,address);
        documentReference.set(bus)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            docId = documentReference.getId();
                            bus.setBusDocID(docId);
                            documentReference.set(bus, SetOptions.merge())
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            kProgressHUD.dismiss();
                                            if (task.isSuccessful()) {
                                                Toast.makeText(getContext(), "Uploaded Successfully", Toast.LENGTH_SHORT).show();
                                                etBusName.setText("");
                                                etBusModel.setText("");
                                                etBusNumber.setText("");
                                                tvBusLocation.setText("");
                                                ivBusPhoto.setImageResource(0);// Reset image view
                                            } else {
                                                Toast.makeText(getContext(), "Failed to upload data", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            kProgressHUD.dismiss();
                                            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        } else {
                            kProgressHUD.dismiss();
                            Toast.makeText(getContext(), "Failed to upload data", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        kProgressHUD.dismiss();
                        Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
