package com.example.mahmo.loginform;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class AddingBusLinesActivity extends AppCompatActivity {

    private EditText busName;
    private Spinner busSpinner;
    private EditText busTime;
    public static EditText stopLocation;
    private Button editStopLocation;
    private Button addBusStop;
    private EditText stopName;
    private FirebaseAuth auth;
    private ArrayAdapter<String> adpter;
    private DatabaseReference myRef;
    private FirebaseDatabase database;
    private String stopLatitude = "", stopLongitude = "";

    private ArrayList<String> myStops = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adding_bus_lines);

        busName = (EditText) findViewById(R.id.Ttypebusname);
        stopName = (EditText)findViewById(R.id.TtypeStopName);
        busTime = (EditText)findViewById(R.id.busTime);
        busSpinner = (Spinner)findViewById(R.id.BusSpinner);
        addBusStop = (Button)findViewById(R.id.BaddBusStop);
        stopLocation = (EditText)findViewById(R.id.TstopLocation);
        editStopLocation = (Button)findViewById(R.id.BstopLocation);
        adpter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,myStops);
        busSpinner.setAdapter(adpter);
        busSpinner.setSelection(0);

        editStopLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AddingBusLinesActivity.this,StopLocationOnMapActivity.class));
            }
        });

        busSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int i, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                busSpinner.setSelection(0);
            }
        });

        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();
        myRef = database.getReference().child("Bus Lines");
        /////////////////////////////////////////////////////////
        myRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                String value = dataSnapshot.getKey();
                myStops.add(value);
                adpter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                String value = dataSnapshot.getKey();
                myStops.remove(value);
                adpter.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        /////////////////////////////////////////////////////////



        addBusStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!busName.getText().toString().matches("") || !stopName.getText().toString().matches(""))
                {
                    //String value = busName.getText().toString();
                    if(!busName.getText().toString().matches("") && stopName.getText().toString().matches(""))
                    {
                        Toast.makeText(AddingBusLinesActivity.this,"Please add a stop for this line",Toast.LENGTH_SHORT).show();
                    }
                    else if(!busName.getText().toString().matches("") && !stopName.getText().toString().matches(""))
                    {
                        if(!stopLocation.getText().toString().matches(""))
                        {
                            if(!busTime.getText().toString().matches(""))
                            {
                                setLocation();
                                myRef.child(busName.getText().toString()).child(stopName.getText().toString()).child("Latitude").setValue(stopLatitude);
                                myRef.child(busName.getText().toString()).child(stopName.getText().toString()).child("Longitude").setValue(stopLongitude);
                                myRef.child(busName.getText().toString()).child(stopName.getText().toString()).child("Time").setValue(busTime.getText().toString());
                                Toast.makeText(AddingBusLinesActivity.this,"Bus line "+(busName.getText().toString())+" was add",Toast.LENGTH_SHORT).show();
                            }
                            else
                            {
                                Toast.makeText(AddingBusLinesActivity.this,"Please add a time for this stop "+ stopName.getText().toString(),Toast.LENGTH_SHORT).show();
                            }
                        }
                        else
                        {
                            Toast.makeText(AddingBusLinesActivity.this,"Please add a location for this stop "+ stopName.getText().toString(),Toast.LENGTH_SHORT).show();
                        }

                    }
                    else
                    {
                        setLocation();
                        myRef.child(busSpinner.getSelectedItem().toString()).child(stopName.getText().toString()).child("Latitude").setValue(stopLatitude);
                        myRef.child(busSpinner.getSelectedItem().toString()).child(stopName.getText().toString()).child("Longitude").setValue(stopLongitude);
                        myRef.child(busSpinner.getSelectedItem().toString()).child(stopName.getText().toString()).child("Time").setValue(busTime.getText().toString());
                        Toast.makeText(AddingBusLinesActivity.this,"Stop "+(stopName.getText().toString())+" was add",Toast.LENGTH_SHORT).show();
                    }
                    busName.setText(null);
                    stopName.setText(null);
                    stopLocation.setText(null);
                    busTime.setText(null);
                }
                else
                {
                    Toast.makeText(AddingBusLinesActivity.this,"Please fill atleast one of the required fields",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void setLocation()
    {
        stopLatitude = "";
        stopLongitude = "";
        int counter = 1;
        for(int i=0;i<stopLocation.getText().toString().length();i++)
        {
            if(stopLocation.getText().toString().charAt(i) != ',' && counter == 1)
            {
                stopLatitude += stopLocation.getText().toString().charAt(i);
            }
            else if(stopLocation.getText().toString().charAt(i) != ',' && counter == 2)
            {
                stopLongitude += stopLocation.getText().toString().charAt(i);
            }
            else
            {
                counter++;
            }
        }
    }


}
