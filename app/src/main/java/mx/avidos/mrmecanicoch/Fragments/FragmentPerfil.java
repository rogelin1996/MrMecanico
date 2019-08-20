package mx.avidos.mrmecanicoch.Fragments;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import mx.avidos.mrmecanicoch.Adapters.AdapterAutomoviles;
import mx.avidos.mrmecanicoch.Bases.BaseFragment;
import mx.avidos.mrmecanicoch.Entidad.Automovil;
import mx.avidos.mrmecanicoch.Entidad.Usuario;
import mx.avidos.mrmecanicoch.R;
import mx.avidos.mrmecanicoch.RegistroActivity;

public class FragmentPerfil extends BaseFragment {
    TextView tvPerfilNombre, tvPerfilCorreo, tvPerfilTelefono;
    ImageView ivPerfil;
    RecyclerView rvAutos;
    AdapterAutomoviles adapterAutomoviles;
    List<Automovil> listaAutomovil;
    FloatingActionButton fabAgregarAuto;

    public FragmentPerfil() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_perfil, container, false);
        initialize(view);
        showInfo();
        showCars();

        fabAgregarAuto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initializeDialogAddCar();
            }
        });

        return view;
    }

    private void initialize (View view){
        tvPerfilNombre = view.findViewById(R.id.tvPerfilNombre);
        tvPerfilCorreo = view.findViewById(R.id.tvPerfilCorreo);
        tvPerfilTelefono = view.findViewById(R.id.tvPerfilTelefono);
        ivPerfil = view.findViewById(R.id.ivPerfil);
        fabAgregarAuto = view.findViewById(R.id.fabAgregarAuto);
        rvAutos = view.findViewById(R.id.rvAutos);
        rvAutos.setLayoutManager(new LinearLayoutManager(getContext()));
        listaAutomovil =new ArrayList<>();
        adapterAutomoviles = new AdapterAutomoviles(listaAutomovil);
        rvAutos.setAdapter(adapterAutomoviles);
    }

    private void showInfo (){
        mDatabaseReference.child("usuarios").child(getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild("imgPerfil")){
                    Glide.with(getActivity()).load(dataSnapshot.child("imgPerfil").getValue(String.class)).apply(RequestOptions.circleCropTransform()).into(ivPerfil);
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

    private void showCars (){
        mDatabaseReference.child("usuarios").child(getUid()).child("automoviles").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    listaAutomovil.removeAll(listaAutomovil);
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Automovil automovil = snapshot.getValue(Automovil.class);
                        automovil.setUidAutomovil(snapshot.getKey());
                        listaAutomovil.add(automovil);
                    }
                    adapterAutomoviles.notifyDataSetChanged();
                    Collections.reverse(listaAutomovil);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void initializeDialogAddCar (){
        layoutInflater = LayoutInflater.from(getContext());
        view = layoutInflater.inflate(R.layout.dialog_agregar_auto, null);

        final EditText etPlaca = view.findViewById(R.id.etPlaca);
        final Spinner spMarca = view.findViewById(R.id.spMarca);
        final Spinner spModelo = view.findViewById(R.id.spModelo);
        final Spinner spAnio = view.findViewById(R.id.spAnio);
        final Spinner spKilometraje = view.findViewById(R.id.spKilometraje);
        final Button btnAgregar = view.findViewById(R.id.btnAgregar);
        final ImageButton ibCancel = view.findViewById(R.id.ibCancel);

        mDatabaseReference.child("autos").child("marca").orderByKey().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    List<String> aliasList = new ArrayList<String>();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        aliasList.add(snapshot.getKey());
                    }
                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, aliasList);
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
                initializeSpinnerModelos(spModelo ,marca);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        initializeSpinnerAnios(spAnio);
        initializeSpinnerKilometraje(spKilometraje);

        btnAgregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(etPlaca.getText().toString()) && !TextUtils.isEmpty(marca) && !TextUtils.isEmpty(modelo) && !TextUtils.isEmpty(anio) && !TextUtils.isEmpty(kilometraje)){
                    mDatabaseReference.child("usuarios").child(getUid()).child("automoviles").push()
                            .setValue(new Automovil(etPlaca.getText().toString(), marca, modelo, anio, kilometraje)).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                alertDialog.dismiss();
                                Toast.makeText(getContext(), getString(R.string.string_vehiculo_agregado_exito), Toast.LENGTH_LONG).show();
                            } else {
                                alertDialog.dismiss();
                                Toast.makeText(getContext(), getString(R.string.string_vehiculo_agregado_fail), Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                } else {
                    if (TextUtils.isEmpty(etPlaca.getText().toString())){
                        etPlaca.setError(getString(R.string.string_error_placa));
                        etPlaca.requestFocus();
                    } else if (TextUtils.isEmpty(marca)){
                        Toast.makeText(getContext(), getString(R.string.string_error_marca), Toast.LENGTH_SHORT).show();
                    } else if (TextUtils.isEmpty(modelo)){
                        Toast.makeText(getContext(), getString(R.string.string_error_modelo), Toast.LENGTH_SHORT).show();
                    } else if (TextUtils.isEmpty(anio)){
                        Toast.makeText(getContext(), getString(R.string.string_error_anio), Toast.LENGTH_SHORT).show();
                    } else if (TextUtils.isEmpty(kilometraje)){
                        Toast.makeText(getContext(), getString(R.string.string_error_kilometraje), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        ibCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        alertDialog = new android.support.v7.app.AlertDialog.Builder(getContext())
                .setView(view)
                .show();
    }

    private void initializeSpinnerModelos (final Spinner spModelo, String keyMarca){
        mDatabaseReference.child("autos").child("marca").child(keyMarca).orderByKey().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    List<String> aliasList = new ArrayList<String>();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        aliasList.add(snapshot.getValue(String.class));
                    }
                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, aliasList);
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

    private void initializeSpinnerAnios (final Spinner spAnio){
        mDatabaseReference.child("autos").child("anios").orderByKey().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    List<String> aliasList = new ArrayList<String>();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        aliasList.add(snapshot.getKey());
                    }
                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, aliasList);
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

    private void initializeSpinnerKilometraje (final Spinner spKilometraje){
        mDatabaseReference.child("autos").child("kilometraje").orderByKey().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    List<String> aliasList = new ArrayList<String>();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        aliasList.add(snapshot.getKey());
                    }
                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, aliasList);
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

}
