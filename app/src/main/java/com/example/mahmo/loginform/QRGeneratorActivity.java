package com.example.mahmo.loginform;

import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

public class QRGeneratorActivity extends AppCompatActivity {

    private Button codeGenerator;
    private ImageView codeImage;
    private FirebaseUser user;
    private FirebaseAuth auth;
    private String userName = "";
    private String busCode = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrgenerator);

        codeGenerator = (Button)findViewById(R.id.generateButton);
        codeImage = (ImageView)findViewById(R.id.QRCodeImage);
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        for(int i=0;i<user.getEmail().length();i++)
        {
            if(user.getEmail().charAt(i)!='@')
            {
                userName += user.getEmail().charAt(i);
            }
            else
            {
                break;
            }
        }
        userName = userName.substring(5);
        busCode = "Bus no "+userName;

        codeGenerator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MultiFormatWriter multiFormatWriter = new MultiFormatWriter();

                try{
                    BitMatrix bitMatrix = multiFormatWriter.encode(busCode, BarcodeFormat.QR_CODE,200,200);
                    BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                    Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
                    codeImage.setImageBitmap(bitmap);
                }
                catch (WriterException e){
                    e.printStackTrace();
                }
            }
        });
    }
}
