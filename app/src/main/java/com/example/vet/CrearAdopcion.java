package com.example.vet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.example.vet.clases.DataEspecies;
import com.example.vet.clases.EspecieAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class CrearAdopcion extends AppCompatActivity {
    private DatabaseReference mDatabase;
    private Spinner sEspecie;
    private EditText edtPeso, edtColor, edtRaza, edtNomM, edtFecha;
    private RadioButton rbMacho, rbHembra;
    private Button btnRegistratmas,btnUpdateMas,btnmost;
    private AwesomeValidation awesomeValidation;
    private String key="",key2="";
    private String item;
    private DatePickerDialog datePickerDialog;
    private int year;
    private CircleImageView imgMascota, btnSubirImg;
    private DataEspecies data;
    private FirebaseAuth mAuth;
    private EspecieAdapter adapter;
    private StorageReference storageReference;
    private String storage_path = "pet/*";
    private  String download_uri, imgupUrl="";
    private Spinner spinnerSta;
    private ProgressDialog progressDialog;

    private static final int COD_SEL_IMAGE = 300;
    private Uri image_url = Uri.parse("");
    private String photo = "photo";
    private String idd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear_adopcion);

//        String key2 = getIntent().getExtras().getString("llave2");
  //      this.key2 = key2;
       // String key = getIntent().getExtras().getString("llave");
        //this.key = key;



        initDatePicker();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        edtNomM = (EditText) findViewById(R.id.edtNomM);
        edtColor = (EditText) findViewById(R.id.edtColor);
        edtRaza = (EditText) findViewById(R.id.edtraza);
        edtPeso = (EditText) findViewById(R.id.edtPeso);
        edtFecha = (EditText) findViewById(R.id.edtFecha);
        sEspecie = (Spinner) findViewById(R.id.sEspecie);
        btnSubirImg = (CircleImageView) findViewById(R.id.btnSubirImgMasc);
        btnmost = (Button) findViewById(R.id.mostro);
        imgMascota = (CircleImageView) findViewById(R.id.imgUser);
        btnRegistratmas = (Button) findViewById(R.id.btnRegMas);
        rbMacho = findViewById(R.id.rbMacho);
        rbHembra = findViewById(R.id.rbHembra);
        edtFecha.setText(getTodaysDate());
        rbMacho.setChecked(true);
        data = new DataEspecies();

        adapter = new EspecieAdapter(this, DataEspecies.getEspecieList());
        sEspecie.setAdapter(adapter);

        ArrayAdapter<CharSequence> adapterst = ArrayAdapter.createFromResource(this, R.array.spinner_items, android.R.layout.simple_spinner_item);
        adapterst.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
   
        btnSubirImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadPhoto();
            }
        });

        edtFecha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerDialog.show();
            }
        });

        btnRegistratmas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setDatos(1);
            }
        });
        btnmost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mmo = new Intent(CrearAdopcion.this,mostrarAdopcion.class);
                startActivity(mmo);
            }
        });

    }
    //termina oncreate


    private void uploadPhoto() {
        Intent i = new Intent(Intent.ACTION_PICK);
        i.setType("image/*");
        startActivityForResult(i, COD_SEL_IMAGE);
    }
    private void setDatos(int op){
        //if(awesomeValidation.validate()){
            String nom = String.valueOf(edtNomM.getText());
            String fecha = String.valueOf(edtFecha.getText());
            String peso = String.valueOf(edtPeso.getText());
            String color = String.valueOf(edtColor.getText());
            String raza = String.valueOf(edtRaza.getText());

            switch ((int) sEspecie.getSelectedItemId()){
                case 0:
                    item = "Canino";
                    break;
                case 1:
                    item = "Felino";
                    break;
                case 2:
                    item = "Ave";
                    break;
                case 3:
                    item = "Roedor";
                    break;
                case 4:
                    item = "Reptil";
                    break;
            }
            String espe = String.valueOf(item);
            if(peso.equals("")){
                peso = "0";
            }
            String sexo = "";
            if(rbMacho.isChecked()==true){
                sexo = "Macho";
            }

            if(rbHembra.isChecked()==true)
                sexo = "Hembra";
            if(op==1)
                registro(nom,fecha,peso,color,raza,espe,sexo);

                //actualizarDatosMascota(nom,fecha,peso,color,raza,espe,sexo,status);
        //}
    }

    private void initDatePicker(){
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener(){
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month +1;
                String date = makeDateString(day,month,year);
                edtFecha.setText(date);
            }
        };
        Calendar cal = Calendar.getInstance();
        year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);
        int style = AlertDialog.THEME_HOLO_LIGHT;

        datePickerDialog = new DatePickerDialog(this,style,dateSetListener,year,month,day);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(resultCode == RESULT_OK){
            if (requestCode == COD_SEL_IMAGE){
                image_url = data.getData();
                imgMascota.setImageURI(image_url);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    private void subirPhoto(Uri image_url, String token) {
        String rute_storage_photo = storage_path + "" + photo + "" + key +""+ idd;
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
                            mDatabase.child("Adopcion").child(token).updateChildren(map);
                            salir();
                        }
                    });
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                if (String.valueOf(image_url).equals(imgupUrl)){
                    Toast.makeText(CrearAdopcion.this, "No se cambio la foto", Toast.LENGTH_SHORT).show();
                    salir();
                }else{
                    Toast.makeText(CrearAdopcion.this, "Error al cargar foto", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    private void registro(String name,String date, String weight, String color, String raze, String species,String sex){
        String idop = mDatabase.push().getKey();
        idd = key+year+species+name.replace(" ","");
        String token= mDatabase.push().getKey();
        final Map<String, Object> map = new HashMap<>();
        map.put("name", name);
        map.put("birth", date);
        map.put("weight", weight);
        map.put("color", color);
        map.put("raze", raze);
        map.put("species", species);
        map.put("sex", sex);
        //map.put("key", key);\
        map.put("key",idop);
        //map.put("status", status);

        mDatabase.child("Adopcion").child(token).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task2) {
                if(task2.isSuccessful()){
                    //mostrarPantallaCarga();
                    Toast.makeText(CrearAdopcion.this, "Registrado", Toast.LENGTH_SHORT).show();
                    Toast.makeText(CrearAdopcion.this, image_url.toString(), Toast.LENGTH_SHORT).show();
                    if(image_url.toString() != ""){
                        subirPhoto(image_url,token);}

                    else {
                        salir();
                    }
                }
                else Toast.makeText(CrearAdopcion.this, "Error al registrar datos", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void regresarmenu() {
        Intent intent = new Intent(this,Menu.class);
        startActivity(intent);
        finish();

    }

    private void regresarmenu2() {
        Intent intent = new Intent(this,MenuSec.class);
        startActivity(intent);
        finish();
    }

    private void salir(){
        String id= mAuth.getCurrentUser().getUid();
        mDatabase.child("Usuario").child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    String lvl;
                    lvl = snapshot.child("lvl").getValue().toString();
                    switch (lvl){
                        case "1":
                            regresarmenu();
                            break;
                        case "2":
                            regresarmenu2();
                            break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private String getTodaysDate() {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        month = month+1;
        int day = cal.get(Calendar.DAY_OF_MONTH);
        return makeDateString(day,month,year);
    }

    private String getMonthFormat(int month) {
        if(month == 1)
            return "JAN";
        if(month == 2)
            return "FEB";
        if(month == 3)
            return "MAR";
        if(month == 4)
            return "APR";
        if(month == 5)
            return "MAY";
        if(month == 6)
            return "JUN";
        if(month == 7)
            return "JUL";
        if(month == 8)
            return "AUG";
        if(month == 9)
            return "SEP";
        if(month == 10)
            return "OCT";
        if(month == 11)
            return "NOV";
        if(month == 12)
            return "DEC";

        return "JAN";
    }

    private String makeDateString(int day, int month, int year) {
        return getMonthFormat(month) + " " + day + " " + year;
    }


}