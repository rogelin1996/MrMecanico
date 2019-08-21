package mx.avidos.mrmecanicoch;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import mx.avidos.mrmecanicoch.Bases.BaseActivity;
import mx.avidos.mrmecanicoch.Entidad.Usuario;

public class EditarPerfil extends BaseActivity implements View.OnClickListener {
    EditText tvPerfilNombre, tvPerfilTelefono;
    TextView tvPerfilCorreo;
    ImageView ivPerfil;
    Button btnGuardar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_perfil);
        initialize();
        initializeToolbar(getString(R.string.string_editar_perfil));
        showInfo();
    }

    private void initialize (){
        tvPerfilNombre = findViewById(R.id.tvPerfilNombre);
        tvPerfilTelefono = findViewById(R.id.tvPerfilTelefono);
        tvPerfilCorreo = findViewById(R.id.tvPerfilCorreo);
        ivPerfil = findViewById(R.id.ivPerfil);
        btnGuardar = findViewById(R.id.btnGuardar);
        ivPerfil.setOnClickListener(this);
        btnGuardar.setOnClickListener(this);
    }

    private void initializeToolbar (String titulo){
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle(titulo);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void showInfo (){
        databaseReference.child("usuarios").child(getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild("imgPerfil")){
                    Glide.with(getApplicationContext()).load(dataSnapshot.child("imgPerfil").getValue(String.class)).apply(RequestOptions.circleCropTransform()).into(ivPerfil);
                }
                Usuario usuario = dataSnapshot.getValue(Usuario.class);
                tvPerfilNombre.setText(usuario.getNombre());
                tvPerfilCorreo.setText(usuario.getCorreo());
                tvPerfilTelefono.setText(usuario.getTelefono());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void editarPerfil (String nombre, final String telefono, final Bitmap bitmap){
        if (!TextUtils.isEmpty(nombre) && telefono.length() == 10){
            progressDialog.setMessage(getString(R.string.string_guardando));
            progressDialog.show();
            databaseReference.child("usuarios").child(getUid()).child("nombre").setValue(nombre).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        databaseReference.child("usuarios").child(getUid()).child("telefono").setValue(telefono).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                progressDialog.dismiss();
                            }
                        });
                        if (bitmap != null){
                            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 30, bytes);
                            byte[] b = bytes.toByteArray();
                            storageReference.child("usuarios").child(getUid()).child("imgPerfil").putBytes(b).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
                                    while (!urlTask.isSuccessful()) ;
                                    Uri downloadUrl = urlTask.getResult();
                                    String url = String.valueOf(downloadUrl);
                                    databaseReference.child("usuarios").child(getUid()).child("imgPerfil").setValue(url);
                                }
                            });
                        }
                        Toast.makeText(EditarPerfil.this, getString(R.string.string_exito_perfil), Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(EditarPerfil.this, getString(R.string.string_error_perfil), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            if (TextUtils.isEmpty(nombre)){
                tvPerfilNombre.setError(getString(R.string.string_error_nombre));
                tvPerfilNombre.requestFocus();
            } else if (TextUtils.isEmpty(telefono)){
                tvPerfilTelefono.setError(getString(R.string.string_error_telefono));
                tvPerfilTelefono.requestFocus();
            } else if (telefono.length() < 10){
                tvPerfilTelefono.setError(getString(R.string.string_error_telefono_format));
                tvPerfilTelefono.requestFocus();
            }
        }
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
                    ActivityCompat.requestPermissions(EditarPerfil.this, new String[]{android.Manifest.permission.CAMERA}, MY_PERMISSIONS_REQUEST_CAMERA);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            File imgFile;
            imgFile = new File(imageFilePath);
            if (imgFile.exists()){
                Uri imageUri = Uri.fromFile(imgFile);
                try {
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
            case R.id.btnGuardar:
                editarPerfil(tvPerfilNombre.getText().toString(), tvPerfilTelefono.getText().toString(), bitmapPhoto);
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
