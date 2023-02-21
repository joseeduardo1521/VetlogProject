package com.example.vet;

import static com.basgeekball.awesomevalidation.ValidationStyle.BASIC;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class MainActivity extends AppCompatActivity {
    Button btnregistrar, btnlogin;
    EditText et_mail, et_pass;
    private DatabaseReference mDatabase;
    AwesomeValidation awesomeValidation;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnregistrar = findViewById(R.id.btnregister);
        btnlogin = findViewById(R.id.loginbtn);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        awesomeValidation = new AwesomeValidation(BASIC);
        awesomeValidation.addValidation(this, R.id.etmail, Patterns.EMAIL_ADDRESS,  R.string.err_email);
        awesomeValidation.addValidation(this,R.id.etPass,".{8,}", R.string.err_pass);

        et_mail= findViewById(R.id.etmail);
        et_pass= findViewById(R.id.etPass);

        btnregistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent regist = new Intent(MainActivity.this,RegisterAct.class);
                startActivity(regist);
            }
        });


     btnlogin.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(awesomeValidation.validate()){
                String mail = String.valueOf(et_mail.getText());//Correo
                String pass = String.valueOf(et_pass.getText());//Contraseña

                mAuth.signInWithEmailAndPassword(mail,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                           /* if(et_mail.getText().toString().contains("josee375")){
                                irMenu();
                            }else if(et_mail.getText().toString().contains("kenji.guillermo")){
                                use();
                            }else{
                                test();
                            }*/
                            verificar(mail);

                        }else{
                            String error = ((FirebaseAuthException) task.getException()).getErrorCode();
                            errotToast(error);
                        }
                    }
                });
            }
        }
    });
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
                            break;
                        case "3":
                            use();
                            break;
                        default:
                            Toast.makeText(MainActivity.this, "Error",
                                    Toast.LENGTH_SHORT).show();
                            break;
                    }
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
    private void test(){
        Intent reg = new Intent(this, Usuarios.class);
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








