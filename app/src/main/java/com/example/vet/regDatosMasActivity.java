package com.example.vet;

import static com.basgeekball.awesomevalidation.ValidationStyle.BASIC;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
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
import com.example.vet.clases.Especies;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;


public class regDatosMasActivity extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private Spinner sEspecie;
    private EditText edtPeso, edtColor, edtRaza, edtNomM, edtFecha;
    private RadioButton rbMacho, rbHembra;
    private Button btnRegistratmas;
    private AwesomeValidation awesomeValidation;
    private String key;
    private String item;
    private DatePickerDialog datePickerDialog;
    private int year;

    private DataEspecies data;
    private EspecieAdapter adapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reg_datos_mas);
        String key = getIntent().getExtras().getString("llave");
        this.key = key;
        initDatePicker();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        edtNomM = (EditText) findViewById(R.id.edtNomM);
        edtColor = (EditText) findViewById(R.id.edtColor);
        edtRaza = (EditText) findViewById(R.id.edtraza);
        edtPeso = (EditText) findViewById(R.id.edtPeso);
        edtFecha = (EditText) findViewById(R.id.edtFecha);
        sEspecie = (Spinner) findViewById(R.id.sEspecie);
        btnRegistratmas = (Button) findViewById(R.id.btnRegMas);
        rbMacho = findViewById(R.id.rbMacho);
        rbHembra = findViewById(R.id.rbHembra);
        edtFecha.setText(getTodaysDate());
        rbMacho.setChecked(true);
        data = new DataEspecies();

        awesomeValidation = new AwesomeValidation(BASIC);
        awesomeValidation.addValidation(this, R.id.edtNomM, "[a-zA-Z0-9\\s]+", R.string.err_campova);
        awesomeValidation.addValidation(this, R.id.edtColor, "[a-zA-Z0-9\\s]+", R.string.err_campova);
        awesomeValidation.addValidation(this, R.id.edtraza, "[a-zA-Z0-9\\s]+", R.string.err_campova);
        awesomeValidation.addValidation(this, R.id.edtPeso, "[0-9\\s]+", R.string.err_campova);

        adapter = new EspecieAdapter(this, DataEspecies.getEspecieList());
        sEspecie.setAdapter(adapter);

        edtFecha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerDialog.show();
            }
        });

        btnRegistratmas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(awesomeValidation.validate()){
                    String nom = String.valueOf(edtNomM.getText());
                    String fecha = String.valueOf(edtFecha.getText());
                    String peso = String.valueOf(edtPeso.getText());
                    String color = String.valueOf(edtColor.getText());
                    String raza = String.valueOf(edtRaza.getText());



                    switch ((int) sEspecie.getSelectedItemId()){
                        case 0:
                            item = "Perro";
                            break;
                        case 1:
                            item = "Gato";
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

                    registro(nom,fecha,peso,color,raza,espe,sexo);

                }
            }
        });
    }

    private void registro(String name,String date, String weight, String color, String raze, String species,String sex){

        final Map<String, Object> map = new HashMap<>();
        map.put("name", name);
        map.put("birth", date);
        map.put("weight", weight);
        map.put("color", color);
        map.put("raze", raze);
        map.put("species", species);
        map.put("sex", sex);





        mDatabase.child("Usuario").child(key).child("Mascotas").child(year+species+name).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task2) {
                if(task2.isSuccessful()){
                    Toast.makeText(regDatosMasActivity.this, "Registrado", Toast.LENGTH_SHORT).show();
                }
                else Toast.makeText(regDatosMasActivity.this, "Error al registrar datos", Toast.LENGTH_SHORT).show();
            }
        });

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
        //datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
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