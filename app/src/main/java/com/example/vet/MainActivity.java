package com.example.vet;

import static android.content.ContentValues.TAG;
import static com.basgeekball.awesomevalidation.ValidationStyle.BASIC;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.example.vet.clases.DeleteExpiredRecordsWorker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.concurrent.TimeUnit;


public class MainActivity extends AppCompatActivity {
    private Button btnregistrar, btnlogin;
    private EditText et_mail, et_pass;
    private DatabaseReference mDatabase;
    private AwesomeValidation awesomeValidation;
    private FirebaseAuth mAuth;
    private Switch swSesion;

    public static final String SHARED_PREFS ="sharedPrefs.mail";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        PeriodicWorkRequest workRequest = new PeriodicWorkRequest.Builder(DeleteExpiredRecordsWorker.class, 24, TimeUnit.HOURS).build();
        WorkManager.getInstance(this).enqueueUniquePeriodicWork("DeleteExpiredRecords", ExistingPeriodicWorkPolicy.KEEP, workRequest);
        obetenerEstadoSw();

        btnregistrar = findViewById(R.id.btnregister);
        btnlogin = findViewById(R.id.loginbtn);
        awesomeValidation = new AwesomeValidation(BASIC);
        awesomeValidation.addValidation(this, R.id.etmail, Patterns.EMAIL_ADDRESS,  R.string.err_email);
        awesomeValidation.addValidation(this,R.id.etPass,".{8,}", R.string.err_pass);
        swSesion = findViewById(R.id.swSesion);

        et_mail= findViewById(R.id.etmail);
        et_pass= findViewById(R.id.etPass);

        btnregistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent regist = new Intent(MainActivity.this,RegisterAct.class);
                startActivity(regist);
            }
        });

       /* FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                        return;
                    }

                    // Obtener el token de registro del dispositivo
                    String token = task.getResult();

                    // Guardar el token en Realtime Database
                    DatabaseReference tokensRef = FirebaseDatabase.getInstance().getReference("tokens");
                    tokensRef.child(token).setValue(true);
                });*/

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                            return;
                        }

                        // Get new FCM registration token
                        String token = task.getResult();

                        // Log and toast
                        Log.d(TAG, "FCM registration token: " + token);
                    }
                });

        FirebaseMessaging.getInstance().subscribeToTopic("campaigns")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            // Suscrito exitosamente al tema
                            Log.d(TAG, "Suscrito");
                        } else {
                            // Error al suscribirse al tema
                            Log.d(TAG, "Error al suscribir");
                        }
                    }
                });

        btnlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(awesomeValidation.validate()){
                    String mail = String.valueOf(et_mail.getText());//Correo
                    String pass = String.valueOf(et_pass.getText());//Contraseña
                    logIn(mail,pass);
                }
            }
        });
    }

    private void   obetenerEstadoSw(){
        SharedPreferences sharedPreferences =getSharedPreferences(SHARED_PREFS,MODE_PRIVATE);
        String check = sharedPreferences.getString("mail", "");
        String check2 = sharedPreferences.getString("pass","");
        if(!check.equals("")){
            logIn(check,check2);
        }

    }


    private void logIn(String mail,String pass){
        mAuth.signInWithEmailAndPassword(mail,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    if(mAuth.getCurrentUser().isEmailVerified())
                    verificar(mail);
                    else
                        verCuenta();
                }else{
                    String error = ((FirebaseAuthException) task.getException()).getErrorCode();
                    errotToast(error);
                }
            }
        });
    }

    private void verCuenta(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Verificación de correo electrónico");
        builder.setMessage("Su cuenta aún no ha sido verificada. ¿Desea reenviar el correo de verificación?");
        builder.setPositiveButton("Reenviar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mAuth.getCurrentUser().sendEmailVerification()
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(getApplicationContext(), "Correo de verificación enviado", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(getApplicationContext(), "Error al enviar el correo de verificación", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
        builder.setNegativeButton("Cancelar", null);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void  guardarEstadoSw(){
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor= sharedPreferences.edit();
        editor.putString("mail",et_mail.getText().toString());
        editor.putString("pass",et_pass.getText().toString());
        editor.apply();
    }


    private void verificar(String mail){
        String id= mAuth.getCurrentUser().getUid();
        mDatabase.child("Usuario").child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    String correo,lvl;
                    correo = snapshot.child("email").getValue().toString();
                    lvl = snapshot.child("lvl").getValue().toString();
                    if(mail.equals(correo)) {
                        switch (lvl){
                            case "1":
                                irMenu();
                                break;
                            case "2":
                                use();
                                break;
                            case "3":
                                menuU();
                                break;
                            default:
                                Toast.makeText(MainActivity.this, "Error",
                                        Toast.LENGTH_SHORT).show();
                                break;
                        }
                    }

                    if(swSesion.isChecked()){
                        guardarEstadoSw();

                    }




                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void irMenu(){
        Intent iniciar = new Intent(this,Menu.class);
        iniciar.putExtra("mail", et_mail.getText().toString());
        iniciar.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(iniciar);
        finish();
    }
    private void use(){
        Intent reg = new Intent(this, MenuSec.class);
        reg.putExtra("mail", et_mail.getText().toString());
        reg.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(reg);
        finish();
    }
    private void menuU(){
        Intent reg = new Intent(this, Usuario_Menu.class);
        reg.putExtra("mail", et_mail.getText().toString());
        reg.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(reg);
    }

    public void clickOlvido(View view){
        Intent iniciar = new Intent(this,OlvidoActivity.class);
        startActivity(iniciar);
    }

    private void errotToast(String error) {

        switch (error) {

            case "ERROR_INVALID_CUSTOM_TOKEN":
                Toast.makeText(MainActivity.this, "El formato del token personalizado es incorrecto. Por favor revise la documentación", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_CUSTOM_TOKEN_MISMATCH":
                Toast.makeText(MainActivity.this, "El token personalizado corresponde a una audiencia diferente.", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_INVALID_CREDENTIAL":
                Toast.makeText(MainActivity.this, "La credencial de autenticación proporcionada tiene un formato incorrecto o ha caducado.", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_INVALID_EMAIL":
                Toast.makeText(MainActivity.this, "La dirección de correo electrónico está mal formateada.", Toast.LENGTH_LONG).show();
                et_mail.setError("La dirección de correo electrónico está mal formateada.");
                et_mail.requestFocus();
                break;

            case "ERROR_WRONG_PASSWORD":
                Toast.makeText(MainActivity.this, "La contraseña no es válida o el usuario no tiene contraseña.", Toast.LENGTH_LONG).show();
                et_pass.setError("la contraseña es incorrecta ");
                et_pass.requestFocus();
                et_pass.setText("");
                break;

            case "ERROR_USER_MISMATCH":
                Toast.makeText(MainActivity.this, "Las credenciales proporcionadas no corresponden al usuario que inició sesión anteriormente..", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_REQUIRES_RECENT_LOGIN":
                Toast.makeText(MainActivity.this,"Esta operación es sensible y requiere autenticación reciente. Inicie sesión nuevamente antes de volver a intentar esta solicitud.", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_ACCOUNT_EXISTS_WITH_DIFFERENT_CREDENTIAL":
                Toast.makeText(MainActivity.this, "Ya existe una cuenta con la misma dirección de correo electrónico pero diferentes credenciales de inicio de sesión. Inicie sesión con un proveedor asociado a esta dirección de correo electrónico.", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_EMAIL_ALREADY_IN_USE":
                Toast.makeText(MainActivity.this, "La dirección de correo electrónico ya está siendo utilizada por otra cuenta..   ", Toast.LENGTH_LONG).show();
                et_mail.setError("La dirección de correo electrónico ya está siendo utilizada por otra cuenta.");
                et_mail.requestFocus();
                break;

            case "ERROR_CREDENTIAL_ALREADY_IN_USE":
                Toast.makeText(MainActivity.this, "Esta credencial ya está asociada con una cuenta de usuario diferente.", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_USER_DISABLED":
                Toast.makeText(MainActivity.this, "La cuenta de usuario ha sido inhabilitada por un administrador..", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_USER_TOKEN_EXPIRED":
                Toast.makeText(MainActivity.this, "La credencial del usuario ya no es válida. El usuario debe iniciar sesión nuevamente.", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_USER_NOT_FOUND":
                Toast.makeText(MainActivity.this, "No hay ningún registro de usuario que corresponda a este identificador. Es posible que se haya eliminado al usuario.", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_INVALID_USER_TOKEN":
                Toast.makeText(MainActivity.this, "La credencial del usuario ya no es válida. El usuario debe iniciar sesión nuevamente.", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_OPERATION_NOT_ALLOWED":
                Toast.makeText(MainActivity.this, "Esta operación no está permitida. Debes habilitar este servicio en la consola.", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_WEAK_PASSWORD":
                Toast.makeText(MainActivity.this, "La contraseña proporcionada no es válida..", Toast.LENGTH_LONG).show();
                et_pass.setError("La contraseña no es válida, debe tener al menos 6 caracteres");
                et_pass.requestFocus();
                break;

        }

    }

}
