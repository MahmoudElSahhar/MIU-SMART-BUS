package com.example.mahmo.loginform;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class BusesInfoActivity extends AppCompatActivity {

    private Button addBus;
    private Button addBusStop;
    private Button removeBus;
    private Button removeBusStop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buses_info);

        addBus = (Button)findViewById(R.id.addBus);
        addBusStop = (Button)findViewById(R.id.addBusStop);
        removeBus = (Button)findViewById(R.id.removeBus);
        removeBusStop = (Button)findViewById(R.id.removeBusStop);

        addBus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(BusesInfoActivity.this,AddBusesActivity.class));
            }
        });

        addBusStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(BusesInfoActivity.this,AddingBusLinesActivity.class));
            }
        });

        removeBus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(BusesInfoActivity.this,RemoveOrEditBusActivity.class));
            }
        });

        removeBusStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(BusesInfoActivity.this,RemovingBusLineActivity.class));
            }
        });

    }
}
