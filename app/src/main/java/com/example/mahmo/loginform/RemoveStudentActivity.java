package com.example.mahmo.loginform;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class RemoveStudentActivity extends AppCompatActivity {

    private Button removeStudent;
    private EditText studentID;
    private EditText studentPassword;
    private DatabaseReference myRef;
    private FirebaseDatabase database;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remove_student);

        removeStudent = (Button)findViewById(R.id.Bremove);
        studentPassword = (EditText)findViewById(R.id.TstudentPassword);
        studentID = (EditText)findViewById(R.id.TstudentID);

        progressDialog = new ProgressDialog(this);
        removeStudent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!studentID.getText().toString().matches("") && !studentPassword.getText().toString().matches(""))
                {
                    check(studentID.getText().toString(),studentPassword.getText().toString());
                    //FirebaseUser user = firebaseAuth.getCurrentUser();
                    //user.delete();
                    //Toast.makeText(RemoveStudentActivity.this,"Deleted",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(RemoveStudentActivity.this,"Please fill the required field",Toast.LENGTH_SHORT).show();
                }
            }
        });

    }


    private void check(final String Username, String Password)
    {
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        firebaseAuth.signInWithEmailAndPassword(Username,Password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful())
                {
                    progressDialog.dismiss();
                    firebaseAuth = FirebaseAuth.getInstance();
                    FirebaseUser user = firebaseAuth.getCurrentUser();
                    user.delete();
                    studentID.setText("");
                    studentPassword.setText("");
                    Toast.makeText(RemoveStudentActivity.this,"Student " + Username + " was deleted",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    progressDialog.dismiss();
                    Toast.makeText(RemoveStudentActivity.this,"Could not find this student",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}
