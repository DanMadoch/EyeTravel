package com.example.attempttwo;

import android.content.Intent;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.here.sdk.core.GeoCoordinates;
import com.here.sdk.mapviewlite.MapScene;
import com.here.sdk.mapviewlite.MapStyle;
import com.here.sdk.mapviewlite.MapViewLite;


public class MainActivity extends AppCompatActivity {

    private MapViewLite mapView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final Button button1 = findViewById(R.id.buttonDirections);
        button1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, InputLocation.class);
                //EditText editText = (EditText) findViewById(R.id.editText);
                startActivity(intent);
            }
        });
        final Button button2 = findViewById(R.id.buttonTime);
        button2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, timeSection.class);
                //EditText editText = (EditText) findViewById(R.id.editText);
                startActivity(intent);
            }
        });

    }

    private void loadMapScene() {
        // Load a scene from the SDK to render the map with a map style.
        mapView.getMapScene().loadScene(MapStyle.NORMAL_DAY, new MapScene.LoadSceneCallback() {
            @Override
            public void onLoadScene(@Nullable MapScene.ErrorCode errorCode) {
                if (errorCode == null) {
                    mapView.getCamera().setTarget(new GeoCoordinates(52.530932, 13.384915));
                    mapView.getCamera().setZoomLevel(14);
                } else {
                    Log.d("TAG", "onLoadScene failed: " + errorCode.toString());
                }
            }
        });
    }

}