package com.example.vet;

import static com.basgeekball.awesomevalidation.ValidationStyle.BASIC;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.utility.RegexTemplate;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class Menu extends AppCompatActivity {

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
        setContentView(R.layout.activity_menu);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        storageReference = FirebaseStorage.getInstance().getReference();
        mnombre = (TextView) findViewById(R.id.usu);
        mRol = (TextView) findViewById(R.id.puesto);
        cardPersonal = (View) findViewById(R.id.D4);
        cardCerSesion = (View) findViewById(R.id.D7);
        cardCitas = (View) findViewById(R.id.D3);
        cardGesM = (View) findViewById(R.id.D1);
        cardQR = (View) findViewById(R.id.D2);
        cardAdop =(View) findViewById(R.id.D5);
        cardVacu = (View) findViewById(R.id.D6);
        imgUsu =  findViewById(R.id.imgUsuario);
        getUserInfo();

        imgUsu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Updateuser();
            }
        });

        cardPersonal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent registrarU = new Intent(Menu.this, RegisUser.class);
                startActivity(registrarU);
            }
        });

        cardCitas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent Citas = new Intent(Menu.this, RegCitas.class);
                startActivity(Citas);
            }
        });

        cardVacu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent vacu = new Intent(Menu.this, CrearVacuna.class);
                startActivity(vacu);
            }
        });

        cardAdop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent adop = new Intent(Menu.this, CrearAdopcion.class);
                startActivity(adop);
            }
        });

        cardCerSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cerrarSesion();
            }
        });

        cardGesM.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gesMascotas = new Intent(Menu.this,gesMacotasActivity.class);
                startActivity(gesMascotas);
            }
        });

        cardQR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent qrs = new Intent(Menu.this,Lector.class);
                startActivity(qrs);
            }
        });


    }

        public void Updateuser(){
            dialogBuilder = new AlertDialog.Builder(this);
            final View userPopupView = getLayoutInflater().inflate(R.layout.popupuser, null);
            modUsupopup_dir = (EditText) userPopupView.findViewById(R.id.edtDirU);
            modUsupopup_tel = (EditText) userPopupView.findViewById(R.id.edtTelU);
            modUsupopup_nom = (EditText) userPopupView.findViewById(R.id.edtNomU);
            modUsupopup_pass = (TextInputEditText) userPopupView.findViewById(R.id.txtPassU);
            modUsupopup_confpass = (TextInputEditText) userPopupView.findViewById(R.id.txtPass2U);
            modUsupopup_passlay= (TextInputLayout) userPopupView.findViewById(R.id.inputU);
            modUsupopup_confpasslay = (TextInputLayout) userPopupView.findViewById(R.id.input2U);

            btnHabilitar = (Button) userPopupView.findViewById(R.id.btnHab);
            btnUpdate = (Button) userPopupView.findViewById(R.id.btncrespoU);
            btnCancel = (Button) userPopupView.findViewById(R.id.btncCancel);
            updImage = (CircleImageView) userPopupView.findViewById(R.id.imgUser);
            btnSubirImgMasc = (CircleImageView) userPopupView.findViewById(R.id.btnSubirImgMasc);

            awesomeValidation = new AwesomeValidation(BASIC);
            awesomeValidation.addValidation(modUsupopup_dir, "[a-zA-Z0-9\\s]+", "Ingrese una direccion");
            awesomeValidation.addValidation(modUsupopup_nom, "[a-zA-Z\\s]+", "Ingrese su nombre completo");
            awesomeValidation.addValidation(modUsupopup_tel , RegexTemplate.TELEPHONE, "Ingrese un numero telefonico");

            dialogBuilder.setView(userPopupView);
            dialogPerfil = dialogBuilder.create();


            String id= mAuth.getCurrentUser().getUid();
            mDatabase.child("Usuario").child(id).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        String nombre, phone, direc,photo;
                        nombre = snapshot.child("name").getValue().toString();
                        phone = snapshot.child("phone").getValue().toString();
                        direc = snapshot.child("address").getValue().toString();
                        photo = snapshot.child("photo").getValue().toString();

                        modUsupopup_nom.setText(nombre);
                        modUsupopup_tel.setText(phone);
                        modUsupopup_dir.setText(direc);
                        Glide.with(Menu.this).load(photo).into(updImage);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            btnSubirImgMasc.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    uploadPhoto();
                }
            });

            btnHabilitar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    modUsupopup_pass.setEnabled(true);
                    modUsupopup_confpass.setEnabled(true);
                    modUsupopup_passlay.setVisibility(View.VISIBLE);
                    modUsupopup_confpasslay.setVisibility(View.VISIBLE);
                }
            });

           btnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialogPerfil.dismiss();
                }
            });

            btnUpdate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (awesomeValidation.validate()) {
                    String name = modUsupopup_nom.getText().toString();
                    String dir = modUsupopup_dir.getText().toString();
                    String phone = modUsupopup_tel.getText().toString();
                    if (modUsupopup_pass.isEnabled()) {
                        awesomeValidation2 = new AwesomeValidation(BASIC);
                        String regexPassword = "(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9]).{8,}";
                        awesomeValidation2.addValidation(modUsupopup_pass, regexPassword, "Este campo no puede estar vacio");
                        awesomeValidation2.addValidation(modUsupopup_confpass, regexPassword, "Este campo no puede estar vacio");
                        if (awesomeValidation2.validate()) {
                        String pass = modUsupopup_confpass.getText().toString();
                        String passconf = modUsupopup_pass.getText().toString();
                        if (pass.equals(passconf)) {
                            String newpas = String.valueOf(modUsupopup_pass.getText());
                            mAuth.getCurrentUser().updatePassword(newpas)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                cerrarSesion();
                                                Toast.makeText(Menu.this, "Inicie sesion nuevamente", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        } else
                            modUsupopup_confpass.setError("Las contraseñas no coiniden");
                        }
                    }
                    actualizarUsu(dir, name, phone);

                 }
                }
            });

            dialogPerfil.show();
        }

        private void  actualizarUsu(String address, String name, String phone){
            String idu = mAuth.getCurrentUser().getUid();
            idd = idu+name;
            String token= idu;
            Map<String, Object> updates = new HashMap<>();
            updates.put("name", name);
            updates.put("address",address);
            updates.put("phone",phone);

            mDatabase.child("Usuario").child(idu).updateChildren(updates).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        Toast.makeText(Menu.this, "Datos Actualizados", Toast.LENGTH_SHORT).show();
                        if(!(image_url == null)) {
                            subirPhoto(image_url, token);
                        }
                    }else
                        Toast.makeText(Menu.this, "Error al actualizar datos", Toast.LENGTH_SHORT).show();
                }
            });
        }



    private void uploadPhoto() {
        Intent i = new Intent(Intent.ACTION_PICK);
        i.setType("image/*");
        startActivityForResult(i, COD_SEL_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(resultCode == RESULT_OK){
            if (requestCode == COD_SEL_IMAGE){
                image_url = data.getData();
                updImage.setImageURI(image_url);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void subirPhoto(Uri image_url, String token) {
        String rute_storage_photo = storage_path + "" + photo  +""+ idd;
        StorageReference reference = storageReference.child(rute_storage_photo);
        reference.putFile(image_url).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                while (!uriTask.isSuccessful());
                if (uriTask.isSuccessful()){
                    uriTask.addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            download_uri = uri.toString();
                            HashMap<String, Object> map = new HashMap<>();
                            map.put("photo", download_uri);
                            mDatabase.child("Usuario").child(token).updateChildren(map);
                            Toast.makeText(Menu.this, "Imagen cargada", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Menu.this, "Error al cargar foto", Toast.LENGTH_SHORT).show();
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
                    photo = snapshot.child("photo").getValue().toString();
                    lvl = snapshot.child("lvl").getValue().toString();
                    switch (lvl){
                        case "1":
                            mRol.setText("Veterinario");
                            break;
                        case "2":
                            mRol.setText("Secretario");
                            break;
                    }
                    mnombre.setText(nombre);
                    Glide.with(Menu.this).load(photo).into(imgUsu);
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