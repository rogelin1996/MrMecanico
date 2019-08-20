package mx.avidos.mrmecanicoch.Fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import mx.avidos.mrmecanicoch.Adapters.AdapterAutomoviles;
import mx.avidos.mrmecanicoch.Adapters.AdapterCitas;
import mx.avidos.mrmecanicoch.Bases.BaseFragment;
import mx.avidos.mrmecanicoch.Entidad.Automovil;
import mx.avidos.mrmecanicoch.Entidad.Cita;
import mx.avidos.mrmecanicoch.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentCitas extends BaseFragment {
    RecyclerView rvAutos;
    AdapterCitas adapterAutomoviles;
    List<Cita> listaAutomovil;
    TextView tvSinCitas;

    public FragmentCitas() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_citas, container, false);
        initialize(view);
        showCars();
        return view;
    }

    private void initialize (View view){
        rvAutos = view.findViewById(R.id.rvCitas);
        rvAutos.setLayoutManager(new LinearLayoutManager(getContext()));
        listaAutomovil =new ArrayList<>();
        adapterAutomoviles = new AdapterCitas(listaAutomovil);
        rvAutos.setAdapter(adapterAutomoviles);
        tvSinCitas = view.findViewById(R.id.tvSinCitas);
    }

    private void showCars (){
        mDatabaseReference.child("paquetesVendidos").child(getUid()).orderByChild("fecha").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    listaAutomovil.removeAll(listaAutomovil);
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Cita cita = snapshot.getValue(Cita.class);
                        listaAutomovil.add(cita);
                    }
                    adapterAutomoviles.notifyDataSetChanged();
                    //Collections.reverse(listaAutomovil);
                    if (adapterAutomoviles != null){
                        if (adapterAutomoviles.getItemCount() == 0){
                            tvSinCitas.setVisibility(View.VISIBLE);
                            rvAutos.setVisibility(View.GONE);
                        }
                    }
                } else {
                    tvSinCitas.setVisibility(View.VISIBLE);
                    rvAutos.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}
