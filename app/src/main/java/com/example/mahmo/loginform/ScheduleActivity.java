package com.example.mahmo.loginform;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class ScheduleActivity extends AppCompatActivity {

    private ListView listView;
    public static ArrayList<String> allBusLines = new ArrayList<>();
    private ArrayAdapter<String> adpter;
    private DatabaseReference myRef, Ref;
    private FirebaseDatabase database;
    private FirebaseAuth auth;
    public static int itemNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);

        listView = (ListView)findViewById(R.id.ScheduleList);

        adpter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,allBusLines);
        listView.setAdapter(adpter);


        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();
        myRef = database.getReference().child("Bus Lines");
        /////////////////////////////////////////////////////////
        myRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                String value = dataSnapshot.getKey();
                allBusLines.add(value);
                adpter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                String value = dataSnapshot.getKey();
                allBusLines.remove(value);
                adpter.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Toast.makeText(ScheduleActivity.this,"Item no: "+position,Toast.LENGTH_SHORT).show();
                itemNumber = position;
                /*Ref = database.getReference().child("Bus Lines").child(ScheduleActivity.allBusLines.get(ScheduleActivity.itemNumber));
                Ref.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {


                        ScheduleSecondActivity.item = dataSnapshot.getKey();
                        ScheduleSecondActivity.allStops.add(ScheduleSecondActivity.item);
                        adpter.notifyDataSetChanged();
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {
                        *//*String item = dataSnapshot.child(allStops.get(itemNumber).getKey();
                        allStops.remove(item);
                        adpter.notifyDataSetChanged();*//*
                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });*/
                startActivity(new Intent(ScheduleActivity.this,ScheduleSecondActivity.class));

            }
        });

    }
    public void onBackPressed(){
        super.onBackPressed();
        allBusLines.removeAll(allBusLines);
        finish();
    }

}
