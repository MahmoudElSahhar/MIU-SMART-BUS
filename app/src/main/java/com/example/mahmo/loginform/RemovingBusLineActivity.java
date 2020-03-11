package com.example.mahmo.loginform;

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

public class RemovingBusLineActivity extends AppCompatActivity {

    private Spinner mybusSpinner;
    private Spinner busStopSpinner;
    private Button remove;
    private ArrayAdapter<String> busAdpter;
    private ArrayAdapter<String> busStopAdpter;
    private ArrayList<String> allBusLines = new ArrayList<>();
    private ArrayList<String> allBusStops = new ArrayList<>();
    private DatabaseReference myRef, Ref;
    private FirebaseDatabase database;
    private FirebaseAuth auth;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_removing_bus_line);

        mybusSpinner = (Spinner)findViewById(R.id.BusSpinner);
        busStopSpinner = (Spinner)findViewById(R.id.BusStopSpinner);
        remove = (Button)findViewById(R.id.removeBusLine);

        busAdpter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,allBusLines);
        mybusSpinner.setAdapter(busAdpter);
        mybusSpinner.setSelection(0);

        mybusSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int i, long id) {

                allBusStops.removeAll(allBusStops);
                displayBusStops();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

                //mybusSpinner.setSelection(0);
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
                allBusLines.add(value);
                busAdpter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                String value = dataSnapshot.getKey();
                allBusLines.remove(value);
                busAdpter.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });




        //////////////////////////////////////////////////////////////////////////////////////////////////

        busStopAdpter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,allBusStops);
        busStopSpinner.setAdapter(busStopAdpter);
        busStopSpinner.setSelection(0);

        busStopSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int i, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

                busStopSpinner.setSelection(0);
            }
        });



        remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(busStopSpinner.getSelectedItem() != null && mybusSpinner.getSelectedItem() != null)
                {
                    DatabaseReference item = FirebaseDatabase.getInstance().getReference().child("Bus Lines").child(mybusSpinner.getSelectedItem().toString()).child(busStopSpinner.getSelectedItem().toString());
                    item.removeValue();
                    Toast.makeText(RemovingBusLineActivity.this,"The bus stop "+busStopSpinner.getSelectedItem().toString()+" was deleted",Toast.LENGTH_SHORT).show();
                    allBusStops.removeAll(allBusStops);
                    displayBusStops();
                }
                else
                {
                    Toast.makeText(RemovingBusLineActivity.this,"Poor internet connection... Please wait",Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    public void displayBusStops()
    {
        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();
        Ref = database.getReference().child("Bus Lines").child(mybusSpinner.getSelectedItem().toString());

        /////////////////////////////////////////////////////////
        Ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {


                String item = dataSnapshot.getKey();
                allBusStops.add(item);
                busStopAdpter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                String item = dataSnapshot.child(mybusSpinner.getSelectedItem().toString()).getKey();
                allBusStops.remove(item);
                busStopAdpter.notifyDataSetChanged();
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
