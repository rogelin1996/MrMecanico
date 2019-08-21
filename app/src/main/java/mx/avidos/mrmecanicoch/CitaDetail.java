package mx.avidos.mrmecanicoch;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import mx.avidos.mrmecanicoch.Bases.BaseActivity;

public class CitaDetail extends BaseActivity {
    private TextView tvNombre, tvTelefono, tvPaquete, tvTaller, tvVehiculo, tvFecha, tvHora;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cita_detail);
        initializeToolbar(getString(R.string.string_citas));
        initialize();

        if (getIntent().hasExtra("UidCita")){
            uidCita = getIntent().getStringExtra("UidCita");
            uidPaquete = getIntent().getStringExtra("UidPaquete");
            uidTaller = getIntent().getStringExtra("UidTaller");
            FechaHora = getIntent().getLongExtra("FechaHora", 1);
            uidAutomovil = getIntent().getStringExtra("UidAutomovil");
            showInfo(uidCita, uidPaquete, uidTaller, FechaHora, uidAutomovil);
        }

    }

    private void initialize (){
        tvNombre = findViewById(R.id.tvNombre);
        tvTelefono = findViewById(R.id.tvTelefono);
        tvPaquete = findViewById(R.id.tvPaquete);
        tvTaller = findViewById(R.id.tvTaller);
        tvVehiculo = findViewById(R.id.tvVehiculo);
        tvFecha = findViewById(R.id.tvFecha);
        tvHora = findViewById(R.id.tvHora);
    }

    private void initializeToolbar (String titulo){
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle(titulo);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void showInfo (String uidCita, String uidPaquete, String uidTaller, long timestamp, final String uidAutomovil){
        databaseReference.child("usuarios").child(getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild("nombre")){
                    tvNombre.setText(dataSnapshot.child("nombre").getValue(String.class));
                }
                if (dataSnapshot.hasChild("telefono")){
                    tvTelefono.setText(dataSnapshot.child("telefono").getValue(String.class));
                }
                if (dataSnapshot.hasChild("automoviles")){
                    if (dataSnapshot.child("automoviles").hasChild(uidAutomovil)){
                        tvVehiculo.setText(dataSnapshot.child("automoviles").child(uidAutomovil).child("modelo").getValue(String.class));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        databaseReference.child("paquetes").child(uidPaquete).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild("paquete")){
                    tvPaquete.setText(dataSnapshot.child("paquete").getValue(String.class));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        databaseReference.child("talleres").child(uidTaller).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild("nombre")){
                    tvTaller.setText(dataSnapshot.child("nombre").getValue(String.class));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        tvFecha.setText(timestampInMillisToStringParse(timestamp));
        tvHora.setText(timestampInMillisToStringParseTime(timestamp));

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
