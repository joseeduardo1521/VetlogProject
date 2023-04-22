package com.example.vet;

import static com.basgeekball.awesomevalidation.ValidationStyle.BASIC;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class RegisterAct extends AppCompatActivity {
    private EditText TxtUsuario;
    private TextInputEditText TxtPass, TxtPass2;
    private EditText TxtTel, TxtDir, TxtNom;
    private Button btncrespo;
    private ProgressDialog progressDialog;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private AwesomeValidation awesomeValidation;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        TxtUsuario = (EditText) findViewById(R.id.txtUsuario);
        TxtNom = (EditText) findViewById(R.id.edtNomU);
        TxtPass = (TextInputEditText) findViewById(R.id.txtPassU);
        TxtPass2 = (TextInputEditText) findViewById(R.id.txtPass2U);
        TxtTel = (EditText) findViewById(R.id.edtTelU);
        TxtDir = (EditText) findViewById(R.id.edtDirU);
        btncrespo = (Button) findViewById(R.id.btncrespoU);
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Cargando datos...");
        progressDialog.setMessage("Por favor, espere.");
        awesomeValidation = new AwesomeValidation(BASIC);
        String regexPassword = "(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9]).{8,}";
        awesomeValidation.addValidation(this, R.id.edtDirU, "[a-zA-Z0-9\\s]+", R.string.err_dir);
        awesomeValidation.addValidation(this, R.id.edtNomU, "[a-zA-Z\\s]+", R.string.err_nom);
        awesomeValidation.addValidation(this, R.id.txtCorreo, Patterns.EMAIL_ADDRESS, R.string.err_email);
        awesomeValidation.addValidation(this, R.id.txtPassU, regexPassword, R.string.err_pass);
        awesomeValidation.addValidation(this, R.id.edtTelU, RegexTemplate.TELEPHONE, R.string.err_tel);
        awesomeValidation.addValidation(this, R.id.txtPass2U, regexPassword, R.string.err_pass);


        btncrespo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (awesomeValidation.validate()) {
                    progressDialog.show();
                    String email, password, password2,address,phone,name,photo;
                    email = String.valueOf(TxtUsuario.getText());
                    password = String.valueOf(TxtPass.getText());
                    password2 = String.valueOf(TxtPass2.getText());
                    address = String.valueOf(TxtDir.getText());
                    name = String.valueOf(TxtNom.getText());
                    phone = String.valueOf(TxtTel.getText());
                    photo = "https://firebasestorage.googleapis.com/v0/b/vetlog-fec63.appspot.com/o/user%2Fvetg.png?alt=media&token=75ea52f3-4fe1-4c8e-821f-575edbced693";

                    if (password.equals(password2)) {

                        mAuth.createUserWithEmailAndPassword(email, password)
                                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {
                                            progressDialog.setTitle("Cuenta casi completa verifique su correo electronico");

                                            mAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()) {
                                                                registro(name,email,address,phone,photo);
                                                                progressDialog.dismiss();
                                                                volverLogin();
                                                            } else {
                                                                Toast.makeText(RegisterAct.this, "Error al verificar el correo",Toast.LENGTH_SHORT).show();
                                                            }
                                                        }
                                                    });


                                        } else {
                                            // If sign in fails, display a message to the user.

                                            Toast.makeText(RegisterAct.this, "Ya existe una cuenta con ese correo",
                                                    Toast.LENGTH_SHORT).show();

                                        }
                                    }
                                });

                    } else {        TxtPass2.setError("Las contraseñas no coiniden");

                    }

                }

            }

        });


    }

    private void registro(String name,String email, String address, String phone,String photo){

        final Map<String, Object> map = new HashMap<>();
        map.put("name", name);
        map.put("email",email);
        map.put("address",address);
        map.put("phone",phone);
        map.put("photo", photo);
        map.put("lvl",3);



        final String id = mAuth.getCurrentUser().getUid();
        mDatabase.child("Usuario").child(id).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task2) {
                if(task2.isSuccessful()){
                    Toast.makeText(RegisterAct.this, "Registro completado con exito",Toast.LENGTH_SHORT).show();
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