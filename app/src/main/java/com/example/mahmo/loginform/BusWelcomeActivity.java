package com.example.mahmo.loginform;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class BusWelcomeActivity extends AppCompatActivity {

    private TextView busName;
    private TextView busStops;
    private Button startTrip;
    private Button busCodeGenerator;
    private TextView signout;
    private FirebaseAuth auth;
    private DatabaseReference myRef, Ref;
    private FirebaseDatabase database;
    private String allText;
    private ArrayList<String> myStops = new ArrayList<>();
    private String userName = "";
    private String userEmail = "";
    public static String servingline = "";
    private String driverName ="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bus_welcome);

        busName = (TextView)findViewById(R.id.TbusName);
        busStops = (TextView)findViewById(R.id.TbusStops);
        startTrip = (Button)findViewById(R.id.Bstart);
        signout = (TextView)findViewById(R.id.TsignOut);
        busCodeGenerator = (Button)findViewById(R.id.busCodeGenerator);

        startTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(BusWelcomeActivity.this,BusOnMapActivity.class));
            }
        });

        signout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BusLoginActivity.busIsChecked = false;
                finish();
            }
        });

        busCodeGenerator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(BusWelcomeActivity.this,QRGeneratorActivity.class));
            }
        });

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();


        userEmail = user.getEmail().substring(5);
        userName = "";
        for(int i=0;i< userEmail.length();i++)
        {
            if(userEmail.charAt(i) != '@')
            {
                userName += userEmail.charAt(i);
            }
            else
                break;
        }


        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();
        Ref = database.getReference().child("Available Buses").child("Bus no " + userName);
        Ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if(dataSnapshot.getKey().equals("Serving Line"))
                {
                    servingline = dataSnapshot.getValue().toString();
                    busName.setText("Bus Number: "+userName + "\nDriver Name: "+driverName + "\nServing Line: "+dataSnapshot.getValue().toString());
                    myRef = database.getReference().child("Bus Lines").child(servingline);
                    /////////////////////////////////////////////////////////
                    myRef.addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                            String value = dataSnapshot.getKey();
                            displayStops(value);
                        }

                        @Override
                        public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                        }

                        @Override
                        public void onChildRemoved(DataSnapshot dataSnapshot) {
                            String value = dataSnapshot.getKey();
                            myStops.remove(value);
                        }

                        @Override
                        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
                else if(dataSnapshot.getKey().equals("Driver Name"))
                {
                    driverName = dataSnapshot.getValue().toString();
                    busName.setText("Bus Number: "+userName + "\nDriver Name: "+driverName+ "\nServing Line: ");
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

        busName.setText("Bus Number: "+userName + "\nDriver Name: "+driverName+ "\nServing Line: ");

    }

    public void displayStops(String value)
    {
        allText = busStops.getText().toString();
        if(myStops.size() == 0)
        {
            busStops.setText(allText + "  " + value);
        }
        else
        {
            busStops.setText(allText + " - " + value);
        }
        myStops.add(value);
    }



}
