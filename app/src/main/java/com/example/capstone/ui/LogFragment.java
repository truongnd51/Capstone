package com.example.capstone.ui;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.capstone.R;
import com.example.capstone.adapter.LogAdapter;
import com.example.capstone.model.Log;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LogFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LogFragment extends Fragment {

    private RecyclerView recyclerView;
    private LogAdapter logAdapter;
    private List<Log> logDataList;
    private LineChart temperatureChart, humidityChart, soilMoisture1Chart, soilMoisture2Chart;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public LogFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LogFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LogFragment newInstance(String param1, String param2) {
        LogFragment fragment = new LogFragment();
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
        View view = inflater.inflate(R.layout.fragment_log, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        logDataList = new ArrayList<>();
        logAdapter = new LogAdapter(logDataList);
        recyclerView.setAdapter(logAdapter);

        loadDataFromRealtime();

        temperatureChart = view.findViewById(R.id.temperatureChart);
        humidityChart = view.findViewById(R.id.humidityChart);
        soilMoisture1Chart = view.findViewById(R.id.soilMoisture1Chart);
        soilMoisture2Chart = view.findViewById(R.id.soilMoisture2Chart);


        return view;
    }

    private void loadDataFromRealtime() {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference();
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        databaseReference.child("logs").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Log log = dataSnapshot.getValue(Log.class);
                    if (log != null) {
                        // Kiểm tra nếu log đã có trong Firestore hay chưa
                        firestore.collection("Log")
                                .whereEqualTo("timestamp", log.getTimestamp())
                                .get()
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        // Kiểm tra xem có bản ghi nào chưa
                                        if (task.getResult().isEmpty()) {
                                            // Nếu không có, thêm log vào Firestore
                                            firestore.collection("Log")
                                                    .document(log.getTimestamp()) // Dùng timestamp làm ID tài liệu
                                                    .set(log) // Thay vì add(), sử dụng set() để đảm bảo chỉ có một bản
                                                    .addOnSuccessListener(documentReference -> {
                                                        // Thành công khi lưu log lên Firestore
                                                    })
                                                    .addOnFailureListener(e -> {
                                                        // Xử lý lỗi khi lưu log lên Firestore
                                                        Toast.makeText(getContext(), "Lỗi khi lưu log lên Firestore", Toast.LENGTH_SHORT).show();
                                                    });
                                        }
                                    }
                                });
                    }
                }
                logAdapter.notifyDataSetChanged();
                loadLogDataFromFirestore();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Lỗi khi lấy dữ liệu từ Realtime Database", Toast.LENGTH_SHORT).show();
            }
        });
    }




    private void loadLogDataFromFirestore() {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection("Log")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .limit(50)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        logDataList.clear(); // Xóa sạch logDataList trước khi cập nhật
                        List<Entry> temperatureEntries = new ArrayList<>();
                        List<Entry> humidityEntries = new ArrayList<>();
                        List<Entry> soilMoisture1Entries = new ArrayList<>();
                        List<Entry> soilMoisture2Entries = new ArrayList<>();

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Log log = document.toObject(Log.class);

                            boolean exists = false;
                            for (Log existingLog : logDataList) {
                                if (existingLog.getTimestamp().equals(log.getTimestamp())) {
                                    exists = true;
                                    break;
                                }
                            }

                            if (!exists) {
                                logDataList.add(log);

                                int currentIndex = logDataList.size() - 1; // Xác định chỉ số dựa trên kích thước danh sách
                                temperatureEntries.add(new Entry(currentIndex, log.getTemperature()));
                                humidityEntries.add(new Entry(currentIndex, log.getHumidity()));
                                soilMoisture1Entries.add(new Entry(currentIndex, log.getSoilMoisture()));
                                soilMoisture2Entries.add(new Entry(currentIndex, log.getSoilMoisture2()));
                            }
                        }

                        logAdapter.notifyDataSetChanged();

                        // Thiết lập biểu đồ sau khi dữ liệu đã được cập nhật
                        setupChart(temperatureChart, temperatureEntries, "Nhiệt độ không khí", Color.RED);
                        setupChart(humidityChart, humidityEntries, "Độ ẩm không khí", Color.BLUE);
                        setupChart(soilMoisture1Chart, soilMoisture1Entries, "Độ ẩm đất 1", Color.GREEN);
                        setupChart(soilMoisture2Chart, soilMoisture2Entries, "Độ ẩm đất 2", Color.MAGENTA);

                    } else {
                        Toast.makeText(getContext(), "Lỗi tải dữ liệu", Toast.LENGTH_SHORT).show();
                    }
                });
    }


    private void setupChart(LineChart chart, List<Entry> entries, String label, int color) {
        LineDataSet dataSet = new LineDataSet(entries, label);
        dataSet.setColor(color);
        dataSet.setCircleColor(color);
        dataSet.setDrawValues(false); // Disable drawing values on data points

        LineData lineData = new LineData(dataSet);
        chart.setData(lineData);

        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setLabelCount(21, true); // Show exactly 21 labels for intervals
        xAxis.setGranularity(1f); // Ensure labels are spaced 1 unit apart
        xAxis.setAxisMinimum(0f); // Start x-axis from 1
        xAxis.setAxisMaximum(20f);

        // Customize y-axis range
        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setAxisMinimum(0f); // Set minimum value for y-axis to 10
        leftAxis.setAxisMaximum(100f); // Set maximum value for y-axis to 80

        // Set the point where x and y intersect at (0, 10)
        leftAxis.setSpaceBottom(0f); // Remove extra space below the minimum y value
        xAxis.setSpaceMin(0f);       // Start x-axis exactly at 0

        // Remove the right y-axis
        YAxis rightAxis = chart.getAxisRight();
        rightAxis.setEnabled(false);

        // Remove the description label
        chart.getDescription().setEnabled(false);

        chart.invalidate();;
    }

}