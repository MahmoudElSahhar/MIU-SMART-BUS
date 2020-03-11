package com.example.mahmo.loginform;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
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

public class AddBusesActivity extends AppCompatActivity {

    private EditText busNumber;
    private EditText driverName;
    private Spinner busSpinner;
    private EditText busCapacity;
    private Button addBus;
    private ArrayAdapter<String> busAdp;
    private ArrayList<String> allBusLines = new ArrayList<>();
    private DatabaseReference myRef, Ref;
    private FirebaseDatabase database;
    private FirebaseAuth auth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_buses);

        busNumber = (EditText)findViewById(R.id.busNumber);
        driverName = (EditText)findViewById(R.id.driverName);
        busSpinner = (Spinner)findViewById(R.id.chooseSpinner);
        addBus = (Button)findViewById(R.id.addBusButton);
        busCapacity = (EditText)findViewById(R.id.busCapacity);

        busAdp = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,allBusLines);
        busSpinner.setAdapter(busAdp);
        busSpinner.setSelection(0);

        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();
        myRef = database.getReference().child("Bus Lines");
        Ref = database.getReference().child("Available Buses");
        /////////////////////////////////////////////////////////
        myRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                String value = dataSnapshot.getKey();
                allBusLines.add(value);
                busAdp.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                String value = dataSnapshot.getKey();
                allBusLines.remove(value);
                busAdp.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        addBus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!busCapacity.getText().toString().matches("") && !busNumber.getText().toString().matches("") && !driverName.getText().toString().matches("") && busSpinner.getSelectedItem() != null)
                {
                    Ref.child("Bus no "+ busNumber.getText().toString()).child("Driver Name").setValue(""+driverName.getText().toString());
                    Ref.child("Bus no "+ busNumber.getText().toString()).child("Serving Line").setValue(""+busSpinner.getSelectedItem().toString());
                    Ref.child("Bus no "+ busNumber.getText().toString()).child("Bus Capacity").setValue(""+busCapacity.getText().toString());
                    Ref.child("Bus no "+ busNumber.getText().toString()).child("Bus Rating").setValue(""+0);
                    Ref.child("Bus no "+ busNumber.getText().toString()).child("Driver Rating").setValue(""+0);
                    Ref.child("Bus no "+ busNumber.getText().toString()).child("Number of Ratings").setValue(""+0);
                    Ref.child("Bus no "+ busNumber.getText().toString()).child("Bus Total Rating").setValue(""+0);
                    Ref.child("Bus no "+ busNumber.getText().toString()).child("Driver Total Rating").setValue(""+0);
                    Toast.makeText(AddBusesActivity.this,"Bus no. "+busNumber.getText().toString() + " was add",Toast.LENGTH_SHORT).show();
                    busNumber.setText("");
                    driverName.setText("");
                    busCapacity.setText("");
                    busSpinner.setSelection(0);
                }
                else
                {
                    Toast.makeText(AddBusesActivity.this,"Please fill all the required fields",Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}
