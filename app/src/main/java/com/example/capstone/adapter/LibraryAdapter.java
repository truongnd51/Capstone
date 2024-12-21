package com.example.capstone.adapter;

import android.annotation.SuppressLint;
import android.text.SpannableStringBuilder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.capstone.R;
import com.example.capstone.model.Library;
import com.example.capstone.ui.HomeFragment;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DecimalFormat;
import java.util.List;

public class LibraryAdapter extends RecyclerView.Adapter<LibraryAdapter.LibraryViewHolder> {

    private List<Library> libraryList;
    private OnLibraryItemClickListener listener;
    private OnShareLibraryListener shareListener;
    private OnUpdateLibraryListener updateLibraryListener;


    public LibraryAdapter(List<Library> libraryList, OnLibraryItemClickListener listener, OnShareLibraryListener shareListener, OnUpdateLibraryListener updateLibraryListener) {
        this.libraryList = libraryList;
        this.listener = listener;
        this.shareListener = shareListener;
        this.updateLibraryListener = updateLibraryListener;
    }

    @NonNull
    @Override
    public LibraryAdapter.LibraryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_library, parent, false);
        return new LibraryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LibraryAdapter.LibraryViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Library library = libraryList.get(position);

        // Giữ nguyên các TextView
        holder.ID.setText("ID: " + String.valueOf(library.getID()));
        holder.ID.setVisibility(View.GONE); // Ẩn ID

        // Loại cây
        holder.CategoryID.setText("Loại cây: " + String.valueOf(library.getCategoryID()));
        holder.CategoryID.setVisibility(View.GONE);

        // Tên cây
        holder.Name.setText("Tên cây: " + String.valueOf(library.getName()));

        // Định dạng độ ẩm
        DecimalFormat df = new DecimalFormat("#.##");
        SpannableStringBuilder humidityBuilder = new SpannableStringBuilder();
        humidityBuilder.append("Độ ẩm: ").append(df.format(library.getHumidity())).append("%");
        humidityBuilder.append(" - ").append(df.format(library.getHumidityUp())).append("%");
        holder.Humidity.setText(humidityBuilder);

        // Định dạng nhiệt độ
        SpannableStringBuilder tempBuilder = new SpannableStringBuilder();
        tempBuilder.append("Nhiệt độ: ").append(df.format(library.getTemp())).append("°C");
        tempBuilder.append(" - ").append(df.format(library.getTempUp())).append("°C");
        holder.Temp.setText(tempBuilder);

        // Định dạng độ ẩm đất
        SpannableStringBuilder soilBuilder = new SpannableStringBuilder();
        soilBuilder.append("Độ ẩm đất: ").append(df.format(library.getSoilDown())).append("%");
        soilBuilder.append(" - ").append(df.format(library.getSoilUp())).append("%");
        holder.SoilDown.setText(soilBuilder);

        // Hiển thị thời gian bật
        SpannableStringBuilder lightBuilder = new SpannableStringBuilder();
        lightBuilder.append("Thời gian bật đèn: ").append(df.format(library.getLightOn())).append(" tiếng/ngày");
        holder.LightOff.setText(lightBuilder);

        // Hiển thị thời gian tắt
        SpannableStringBuilder lightOnBuilder = new SpannableStringBuilder();
        lightOnBuilder.append("Thời gian tắt đèn: ").append(df.format(library.getLightOff())).append(" ngày đầu");
        holder.LightOn.setText(lightOnBuilder);

        // Thêm hiệu ứng cho các nút
        holder.selectButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onLibraryItemSelected(library);
            }
        });

        // Kiểm tra để hiển thị nút chia sẻ nếu có UserID
        if (library.getUserID() != 0) {
            holder.shareButton.setVisibility(View.VISIBLE);
            holder.shareButton.setOnClickListener(v -> {
                if (shareListener != null) {
                    shareListener.onShareLibrary(library);
                }
            });
        } else {
            holder.shareButton.setVisibility(View.GONE);
        }

        if (library.getUserID() != 0) {
            holder.updateButton.setVisibility(View.VISIBLE);
            holder.updateButton.setOnClickListener(v -> {
                if (updateLibraryListener != null) {
                    updateLibraryListener.onUpdateLibrary(library);
                }
            });
        } else {
            holder.updateButton.setVisibility(View.GONE);
        }
    }


    @Override
    public int getItemCount() {
        return libraryList.size();
    }

    public static class LibraryViewHolder extends RecyclerView.ViewHolder {

        public TextView ID;
        public TextView CategoryID;
        public TextView Name;
        public TextView Humidity;
        public TextView HumidityUp;
        public TextView Temp;
        public TextView TempUp;
        public TextView LightOn;
        public TextView LightOff;
        public TextView SoilDown;
        public TextView SoilUp;
        public Button selectButton;
        public Button shareButton;
        public Button updateButton;

        public LibraryViewHolder(View itemView) {
            super(itemView);
            ID = itemView.findViewById(R.id.ID);
            CategoryID = itemView.findViewById(R.id.CategoryID);
            Name = itemView.findViewById(R.id.Library_Name);
            Humidity = itemView.findViewById(R.id.Humidity);
            HumidityUp = itemView.findViewById(R.id.HumidityUp);
            Temp = itemView.findViewById(R.id.Temp);
            TempUp = itemView.findViewById(R.id.TempUp);
            SoilDown = itemView.findViewById(R.id.SoilDown);
            SoilUp = itemView.findViewById(R.id.SoilUp);
            LightOn = itemView.findViewById(R.id.LightOn);
            LightOff = itemView.findViewById(R.id.LightOff);
            selectButton = itemView.findViewById(R.id.selectButton);
            shareButton  = itemView.findViewById(R.id.shareButton);
            updateButton = itemView.findViewById(R.id.updateButton);
        }
    }

    public interface OnLibraryItemClickListener {
        void onLibraryItemSelected(Library library);
    }

    public interface OnShareLibraryListener {
        void onShareLibrary(Library library);
    }

    public interface OnUpdateLibraryListener {
        void onUpdateLibrary(Library library);
    }

    // Trong LibraryAdapter
    public void updateData(List<Library> newData) {
        this.libraryList.clear();
        this.libraryList.addAll(newData);
        notifyDataSetChanged();
    }


}
