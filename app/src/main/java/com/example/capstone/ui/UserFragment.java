package com.example.capstone.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.capstone.R;
import com.example.capstone.inter.WeatherAPI;
import com.example.capstone.model.WeatherResponse;
import com.example.capstone.retro.RetrofitClient;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UserFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UserFragment extends Fragment {

    private Button btnLogout, btnQDSD, btnDKDV, btnCSBM, btnCall, btnHDSD;
    private TextView nametextv;

    private TextView cityTextView;
    private TextView descriptionTextView;
    private TextView windTextView;
    private TextView temperatureTextView;
    private TextView humidityTextView;

    private static final String WEATHER_API_KEY = "4c9b43253049bd15c8daa03ad1140cee"; // Thay bằng API key của bạn
    private static final String CITY_NAME = "Hanoi"; // Tên thành phố bạn muốn lấy thời tiết
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public UserFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment UserFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static UserFragment newInstance(String param1, String param2) {
        UserFragment fragment = new UserFragment();
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
        View view = inflater.inflate(R.layout.fragment_user, container, false);

        cityTextView = view.findViewById(R.id.cityTextView);
        descriptionTextView = view.findViewById(R.id.descriptionTextView);
        windTextView = view.findViewById(R.id.windTextView);
        temperatureTextView = view.findViewById(R.id.temperatureTextView);
        humidityTextView = view.findViewById(R.id.humidityTextView);

        // Gửi yêu cầu API để lấy thông tin thời tiết
        fetchWeatherData();

        btnLogout = view.findViewById(R.id.logoutButton);
        btnLogout.setOnClickListener(v -> logoutUser());
        // Trong phương thức onCreateView hoặc tương tự của fragment
        nametextv = view.findViewById(R.id.nameTextView);
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE);
        String name = sharedPreferences.getString("Username", null); // Lấy tên người dùng từ SharedPreferences

        if (name != null) {
            nametextv.setText(name); // Hiển thị tên người dùng nếu có
        } else {
            nametextv.setText("Tên không có"); // Hoặc giá trị mặc định nếu không có tên
        }

        btnCSBM = view.findViewById(R.id.button_csbm);
        btnDKDV = view.findViewById(R.id.button_dkdv);
        btnQDSD = view.findViewById(R.id.button_qdsd);
        btnHDSD = view.findViewById(R.id.button_hdsd);
        btnQDSD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavController navController = NavHostFragment.findNavController(UserFragment.this);
                navController.navigate(R.id.action_userFragment_to_QDSD);
            }
        });
        btnCSBM.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavController navController = NavHostFragment.findNavController(UserFragment.this);
                navController.navigate(R.id.action_userFragment_to_CSBM);
            }
        });
        btnDKDV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavController navController = NavHostFragment.findNavController(UserFragment.this);
                navController.navigate(R.id.action_userFragment_to_DKDV);
            }
        });
        btnHDSD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavController navController = NavHostFragment.findNavController(UserFragment.this);
                navController.navigate(R.id.action_userFragment_to_HDSD);
            }
        });
        btnCall = view.findViewById(R.id.call);
        btnCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phoneNumber = "0936404009";
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel: " + phoneNumber));
                startActivity(intent);
            }
        });

        return view;
    }

    private void logoutUser() {
        // Xóa trạng thái đăng nhập từ SharedPreferences
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("isLoggedIn", false);
        editor.apply(); // Chuyển đến LoginFragment
        NavHostFragment navHostFragment = (NavHostFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        if (navHostFragment != null) {
            NavController navController = navHostFragment.getNavController();
            navController.navigate(R.id.loginFragment);
        }
        // Ẩn BottomNavigationView
        BottomNavigationView bnvMain = getActivity().findViewById(R.id.bnvMain);
        if (bnvMain != null) {
            bnvMain.setVisibility(View.GONE);
        }
    }

    private void fetchWeatherData() {
        WeatherAPI weatherAPI = RetrofitClient.getClient().create(WeatherAPI.class);
        Call<WeatherResponse> call = weatherAPI.getWeather(CITY_NAME, WEATHER_API_KEY, "metric");

        call.enqueue(new Callback<WeatherResponse>() {
            @Override
            public void onResponse(Call<WeatherResponse> call, Response<WeatherResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    WeatherResponse weatherResponse = response.body();

                    // Hiển thị thông tin vào các TextView
                    cityTextView.setText("🌆 Thành phố: " + weatherResponse.name); // Thành phố
                    descriptionTextView.setText("☁️ Mô tả: " + weatherResponse.weather[0].description); // Mô tả
                    windTextView.setText("🌬️ Gió: " + weatherResponse.wind.speed + " m/s"); // Gió
                    temperatureTextView.setText("🌡️ Nhiệt độ: " + weatherResponse.main.temp + "°C"); // Nhiệt độ
                    humidityTextView.setText("💧 Độ ẩm: " + weatherResponse.main.humidity + "%"); // Độ ẩm
                } else {
                    Toast.makeText(getContext(), "Lỗi: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<WeatherResponse> call, Throwable t) {
                Toast.makeText(getContext(), "Không thể lấy dữ liệu thời tiết", Toast.LENGTH_SHORT).show();
            }
        });
    }


}