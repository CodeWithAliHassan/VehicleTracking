package com.example.vehicletracking;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class GarageFragment extends Fragment {
    private FirebaseFirestore firebaseFirestore;
    private RecyclerView recyclerView;
    private BusInfoAdapter busInfoAdapter;
    private ArrayList<BusModel> busList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_garage, container, false);

        firebaseFirestore = FirebaseFirestore.getInstance();
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        busList = new ArrayList<>();

        // Add hardcoded bus data
        busList.add(new BusModel("Bus A", "Model A", "12345", "35.6895", "139.6917", "bus_image_a.jpeg", "bus_doc_id_a", "user_id_1", "Bus A Address"));
        busList.add(new BusModel("Bus B", "Model B", "67890", "40.7128", "-74.0060", "bus_image_b.jpg", "bus_doc_id_b", "user_id_2", "Bus B Address"));
        busList.add(new BusModel("Bus C", "Model C", "54321", "51.5074", "-0.1278", "bus_image_c.jpg", "bus_doc_id_c", "user_id_3", "Bus C Address"));
        busList.add(new BusModel("Bus D", "Model D", "98765", "48.8566", "2.3522", "bus_image_d.jpg", "bus_doc_id_d", "user_id_4", "Bus D Address"));
        busList.add(new BusModel("Bus E", "Model E", "13579", "37.7749", "-122.4194", "bus_image_e.jpg", "bus_doc_id_e", "user_id_5", "Bus E Address"));
        busList.add(new BusModel("Bus F", "Model F", "24680", "52.5200", "13.4050", "bus_image_f.jpg", "bus_doc_id_f", "user_id_6", "Bus F Address"));
        busList.add(new BusModel("Bus G", "Model G", "11223", "-33.8688", "151.2093", "bus_image_g.jpg", "bus_doc_id_g", "user_id_7", "Bus G Address"));

        // Initialize adapter
        busInfoAdapter = new BusInfoAdapter(busList, getActivity());
        recyclerView.setAdapter(busInfoAdapter);

        return view;
    }
}
