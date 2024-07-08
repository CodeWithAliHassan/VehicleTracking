package com.example.vehicletracking;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class BusInfoAdapter extends RecyclerView.Adapter<BusInfoAdapter.ViewHolder> {

    private ArrayList<BusModel> busList;
    private Context context;

    public BusInfoAdapter(ArrayList<BusModel> busList, Context context) {
        this.busList = busList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.bus_item_design, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Bind data to views
        BusModel bus=busList.get(position);
        holder.tvBusName.setText(bus.getBusName());
        holder.tvBusLocation.setText(bus.getBusRealAddress());
        Picasso.get().load(bus.getBusImage()).into(holder.ivBusImage);

    }


    public int getItemCount() {
        return busList.size();
    }





    public static class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivBusImage;
        private TextView tvBusName,tvBusLocation,tvBusOpen,tvBusStart;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            ivBusImage=itemView.findViewById(R.id.ivBusImage);
            tvBusName=itemView.findViewById(R.id.tvBusName);
            tvBusLocation=itemView.findViewById(R.id.tvBusLocation);
            tvBusStart=itemView.findViewById(R.id.tvBusStart);
            tvBusOpen=itemView.findViewById(R.id.tvBusOpen);
            // Initialize your views here using itemView.findViewById()
        }
    }
}
