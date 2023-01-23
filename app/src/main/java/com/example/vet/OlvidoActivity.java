package com.example.vet;

import static com.basgeekball.awesomevalidation.ValidationStyle.BASIC;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class OlvidoActivity extends AppCompatActivity {

    Button btnRecuperar;
    EditText edt_Mail;
    AwesomeValidation awesomeValidation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_olvido);

        edt_Mail = findViewById(R.id.edtCorreoRec);
        btnRecuperar= findViewById(R.id.btnRestablecer);
        awesomeValidation = new AwesomeValidation(BASIC);
        awesomeValidation.addValidation(this, R.id.etmail, Patterns.EMAIL_ADDRESS,  R.string.err_email);

        btnRecuperar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(awesomeValidation.validate()){
                    String mail = String.valueOf(edt_Mail.getText());//Correo
                    enviarCor(mail);
                }
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(OlvidoActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void enviarCor(String mail){
        FirebaseAuth auth = FirebaseAuth.getInstance();
        String emailAddress = mail;
        auth.sendPasswordResetEmail(emailAddress)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()) {
                            Toast.makeText(OlvidoActivity.this, "Correo de restauracion enviado", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(OlvidoActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        }else {
                            Toast.makeText(OlvidoActivity.this, "NO existe una cuenta relacionada al correo", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

}