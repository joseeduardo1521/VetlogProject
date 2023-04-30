package com.example.vet;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.zxing.BarcodeFormat;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

public class Genqr extends AppCompatActivity {
    ImageView imgQr;
    FirebaseAuth mAuth;
    DatabaseReference mDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_genqr);
        EditText txtDatos = findViewById(R.id.txtDatos);
        Button btnGenera = findViewById(R.id.btnGenera);
        imgQr = findViewById(R.id.qrCode);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        Button btnSave = findViewById(R.id.btnSave);

        btnGenera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    // Oculta el teclado
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

                    BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                    Bitmap bitmap = barcodeEncoder.encodeBitmap(txtDatos.getText().toString(), BarcodeFormat.QR_CODE, 750,750);
                    imgQr.setImageBitmap(bitmap);
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveToGallery();
                String hab;
                hab = String.valueOf(txtDatos.getText());
                registro(hab);
            }
        });

    }


    private void saveToGallery() {
        if (imgQr != null) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) imgQr.getDrawable();
            Bitmap bitmap = bitmapDrawable.getBitmap();
            ContentResolver resolver = getContentResolver();
            ContentValues contentValues = new ContentValues();
            contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, "imagen_qr.jpg");
            contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg");

            // Cambiar el directorio de almacenamiento a DCIM o Pictures
            contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DCIM);
            // contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES);

            Uri imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
            if (imageUri != null) {
                try {
                    OutputStream outputStream = resolver.openOutputStream(imageUri);
                    if (outputStream != null) {
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                        outputStream.close();
                        Toast.makeText(this, "Imagen guardada en la galería", Toast.LENGTH_SHORT).show(); // Mostrar mensaje de guardado
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        else {
            Toast.makeText(this, "Primero debes generar el QR", Toast.LENGTH_SHORT).show();
        }
    }
    private void registro(String hab){

        final Map<String, Object> map = new HashMap<>();
        map.put("lugar", hab);
        map.put("idMas", "");
        map.put("fecha_ingreso", "");

        String id = mDatabase.push().getKey();
        mDatabase.child("habitaculo").child(id).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task2) {
                if(task2.isSuccessful()){
                    Toast.makeText(Genqr.this, "Registrado", Toast.LENGTH_SHORT).show();
                }
                else Toast.makeText(Genqr.this, "Error al registrar datos", Toast.LENGTH_SHORT).show();
            }
        });

    }

}