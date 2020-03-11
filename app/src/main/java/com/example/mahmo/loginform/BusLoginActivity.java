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

public class BusLoginActivity extends AppCompatActivity {

    public EditText username;
    private EditText password;
    private Button login;
    private CheckBox stayLoggedIn;
    private TextView signUp;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;
    public static boolean busIsChecked = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bus_login);

        username = (EditText)findViewById(R.id.TadminID);
        password = (EditText)findViewById(R.id.Tadminpassword);
        login = (Button)findViewById(R.id.Blogin);
        signUp = (TextView) findViewById(R.id.THelp);
        stayLoggedIn = (CheckBox)findViewById(R.id.stayLoggedIn);

        if(busIsChecked == true)
        {
            stayLoggedIn.setChecked(true);
        }

        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);

        FirebaseUser user = firebaseAuth.getCurrentUser();

        String name = "";
        for(int i=0;i<user.getEmail().length();i++)
        {
            if(user.getEmail().charAt(i)!='@')
            {
                name += user.getEmail().charAt(i);
            }
            else
            {
                break;
            }
        }

        if(user != null && stayLoggedIn.isChecked() && name.length() < 8)
        {
                 finish();
                 startActivity(new Intent(this,BusWelcomeActivity.class));
        }


        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(BusLoginActivity.this, RegistrationActivity.class));
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(username.getText().toString().matches("") || password.getText().toString().matches(""))
                {
                    Toast.makeText(BusLoginActivity.this,"Please fill all the required fields",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    check(("busno"+username.getText().toString()+"@miuegypt.edu.eg"),password.getText().toString());
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
                    progressDialog.dismiss();
                    Toast.makeText(BusLoginActivity.this,"Login Done",Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(BusLoginActivity.this,BusWelcomeActivity.class));
                }
                else
                {
                    progressDialog.dismiss();
                    Toast.makeText(BusLoginActivity.this,"Incorrect username or password",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


}
