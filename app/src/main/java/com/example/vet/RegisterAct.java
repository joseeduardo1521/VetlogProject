package com.example.vet;

import static com.basgeekball.awesomevalidation.ValidationStyle.BASIC;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.utility.RegexTemplate;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class RegisterAct extends AppCompatActivity {
    private EditText TxtUsuario;
    private TextInputEditText TxtPass, TxtPass2;
    private EditText TxtTel, TxtDir, TxtNom;
    private Button btncrespo;
    FirebaseAuth mAuth;
    DatabaseReference mDatabase;
    AwesomeValidation awesomeValidation;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        TxtUsuario = (EditText) findViewById(R.id.txtUsuario);
        TxtNom = (EditText) findViewById(R.id.edtNom);
        TxtPass = (TextInputEditText) findViewById(R.id.txtPass);
        TxtPass2 = (TextInputEditText) findViewById(R.id.txtPass2);
        TxtTel = (EditText) findViewById(R.id.edtTel);
        TxtDir = (EditText) findViewById(R.id.edtDir);
        btncrespo = (Button) findViewById(R.id.btncrespo);
        awesomeValidation = new AwesomeValidation(BASIC);
        String regexPassword = "(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9]).{8,}";
        awesomeValidation.addValidation(this, R.id.edtDir, "[a-zA-Z0-9\\s]+", R.string.err_dir);
        awesomeValidation.addValidation(this, R.id.edtNom, "[a-zA-Z\\s]+", R.string.err_nom);
        awesomeValidation.addValidation(this, R.id.txtCorreo, Patterns.EMAIL_ADDRESS, R.string.err_email);
        awesomeValidation.addValidation(this, R.id.txtPass, regexPassword, R.string.err_pass);
        awesomeValidation.addValidation(this, R.id.edtTel, RegexTemplate.TELEPHONE, R.string.err_tel);
        awesomeValidation.addValidation(this, R.id.txtPass2, regexPassword, R.string.err_pass);


        btncrespo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (awesomeValidation.validate()) {
                    String email, password, password2,address,phone,name;
                    email = String.valueOf(TxtUsuario.getText());
                    password = String.valueOf(TxtPass.getText());
                    password2 = String.valueOf(TxtPass2.getText());
                    address = String.valueOf(TxtDir.getText());
                    name = String.valueOf(TxtNom.getText());
                    phone = String.valueOf(TxtTel.getText());

                    if (password.equals(password2)) {

                        mAuth.createUserWithEmailAndPassword(email, password)
                                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {
                                            registro(name,email,address,phone);
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

    private void registro(String name,String email, String address, String phone){

        final Map<String, Object> map = new HashMap<>();
        map.put("name", name);
        map.put("email",email);
        map.put("address",address);
        map.put("phone",phone);
        map.put("lvl",3);



        final String id = mAuth.getCurrentUser().getUid();
        mDatabase.child("Usuario").child(id).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task2) {
                if(task2.isSuccessful()){
                    Toast.makeText(RegisterAct.this, "Registrado", Toast.LENGTH_SHORT).show();
                }
                else Toast.makeText(RegisterAct.this, "Error al registrar datos", Toast.LENGTH_SHORT).show();
            }
        });

    }


    private void volverLogin (){
        Intent iniciar = new Intent(this,MainActivity.class);
        startActivity(iniciar);
        finish();
    }

}