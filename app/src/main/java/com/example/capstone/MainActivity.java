package com.example.capstone;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.bottomnavigation.BottomNavigationView;


public class MainActivity extends AppCompatActivity {
    BottomNavigationView bnvMain;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Tìm và gán BottomNavigationView từ layout
        bnvMain = findViewById(R.id.bnvMain);

        // Tìm NavHostFragment
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);

        if (navHostFragment != null) {
            // Lấy NavController từ NavHostFragment
            NavController navController = navHostFragment.getNavController();

            // Kiểm tra trạng thái đăng nhập từ SharedPreferences
            SharedPreferences sharedPreferences = getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE);
            boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);

            if (!isLoggedIn) {
                // Người dùng chưa đăng nhập, điều hướng đến LoginFragment
                navController.navigate(R.id.loginFragment);
                bnvMain.setVisibility(View.GONE); // Ẩn BottomNavigationView
            } else {
                // Người dùng đã đăng nhập, hiển thị HomeFragment và thanh BottomNavigation
                navController.navigate(R.id.homeFragment);
                bnvMain.setVisibility(View.VISIBLE); // Hiển thị BottomNavigationView
            }

            // Thiết lập BottomNavigationView với NavController
            NavigationUI.setupWithNavController(bnvMain, navController);
        }
    }
}


