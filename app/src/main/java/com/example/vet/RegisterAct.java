package com.example.vet;

import static com.basgeekball.awesomevalidation.ValidationStyle.BASIC;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterAct extends AppCompatActivity {
    private EditText TxtUsuario;
    private EditText TxtPass, TxtPass2;
    private Button btncrespo;
    FirebaseAuth mAuth;
    AwesomeValidation awesomeValidation;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        TxtUsuario = (EditText) findViewById(R.id.txtUsuario);
        TxtPass = (EditText) findViewById(R.id.txtPass);
        TxtPass2 = (EditText) findViewById(R.id.txtPass2);
        btncrespo = (Button) findViewById(R.id.btncrespo);
        awesomeValidation = new AwesomeValidation(BASIC);
        awesomeValidation.addValidation(this, R.id.txtCorreo, Patterns.EMAIL_ADDRESS, R.string.err_email);
        awesomeValidation.addValidation(this, R.id.txtPass, ".{6,}", R.string.err_pass);
        awesomeValidation.addValidation(this, R.id.txtPass2, ".{6,}", R.string.err_pass);


        btncrespo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (awesomeValidation.validate()) {
                    String email, password, password2;
                    email = String.valueOf(TxtUsuario.getText());
                    password = String.valueOf(TxtPass.getText());
                    password2 = String.valueOf(TxtPass2.getText());

                    if (password.equals(password2)) {

                        mAuth.createUserWithEmailAndPassword(email, password)
                                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(RegisterAct.this, "Registrado con exito",Toast.LENGTH_SHORT).show();
                                            volverLogin();
                                        } else {
                                            // If sign in fails, display a message to the user.

                                            Toast.makeText(RegisterAct.this, "Ya existe una cuenta con ese correo",
                                                    Toast.LENGTH_SHORT).show();

                                        }
                                    }
                                });

                    } else {        TxtPass2.setError("No coiniden");

                    }

                }

            }

        });


    }

    private void volverLogin (){
        Intent iniciar = new Intent(this,MainActivity.class);
        startActivity(iniciar);
        finish();
    }

}