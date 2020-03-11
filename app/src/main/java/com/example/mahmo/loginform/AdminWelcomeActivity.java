package com.example.mahmo.loginform;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class AdminWelcomeActivity extends AppCompatActivity {

    private Button addStudent;
    private Button watchLive;
    private Button removeStudent;
    private Button editBuses;
    private Button getRating;
    private TextView signOut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_welcome);

        addStudent = (Button)findViewById(R.id.BaddStudents);
        watchLive = (Button)findViewById(R.id.Bwatchlive);
        editBuses = (Button)findViewById(R.id.edit);
        removeStudent = (Button)findViewById(R.id.BremoveStudent);
        signOut = (TextView)findViewById(R.id.AdminSignOut);
        getRating = (Button)findViewById(R.id.BusRatings);

        addStudent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AdminWelcomeActivity.this,RegistrationActivity.class));
            }
        });

        editBuses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AdminWelcomeActivity.this,BusesInfoActivity.class));
            }
        });

        removeStudent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AdminWelcomeActivity.this,RemoveStudentActivity.class));
            }
        });

        watchLive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AdminWelcomeActivity.this,AdminWatchLiveActivity.class));
            }
        });

        getRating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AdminWelcomeActivity.this,GetBusRatingActivity.class));
            }
        });

        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AdminLoginActivity.isChecked = false;
                finish();
            }
        });
    }
}
