package mx.avidos.mrmecanicoch;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import mx.avidos.mrmecanicoch.Bases.BaseActivity;

public class LoginActivity extends BaseActivity implements View.OnClickListener {

    private EditText etUsuario, etContrasena;
    private TextView tvOlvidoContrasena, tvRegistro;
    private Button btnIniciarSesion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initialize();
        etUsuario.setText("rogegzll96@gmail.com");
        etContrasena.setText("welcome");
    }

    private void initialize (){
        etUsuario = findViewById(R.id.etUsuario);
        etContrasena = findViewById(R.id.etContrasena);
        tvOlvidoContrasena = findViewById(R.id.tvOlvidoContrasena);
        tvRegistro = findViewById(R.id.tvRegistro);
        btnIniciarSesion = findViewById(R.id.btnIniciarSesion);
        tvOlvidoContrasena.setOnClickListener(this);
        tvRegistro.setOnClickListener(this);
        btnIniciarSesion.setOnClickListener(this);
    }

    private void validarUsuario (){
        String sEmail = etUsuario.getText().toString().trim();
        String sPass = etContrasena.getText().toString().trim();

        if (!TextUtils.isEmpty(sEmail) && !TextUtils.isEmpty(sPass)){
            progressDialog.setTitle("Espere un momento por favor...");
            progressDialog.setMessage("Iniciando sesión");
            progressDialog.setCancelable(false);
            progressDialog.show();
            auth.signInWithEmailAndPassword(sEmail, sPass).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()){
                        progressDialog.dismiss();
                        comprobarSesion();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    validarErrores(e);
                }
            });
        } else {
            if (TextUtils.isEmpty(sEmail)){
                etUsuario.setError(getString(R.string.string_error_correo));
                etUsuario.requestFocus();
            } else if (TextUtils.isEmpty(sPass)){
                etContrasena.setError(getString(R.string.string_error_contrasena));
                etContrasena.requestFocus();
            }
        }
    }

    public void validarErrores (Exception e){
        progressDialog.dismiss();
        if(e.getMessage().toString().equals("The email address is badly formatted.")){
            etUsuario.setError("La dirección de correo es incorrecta");
            etUsuario.requestFocus();
        }
        if(e.getMessage().toString().equals("The password is invalid or the user does not have a password.")){
            etContrasena.setError("La contraseña es incorrecta.");
            etContrasena.requestFocus();
        }
        if(e.getMessage().toString().equals("There is no user record corresponding to this identifier. The user may have been deleted.")){
            etUsuario.setError("No existe una cuenta con el usuario ingresado.");
            etUsuario.requestFocus();
        }
        if(e.getMessage().toString().equals("A network error (such as timeout, interrupted connection or unreachable host) has occurred.")){
            etUsuario.setError("El acceso a internet se encuentra denegado.");
            etUsuario.requestFocus();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tvOlvidoContrasena:

                break;

            case R.id.tvRegistro:
                intentGo(this, RegistroActivity.class);
                break;

            case R.id.btnIniciarSesion:
                validarUsuario();
                break;
        }
    }
}
