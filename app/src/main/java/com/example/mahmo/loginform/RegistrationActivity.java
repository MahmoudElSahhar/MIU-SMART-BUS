package com.example.mahmo.loginform;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegistrationActivity extends AppCompatActivity {

    private TextView Registration;
    private TextView hasAccount;
    private EditText username;
    private EditText password;
    public static EditText email;
    private Button signUp;
    private FirebaseAuth firebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        firebaseAuth = FirebaseAuth.getInstance();

        Registration = (TextView)findViewById(R.id.Tregistration);
        hasAccount = (TextView)findViewById(R.id.ThasAccount);
        username = (EditText)findViewById(R.id.TadminID);
        password = (EditText)findViewById(R.id.Tadminpassword);
        email = (EditText)findViewById(R.id.Temail);
        signUp = (Button)findViewById(R.id.BsignUp);


        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkFeilds())
                {
                    String User_email = email.getText().toString().trim() + "@miuegypt.edu.eg";
                    String User_password = password.getText().toString().trim();
                    firebaseAuth.createUserWithEmailAndPassword(User_email,User_password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                    if(task.isSuccessful())
                    {
                        Toast.makeText(RegistrationActivity.this,"Registration Done",Toast.LENGTH_SHORT).show();
                        if(username.getText().toString().matches("student"))
                            startActivity(new Intent(RegistrationActivity.this,AddIdImageActivity.class));
                        else
                            startActivity(new Intent(RegistrationActivity.this,HomeScreenActivity.class));
                    }
                    else
                    {
                        Toast.makeText(RegistrationActivity.this,"Registration Failed",Toast.LENGTH_SHORT).show();
                    }

                }
                });
                }
            }
        });


        hasAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegistrationActivity.this,AdminLoginActivity.class);
                startActivity(intent);
            }
        });

    }

    private boolean checkFeilds()
    {
        boolean result = false;
        if(username.getText().toString().matches("") || password.getText().toString().matches("") || email.getText().toString().matches(""))
        {
            Toast.makeText(this,"Please fill all the required fields",Toast.LENGTH_SHORT).show();
            result = false;
        }
        else
        {
            result = true;
        }
        return result;
    }

}
