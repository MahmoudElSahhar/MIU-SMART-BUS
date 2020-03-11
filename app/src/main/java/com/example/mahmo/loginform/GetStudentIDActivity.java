package com.example.mahmo.loginform;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

public class GetStudentIDActivity extends AppCompatActivity {

    private Button searchForID;
    private EditText enterStudentID;
    private ImageView IDimage;
    private DatabaseReference myRef, Ref;
    private FirebaseDatabase database;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_student_id);

        searchForID = (Button)findViewById(R.id.searchForID);
        enterStudentID = (EditText)findViewById(R.id.enterStudentID);
        IDimage = (ImageView)findViewById(R.id.IDimages);

        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();
        myRef = database.getReference().child("Students IDs");

        myRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                if(enterStudentID.getText().toString().matches(""))
                {
                    if(dataSnapshot.getKey().equals(BusOnMapActivity.studentID))
                        Picasso.with(GetStudentIDActivity.this).load(""+dataSnapshot.getValue().toString()).into(IDimage);
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


        searchForID.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                myRef.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                        if(dataSnapshot.getKey().equals(enterStudentID.getText().toString()))
                        {
                            Picasso.with(GetStudentIDActivity.this).load(""+dataSnapshot.getValue().toString()).into(IDimage);
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
        });
    }
}
