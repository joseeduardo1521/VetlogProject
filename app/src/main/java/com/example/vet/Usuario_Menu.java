package com.example.vet;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.sax.StartElementListener;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.bumptech.glide.Glide;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import de.hdodenhof.circleimageview.CircleImageView;

public class Usuario_Menu extends AppCompatActivity {

    private View cardPersonal,cardCerSesion,cardCitas,cardGesM, cardQR, cardAdop, cardVacu;
    private TextView mnombre,mRol;
    private FirebaseAuth mAuth;
    private String idMas;
    private CircleImageView imgUsu;
    private DatabaseReference mDatabase;
    public static final String SHARED_PREFS ="sharedPrefs.mail";

    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialogPerfil;
    private EditText modUsupopup_dir, modUsupopup_tel, modUsupopup_nom;
    private TextInputEditText modUsupopup_pass,modUsupopup_confpass;
    private TextInputLayout modUsupopup_passlay,modUsupopup_confpasslay;
    private Button btnUpdate, btnCancel, btnHabilitar;
    private CircleImageView updImage,btnSubirImgMasc;

    StorageReference storageReference;
    String storage_path = "user/*";
    private  String download_uri;
    private static final int COD_SEL_IMAGE = 300;
    private Uri image_url;
    String photo = "photo";
    String idd;
    AwesomeValidation awesomeValidation, awesomeValidation2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usuario_menu);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        storageReference = FirebaseStorage.getInstance().getReference();
        mnombre = (TextView) findViewById(R.id.usu);
        mRol = (TextView) findViewById(R.id.puesto);
        cardCerSesion = (View) findViewById(R.id.D7);
        cardCitas = (View) findViewById(R.id.D3);
        cardGesM = (View) findViewById(R.id.D1);
        cardAdop =(View) findViewById(R.id.D5);
        cardVacu = (View) findViewById(R.id.D6);
        imgUsu =  findViewById(R.id.imgUsuario);
        getUserInfo();

        cardCerSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cerrarSesion();
            }
        });

        cardCitas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent citas = new Intent(Usuario_Menu.this, RegCitas.class);
                startActivity(citas);
            }
        });
        cardVacu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent vacu = new Intent(Usuario_Menu.this,mostrarCamp.class);
                startActivity(vacu);
            }
        });

        cardGesM.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gesMascotas = new Intent(Usuario_Menu.this,verMascotasD.class);
                startActivity(gesMascotas);
            }
        });

        cardAdop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent Adopmas = new Intent(Usuario_Menu.this,mostrarAdopcion.class);
                startActivity(Adopmas);
            }
        });

    }





    private void getUserInfo(){

        String id= mAuth.getCurrentUser().getUid();
        mDatabase.child("Usuario").child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    String nombre,lvl,photo;
                    nombre = snapshot.child("name").getValue().toString();
                    if (snapshot.child("photo").exists()) {
                        photo = snapshot.child("photo").getValue().toString();
                        Glide.with(Usuario_Menu.this).load(photo).into(imgUsu);
                    }
                    lvl = snapshot.child("lvl").getValue().toString();
                    switch (lvl){
                        case "1":
                            mRol.setText("Rol: Veterinario");
                            break;
                        case "2":
                            mRol.setText("Rol: Secretario");
                            break;
                        case "3":
                            mRol.setText("Rol: Dueño de mascota");
                            break;
                    }
                    mnombre.setText(nombre);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void cerrarSesion(){
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor= sharedPreferences.edit();
        editor.putString("mail","");
        editor.putString("pass","");
        editor.apply();
        Intent iniciar = new Intent(this,MainActivity.class);
        iniciar.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(iniciar);
        finish();
    }

}