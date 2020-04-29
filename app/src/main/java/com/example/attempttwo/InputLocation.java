package com.example.attempttwo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class InputLocation extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.destination_input);

        EditText textInput = findViewById(R.id.editTextInput);


        final Button button1 = findViewById(R.id.buttonSubmit);
        button1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String destination = textInput.getText().toString();
                Intent intent = new Intent(InputLocation.this, Directions.class);
                intent.putExtra("address", destination);
                //EditText editText = (EditText) findViewById(R.id.editText);
                startActivity(intent);

            }
        });

    }
}