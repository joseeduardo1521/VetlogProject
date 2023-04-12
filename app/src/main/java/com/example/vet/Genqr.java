package com.example.vet;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.io.IOException;
import java.io.OutputStream;

public class Genqr extends AppCompatActivity {
    ImageView imgQr;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_genqr);
        EditText txtDatos = findViewById(R.id.txtDatos);
        Button btnGenera = findViewById(R.id.btnGenera);
        imgQr = findViewById(R.id.qrCode);
        Button btnSave = findViewById(R.id.btnSave);
        btnGenera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
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
    }

}