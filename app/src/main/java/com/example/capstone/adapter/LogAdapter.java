package com.example.capstone.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.capstone.R;
import com.example.capstone.model.Log;

import java.util.List;

public class LogAdapter extends RecyclerView.Adapter<LogAdapter.LogViewHolder> {

    private List<Log> logList;

    public LogAdapter(List<Log> logList) {
        this.logList = logList;
    }

    @NonNull
    @Override
    public LogAdapter.LogViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_log, parent, false);
        return new LogViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LogAdapter.LogViewHolder holder, int position) {
        Log log = logList.get(position);
        holder.timestampTextView.setText(log.getTimestamp());
        holder.temperatureTextView.setText("Nhiệt độ: " + log.getTemperature() + "°C");
        holder.humidityTextView.setText("Độ ẩm: " + log.getHumidity() + "%");
        holder.soilMoisture1TextView.setText("Độ ẩm đất 1: " + log.getSoilMoisture() + "%");
        holder.soilMoisture2TextView.setText("Độ ẩm đất 2: " + log.getSoilMoisture2() + "%");
    }

    @Override
    public int getItemCount() {
        return logList.size();
    }

    public static class LogViewHolder extends RecyclerView.ViewHolder {
        TextView timestampTextView, temperatureTextView, humidityTextView, soilMoisture1TextView, soilMoisture2TextView;

        public LogViewHolder(@NonNull View itemView) {
            super(itemView);
            timestampTextView = itemView.findViewById(R.id.timestampTextView);
            temperatureTextView = itemView.findViewById(R.id.temperatureTextView);
            humidityTextView = itemView.findViewById(R.id.humidityTextView);
            soilMoisture1TextView = itemView.findViewById(R.id.soilMoisture1TextView);
            soilMoisture2TextView = itemView.findViewById(R.id.soilMoisture2TextView);
        }
    }
}
