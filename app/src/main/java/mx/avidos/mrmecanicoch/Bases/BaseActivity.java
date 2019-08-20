package mx.avidos.mrmecanicoch.Bases;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Pattern;

import mx.avidos.mrmecanicoch.LoginActivity;
import mx.avidos.mrmecanicoch.MainActivity;
import mx.avidos.mrmecanicoch.SplashActivity;

public class BaseActivity extends AppCompatActivity {

    public View view;
    public LayoutInflater layoutInflater;
    public android.app.AlertDialog alertDialog;

    public static final int GALLERY_INTENT = 1;
    public static final int MY_PERMISSIONS_REQUEST_CAMERA = 2;
    public static final int REQUEST_PERMISSION_WRITE = 3;
    public static final int REQUEST_IMAGE_CAPTURE = 4;
    public String imageFilePath = "";
    public Bitmap bitmapPhoto;
    public Uri imgUri;
    public BottomSheetDialog bottomSheetDialog;

    public Toolbar toolbar;
    public DatabaseReference databaseReference;
    public StorageReference storageReference;
    public FirebaseAuth auth;
    public FirebaseAuth.AuthStateListener stateListener;

    public String uidTaller;
    public String uidAutomovil;
    public String uidPaquete;
    public String nombrePaquete;
    public String desCorta;
    public String desLarga;
    public String urlPaquete;
    public String precioPaquete;

    public String nombre, correoElectronico, telefono, contrasena, placa, marca, modelo, anio, kilometraje;

    public ProgressDialog progressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        databaseReference = FirebaseDatabase.getInstance().getReference();
        storageReference = FirebaseStorage.getInstance().getReference();
        auth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);

    }

    public void comprobarSesion (){
        if (((this instanceof SplashActivity))) {
            auth = FirebaseAuth.getInstance();
            stateListener = new FirebaseAuth.AuthStateListener() {
                @Override
                public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                    final FirebaseUser user = firebaseAuth.getCurrentUser();
                    if (user != null) {
                        toMainMenu();
                    } else {
                        takeUserToLoginScreenOnUnAuth();
                    }
                }
            };
            auth.addAuthStateListener(stateListener);
        } else if (!((this instanceof LoginActivity))){
            auth = FirebaseAuth.getInstance();
            stateListener = new FirebaseAuth.AuthStateListener() {
                @Override
                public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                    final FirebaseUser user = firebaseAuth.getCurrentUser();
                    if (user == null) {
                        takeUserToLoginScreenOnUnAuth();
                    }
                }
            };
            auth.addAuthStateListener(stateListener);
        }
    }

    private void toMainMenu (){
        finish();
        Intent intent = new Intent(BaseActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void takeUserToLoginScreenOnUnAuth() {
        Intent intent = new Intent(BaseActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    public void intentGo (Context context, Class <?> cls){
        Intent intent = new Intent().setClass(context, cls);
        startActivity(intent);
    }

    public boolean validarEmail(String email) {
        Pattern pattern = Patterns.EMAIL_ADDRESS;
        return pattern.matcher(email).matches();
    }

    public void checkPermissionsStorage (){
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_PERMISSION_WRITE);
        }
    }

    public void galleryIntent (){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, GALLERY_INTENT);
    }

    public static String timestampInMillisToStringParse(long timestampInMillis) {
        final Date date = new Date(timestampInMillis);
        try {
            final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
            return simpleDateFormat.format(date);
        } catch (Exception e) {
            return "Error en la fecha";
        }
    }

    public String timestampInMillisToStringParseTime(long timestampInMillis) {
        final Date date = new Date(timestampInMillis);
        try {
            final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("h:mm a", Locale.getDefault());
            return simpleDateFormat.format(date);
        } catch (Exception e) {
            return "Error en la fecha";
        }
    }

    public Date stringToDate(String dateString) {
        try {
            final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
            return simpleDateFormat.parse(dateString);
        } catch (Exception e) {
            return null;
        }
    }

    public Date stringToTime(String dateString) {
        try {
            final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("h:mm a", Locale.getDefault());
            return simpleDateFormat.parse(dateString);
        } catch (Exception e) {
            return null;
        }
    }

    public Long getTimestampInMillis (String fecha, String hora){
        Long dateSelected;
        Date date = stringToDate(fecha);
        Date time = stringToTime(hora);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        Calendar calendar1 = Calendar.getInstance();
        calendar1.setTime(time);
        calendar.set(Calendar.HOUR_OF_DAY, calendar1.get(Calendar.HOUR_OF_DAY));
        calendar.set(Calendar.MINUTE, calendar1.get(Calendar.MINUTE));
        return dateSelected = new Long(calendar.getTimeInMillis());
    }

    public String getUid (){
        return auth.getCurrentUser().getUid();
    }

    @Override
    public void onBackPressed() {
        finish();
    }

}
