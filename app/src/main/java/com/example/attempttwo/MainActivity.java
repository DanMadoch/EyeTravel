package com.example.attempttwo;

import android.content.Intent;
import android.os.Bundle;

import android.view.View;
import android.widget.Button;


import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Button on the main screen to access destination_input
        final Button button1 = findViewById(R.id.buttonDirections);
        button1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, InputLocation.class);
                startActivity(intent);
            }
        });

        //Button on the main screen to access time_layout
        final Button button2 = findViewById(R.id.buttonTime);
        button2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, timeSection.class);
                startActivity(intent);
            }
        });

    }

}