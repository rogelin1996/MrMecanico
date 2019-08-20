package mx.avidos.mrmecanicoch;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import mx.avidos.mrmecanicoch.Bases.BaseActivity;
import mx.avidos.mrmecanicoch.Entidad.Cita;

public class AgendarActivity extends BaseActivity implements View.OnClickListener, OnMapReadyCallback {

    private Cita cita;

    EditText etFecha, etHora;
    Button btnAgendar;
    Spinner spTalleres;
    List<String> aliasList = new ArrayList<String>();
    List<String> uidTallerList = new ArrayList<String>();
    private GoogleMap mMap;

    private ArrayList<Marker> tmpRealTimeMarkers = new ArrayList<>();
    private ArrayList<Marker> realTimeMarkers = new ArrayList<>();

    private static String[] PERMISSIONS_LOCATION = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
    };
    private static final int REQUEST_ACCESS_LOCATION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agendar);

        if (getIntent().hasExtra("UidPaquete")){
            uidPaquete = getIntent().getStringExtra("UidPaquete");
            uidAutomovil = getIntent().getStringExtra("UidAuto");
            initializeSpinnerTalleres();
        }

        initialize();
        initializeToolbar(getString(R.string.string_agendar_cita));
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }

    private void initialize (){
        cita = new Cita();
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.mapTalleres);
        mapFragment.getMapAsync(this);
        etFecha = findViewById(R.id.etFecha);
        etHora = findViewById(R.id.etHora);
        btnAgendar = findViewById(R.id.btnAgendar);
        etFecha.setOnClickListener(this);
        etHora.setOnClickListener(this);
        btnAgendar.setOnClickListener(this);
    }

    private void initializeToolbar (String titulo){
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle(titulo);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initializeSpinnerTalleres (){
        spTalleres = findViewById(R.id.spTalleres);
        databaseReference.child("paquetes").child(uidPaquete).child("talleres").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (final DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        databaseReference.child("talleres").child(snapshot.getKey()).child("nombre").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot1) {
                                uidTallerList.add(snapshot.getKey());
                                aliasList.add(dataSnapshot1.getValue(String.class));
                                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(AgendarActivity.this, android.R.layout.simple_spinner_item, aliasList);
                                arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                spTalleres.setAdapter(arrayAdapter);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        spTalleres.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Object item = parent.getItemAtPosition(position);
                uidTaller = uidTallerList.get(position);
                updateBusPosition(uidTaller);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public void updateBusPosition(String uidTaller) {
        databaseReference.child("talleres").child(uidTaller).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final double latitud = Double.parseDouble(dataSnapshot.child("latitud").getValue().toString());
                final double longitud = Double.parseDouble(dataSnapshot.child("longitud").getValue().toString());
                final String nombre = dataSnapshot.child("nombre").getValue(String.class);
                setMarkerPosition(latitud, longitud, nombre);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void setMarkerPosition(double lat, double lon, String nombreTaller) {
        LatLng currentPosition = new LatLng(lat, lon);
        mMap.clear();
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            mMap.setMyLocationEnabled(false);
        else
            mMap.setMyLocationEnabled(true);

        mMap.addMarker(new MarkerOptions().position(currentPosition)
                .title(nombreTaller)
                .snippet("Presiona el botón de Google para ver el tiempo de llegada estimado.")
                /*.icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("ic_bus_marker", 200, 200)))*/);

        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(currentPosition)
                .zoom(15)
                .bearing(90)
                .tilt(30)
                .build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    private void showDatePickerDialog() {
        DatePickerFragment newFragment = DatePickerFragment.newInstance(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.DAY_OF_MONTH, day);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.YEAR, year);
                etFecha.setText(timestampInMillisToStringParse(calendar.getTime().getTime()));
            }
        });
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    private void agendarCita (){
        if (!TextUtils.isEmpty(etFecha.getText().toString()) && !TextUtils.isEmpty(etHora.getText().toString())){
            Log.i("DATOS", uidAutomovil + getTimestampInMillis(etFecha.getText().toString(), etHora.getText().toString())+1
            +uidTaller+uidPaquete+getUid());
            databaseReference.child("paquetesVendidos").child(getUid()).push().setValue(new Cita(uidAutomovil, getTimestampInMillis(etFecha.getText().toString(), etHora.getText().toString()),
                    1, uidTaller, uidPaquete, getUid())).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        Toast.makeText(AgendarActivity.this, "Cita agendada con éxito", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }
            });
        } else {
            if (TextUtils.isEmpty(etFecha.getText().toString())){
                Toast.makeText(this, getString(R.string.string_fecha_cita), Toast.LENGTH_SHORT).show();
            } else if (TextUtils.isEmpty(etHora.getText().toString())){
                Toast.makeText(this, getString(R.string.string_hora_cita), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void showTimePickerDialog() {
        TimePickerFragment newFragment = TimePickerFragment.newInstance(new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.MINUTE, minute);
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                etHora.setText(timestampInMillisToStringParseTime(calendar.getTime().getTime()));
            }
        });
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    public static class DatePickerFragment extends DialogFragment {
        private DatePickerDialog.OnDateSetListener listener;
        public static DatePickerFragment newInstance(DatePickerDialog.OnDateSetListener listener) {
            DatePickerFragment fragment = new DatePickerFragment();
            fragment.setListener(listener);
            return fragment;
        }
        public void setListener(DatePickerDialog.OnDateSetListener listener) {
            this.listener = listener;
        }
        @Override
        @NonNull
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);
            return new DatePickerDialog(getActivity(), listener, year, month, day);
        }
    }

    public static class TimePickerFragment extends DialogFragment {
        private TimePickerDialog.OnTimeSetListener listener;
        public static TimePickerFragment newInstance(TimePickerDialog.OnTimeSetListener listener) {
            TimePickerFragment fragment = new TimePickerFragment();
            fragment.setListener(listener);
            return fragment;
        }
        public void setListener(TimePickerDialog.OnTimeSetListener listener) {
            this.listener = listener;
        }
        @Override
        @NonNull
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Calendar c = Calendar.getInstance();
            int hourOfDay = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);
            return new TimePickerDialog(getActivity(), listener, hourOfDay, minute, false);
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

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.etFecha:
                showDatePickerDialog();
                break;

            case R.id.etHora:
                showTimePickerDialog();
                break;

            case R.id.btnAgendar:
                agendarCita();
                break;
        }
    }
}