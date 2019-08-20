package mx.avidos.mrmecanicoch;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import mx.avidos.mrmecanicoch.Bases.BaseActivity;

public class PaqueteDetail extends BaseActivity {
    private CollapsingToolbarLayout collapsingToolbarLayout;
    ImageView ivBackground;
    TextView tvNombre, tvPrecio, tvDescripcion;
    Button btnAgendar;
    BottomSheetDialog bottomSheetDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paquete_detail);

        if (getIntent().getExtras() != null){
            uidAutomovil = getIntent().getStringExtra("UidAuto");
            uidPaquete = getIntent().getStringExtra("UidPaquete");
            nombrePaquete = getIntent().getStringExtra("Nombre");
            desCorta = getIntent().getStringExtra("DescripcionCorta");
            desLarga = getIntent().getStringExtra("DescripcionLarga");
            urlPaquete = getIntent().getStringExtra("Url");
            precioPaquete = getIntent().getStringExtra("Precio");
            initialize(nombrePaquete);
            tvNombre.setText(nombrePaquete);
            tvPrecio.setText("$ "+precioPaquete);
            tvDescripcion.setText(" - "+desLarga.replace("/", "\n \n - "));
            //Prueba gitHub
        }

        btnAgendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent().setClass(PaqueteDetail.this, AgendarActivity.class);
                intent.putExtra("UidPaquete", uidPaquete);
                intent.putExtra("UidAuto", uidAutomovil);
                startActivity(intent);
            }
        });

    }

    private void initialize (String titulo){
        collapsingToolbarLayout = findViewById(R.id.collapsingToolbarMaterial);
        collapsingToolbarLayout.setTitle(titulo);
        bottomSheetDialog = new BottomSheetDialog(this);
        toolbar = findViewById(R.id.toolbarMaterial);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        tvNombre = findViewById(R.id.tvNombre);
        tvPrecio = findViewById(R.id.tvPrecio);
        tvDescripcion = findViewById(R.id.tvDescripcion);
        btnAgendar = findViewById(R.id.btnAgendar);
        ivBackground = findViewById(R.id.ivBackground);
        Glide.with(getApplicationContext()).load(urlPaquete).into(ivBackground);
    }

    private void initBottonSheet (){
        View view = LayoutInflater.from(this).inflate(R.layout.bottom_sheet_agendar, null);
        bottomSheetDialog.setContentView(view);
        bottomSheetDialog.show();
        final ImageButton ibCancel = view.findViewById(R.id.ibCancel);
        ibCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.dismiss();
            }
        });
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