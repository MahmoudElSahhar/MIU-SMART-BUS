package com.example.mahmo.loginform;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AdminLoginActivity extends AppCompatActivity {

    public EditText adminusername;
    private EditText adminpassword;
    private Button login;
    private TextView signUp;
    private CheckBox stayLogged;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;
    public static boolean isChecked = true;
    private boolean accountAllowed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_login);

        adminusername = (EditText)findViewById(R.id.TadminID);
        adminpassword = (EditText)findViewById(R.id.Tadminpassword);
        login = (Button)findViewById(R.id.Blogin);
        stayLogged = (CheckBox)findViewById(R.id.stayLogged);
        signUp = (TextView) findViewById(R.id.TaddAdmin);
        //stayLogged.setChecked(true);
        if(isChecked == true)
        {
            stayLogged.setChecked(true);
        }

        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);

        FirebaseUser user = firebaseAuth.getCurrentUser();

        if(user != null && stayLogged.isChecked())
        {
                 finish();
                 startActivity(new Intent(this,AdminWelcomeActivity.class));
        }


        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AdminLoginActivity.this, RegistrationActivity.class));
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(adminusername.getText().toString().matches("") || adminpassword.getText().toString().matches(""))
                {
                    Toast.makeText(AdminLoginActivity.this,"Please fill all the required fields",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    check(adminusername.getText().toString()+"@miuegypt.edu.eg",adminpassword.getText().toString());
                }
            }
        });

    }

    private void check(String Username, String Password)
    {
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        firebaseAuth.signInWithEmailAndPassword(Username,Password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful())
                {
                    progressDialog.dismiss();stayLogged.setChecked(true);
                    Toast.makeText(AdminLoginActivity.this,"Login Done",Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(AdminLoginActivity.this,AdminWelcomeActivity.class));
                }
                else
                {
                    progressDialog.dismiss();
                    Toast.makeText(AdminLoginActivity.this,"Incorrect username or password",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}
