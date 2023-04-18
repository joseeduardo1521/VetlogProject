package com.example.vet;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class Sms extends AppCompatActivity {
    EditText etMsj, etCel;
    Button btnEnviar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms);
        etMsj = findViewById(R.id.txtNombre);
        etCel = findViewById(R.id.txtSms);
        btnEnviar = findViewById(R.id.btnSend);

        btnEnviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(etCel.getText().toString(),null, etMsj.getText().toString(),null,null);
            }
        });

    }
    }
