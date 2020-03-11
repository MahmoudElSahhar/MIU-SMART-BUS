package com.example.mahmo.loginform;

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

public class RemoveOrEditBusActivity extends AppCompatActivity {

    private Spinner removeBusSpinner;
    private Button removeBus;
    private EditText busNumber;
    private EditText driverName;
    private EditText busCapacity;
    private Button saveChanges;
    private Spinner lines;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> busLines = new ArrayList<>();
    private ArrayAdapter<String> busAdp;
    private ArrayList<String> allBusNo = new ArrayList<>();
    private DatabaseReference myRef, Ref, reference;
    private FirebaseDatabase database;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remove_or_edit_bus);

        removeBusSpinner = (Spinner)findViewById(R.id.removeBusSpinner);
        removeBus = (Button)findViewById(R.id.removeBus);
        busNumber = (EditText)findViewById(R.id.oldBusNumber);
        driverName = (EditText)findViewById(R.id.oldDriverName);
        lines = (Spinner)findViewById(R.id.editOrRemoveSpinner);
        busCapacity = (EditText)findViewById(R.id.editBusCapacity);
        saveChanges = (Button)findViewById(R.id.saveAndEdit);

        busAdp = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,allBusNo);
        removeBusSpinner.setAdapter(busAdp);
        removeBusSpinner.setSelection(0);

        adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,busLines);
        lines.setAdapter(adapter);
        lines.setSelection(0);

        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();
        myRef = database.getReference().child("Available Buses");
        Ref = database.getReference().child("Bus Lines");

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

        ////////////////////////////////////////////////////////////

        Ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                String value = dataSnapshot.getKey();
                busLines.add(value);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                String value = dataSnapshot.getKey();
                busLines.remove(value);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        removeBusSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                reference = database.getReference().child("Available Buses").child(removeBusSpinner.getSelectedItem().toString());
                reference.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                        if(dataSnapshot.getKey().equals("Driver Name"))
                        {
                            driverName.setText(dataSnapshot.getValue().toString());
                        }
                        else if(dataSnapshot.getKey().equals("Serving Line"))
                        {
                            lines.setSelection(SpinnerNum(dataSnapshot.getValue().toString(),busLines));
                        }
                        else if(dataSnapshot.getKey().equals("Bus Capacity"))
                        {
                            busCapacity.setText(dataSnapshot.getValue().toString());
                        }
                        busNumber.setText(removeBusSpinner.getSelectedItem().toString());
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

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        saveChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!busNumber.getText().toString().matches("") && !driverName.getText().toString().matches("") &&
                        !busCapacity.getText().toString().matches("") && removeBusSpinner.getSelectedItem() != null && lines.getSelectedItem() != null)
                {
                    myRef.child(busNumber.getText().toString()).child("Driver Name").setValue(""+driverName.getText().toString());
                    myRef.child(busNumber.getText().toString()).child("Serving Line").setValue(""+lines.getSelectedItem().toString());
                    myRef.child(busNumber.getText().toString()).child("Bus Capacity").setValue(""+busCapacity.getText().toString());
                    Toast.makeText(RemoveOrEditBusActivity.this,"Bus no. "+busNumber.getText().toString() + " was edited",Toast.LENGTH_SHORT).show();
                    busNumber.setText("");
                    driverName.setText("");
                    busCapacity.setText("");
                    Toast.makeText(RemoveOrEditBusActivity.this,"Changes were saved",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(RemoveOrEditBusActivity.this,"Poor internet connection... Please wait",Toast.LENGTH_SHORT).show();
                }

            }
        });

        removeBus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!busNumber.getText().toString().matches("") && !driverName.getText().toString().matches("") &&
                        removeBusSpinner.getSelectedItem() != null && lines.getSelectedItem() != null)
                {
                    DatabaseReference item = FirebaseDatabase.getInstance().getReference().child("Available Buses").child(removeBusSpinner.getSelectedItem().toString());
                    item.removeValue();
                    busNumber.setText("");
                    driverName.setText("");
                    Toast.makeText(RemoveOrEditBusActivity.this,""+removeBusSpinner.getSelectedItem().toString()+" was deleted",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(RemoveOrEditBusActivity.this,"Poor internet connection... Please wait",Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    int SpinnerNum(String stopName, ArrayList busLines)
    {
        int num = 0;
        for(int i=0;i<busLines.size();i++)
        {
            if(busLines.get(i).equals(stopName))
            {
                num = i;
                break;
            }
        }
        return num;
    }

}
