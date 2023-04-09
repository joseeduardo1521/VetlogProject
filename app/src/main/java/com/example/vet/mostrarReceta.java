package com.example.vet;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class mostrarReceta extends AppCompatActivity {

    String key="";
    Button btnnvRec;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mostrar_receta);
        String key = getIntent().getExtras().getString("llave2");
        this.key = key;
        btnnvRec = findViewById(R.id.btn_nueva_receta);

        btnnvRec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mostrarReceta.this, new_receta.class);
                startActivity(intent);
            }
        });



    }
}