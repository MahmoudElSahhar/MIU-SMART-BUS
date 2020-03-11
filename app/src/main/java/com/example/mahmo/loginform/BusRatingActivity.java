package com.example.mahmo.loginform;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class BusRatingActivity extends AppCompatActivity {

    private Button submit;
    private RatingBar driverRating;
    private RatingBar busRating;
    private DatabaseReference myRef;
    private FirebaseDatabase database;
    private FirebaseAuth auth;
    private float busTotalRating = 0, driverTotalRating = 0, numOfRatings = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bus_rating);

        submit = (Button)findViewById(R.id.submitRatingButton);
        driverRating = (RatingBar)findViewById(R.id.driverRatingBar);
        busRating = (RatingBar)findViewById(R.id.busRatingBar);

        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();
        myRef = database.getReference().child("Available Buses").child(QRScannerActivity.result.getContents().toString());

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myRef.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        if(dataSnapshot.getKey().equals("Number of Ratings"))
                        {
                            numOfRatings = Float.parseFloat(dataSnapshot.getValue().toString());
                            numOfRatings++;

                            myRef.child("Bus Rating").setValue( ((busTotalRating + busRating.getRating())/numOfRatings));
                            myRef.child("Driver Rating").setValue((driverTotalRating + driverRating.getRating())/numOfRatings);
                            myRef.child("Number of Ratings").setValue(numOfRatings);
                            myRef.child("Bus Total Rating").setValue(busTotalRating + busRating.getRating());
                            myRef.child("Driver Total Rating").setValue(driverTotalRating + driverRating.getRating());
                        }
                        else if(dataSnapshot.getKey().equals("Bus Total Rating"))
                        {
                            busTotalRating = Float.parseFloat(dataSnapshot.getValue().toString());
                        }
                        else if(dataSnapshot.getKey().equals("Driver Total Rating"))
                        {
                            driverTotalRating = Float.parseFloat(dataSnapshot.getValue().toString());
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
            finish();
            }
        });


    }
}
