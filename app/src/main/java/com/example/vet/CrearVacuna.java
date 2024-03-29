package com.example.vet;

import static android.content.ContentValues.TAG;
import static com.basgeekball.awesomevalidation.ValidationStyle.BASIC;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.app.DatePickerDialog;
import android.app.Notification;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.example.vet.clases.ApiService;
import com.example.vet.clases.DataEspecies;
import com.example.vet.clases.EspecieAdapter;
import com.example.vet.clases.RetrofitClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class CrearVacuna extends AppCompatActivity {

    private EditText startDateEditText, endDateEditText,campaign_name_edit_text, location_edit_text, notes_edit_text;
    private DatabaseReference mDatabase;
    private Spinner species;
    private String item;
    private String keyCam="";
    private FirebaseAuth mAuth;
    private EspecieAdapter adapter;
    private Calendar startDateCalendar, endDateCalendar;
    private AwesomeValidation awesomeValidation;
    private Button btnNuevaCamp,btnUpdCamp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear_vacuna);
        Bundle extras = getIntent().getExtras();
        if (extras != null && extras.containsKey("llaveCam")) {
            String keyCam = extras.getString("llaveCam");
            this.keyCam = keyCam;
        } else {
            // Manejar el caso en que "llaveCam" no esté presente
            // Por ejemplo, asignar un valor predeterminado a keyCam o mostrar un mensaje de error
        }
        btnNuevaCamp = findViewById(R.id.btnNuevaCamp);
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.pulse_animation);
        btnNuevaCamp.startAnimation(animation);
        btnUpdCamp = findViewById(R.id.btnactualizar);
        btnUpdCamp.startAnimation(animation);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        startDateEditText = findViewById(R.id.start_date_edit_text);
        endDateEditText = findViewById(R.id.end_date_edit_text);
        campaign_name_edit_text = findViewById(R.id.campaign_name_edit_text);
        location_edit_text = findViewById(R.id.location_edit_text);
        species = findViewById(R.id.species);
        notes_edit_text = findViewById(R.id.notes_edit_text);

        if(keyCam != "") {
            btnNuevaCamp.setVisibility(View.GONE);
            btnUpdCamp.setVisibility(View.VISIBLE);
            obtenerDatosCamp(this.keyCam);
        }else {
            btnNuevaCamp.setVisibility(View.VISIBLE);
            btnUpdCamp.setVisibility(View.GONE);
        }


        // Inicializar los calendarios de fecha de inicio y fecha de fin
        startDateCalendar = Calendar.getInstance();
        endDateCalendar = Calendar.getInstance();
        String regex= "^(?!\\s*$).+";
        awesomeValidation = new AwesomeValidation(BASIC);
        awesomeValidation.addValidation(this, R.id.campaign_name_edit_text, regex, R.string.err_campova);
        awesomeValidation.addValidation(this, R.id.start_date_edit_text, regex, R.string.err_campova);
        awesomeValidation.addValidation(this, R.id.end_date_edit_text, regex, R.string.err_campova);
        awesomeValidation.addValidation(this, R.id.location_edit_text, regex, R.string.err_campova);
        adapter = new EspecieAdapter(this, DataEspecies.getEspecieList());
        species.setAdapter(adapter);



        // Establecer el Listener para la selección de fecha de inicio
        startDateEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Crear un DatePickerDialog con la fecha actual como fecha mínima y mostrarlo
                DatePickerDialog datePickerDialog = new DatePickerDialog(CrearVacuna.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        // Establecer la fecha seleccionada en el calendario de fecha de inicio
                        startDateCalendar.set(Calendar.YEAR, year);
                        startDateCalendar.set(Calendar.MONTH, monthOfYear);
                        startDateCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                        // Actualizar el valor del TextInputEditText de fecha de inicio con la fecha seleccionada
                        startDateEditText.setText(formatDate(startDateCalendar.getTime()));
                    }
                }, startDateCalendar.get(Calendar.YEAR), startDateCalendar.get(Calendar.MONTH), startDateCalendar.get(Calendar.DAY_OF_MONTH));

                // Establecer la fecha actual como fecha mínima seleccionable en el diálogo
                datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);

                datePickerDialog.show();
            }
        });

        // Establecer el Listener para la selección de fecha de fin
        endDateEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Verificar que la fecha de inicio haya sido seleccionada
                if (startDateEditText.getText().toString().isEmpty()) {
                    Toast.makeText(CrearVacuna.this, "Debe seleccionar primero la fecha de inicio", Toast.LENGTH_SHORT).show();
                } else {
                    // Crear un DatePickerDialog con la fecha de inicio y mostrarlo
                    DatePickerDialog datePickerDialog = new DatePickerDialog(CrearVacuna.this, new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                            // Establecer la fecha seleccionada en el calendario de fecha de fin
                            endDateCalendar.set(Calendar.YEAR, year);
                            endDateCalendar.set(Calendar.MONTH, monthOfYear);
                            endDateCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                            // Verificar que la fecha de fin no sea anterior a la fecha de inicio
                            if (endDateCalendar.before(startDateCalendar)) {
                                Toast.makeText(CrearVacuna.this, "La fecha de fin no puede ser anterior a la fecha de inicio", Toast.LENGTH_SHORT).show();
                                endDateEditText.setText("");
                            } else {
                                // Actualizar el valor del TextInputEditText de fecha de fin con la fecha seleccionada
                                endDateEditText.setText(formatDate(endDateCalendar.getTime()));
                            }
                        }
                    }, endDateCalendar.get(Calendar.YEAR), endDateCalendar.get(Calendar.MONTH), endDateCalendar.get(Calendar.DAY_OF_MONTH));

                    // Establecer la fecha mínima del DatePickerDialog como la fecha de inicio
                    datePickerDialog.getDatePicker().setMinDate(startDateCalendar.getTimeInMillis());

                    // Mostrar el DatePickerDialog
                    datePickerDialog.show();
                }
            }
        });

        // Agregar un día a la fecha de fin cuando se seleccione la fecha de inicio
        startDateEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    // Obtener la fecha de inicio y agregar un día
                    Date startDate = startDateCalendar.getTime();
                    Calendar endDate = Calendar.getInstance();
                    endDate.setTime(startDate);
                    endDate.add(Calendar.DATE, 1);

                    // Establecer la fecha de fin automáticamente
                    endDateEditText.setText(formatDate(endDate.getTime()));
                }
            }
        });

        btnNuevaCamp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(awesomeValidation.validate()) {
                    String nomCam = campaign_name_edit_text.getText().toString();
                    String ubiCam = location_edit_text.getText().toString();
                    String startCam = startDateEditText.getText().toString();
                    String endCam = endDateEditText.getText().toString();
                    String noteCam = notes_edit_text.getText().toString();

                    switch ((int) species.getSelectedItemId()){
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


                    guardarCamp(nomCam, ubiCam,startCam,endCam,noteCam,espe);
                }
            }
        });


        btnUpdCamp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(awesomeValidation.validate()) {
                    String nomCam = campaign_name_edit_text.getText().toString();
                    String ubiCam = location_edit_text.getText().toString();
                    String startCam = startDateEditText.getText().toString();
                    String endCam = endDateEditText.getText().toString();
                    String noteCam = notes_edit_text.getText().toString();

                    switch ((int) species.getSelectedItemId()){
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


                    updCamp(nomCam, ubiCam,startCam,endCam,noteCam,espe);
                }
            }
        });

    }

    private void obtenerDatosCamp(String claveCa) {
        mDatabase.child("Campains").child(claveCa).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    String nomCa = snapshot.child("namecam").getValue().toString();
                    String iniCa = snapshot.child("iniciodate").getValue().toString();
                    String finCa =snapshot.child("findate").getValue().toString();
                    String esp =snapshot.child("speciescam").getValue().toString();
                    String ubi =snapshot.child("ubicam").getValue().toString();
                    String note =snapshot.child("notecam").getValue().toString();

                    switch (esp){
                        case "Canino":
                            species.setSelection(0);
                            break;
                        case "Felino":
                            species.setSelection(1);
                            break;
                        case "Ave":
                            species.setSelection(2);
                            break;
                        case "Roedor":
                            species.setSelection(3);
                            break;
                        case "Reptil":
                            species.setSelection(4);
                            break;
                    }

                    endDateEditText.setText(finCa);
                    startDateEditText.setText(iniCa);
                    campaign_name_edit_text.setText(nomCa);
                    location_edit_text.setText(ubi);
                    notes_edit_text.setText(note);

                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }




    private void guardarCamp(String nomCam,String ubiCam,String startCam,String endCam,String noteCam,String espe){
        final Map<String, Object> map = new HashMap<>();
        map.put("namecam", nomCam);
        map.put("iniciodate", startCam);
        map.put("findate", endCam);
        map.put("ubicam", ubiCam);
        map.put("notecam", noteCam);
        map.put("speciescam", espe);

        String id = mDatabase.push().getKey();

        mDatabase.child("Campains").child(id).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task2) {
                if(task2.isSuccessful()){
                    Toast.makeText(CrearVacuna.this, "Campaña registrada correctamente", Toast.LENGTH_SHORT).show();
                    sendNotificationToTopic("campaigns", "Nueva campaña de: "+nomCam+" \n Fechas activas del " +startCam+" al "+endCam, "Una nueva campaña ha sido creada");
                    salir();
                }
                else Toast.makeText(CrearVacuna.this, "Error al registrar datos de la campaña", Toast.LENGTH_SHORT).show();
            }
        });


    }






    private void sendNotificationToTopic(String topic, String title, String message) {
        Retrofit retrofit = RetrofitClient.getClient();
        ApiService apiService = retrofit.create(ApiService.class);

        JSONObject notification = new JSONObject();
        try {
            notification.put("title", title);
            notification.put("body", message);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JSONObject data = new JSONObject();
        try {
            data.put("to", "/topics/" + topic);
            data.put("notification", notification);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), data.toString());
        Call<Void> call = apiService.sendNotification(requestBody);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.d("Notification", "Notification sent successfully");
                } else {
                    Log.e("Notification", "Failed to send notification, response code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("Notification", "Failed to send notification: " + t.getMessage());
            }
        });
    }


    private void updCamp(String nomCam,String ubiCam,String startCam,String endCam,String noteCam,String espe){

        final Map<String, Object> map = new HashMap<>();
        map.put("namecam", nomCam);
        map.put("iniciodate", startCam);
        map.put("findate", endCam);
        map.put("ubicam", ubiCam);
        map.put("notecam", noteCam);
        map.put("speciescam", espe);

        mDatabase.child("Campains").child(keyCam).updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task2) {
                if(task2.isSuccessful()){
                    Toast.makeText(CrearVacuna.this, "Campaña Actualizada correctamente", Toast.LENGTH_SHORT).show();
                    salir();
                }
                else Toast.makeText(CrearVacuna.this, "Error al Actualizar datos de la campaña", Toast.LENGTH_SHORT).show();
            }
        });


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

    // Método para formatear una fecha como cadena de texto
    private String formatDate(Date date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        return simpleDateFormat.format(date.getTime());
    }
    }