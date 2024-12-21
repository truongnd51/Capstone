package com.example.capstone.ScollView;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ScrollView;

import com.example.capstone.R;

public class DKDV extends AppCompatActivity {

    private ScrollView scrollView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dkdv);
        this.scrollView = findViewById(R.id.DKDV_ScrollView);
    }
}