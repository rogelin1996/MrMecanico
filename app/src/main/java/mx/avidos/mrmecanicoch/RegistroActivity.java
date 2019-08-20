package mx.avidos.mrmecanicoch;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import mx.avidos.mrmecanicoch.Bases.BaseActivity;
import mx.avidos.mrmecanicoch.Entidad.Automovil;
import mx.avidos.mrmecanicoch.Entidad.Usuario;

public class RegistroActivity extends BaseActivity implements View.OnClickListener {
    FrameLayout flCamara;
    EditText etNombre, etCorreo, etTelefono, etContrasena, etPlaca;
    Button btnRegistrarme;
    Spinner spMarca, spModelo, spAnio, spKilometraje;
    ImageView ivPerfil, ivImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);
        initialize();
        initializeSpinnerMarcas();
        initializeSpinnerAnios();
        initializeSpinnerKilometraje();
        checkPermissionsStorage();
    }

    private void initialize (){
        initializeToolbar(getString(R.string.string_registro));
        flCamara = findViewById(R.id.flCamara);
        etNombre = findViewById(R.id.etNombre);
        etCorreo = findViewById(R.id.etCorreo);
        etContrasena = findViewById(R.id.etContrasena);
        etPlaca = findViewById(R.id.etPlaca);
        etTelefono = findViewById(R.id.etTelefono);
        ivPerfil = findViewById(R.id.ivPerfil);
        ivImage = findViewById(R.id.ivImage);
        btnRegistrarme = findViewById(R.id.btnRegistrarme);
        btnRegistrarme.setOnClickListener(this);
        ivPerfil.setOnClickListener(this);
        ivImage.setOnClickListener(this);
    }

    private void initializeToolbar (String titulo){
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle(titulo);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initBottonSheet (){
        bottomSheetDialog = new BottomSheetDialog(this);
        View view = LayoutInflater.from(this).inflate(R.layout.bottom_sheet_image_options, null);
        bottomSheetDialog.setContentView(view);
        bottomSheetDialog.show();

        final LinearLayout llCamara = view.findViewById(R.id.llCamara);
        final LinearLayout llGaleria = view.findViewById(R.id.llGaleria);

        llCamara.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.dismiss();
                if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    openCameraIntent();
                } else {
                    ActivityCompat.requestPermissions(RegistroActivity.this, new String[]{android.Manifest.permission.CAMERA}, MY_PERMISSIONS_REQUEST_CAMERA);
                }
            }
        });

        llGaleria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.dismiss();
                galleryIntent();
            }
        });
    }

    private void openCameraIntent() {
        Intent pictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (pictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            }
            catch (IOException e) {
                e.printStackTrace();
                return;
            }
            Uri photoUri = FileProvider.getUriForFile(this, "mx.avidos.mrmecanicoch.fileprovider", photoFile);
            pictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
            startActivityForResult(pictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    private File createImageFile() throws IOException{
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "IMG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
        imageFilePath = image.getAbsolutePath();
        return image;
    }

    private void initializeSpinnerMarcas (){
        spMarca = findViewById(R.id.spMarca);
        databaseReference.child("autos").child("marca").orderByKey().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    List<String> aliasList = new ArrayList<String>();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        aliasList.add(snapshot.getKey());
                    }
                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(RegistroActivity.this, android.R.layout.simple_spinner_item, aliasList);
                    arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spMarca.setAdapter(arrayAdapter);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        spMarca.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Object item = parent.getItemAtPosition(position);
                marca = item.toString();
                initializeSpinnerModelos(marca);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void initializeSpinnerModelos (String keyMarca){
        spModelo = findViewById(R.id.spModelo);
        databaseReference.child("autos").child("marca").child(keyMarca).orderByKey().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    List<String> aliasList = new ArrayList<String>();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        aliasList.add(snapshot.getValue(String.class));
                    }
                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(RegistroActivity.this, android.R.layout.simple_spinner_item, aliasList);
                    arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spModelo.setAdapter(arrayAdapter);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        spModelo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Object item = parent.getItemAtPosition(position);
                modelo = item.toString();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void initializeSpinnerAnios (){
        spAnio = findViewById(R.id.spAnio);
        databaseReference.child("autos").child("anios").orderByKey().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    List<String> aliasList = new ArrayList<String>();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        aliasList.add(snapshot.getKey());
                    }
                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(RegistroActivity.this, android.R.layout.simple_spinner_item, aliasList);
                    arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spAnio.setAdapter(arrayAdapter);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        spAnio.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Object item = parent.getItemAtPosition(position);
                anio = item.toString();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void initializeSpinnerKilometraje (){
        spKilometraje = findViewById(R.id.spKilometraje);
        databaseReference.child("autos").child("kilometraje").orderByKey().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    List<String> aliasList = new ArrayList<String>();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        aliasList.add(snapshot.getKey());
                    }
                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(RegistroActivity.this, android.R.layout.simple_spinner_item, aliasList);
                    arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spKilometraje.setAdapter(arrayAdapter);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        spKilometraje.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Object item = parent.getItemAtPosition(position);
                kilometraje = item.toString();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void validarRegistro (){
        nombre = etNombre.getText().toString();
        correoElectronico = etCorreo.getText().toString();
        telefono = etTelefono.getText().toString();
        contrasena = etContrasena.getText().toString();
        placa = etPlaca.getText().toString();

        if (!TextUtils.isEmpty(nombre) && !TextUtils.isEmpty(correoElectronico) && !TextUtils.isEmpty(telefono) && !TextUtils.isEmpty(contrasena)
                && !TextUtils.isEmpty(placa) && !TextUtils.isEmpty(marca) && !TextUtils.isEmpty(modelo) && !TextUtils.isEmpty(anio) && !TextUtils.isEmpty(kilometraje)){

            if (validarEmail(correoElectronico) && contrasena.length() >= 6 && telefono.length() == 10){
                //Crear cuenta
                createAcount();
            } else {
                if (!validarEmail(correoElectronico)){
                    etCorreo.setError(getString(R.string.string_error_correo_format));
                    etCorreo.requestFocus();
                }
                else if (contrasena.length() < 6){
                    etContrasena.setError(getString(R.string.string_error_contrasena_format));
                    etContrasena.requestFocus();
                }
                else if (telefono.length() < 10){
                    etTelefono.setError(getString(R.string.string_error_telefono_format));
                    etTelefono.requestFocus();
                }
            }

        } else {
            if (TextUtils.isEmpty(nombre)){
                etNombre.setError(getString(R.string.string_error_nombre));
                etNombre.requestFocus();
            }
            else if (TextUtils.isEmpty(correoElectronico)){
                etCorreo.setError(getString(R.string.string_error_correo));
                etCorreo.requestFocus();
            }
            else if (TextUtils.isEmpty(telefono)){
                etTelefono.setError(getString(R.string.string_error_telefono));
                etTelefono.requestFocus();
            }
            else if (TextUtils.isEmpty(contrasena)){
                etContrasena.setError(getString(R.string.string_error_contrasena));
                etContrasena.requestFocus();
            }
            else if (TextUtils.isEmpty(placa)){
                etPlaca.setError(getString(R.string.string_error_placa));
                etPlaca.requestFocus();
            }
            else if (TextUtils.isEmpty(marca)){
                Toast.makeText(this, getString(R.string.string_error_marca), Toast.LENGTH_SHORT).show();
            }
            else if (TextUtils.isEmpty(modelo)){
                Toast.makeText(this, getString(R.string.string_error_modelo), Toast.LENGTH_SHORT).show();
            }
            else if (TextUtils.isEmpty(anio)){
                Toast.makeText(this, getString(R.string.string_error_anio), Toast.LENGTH_SHORT).show();
            }
            else if (TextUtils.isEmpty(kilometraje)){
                Toast.makeText(this, getString(R.string.string_error_kilometraje), Toast.LENGTH_SHORT).show();
            }
        }

    }

    private void createAcount (){
        progressDialog.setMessage("Creando tu cuenta");
        progressDialog.setCancelable(false);
        progressDialog.show();
        auth.createUserWithEmailAndPassword(correoElectronico, contrasena).addOnCompleteListener(
                this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            createUserAndAuto(auth.getCurrentUser().getUid(), bitmapPhoto);
                        } else {
                            progressDialog.dismiss();
                            if (task.getException() instanceof FirebaseAuthUserCollisionException) {//si se presenta una colisión

                            } else {
                                Toast.makeText(RegistroActivity.this, "No se pudo registrar el usuario", Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                }
        ).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e.getMessage().toString().equals("A network error (such as timeout, interrupted connection or unreachable host) has occurred.")) {
                    progressDialog.dismiss();
                    Toast.makeText(RegistroActivity.this, "El acceso a internet se encuentra denegado", Toast.LENGTH_SHORT).show();
                }
                if (e.getMessage().toString().equals("The email address is badly formatted.")) {
                    progressDialog.dismiss();
                    Toast.makeText(RegistroActivity.this, "El formato de correo no es válido o es posible que el usuario ya exista", Toast.LENGTH_SHORT).show();
                }
                if (e.getMessage().toString().equals("There is not user record corresponding to this identifier. The user may have been deleted")) {
                    progressDialog.dismiss();
                    Toast.makeText(RegistroActivity.this, "No existe una cuenta con el usuario ingresado", Toast.LENGTH_SHORT).show();
                } else {
                    progressDialog.dismiss();
                    Toast.makeText(RegistroActivity.this, "El formato de correo no es válido o es posible que el usuario ya exista", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void createUserAndAuto (final String uid, final Bitmap bitmap){
        databaseReference.child("usuarios").child(uid).setValue(new Usuario(nombre, correoElectronico, telefono)).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                if (bitmap != null){
                    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 30, bytes);
                    byte[] b = bytes.toByteArray();
                    storageReference.child("usuarios").child(uid).child("imgPerfil").putBytes(b).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
                            while (!urlTask.isSuccessful()) ;
                            Uri downloadUrl = urlTask.getResult();
                            String url = String.valueOf(downloadUrl);
                            databaseReference.child("usuarios").child(uid).child("imgPerfil").setValue(url);
                        }
                    });
                }
                databaseReference.child("usuarios").child(uid).child("automoviles").push()
                        .setValue(new Automovil(placa, marca, modelo, anio, kilometraje)).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(RegistroActivity.this, "Tu cuenta ha sido creada con éxito... Bienvenido", Toast.LENGTH_LONG).show();
                        progressDialog.dismiss();
                        toMainMenu();
                    }
                });
            }
        });
    }

    private void toMainMenu (){
        Intent intent = new Intent(RegistroActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            File imgFile;
            imgFile = new File(imageFilePath);
            if (imgFile.exists()){
                Uri imageUri = Uri.fromFile(imgFile);
                try {
                    flCamara.setVisibility(View.GONE);
                    ivPerfil.setVisibility(View.VISIBLE);
                    bitmapPhoto = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                    Glide.with(getApplicationContext()).load(bitmapPhoto).apply(RequestOptions.circleCropTransform()).into(ivPerfil);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } if(requestCode == GALLERY_INTENT && resultCode == RESULT_OK){
            imgUri = data.getData();
            try {
                flCamara.setVisibility(View.GONE);
                ivPerfil.setVisibility(View.VISIBLE);
                bitmapPhoto = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imgUri);
                Glide.with(getApplicationContext()).load(bitmapPhoto).apply(RequestOptions.circleCropTransform()).into(ivPerfil);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnRegistrarme:
                validarRegistro();
                break;

            case R.id.ivImage:
                initBottonSheet();
                break;

            case R.id.ivPerfil:
                initBottonSheet();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onSupportNavigateUp(){
        super.onBackPressed();
        return true;
    }

}
