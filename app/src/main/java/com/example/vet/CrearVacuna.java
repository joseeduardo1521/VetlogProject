package com.example.vet;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class CrearVacuna extends AppCompatActivity {

    Button btnNuevaCamp;

    private TextInputEditText startDateEditText, endDateEditText;
    private Calendar startDateCalendar, endDateCalendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear_vacuna);
        btnNuevaCamp = findViewById(R.id.btnNuevaCamp);

        Animation animation = AnimationUtils.loadAnimation(this, R.anim.pulse_animation);
        btnNuevaCamp.startAnimation(animation);

        startDateEditText = findViewById(R.id.start_date_edit_text);
        endDateEditText = findViewById(R.id.end_date_edit_text);

        // Inicializar los calendarios de fecha de inicio y fecha de fin
        startDateCalendar = Calendar.getInstance();
        endDateCalendar = Calendar.getInstance();

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
                // Obtener la fecha actual
                Calendar currentDate = Calendar.getInstance();

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
    }

    // Método para formatear una fecha como cadena de texto
    private String formatDate(Date date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        return simpleDateFormat.format(date.getTime());
    }
    }