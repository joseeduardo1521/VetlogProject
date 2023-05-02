package com.example.vet;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

public class RegCitas extends AppCompatActivity {

    private DatePickerDialog datePickerDialog;
    private TimePickerDialog timePickerDialog;
    private Button dateButton;
    private Button timeButton,grabacita;
    private String mnombre;
    //private Button grabacita;
    FirebaseAuth mAuth;
    DatabaseReference mDatabase;
    private EditText textoss, hor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reg_citas);
        initDatePicker();
        dateButton = findViewById(R.id.dataPickerButton);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        textoss = (EditText) findViewById(R.id.fechitas);
        hor = (EditText) findViewById(R.id.horitas);
        grabacita = findViewById(R.id.ecita);
        dateButton.setText(getTodaysDate());
        //hor = findViewById(R.id.TimePickerButton);
        getUserInfo();
        hor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                int hours = calendar.get(Calendar.HOUR_OF_DAY);
                int mins = calendar.get(Calendar.MINUTE);
                TimePickerDialog timePickerDialog = new TimePickerDialog(RegCitas.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        Calendar c = Calendar.getInstance();
                        c.set(Calendar.HOUR_OF_DAY,hourOfDay);
                        c.set(Calendar.MINUTE,minute);
                        c.setTimeZone(TimeZone.getDefault());
                        SimpleDateFormat format = new SimpleDateFormat("k:mm a");
                        String time = format.format(c.getTime());
                        hor.setText(time);

                    }
                },hours, mins, false);
                timePickerDialog.show();
            }
        });

        grabacita.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String fecha,hora;
                fecha = String.valueOf(textoss.getText());
                hora = String.valueOf(hor.getText());
                registro(fecha,hora,mnombre);
               //Intent mas = new Intent(RegCitas.this,Usuarios.class);
                //startActivity(mas);
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
                    mnombre= nombre;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void registro(String fecha, String hora, String mnombre) {
        final Map<String, Object> map = new HashMap<>();
        map.put("date", fecha);
        map.put("time", hora);
        map.put("name", mnombre);
        String id = mDatabase.push().getKey();
        String idu = mAuth.getCurrentUser().getUid();
        map.put("uid", id);
        map.put("id", idu);

        // Crear referencia a la ubicación de la base de datos que contiene los registros
        DatabaseReference citasRef = mDatabase.child("Citas");

        // Crear consulta para verificar si ya existe un registro con la misma fecha, hora y nombre
        Query consulta = citasRef.orderByChild("time").equalTo(hora);

        // Agregar un ValueEventListener para escuchar cambios en los datos
        consulta.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Comprobar si los datos existen
                if (dataSnapshot.exists()) {
                    Toast.makeText(RegCitas.this, "La hora de la cita ya existe", Toast.LENGTH_SHORT).show();
                } else {
                    // Agregar el nuevo registro
                    citasRef.child(id).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task2) {
                            if (task2.isSuccessful()) {
                                Toast.makeText(RegCitas.this, "Registrado", Toast.LENGTH_SHORT).show();
                                Intent intent =new Intent(RegCitas.this, Usuario_Menu.class);
                                startActivity(intent);
                                finishAffinity();
                            } else {
                                Toast.makeText(RegCitas.this, "Error al registrar datos", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Manejar error de la consulta
                Toast.makeText(RegCitas.this, "Error al verificar si el registro existe", Toast.LENGTH_SHORT).show();
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


    private void initDatePicker(){
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener(){
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month +1;
                String date = makeDateString(day,month,year);
                dateButton.setText(date);
                textoss.setText(date);
            }
        };
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        int style = AlertDialog.THEME_HOLO_LIGHT;

        datePickerDialog = new DatePickerDialog(this,style,dateSetListener,year,month,day);
        //datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
    }

    private String makeDateString(int day, int month, int year) {
        return getMonthFormat(month) + " " + day + " " + year; 
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

    public void openDatePicker(View view){
        datePickerDialog.show();
    }


}