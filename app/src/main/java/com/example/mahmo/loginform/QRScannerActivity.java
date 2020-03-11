package com.example.mahmo.loginform;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class QRScannerActivity extends AppCompatActivity {

    private Button scanCode;
    private DatabaseReference myRef, ref;
    private FirebaseDatabase database;
    private FirebaseUser user;
    private FirebaseAuth auth;
    private String userName = "", busCapacity = "";
    private Calendar calendar;
    private String time, day;
    private SimpleDateFormat dayFormat, timeFormat;
    private int numOnBus;
    public static IntentResult result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrscanner);

        scanCode = (Button)findViewById(R.id.scanButton);
        final Activity activity = this;
        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        calendar = Calendar.getInstance();
        dayFormat = new SimpleDateFormat("dd-MM-yyyy");
        timeFormat = new SimpleDateFormat(" HH:mm");
        time = dayFormat.format(calendar.getTime());
        day = timeFormat.format(calendar.getTime());

        for(int i=0;i<user.getEmail().length();i++)
        {
            if(user.getEmail().charAt(i) != '@')
            {
                userName += user.getEmail().charAt(i);
            }
            else
                break;
        }

        scanCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentIntegrator Integrator = new IntentIntegrator(activity);
                Integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
                Integrator.setPrompt("Scan");
                Integrator.setCameraId(0);
                Integrator.setBeepEnabled(false);
                Integrator.setBarcodeImageEnabled(false);
                Integrator.initiateScan();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        result = IntentIntegrator.parseActivityResult(requestCode,resultCode,data);
        if(result != null)
        {
            if(result.getContents() == null)
            {
                Toast.makeText(this, "Scan was cancelled", Toast.LENGTH_SHORT).show();
            }
            else
            {
                ref = database.getReference().child("Active Buses").child(result.getContents().toString());

                ref.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        if(dataSnapshot.getKey().equals("Students On Board"))
                        {
                            String num = dataSnapshot.getValue().toString();
                            String numOfStudents = "";
                            for(int i=0;i<num.length();i++)
                            {
                                if(num.charAt(i) != '/')
                                {
                                    numOfStudents += num.charAt(i);
                                }
                                else
                                {
                                    busCapacity = num.substring(i);
                                    break;
                                }
                            }
                            numOnBus = Integer.parseInt(numOfStudents);
                            numOnBus++;
                            myRef = database.getReference().child("Active Buses").child(result.getContents().toString());
                            myRef.child("Students On Board").setValue(""+numOnBus+busCapacity);
                            myRef = database.getReference().child("Active Buses").child(result.getContents().toString()).child("Students On Bus").child(userName);
                            myRef.child("At Time").setValue(time);
                            myRef.child("On Date").setValue(day);
                            Toast.makeText(QRScannerActivity.this, "This is "+result.getContents(), Toast.LENGTH_SHORT).show();
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

                /*numOnBus++;
                myRef = database.getReference().child("Active Buses").child(result.getContents().toString());
                Toast.makeText(this, "size: "+busCapacity, Toast.LENGTH_LONG).show();
                myRef.child("Students On Board").setValue(""+numOnBus+busCapacity);
                myRef = database.getReference().child("Active Buses").child(result.getContents().toString()).child("Students On Bus").child(userName);
                myRef.child("At Time").setValue(time);
                myRef.child("On Date").setValue(day);*/
                Toast.makeText(QRScannerActivity.this, "This is "+result.getContents(), Toast.LENGTH_SHORT).show();
                /////////////////////////////////////////////////////////
                AlertDialog.Builder alert = new AlertDialog.Builder(QRScannerActivity.this);
                alert.setMessage("Would you like to rate this bus").setCancelable(false)
                        .setPositiveButton("Rate", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                                startActivity(new Intent(QRScannerActivity.this,BusRatingActivity.class));
                            }
                        }).setNegativeButton("No thanks", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                AlertDialog alertDialog = alert.create();
                alertDialog.setTitle("Rating buses");
                alertDialog.show();


            }
        }
        else
        {
            super.onActivityResult(requestCode, resultCode, data);
        }

    }

}
