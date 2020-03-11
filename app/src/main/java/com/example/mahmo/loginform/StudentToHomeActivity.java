package com.example.mahmo.loginform;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class StudentToHomeActivity extends AppCompatActivity {

    public static Spinner spinner;
    private Button findBusLocation;
    private Button busStops;
    private Button searchOnMap;
    private FirebaseAuth auth;
    private ArrayAdapter<String> adpter;
    private DatabaseReference myRef;
    private FirebaseDatabase database;
    private ArrayList<String> myStops = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_to_home);

        spinner = (Spinner)findViewById(R.id.spinner);
        findBusLocation = (Button)findViewById(R.id.BfindLocation);
        busStops = (Button)findViewById(R.id.BbusStop);
        searchOnMap = (Button)findViewById(R.id.Bsearch);

        adpter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,myStops);
        spinner.setAdapter(adpter);
        spinner.setSelection(0);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int i, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

                spinner.setSelection(0);
            }
        });

        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();
        myRef = database.getReference().child("Bus Lines");
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

        findBusLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(spinner.getSelectedItem() == null)
                {
                    Toast.makeText(StudentToHomeActivity.this,"Poor internet connection... Please wait",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    startActivity(new Intent(StudentToHomeActivity.this,StudentWatchLiveActivity.class));
                }
            }
        });

        busStops.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(spinner.getSelectedItem() == null)
                {
                    Toast.makeText(StudentToHomeActivity.this,"Poor internet connection... Please wait",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    startActivity(new Intent(StudentToHomeActivity.this,WatchBusStopsOnMapActivity.class));
                }
            }
        });

        searchOnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(spinner.getSelectedItem() == null)
                {
                    Toast.makeText(StudentToHomeActivity.this,"Poor internet connection... Please wait",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    startActivity(new Intent(StudentToHomeActivity.this,FindDestinationOnMapActivity.class));
                }
            }
        });


    }
}
