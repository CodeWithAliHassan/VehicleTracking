package com.example.vehicletracking;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class GarageFragment extends Fragment implements BusInfoAdapter.OnBusInfoListener {
    private FirebaseFirestore firebaseFirestore;
    private BusInfoAdapter busInfoAdapter;
    private ArrayList<BusModel> busList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_garage, container, false);

        firebaseFirestore = FirebaseFirestore.getInstance();
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        busList = new ArrayList<>();

        // Initialize adapter with listener
        busInfoAdapter = new BusInfoAdapter(busList, getActivity(), this);
        recyclerView.setAdapter(busInfoAdapter);

        // Fetch data from Firestore
        fetchBusData();

        return view;
    }

    private void fetchBusData() {
        firebaseFirestore.collection("BusInfo")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        busList.clear(); // Clear existing data
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            BusModel bus = document.toObject(BusModel.class);
                            busList.add(bus);
                        }
                        busInfoAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(getActivity(), "Error getting documents.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onEditClick(BusModel bus, int position) {
        // Inflate the dialog with custom view
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View dialogView = inflater.inflate(R.layout.fragment_addbus_info, null);

        // Build the dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(dialogView);
        builder.setTitle("Edit Bus");

        // Get the EditText from the dialog
        EditText etBusName = dialogView.findViewById(R.id.etBusName);
        EditText etBusNumber = dialogView.findViewById(R.id.etBusNumber);
        EditText etBusModel = dialogView.findViewById(R.id.etBusModel);

        // Set current values
        etBusName.setText(bus.getBusName());
        etBusNumber.setText(bus.getBusNumber());
        etBusModel.setText(bus.getBusModel());

        builder.setPositiveButton("Save", (dialog, which) -> {
            String newBusName = etBusName.getText().toString();
            String newBusNumber = etBusNumber.getText().toString();
            String newBusModel = etBusModel.getText().toString();

            // Update bus model
            bus.setBusName(newBusName);
            bus.setBusNumber(newBusNumber);
            bus.setBusModel(newBusModel);

            firebaseFirestore.collection("BusInfo").document(bus.getBusDocID())
                    .set(bus)
                    .addOnSuccessListener(aVoid -> {
                        busList.set(position, bus);
                        busInfoAdapter.notifyItemChanged(position);
                        Toast.makeText(getActivity(), "Bus updated", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> Toast.makeText(getActivity(), "Error updating bus", Toast.LENGTH_SHORT).show());
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        builder.create().show();
    }

    @Override
    public void onDeleteClick(BusModel bus, int position) {
        // Handle delete action
        firebaseFirestore.collection("BusInfo").document(bus.getBusDocID())
                .delete()
                .addOnSuccessListener(aVoid -> {
                    busList.remove(position);
                    busInfoAdapter.notifyItemRemoved(position);
                    Toast.makeText(getActivity(), "Bus deleted", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> Toast.makeText(getActivity(), "Error deleting bus", Toast.LENGTH_SHORT).show());
    }
}
