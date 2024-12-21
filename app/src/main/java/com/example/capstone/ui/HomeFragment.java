package com.example.capstone.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.capstone.R;
import com.example.capstone.adapter.ActuatorAdapter;
import com.example.capstone.model.Actuator;
import com.example.capstone.model.Library;
import com.example.capstone.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

    private TextView temp, humidity, moisture1, moisture2;

    private TextView libraryTemp, libraryHumidity, librarySoil, librarySoil2;
    private TextView treeName;

    private TextView warningButton;

    private RecyclerView recyclerView;
    private ActuatorAdapter actuatorAdapter;
    private List<Actuator> actuatorList;

    String humidityValue;
    String soilMoistureValue;
    String soilMoistureValue2;
    String tempValue;

    private Switch switchButton;
    private boolean isAutoMode = false;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Return the view
        recyclerView = view.findViewById(R.id.actuator_device_list);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);
        recyclerView.setLayoutManager(gridLayoutManager);

        // Tạo danh sách sản phẩm
        actuatorList = new ArrayList<>();
        actuatorList.add(new Actuator("Pump", R.drawable.fertilized));
        actuatorList.add(new Actuator("Spray", R.drawable.phunsuong));
        actuatorList.add(new Actuator("Light", R.drawable.light));
        actuatorList.add(new Actuator("Fan", R.drawable.fan));
        // Thêm nhiều sản phẩm khác

        // Khởi tạo Adapter và gán cho RecyclerView
        actuatorAdapter = new ActuatorAdapter(actuatorList, getContext());
        recyclerView.setAdapter(actuatorAdapter);

        temp = view.findViewById(R.id.nhietdo);
        humidity = view.findViewById(R.id.doamkk);
        moisture1 = view.findViewById(R.id.doamdat1);
        moisture2 = view.findViewById(R.id.doamdat2);

        libraryTemp = view.findViewById(R.id.selected_temp);
        libraryHumidity = view.findViewById(R.id.selected_humidity);
        treeName = view.findViewById(R.id.tree_name);
        librarySoil = view.findViewById(R.id.selected_soil);
        librarySoil2 = view.findViewById(R.id.selected_soil2);


        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = database.getReference();

        warningButton = view.findViewById(R.id.warning_button);
        warningButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference Emergency = database.getReference("devices");
                Emergency.child("emergency").setValue("true");

                DatabaseReference PumpSta = database.getReference("devices").child("Pump");
                PumpSta.child("status").setValue("off");

                DatabaseReference FanSta = database.getReference("devices").child("Fan");
                FanSta.child("status").setValue("off");

                DatabaseReference SpraySta = database.getReference("devices").child("Spray");
                SpraySta.child("status").setValue("off");

                DatabaseReference LightSta = database.getReference("devices").child("Light");
                LightSta.child("status").setValue("off");

                DatabaseReference Mode = database.getReference("devices");
                Mode.child("Mode").setValue("Manual");

                Toast.makeText(getActivity(), "Toàn bộ thiết bị sẽ bị tắt - Chế độ sẽ chuyển về thủ công", Toast.LENGTH_SHORT).show();
            }
        });

        DatabaseReference modeStautus = database.getReference("devices").child("Mode");
        modeStautus.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String status = dataSnapshot.getValue(String.class);
                boolean isOn = "Auto".equals(status);
                switchButton.setChecked(isOn);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Xử lý lỗi nếu có
            }
        });

        // Read from the database
        databaseReference.child("readings/humidity").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                humidityValue = snapshot.getValue(String.class);
                humidity.setText(humidityValue + "%");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        databaseReference.child("readings/soilMoisture").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                soilMoistureValue = snapshot.getValue(String.class);
                moisture1.setText(soilMoistureValue + "%");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        databaseReference.child("readings/soilMoisture2").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                soilMoistureValue2 = snapshot.getValue(String.class);
                moisture2.setText(soilMoistureValue2 + "%");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        databaseReference.child("readings/temperature").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                tempValue = snapshot.getValue(String.class);
                temp.setText(tempValue + "°C");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        switchButton = view.findViewById(R.id.mode_switch);

        //Thiết lập OnCheckedChangeListener cho switch button
        switchButton.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (!isAutoMode) {
                // Nếu không ở chế độ Auto, gửi trạng thái on/off
                sendDataToFirebase(isChecked ? "Auto" : "Manual");
            }
        });

        SharedPreferences sharedPreferences = getContext().getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE);
        int LibraryIDChoose = sharedPreferences.getInt("LibraryID", 0);
        Log.d("LibraryID", "LibraryIDChoose: " + LibraryIDChoose);


        FirebaseFirestore db = FirebaseFirestore.getInstance();

        if (LibraryIDChoose != 0) {
            // Truy vấn thư viện từ Firebase theo LibraryIDChoose
            db.collection("Library")
                    .whereEqualTo("id", LibraryIDChoose)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            QuerySnapshot querySnapshot = task.getResult();
                            if (!querySnapshot.isEmpty()) {
                                // Duyệt qua tất cả các tài liệu trong QuerySnapshot
                                for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                                    // Chuyển đổi tài liệu từ Firestore thành đối tượng Library
                                    Library library = document.toObject(Library.class);
                                    if (library != null) {
                                        updateSelectedLibrary(library);
                                        break;  // Nếu bạn chỉ cần lấy một tài liệu đầu tiên, có thể break
                                    }
                                }
                            } else {
                                Log.d("HomeFragment", "No matching documents!");
                            }
                        } else {
                            Log.d("HomeFragment", "Get failed with ", task.getException());
                        }
                    });

        }

        return view;
    }

    public void updateSelectedLibrary(Library library) {
        if (library != null) {
            if (temp != null && humidity != null) {
                String combinedTemperature = library.getTemp() + "°C" + " - " + library.getTempUp() + "°C";
                String combinedHumidity = library.getHumidity() + "%" + " - " + library.getHumidityUp() + "%";
                String combinedSoil = library.getSoilDown() + "%" + " - " + library.getSoilUp() + "%";
                String combinedSoil2 = library.getSoilDown() + "%" + " - " + library.getSoilUp() + "%";
                String name = library.getName();
                treeName.setText("Tên cây: " + name);
                libraryTemp.setText(" ( " + combinedTemperature + " ) ");
                libraryHumidity.setText(" ( " + combinedHumidity + " ) ");
                librarySoil.setText(" ( " + combinedSoil + " ) ");
                librarySoil2.setText(" ( " + combinedSoil2 + " ) ");
            }
        }
    }

    private void sendDataToFirebase(String mode) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("devices");
        databaseReference.child("Mode").setValue(mode);
        // Nếu là chế độ Auto, không gửi trạng thái on/off
    }

}