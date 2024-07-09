package com.example.vehicletracking;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BusInfoAdapter extends RecyclerView.Adapter<BusInfoAdapter.ViewHolder> {

    private ArrayList<BusModel> busList;
    private Context context;
    private OnBusInfoListener onBusInfoListener;

    public BusInfoAdapter(ArrayList<BusModel> busList, Context context, OnBusInfoListener onBusInfoListener) {
        this.busList = busList;
        this.context = context;
        this.onBusInfoListener = onBusInfoListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.bus_item_design, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        BusModel bus = busList.get(position);

        holder.tvBusName.setText(bus.getBusName());
        holder.tvBusLocation.setText(bus.getBusRealAddress());
        holder.tvBusNumber.setText(bus.getBusNumber());
        holder.tvBusModel.setText(bus.getBusModel());

        if (bus.getBusImage() != null && !bus.getBusImage().isEmpty()) {
            Picasso.get().load(bus.getBusImage()).into(holder.ivBusImage);
        } else {
            holder.ivBusImage.setImageResource(R.drawable.placeholder_image);
        }

        holder.ivEdit.setOnClickListener(v -> {
            int adapterPosition = holder.getAdapterPosition();
            if (adapterPosition != RecyclerView.NO_POSITION) {
                onBusInfoListener.onEditClick(busList.get(adapterPosition), adapterPosition);
            }
        });

        holder.ivDelete.setOnClickListener(v -> {
            int adapterPosition = holder.getAdapterPosition();
            if (adapterPosition != RecyclerView.NO_POSITION) {
                onBusInfoListener.onDeleteClick(busList.get(adapterPosition), adapterPosition);
            }
        });

        holder.ivBusImage.setOnClickListener(v -> {
            int adapterPosition = holder.getAdapterPosition();
            if (adapterPosition != RecyclerView.NO_POSITION) {
                String busLocation = busList.get(adapterPosition).getBusRealAddress();
                navigateToMapFragment(busLocation);
            }
        });
    }

    private void navigateToMapFragment(String busLocation) {
        Geocoder geocoder = new Geocoder(context);
        try {
            List<Address> addresses = geocoder.getFromLocationName(busLocation, 1);
            if (!addresses.isEmpty()) {
                double latitude = addresses.get(0).getLatitude();
                double longitude = addresses.get(0).getLongitude();
                MapFragment mapFragment = MapFragment.newInstance(busLocation);
                FragmentManager fragmentManager = ((FragmentActivity) context).getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.container, mapFragment)
                        .addToBackStack(null)
                        .commit();
            } else {
                Toast.makeText(context, "Location not found: " + busLocation, Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return busList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivBusImage, ivEdit, ivDelete;
        private TextView tvBusName, tvBusLocation, tvBusModel, tvBusNumber;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivBusImage = itemView.findViewById(R.id.ivBusImage);
            tvBusName = itemView.findViewById(R.id.tvBusName);
            tvBusLocation = itemView.findViewById(R.id.tvBusLocation);
            tvBusModel = itemView.findViewById(R.id.tvBusModel);
            tvBusNumber = itemView.findViewById(R.id.tvBusNumber);
            ivEdit = itemView.findViewById(R.id.ivEdit);
            ivDelete = itemView.findViewById(R.id.ivDelete);
        }
    }

    public interface OnBusInfoListener {
        void onEditClick(BusModel bus, int position);
        void onDeleteClick(BusModel bus, int position);
    }
}

