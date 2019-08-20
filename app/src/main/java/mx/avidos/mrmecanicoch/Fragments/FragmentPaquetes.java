package mx.avidos.mrmecanicoch.Fragments;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;
import mx.avidos.mrmecanicoch.Adapters.AdapterPaquetes;
import mx.avidos.mrmecanicoch.Bases.BaseFragment;
import mx.avidos.mrmecanicoch.Entidad.Paquete;
import mx.avidos.mrmecanicoch.R;
import mx.avidos.mrmecanicoch.RegistroActivity;

public class FragmentPaquetes extends BaseFragment {

    RecyclerView rvPaquetes;
    AdapterPaquetes adapterPaquetes;
    List<Paquete> listaPaquetes;
    LinearLayoutManager layoutManager;
    Spinner spAutomovil;
    List<String> kmList = new ArrayList<String>();
    List<String> uidAutoList = new ArrayList<String>();
    TextView tvSinPaquetes;

    public FragmentPaquetes() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_paquetes, container, false);
        initializeScreen(view);
        initializeSpinnerAutos(view);

        return view;
    }

    public void initializeScreen(View view){
        rvPaquetes =(RecyclerView) view.findViewById(R.id.rvPaquetes);
        layoutManager = new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL, false);
        rvPaquetes.setLayoutManager(layoutManager);
        listaPaquetes = new ArrayList<>();
        adapterPaquetes = new AdapterPaquetes(listaPaquetes);
        rvPaquetes.setAdapter(adapterPaquetes);
        tvSinPaquetes = view.findViewById(R.id.tvSinPaquetes);
    }

    private void initializeSpinnerAutos (View view){
        spAutomovil = view.findViewById(R.id.spAutomovil);
        mDatabaseReference.child("usuarios").child(getUid()).child("automoviles").orderByChild("modelo").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    List<String> modeloList = new ArrayList<String>();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        modeloList.add(snapshot.child("modelo").getValue(String.class));
                        uidAutoList.add(snapshot.getKey());
                        kmList.add(snapshot.child("kilometraje").getValue(String.class));
                    }
                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, modeloList);
                    arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spAutomovil.setAdapter(arrayAdapter);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        spAutomovil.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Object item = parent.getItemAtPosition(position);
                modelo = item.toString();
                kilometraje = kmList.get(position);
                uidAutomovil = uidAutoList.get(position);
                showPaquetes(kilometraje, uidAutomovil);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void showPaquetes (final String kilometraje, final String uidAutomovil){
        mDatabaseReference.child("paquetes").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    listaPaquetes.removeAll(listaPaquetes);
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Paquete paquete = snapshot.getValue(Paquete.class);
                        paquete.setUidPaquete(snapshot.getKey());
                        paquete.setUidAutomovil(uidAutomovil);

                        if (paquete.getKilometraje().equals(kilometraje)){
                            rvPaquetes.setVisibility(View.VISIBLE);
                            tvSinPaquetes.setVisibility(View.GONE);
                            listaPaquetes.add(paquete);
                        }

                    }
                    adapterPaquetes.notifyDataSetChanged();
                    if (adapterPaquetes != null){
                        if (adapterPaquetes.getItemCount() == 0){
                            tvSinPaquetes.setVisibility(View.VISIBLE);
                            rvPaquetes.setVisibility(View.GONE);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

}
