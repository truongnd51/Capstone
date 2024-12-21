package com.example.capstone.adapter;

import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.capstone.R;
import com.example.capstone.model.Actuator;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class ActuatorAdapter extends RecyclerView.Adapter<ActuatorAdapter.ActuatorViewHolder> {

    public List<Actuator> actuatorList;
    public Context context;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private boolean isAutoMode = false; // Biến theo dõi trạng thái chế độ

    public ActuatorAdapter(List<Actuator> actuatorList, Context context) {
        this.actuatorList = actuatorList;
        this.context = context;

        DatabaseReference modeRef = FirebaseDatabase.getInstance().getReference("devices").child("Mode");
        modeRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String mode = snapshot.getValue(String.class);
                isAutoMode = "Auto".equalsIgnoreCase(mode); // Cập nhật trạng thái chế độ
                if(isAutoMode) {
                    for (Actuator device : actuatorList) {
                        DatabaseReference deviceRef = database.getReference("devices").child(device.getName()).child("status");
                        deviceRef.setValue("off");
                        device.setOn(false);
                    }
                }
                notifyDataSetChanged(); // Làm mới danh sách để áp dụng thay đổi
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Xử lý lỗi nếu có
            }
        });
    }

    @NonNull
    @Override
    public ActuatorAdapter.ActuatorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_layout, parent, false);
        return new ActuatorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ActuatorAdapter.ActuatorViewHolder holder, int position) {
        Actuator actuator = actuatorList.get(position);
        holder.actuatorImage.setImageResource(actuator.getImage());
        holder.actuatorName.setText(actuator.getName());

        holder.switchButton.setChecked(actuator.isOn());

        holder.switchButton.setEnabled(!isAutoMode);

        if (!isAutoMode) {
            holder.switchButton.setOnCheckedChangeListener((buttonView, isChecked) -> {
                actuator.setOn(isChecked);
                DatabaseReference deviceRef = database.getReference("devices").child(actuator.getName()).child("status");
                deviceRef.setValue(isChecked ? "on" : "off");
            });
        } else {
            // Khi ở chế độ Auto, xóa sự kiện lắng nghe
            holder.switchButton.setOnCheckedChangeListener(null);
        }

        // Đồng bộ trạng thái từ Firebase cho thiết bị
        DatabaseReference deviceStatusRef = database.getReference("readings").child(actuator.getName()).child("status");
        deviceStatusRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String status = dataSnapshot.getValue(String.class);
                boolean isOn = "on".equals(status);
                holder.switchButton.setChecked(isOn);  // Cập nhật trạng thái cho Switch
                actuator.setOn(isOn);  // Cập nhật trạng thái trong danh sách
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Xử lý lỗi nếu có
            }
        });
    }


    @Override
    public int getItemCount() {
        return actuatorList.size();
    }

    public class ActuatorViewHolder extends RecyclerView.ViewHolder{

        ImageView actuatorImage;
        TextView actuatorName;
        Switch switchButton;


        public ActuatorViewHolder(@NonNull View itemView) {
            super(itemView);
            actuatorImage = itemView.findViewById(R.id.image);
            actuatorName = itemView.findViewById(R.id.name);
            switchButton = itemView.findViewById(R.id.product_switch);
        }
    }

}
