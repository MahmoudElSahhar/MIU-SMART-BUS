package com.example.mahmo.loginform;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class StudentWelcomeActivity extends AppCompatActivity {

    public TextView nameWelcome;
    public static Spinner destination;
    private Button schedules;
    private Button studentScanner;
    private TextView SignOut;
    private Button start;
    public String[] chocies = {"-Choose a destination-", "To University", "From University"};
    public ArrayAdapter<String> adpter;
    private GoogleMap mMap;
    private final static int num = 177;
    private FirebaseAuth auth;
    private DatabaseReference myRef, Ref;
    private FirebaseDatabase database;
    private Location location;
    private String userName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_welcome);

        nameWelcome = (TextView)findViewById(R.id.Twelcome);
        start = (Button)findViewById(R.id.Bstart);
        schedules = (Button)findViewById(R.id.Bschedule);
        destination = (Spinner)findViewById(R.id.destinations);
        SignOut = (TextView)findViewById(R.id.StudentSignOut);
        studentScanner = (Button)findViewById(R.id.StudentScanner);
        adpter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,chocies);
        destination.setAdapter(adpter);
        destination.setSelection(0);
        start.setEnabled(false);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        for(int i=0;i<user.getEmail().length();i++)
        {
            if(user.getEmail().charAt(i) != '@')
            {
                userName += user.getEmail().charAt(i);
            }
            else
                break;
        }

        nameWelcome.setText("Welcome, "+userName);

        destination.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int i, long id) {

                switch (i) {
                    case 0:
                        destination.setSelection(0);
                        break;
                    case 1:
                        destination.setSelection(1);
                        start.setEnabled(true);
                        break;
                    case 2:
                        destination.setSelection(2);
                        start.setEnabled(true);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                destination.setSelection(0);
            }
        });


        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();
        myRef = database.getReference().child("Active Students");
        //myRef.child(userName).child(""+location.getLatitude()).setValue(""+location.getLongitude());


        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(destination.getSelectedItemPosition() == 1)
                {
                    startActivity(new Intent(StudentWelcomeActivity.this,StudentToUniActivity.class));
                }
                else if(destination.getSelectedItemPosition() == 2)
                {
                    startActivity(new Intent(StudentWelcomeActivity.this,StudentToHomeActivity.class));
                }
            }
        });

        schedules.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(StudentWelcomeActivity.this,ScheduleActivity.class));
            }
        });

        SignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StudentSignInActivity.studentIsChecked = false;
                finish();
            }
        });

        studentScanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(StudentWelcomeActivity.this,QRScannerActivity.class));
            }
        });
    }




}
