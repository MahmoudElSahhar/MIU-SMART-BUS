package com.example.mahmo.loginform;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.RatingBar;
import android.widget.Spinner;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class GetBusRatingActivity extends AppCompatActivity {

    private RatingBar getBusRating;
    private RatingBar getDriverRating;
    private Spinner listOfAllBuses;
    private ArrayAdapter<String> busAdp;
    private ArrayList<String> allBusNo = new ArrayList<>();
    private DatabaseReference myRef, ref;
    private FirebaseDatabase database;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_bus_rating);

        getBusRating = (RatingBar)findViewById(R.id.getBusRating);
        getDriverRating = (RatingBar)findViewById(R.id.getDriverRating);
        listOfAllBuses = (Spinner)findViewById(R.id.listOfAllBuses);

        getDriverRating.setClickable(false);
        getBusRating.setClickable(false);

        busAdp = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,allBusNo);
        listOfAllBuses.setAdapter(busAdp);
        listOfAllBuses.setSelection(0);

        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();
        myRef = database.getReference().child("Available Buses");


        listOfAllBuses.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int i, long id) {

                displayBusStops();
                getBusRating.setRating(0);
                getDriverRating.setRating(0);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

                //mybusSpinner.setSelection(0);
            }
        });

        myRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                String value = dataSnapshot.getKey();
                allBusNo.add(value);
                busAdp.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                String value = dataSnapshot.getKey();
                allBusNo.remove(value);
                busAdp.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



    }

    private void displayBusStops() {

        ref = database.getReference().child("Available Buses").child(listOfAllBuses.getSelectedItem().toString());
        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if(dataSnapshot.getKey().equals("Bus Rating"))
                {
                    getBusRating.setRating(Float.parseFloat(dataSnapshot.getValue().toString()));
                }
                else if(dataSnapshot.getKey().equals("Driver Rating"))
                {
                    getDriverRating.setRating(Float.parseFloat(dataSnapshot.getValue().toString()));
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
