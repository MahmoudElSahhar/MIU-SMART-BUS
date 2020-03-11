package com.example.mahmo.loginform;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class ScheduleSecondActivity extends AppCompatActivity {

    private ListView listView;
    public static ArrayList<String> allBusStops = new ArrayList<>();
    public static ArrayList<String> allTime = new ArrayList<>();
    public static ArrayList<String> finalSchedule = new ArrayList<>();
    private ArrayList<String> time = new ArrayList<>();
    private ArrayAdapter<String> adpter;
    private DatabaseReference Ref, reference;
    private FirebaseDatabase database;
    private FirebaseAuth auth;
    public static String item;
    private String object;
    private int counter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_second);

        listView = (ListView)findViewById(R.id.ScheduleSecondList);


        adpter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,finalSchedule);
        listView.setAdapter(adpter);


        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();

        Ref = database.getReference().child("Bus Lines").child(ScheduleActivity.allBusLines.get(ScheduleActivity.itemNumber));
        Ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {


                item = dataSnapshot.getKey();
                reference = database.getReference().child("Bus Lines").child(ScheduleActivity.allBusLines.get(ScheduleActivity.itemNumber)).child(dataSnapshot.getKey());
                reference.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        if(dataSnapshot.getKey().equals("Time"))
                        {
                            object = " Bus arrives at: "+dataSnapshot.getValue().toString();
                            allTime.add(object);
                            help(counter);
                            counter++;
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
                allBusStops.add(item);
                adpter.notifyDataSetChanged();
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

    public void onBackPressed(){
        super.onBackPressed();
        allBusStops.removeAll(allBusStops);
        allTime.removeAll(allTime);
        finalSchedule.removeAll(finalSchedule);
        finish();
    }

    public void help(int counter)
    {
        finalSchedule.add(counter, allBusStops.get(counter)+" ----->"+allTime.get(counter));
        adpter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,finalSchedule);
        listView.setAdapter(adpter);
    }

}
